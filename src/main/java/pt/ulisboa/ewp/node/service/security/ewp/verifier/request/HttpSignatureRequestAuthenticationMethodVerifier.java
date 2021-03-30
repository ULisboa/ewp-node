package pt.ulisboa.ewp.node.service.security.ewp.verifier.request;

import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

@Service
public class HttpSignatureRequestAuthenticationMethodVerifier
    implements AbstractRequestAuthenticationMethodVerifier {

  private final RegistryClient registryClient;

  public HttpSignatureRequestAuthenticationMethodVerifier(
      RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.HTTP_SIGNATURE;
  }

  @Override
  public EwpApiAuthenticateMethodResponse verify(EwpApiHttpRequestWrapper request) {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorization == null || !authorization.toLowerCase().startsWith("signature")) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE, "Request is not using authentication method")
          .notUsingMethod()
          .withRequiredMethodInfoFulfilled(false)
          .withResponseCode(HttpStatus.UNAUTHORIZED)
          .build();
    }

    Signature signature = Signature.fromString(authorization);
    if (signature.getAlgorithm() != Algorithm.RSA_SHA256) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE,
          "Only signature algorithm rsa-sha256 is supported.")
          .withRequiredMethodInfoFulfilled(false)
          .withResponseCode(HttpStatus.UNAUTHORIZED)
          .build();
    }

    Optional<EwpApiAuthenticateMethodResponse> authenticateMethodResponse =
        HttpSignatureUtils.checkRequiredSignedHeaders(
            signature,
            HttpSignatureUtils.HEADER_REQUEST_TARGET,
            "host",
            "date|original-date",
            "digest",
            "x-request-id");
    if (authenticateMethodResponse.isPresent()) {
      return authenticateMethodResponse.get();
    }

    ExtendedHttpHeaders headers = HttpUtils.toExtendedHttpHeaders(request);

    if (!HttpSignatureUtils.verifyHost(request, headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE, "Host does not match")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    RSAPublicKey publicKey = registryClient.findClientRsaPublicKey(signature.getKeyId());
    if (publicKey == null) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE,
          "Key not found for fingerprint: " + signature.getKeyId())
          .withResponseCode(HttpStatus.FORBIDDEN)
          .build();
    }

    if (!HttpSignatureUtils.verifyDate(headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE,
          "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    // CONSIDER verify nonce (optional by specification)

    if (!HttpSignatureUtils.verifyXRequestId(headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.HTTP_SIGNATURE,
          "Authentication with non-canonical X-Request-ID")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    Optional<EwpApiAuthenticateMethodResponse> signatureMethodResponse =
        HttpSignatureUtils.verifySignature(
            request.getMethod(),
            HttpSignatureUtils.getRequestUriWithQueryString(request),
            headers,
            signature,
            publicKey);
    if (signatureMethodResponse.isPresent()) {
      return signatureMethodResponse.get();
    }

    Optional<EwpApiAuthenticateMethodResponse> digestMethodResponse =
        HttpSignatureUtils.verifyDigest(headers, HttpSignatureUtils.getByteArray(request));
    if (digestMethodResponse.isPresent()) {
      return digestMethodResponse.get();
    }

    request.setHeadersToIncludeFilter(
        header ->
            header.equalsIgnoreCase(HttpHeaders.AUTHORIZATION)
                || signature.getHeaders().contains(header));

    return EwpApiAuthenticateMethodResponse.successBuilder(
        EwpAuthenticationMethod.HTTP_SIGNATURE,
        registryClient.getHeisCoveredByClientKey(publicKey))
        .build();
  }
}
