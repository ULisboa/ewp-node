package pt.ulisboa.ewp.node.service.security.ewp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.MissingRequiredHeaderException;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import org.tomitribe.auth.signatures.Verifier;

import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.HttpSignatureAuthenticationResult;
import pt.ulisboa.ewp.node.utils.DateUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpHeadersMap;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

/**
 * Provides methods respecting
 * https://github.com/erasmus-without-paper/ewp-specs-sec-cliauth-httpsig .
 */
@Service
public class HttpSignatureService {

  private static final String HEADER_AUTHORIZATION = "Authorization";

  private static final String DATETIME_WITH_TIMEZONE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

  private static final String MESSAGE_NO_SUCH_ALGORITHM = "No such algorithm";
  private static final String MESSAGE_MISSING_REQUIRED_HEADER = "Missing required header";

  private static final String SHA_256 = "SHA-256";

  public static final String HEADER_REQUEST_TARGET = "(request-target)";

  public static final int DATE_THRESHOLD_IN_MILLISECONDS = 5 * 60 * 1000;

  @Autowired private Logger log;

  @Autowired private RegistryClient registryClient;

  @Autowired private KeyStoreService keyStoreService;

  public boolean clientWantsSignedResponse(HttpServletRequest request) {
    // Check if client wants signed response and that the header is correct
    return request.getHeader(HttpConstants.HEADER_ACCEPT_SIGNATURE) != null
        && Arrays.stream(request.getHeader(HttpConstants.HEADER_ACCEPT_SIGNATURE).split(",\\s?"))
            .anyMatch(m -> Algorithm.RSA_SHA256.getPortableName().equalsIgnoreCase(m));
  }

  public void signResponse(
      HttpServletRequest request, HttpServletResponse response, byte[] bodyBytes) {
    try {
      String requestId = request.getHeader(HttpConstants.HEADER_X_REQUEST_ID);
      String requestAuthorization = request.getHeader(HttpHeaders.AUTHORIZATION);
      Signature requestSignature = null;
      if (requestAuthorization != null) {
        requestSignature = Signature.fromString(requestAuthorization);
      }

      String stringToday = DateUtils.toStringAsGMT(new Date(), DATETIME_WITH_TIMEZONE_FORMAT);

      byte[] digest = MessageDigest.getInstance(SHA_256).digest(bodyBytes);
      String digestHeader = SHA_256 + "=" + new String(Base64.encodeBase64(digest));

      HttpHeadersMap headers = new HttpHeadersMap();

      headers.put(HttpConstants.HEADER_ORIGINAL_DATE, stringToday);
      headers.put(HttpConstants.HEADER_DIGEST, digestHeader);

      if (requestId != null) {
        headers.put(HttpConstants.HEADER_X_REQUEST_ID, requestId);
      }

      if (requestSignature != null) {
        headers.put(HttpConstants.HEADER_X_REQUEST_SIGNATURE, requestSignature.getSignature());
      }

      // Update the response with the new headers
      headers.forEach(response::addHeader);

      List<String> headerNames = new ArrayList<>(response.getHeaderNames());

      DecodedCertificateAndKey decodedCertificateAndKey =
          keyStoreService.getDecodedCertificateAndKeyFromStorage();
      Signature signature =
          new Signature(
              decodedCertificateAndKey.getPublicKeyFingerprint(),
              Algorithm.RSA_SHA256,
              null,
              headerNames);
      Key key = decodedCertificateAndKey.getPrivateKey();

      Signer signer = new Signer(key, signature);
      Signature signed = signer.sign("", "", HttpUtils.getHeaders(response));

      response.addHeader(
          HttpConstants.HEADER_SIGNATURE, signed.toString().replaceAll("Signature ", ""));

    } catch (IOException | NoSuchAlgorithmException e) {
      log.error("Can't sign response", e);
    }
  }

