package pt.ulisboa.ewp.node.service.security.ewp.verifier.response;

import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.HttpSignatureAuthenticationResult;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;

@Service
public class HttpSignatureResponseAuthenticationMethodVerifier
    implements AbstractResponseAuthenticationMethodVerifier {

  private final RegistryClient registryClient;

  public HttpSignatureResponseAuthenticationMethodVerifier(
      RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @Override
  public boolean verifiesAgainstMethod(EwpAuthenticationMethod method) {
    return EwpAuthenticationMethod.HTTP_SIGNATURE.equals(method);
  }

  @Override
  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    if (!request.getId()
        .equals(response.getHeaders().getFirst(HttpConstants.HEADER_X_REQUEST_ID))) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Header X-Request-Id does not match the id sent in the request");
    }

    if (!response.getHeaders().containsKey(HttpConstants.HEADER_SIGNATURE)) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Missing Signature header in response");
    }

    String signatureHeader = response.getHeaders().getFirst(HttpConstants.HEADER_SIGNATURE);
    if (StringUtils.isEmpty(signatureHeader)) {
      return HttpSignatureAuthenticationResult.createInvalid("Signature header must be set.");
    }

    Signature signature = Signature.fromString(signatureHeader);
    if (signature.getAlgorithm() != Algorithm.RSA_SHA256) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Only signature algorithm rsa-sha256 is supported.");
    }

    Optional<EwpApiAuthenticateMethodResponse> authenticateMethodResponse =
        HttpSignatureUtils.checkRequiredSignedHeaders(
            signature, "date|original-date", "digest", "x-request-id", "x-request-signature");
    if (authenticateMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          authenticateMethodResponse.get().getErrorMessage());
    }

    if (!HttpSignatureUtils.verifyDate(response.getHeaders())) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.");
    }

    String fingerprint = signature.getKeyId();
    RSAPublicKey publicKey = registryClient.findRsaPublicKey(fingerprint);
    if (publicKey == null) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Key not found for fingerprint: " + fingerprint);
    }

    Optional<EwpApiAuthenticateMethodResponse> signatureMethodResponse =
        HttpSignatureUtils
            .verifySignature(request.getMethod().name(), request.getUrlWithoutQueryParams(),
                response.getHeaders(), signature, publicKey);
    if (signatureMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          signatureMethodResponse.get().getErrorMessage());
    }

    Optional<EwpApiAuthenticateMethodResponse> digestMethodResponse =
        HttpSignatureUtils.verifyDigest(response.getHeaders(), response.getRawBody().getBytes());
    if (digestMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          digestMethodResponse.get().getErrorMessage());
    }

    return HttpSignatureAuthenticationResult.createValid();
  }
}