  public void signRequest(
      String method,
      URI uri,
      String formData,
      String requestId,
      BiConsumer<String, String> headerSetter) {
    try {
      final HttpHeadersMap headers = new HttpHeadersMap();

      headers.put(HttpConstants.HEADER_WANT_DIGEST, SHA_256);

      headers.put(HttpConstants.HEADER_ACCEPT_SIGNATURE, Algorithm.RSA_SHA256.getPortableName());

      headers.put(HttpConstants.HEADER_X_REQUEST_ID, requestId);

      headers.put(
          HttpConstants.HEADER_ORIGINAL_DATE,
          DateUtils.toStringAsGMT(new Date(), DATETIME_WITH_TIMEZONE_FORMAT));

      byte[] bodyBytes = formData.getBytes();
      byte[] digest = MessageDigest.getInstance(SHA_256).digest(bodyBytes);
      String digestHeader = SHA_256 + "=" + new String(Base64.encodeBase64(digest));
      headers.put(HttpConstants.HEADER_DIGEST, digestHeader);

      List<String> requiredSignatureHeaderNames = new ArrayList<>();
      requiredSignatureHeaderNames.add(HEADER_REQUEST_TARGET);
      requiredSignatureHeaderNames.add(HttpHeaders.HOST);
      headers.forEach(
          (key, value) -> {
            requiredSignatureHeaderNames.add(key);
            headerSetter.accept(key, value);
          });

      DecodedCertificateAndKey decodedCertificateAndKey =
          keyStoreService.getDecodedCertificateAndKeyFromStorage();
      Signature signature =
          new Signature(
              decodedCertificateAndKey.getPublicKeyFingerprint(),
              Algorithm.RSA_SHA256,
              null,
              requiredSignatureHeaderNames);
      Key key = decodedCertificateAndKey.getPrivateKey();

      Signer signer = new Signer(key, signature);
      String queryParams = uri.getQuery() == null ? "" : "?" + uri.getQuery();
      HttpHeadersMap headersWithHostHeader =
          new HttpHeadersMap(headers).header(HttpHeaders.HOST, HttpUtils.getHostHeaderValue(uri));
      Signature signed = signer.sign(method, uri.getPath() + queryParams, headersWithHostHeader);

      headerSetter.accept(HttpHeaders.AUTHORIZATION, signed.toString());
    } catch (IOException | NoSuchAlgorithmException e) {
      log.error("Can't sign request", e);
    }
  }

  public EwpApiAuthenticateMethodResponse verifyHttpSignatureRequest(
      EwpApiHttpRequestWrapper request) throws IOException {
    String authorization = request.getHeader(HEADER_AUTHORIZATION);
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
        checkRequiredSignedHeaders(
            signature,
            HEADER_REQUEST_TARGET,
            "host",
            "date|original-date",
            "digest",
            "x-request-id");
    if (authenticateMethodResponse.isPresent()) {
      return authenticateMethodResponse.get();
    }

    HttpHeadersMap headers = HttpUtils.getCaseInsensitiveHeadersMap(request);

    if (!verifyHost(request, headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, "Host does not match")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    RSAPublicKey publicKey = getClientPublicKeyFromKeyId(signature);
    if (publicKey == null) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              "Key not found for fingerprint: " + signature.getKeyId())
          .withResponseCode(HttpStatus.FORBIDDEN)
          .build();
    }

    if (!verifyDate(headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    // CONSIDER verify nonce (optional by specification)

    if (!verifyXRequestId(headers)) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              "Authentication with non-canonical X-Request-ID")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    Optional<EwpApiAuthenticateMethodResponse> signatureMethodResponse =
        verifySignature(
            request.getMethod(),
            getRequestUriWithQueryString(request),
            headers,
            signature,
            publicKey);
    if (signatureMethodResponse.isPresent()) {
      return signatureMethodResponse.get();
    }

    Optional<EwpApiAuthenticateMethodResponse> digestMethodResponse =
        verifyDigest(headers, getByteArray(request));
    if (digestMethodResponse.isPresent()) {
      return digestMethodResponse.get();
    }

    request.setHeadersToIncludeFilter(
        header ->
            header.equalsIgnoreCase(HEADER_AUTHORIZATION)
                || signature.getHeaders().contains(header));

    return EwpApiAuthenticateMethodResponse.successBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            registryClient.getHeisCoveredByClientKey(publicKey))
        .build();
  }

  /**
   * Verifies an HTTP response.
   *
   * @return The result of the HTTP signature verification
   */
  public HttpSignatureAuthenticationResult verifyHttpSignatureResponse(
      String method,
      String requestUri,
      HttpHeadersMap headers,
      String rawResponse,
      String requestId) {
    if (!requestId.equals(headers.getAsList(HttpConstants.HEADER_X_REQUEST_ID).get(0))) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Header X-Request-Id does not match the id sent in the request");
    }

    if (!headers.containsKey(HttpConstants.HEADER_SIGNATURE)) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Missing Signature header in response");
    }

    String signatureHeader = headers.getAsList(HttpConstants.HEADER_SIGNATURE).get(0);

    Signature signature = Signature.fromString(signatureHeader);
    if (signature.getAlgorithm() != Algorithm.RSA_SHA256) {
      return HttpSignatureAuthenticationResult.createInvalid(
          "Only signature algorithm rsa-sha256 is supported.");
    }

    Optional<EwpApiAuthenticateMethodResponse> authenticateMethodResponse =
        checkRequiredSignedHeaders(
            signature, "date|original-date", "digest", "x-request-id", "x-request-signature");
    if (authenticateMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          authenticateMethodResponse.get().getErrorMessage());
    }

    if (!verifyDate(headers)) {
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
        verifySignature(method, requestUri, headers, signature, publicKey);
    if (signatureMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          signatureMethodResponse.get().getErrorMessage());
    }

    Optional<EwpApiAuthenticateMethodResponse> digestMethodResponse =
        verifyDigest(headers, rawResponse.getBytes());
    if (digestMethodResponse.isPresent()) {
      return HttpSignatureAuthenticationResult.createInvalid(
          digestMethodResponse.get().getErrorMessage());
    }

    return HttpSignatureAuthenticationResult.createValid();
  }

  private boolean verifyHost(HttpServletRequest request, HttpHeadersMap headers)
      throws MalformedURLException {
    URL requestUrl = new URL(request.getRequestURL().toString());
    String expectedHost =
        requestUrl.getHost() + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort());
    return headers.containsKey(HttpHeaders.HOST)
        && expectedHost.equals(headers.get(HttpHeaders.HOST));
  }

  private boolean verifyXRequestId(HttpHeadersMap headers) {
    return headers.containsKey(HttpConstants.HEADER_X_REQUEST_ID)
        && headers
            .get(HttpConstants.HEADER_X_REQUEST_ID)
            .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
  }

  private boolean verifyDate(HttpHeadersMap headers) {
    if (headers.containsKey(HttpHeaders.DATE)
        && !isDateWithinTimeThreshold(headers.get(HttpHeaders.DATE))) {
      return false;
    }

    if (headers.containsKey(HttpConstants.HEADER_ORIGINAL_DATE)
        && !isDateWithinTimeThreshold(headers.get(HttpConstants.HEADER_ORIGINAL_DATE))) {
      return false;
    }

    return true;
  }

  private Optional<EwpApiAuthenticateMethodResponse> verifyDigest(
      HttpHeadersMap headers, byte[] bodyBytes) {
    if (headers.containsKey(HttpConstants.HEADER_DIGEST)) {
      byte[] digest;
      try {
        digest = MessageDigest.getInstance(SHA_256).digest(bodyBytes);
      } catch (NoSuchAlgorithmException e) {
        log.warn(MESSAGE_NO_SUCH_ALGORITHM, e);
        return Optional.of(
            EwpApiAuthenticateMethodResponse.failureBuilder(
                    EwpAuthenticationMethod.HTTP_SIGNATURE, MESSAGE_NO_SUCH_ALGORITHM)
                .withResponseCode(HttpStatus.BAD_REQUEST)
                .build());
      }
      String digestCalculatedWithoutAlgorithm = new String(Base64.encodeBase64(digest));
      String digestCalculatedWithAlgorithm = SHA_256 + "=" + digestCalculatedWithoutAlgorithm;

      String requestDigest =
          Arrays.stream(headers.get(HttpConstants.HEADER_DIGEST).split(", "))
              .filter(d -> d.substring(0, d.indexOf('=')).equalsIgnoreCase(SHA_256))
              .findFirst()
              .orElse(null);
      if (requestDigest == null
          || !digestCalculatedWithoutAlgorithm.equals(
              requestDigest.substring(requestDigest.indexOf('=') + 1))) {
        return Optional.of(
            EwpApiAuthenticateMethodResponse.failureBuilder(
                    EwpAuthenticationMethod.HTTP_SIGNATURE,
                    "Digest mismatch! calculated (body length: "
                        + bodyBytes.length
                        + "): "
                        + digestCalculatedWithAlgorithm
                        + ", header: "
                        + requestDigest)
                .withResponseCode(HttpStatus.BAD_REQUEST)
                .build());
      }

      return Optional.empty();
    } else {
      // DOUBT: Digest header is required or not?
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
                  EwpAuthenticationMethod.HTTP_SIGNATURE, "Digest header is missing")
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    }
  }

  private Optional<EwpApiAuthenticateMethodResponse> verifySignature(
      String method,
      String requestUri,
      HttpHeadersMap headers,
      Signature signature,
      RSAPublicKey publicKey) {
    try {
      Verifier verifier = new Verifier(publicKey, signature);

      boolean valid = verifier.verify(method.toLowerCase(), requestUri, headers);
      if (!valid) {
        return Optional.of(
            EwpApiAuthenticateMethodResponse.failureBuilder(
                    EwpAuthenticationMethod.HTTP_SIGNATURE, "Signature verification failed")
                .withResponseCode(HttpStatus.BAD_REQUEST)
                .build());
      }
    } catch (NoSuchAlgorithmException e) {
      log.warn(MESSAGE_NO_SUCH_ALGORITHM + ": " + e.getMessage());
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
                  EwpAuthenticationMethod.HTTP_SIGNATURE, MESSAGE_NO_SUCH_ALGORITHM)
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (MissingRequiredHeaderException e) {
      log.warn(MESSAGE_MISSING_REQUIRED_HEADER + ": " + e.getMessage());
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
                  EwpAuthenticationMethod.HTTP_SIGNATURE,
                  MESSAGE_MISSING_REQUIRED_HEADER + ": " + e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (IOException e) {
      log.warn("Error reading: " + e.getMessage());
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
                  EwpAuthenticationMethod.HTTP_SIGNATURE, e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (SignatureException e) {
      log.warn("Signature error: " + e.getMessage());
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
                  EwpAuthenticationMethod.HTTP_SIGNATURE, e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    }

    return Optional.empty();
  }

  private RSAPublicKey getClientPublicKeyFromKeyId(Signature signature) {
    String fingerprint = signature.getKeyId();
    return registryClient.findClientRsaPublicKey(fingerprint);
  }

  private Optional<EwpApiAuthenticateMethodResponse> checkRequiredSignedHeaders(
      Signature signature, String... headers) {
    return Arrays.stream(headers)
        .map(
            header -> {
              if (Arrays.stream(header.split("\\|"))
                  .noneMatch((String h) -> signature.getHeaders().contains(h))) {
                return EwpApiAuthenticateMethodResponse.failureBuilder(
                        EwpAuthenticationMethod.HTTP_SIGNATURE,
                        "Missing required signed header '" + header + "'")
                    .withRequiredMethodInfoFulfilled(false)
                    .withResponseCode(HttpStatus.BAD_REQUEST)
                    .build();
              } else {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .findFirst();
  }

  private boolean isDateWithinTimeThreshold(String dateString) {
    Date today = new Date();
    try {
      Date requestDate =
          new SimpleDateFormat(DATETIME_WITH_TIMEZONE_FORMAT, Locale.US).parse(dateString);
      // Check that time diff is less than five minutes
      return Math.abs(today.getTime() - requestDate.getTime()) <= DATE_THRESHOLD_IN_MILLISECONDS;
    } catch (ParseException e) {
      log.warn("Can't parse date: " + dateString, e);
    }
    return false;
  }

  private String getRequestUriWithQueryString(EwpApiHttpRequestWrapper request) {
    String queryParams = request.getOriginalQueryString();
    return (request.getRequestURI() == null ? "/" : request.getRequestURI())
        + (queryParams == null || queryParams.isEmpty() ? "" : "?" + queryParams);
  }

  private byte[] getByteArray(EwpApiHttpRequestWrapper request) {
    return request.getBody().getBytes();
  }
}
