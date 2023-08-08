package pt.ulisboa.ewp.node.utils.http;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.MissingRequiredHeaderException;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import org.tomitribe.auth.signatures.Verifier;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

public class HttpSignatureUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpSignatureUtils.class);

  public static final String DATETIME_WITH_TIMEZONE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
  public static final String HEADER_REQUEST_TARGET = "(request-target)";
  public static final int DATE_THRESHOLD_IN_MILLISECONDS = 5 * 60 * 1000;
  public static final String SHA_256 = "SHA-256";

  private static final List<String> WHITELIST_DIGEST_ALGORITHMS = Arrays.asList(SHA_256.toLowerCase());

  private HttpSignatureUtils() {
  }

  public static VerificationResult checkRequiredSignedHeaders(
      Signature signature, String... requiredHeaderNames) {
    for (String requiredHeaderName : requiredHeaderNames) {
      if (Arrays.stream(requiredHeaderName.split("\\|"))
          .noneMatch((String h) -> signature.getHeaders().contains(h))) {
        return VerificationResult
            .createFailure("Missing required signed header '" + requiredHeaderName + "'");
      }
    }

    return VerificationResult.createSuccess();
  }

  public static boolean verifyDate(HttpHeaders headers) {
    if (headers.containsKey(HttpHeaders.DATE)
        && !isDateWithinTimeThreshold(headers.getFirst(HttpHeaders.DATE))) {
      return false;
    }

    return !headers.containsKey(HttpConstants.HEADER_ORIGINAL_DATE)
        || isDateWithinTimeThreshold(headers.getFirst(HttpConstants.HEADER_ORIGINAL_DATE));
  }

  public static VerificationResult verifyDigest(
      ExtendedHttpHeaders headers, byte[] bodyBytes) {
    if (headers.containsKey(HttpConstants.HEADER_DIGEST)) {
      return verifyDigestValues(headers.getDigestValues(), bodyBytes);
    } else {
      return VerificationResult.createFailure("Digest header missing");
    }
  }

  public static boolean verifyHost(HttpServletRequest request, HttpHeaders headers) {
    try {
      URL requestUrl = new URL(request.getRequestURL().toString());
      String expectedHost =
          requestUrl.getHost() + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort());
      return headers.containsKey(HttpHeaders.HOST)
          && expectedHost.equals(headers.getFirst(HttpHeaders.HOST));
    } catch (MalformedURLException e) {
      LOGGER.warn("Invalid URL", e);
      return false;
    }
  }

  public static VerificationResult verifySignature(
      String method,
      String requestUri,
      HttpHeaders headers,
      Signature signature,
      RSAPublicKey publicKey) {
    try {
      Verifier verifier = new Verifier(publicKey, signature);

      boolean valid =
          verifier.verify(method.toLowerCase(), requestUri, HttpUtils.toHeadersMap(headers));
      if (!valid) {
        return VerificationResult.createFailure("Signature verification failed");
      }
    } catch (MissingRequiredHeaderException | IOException | NoSuchAlgorithmException | SignatureException e) {
      return VerificationResult.createFailure("Signature verification error: " + e.getMessage());
    }

    return VerificationResult.createSuccess();
  }

  public static boolean verifyXRequestId(HttpHeaders headers) {
    return headers.containsKey(HttpConstants.HEADER_X_REQUEST_ID)
        && Objects.requireNonNull(headers.getFirst(HttpConstants.HEADER_X_REQUEST_ID))
        .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
  }

  public static String getRequestUriWithQueryString(EwpApiHttpRequestWrapper request) {
    String queryParams = request.getOriginalQueryString();
    return (request.getRequestURI() == null ? "/" : request.getRequestURI())
        + (queryParams == null || queryParams.isEmpty() ? "" : "?" + queryParams);
  }

  public static byte[] getByteArray(EwpApiHttpRequestWrapper request) {
    return request.getBody().getBytes();
  }

  private static boolean isDateWithinTimeThreshold(String dateString) {
    Date today = new Date();
    try {
      Date requestDate =
          new SimpleDateFormat(DATETIME_WITH_TIMEZONE_FORMAT, Locale.US).parse(dateString);
      // Check that time diff is less than five minutes
      return Math.abs(today.getTime() - requestDate.getTime()) <= DATE_THRESHOLD_IN_MILLISECONDS;
    } catch (ParseException e) {
      LOGGER.warn("Can't parse date: " + dateString, e);
    }
    return false;
  }

  private static VerificationResult verifyDigestValues(
      Map<String, String> digestValues, byte[] bodyBytes) {

    if (digestValues.containsKey(SHA_256)) {
      return verifyDigestAgainstAlgorithm(SHA_256, digestValues.get(SHA_256), bodyBytes);
    }

    for (Map.Entry<String, String> entry : digestValues.entrySet()) {
      String algorithm = entry.getKey();
      if (WHITELIST_DIGEST_ALGORITHMS.contains(algorithm.toLowerCase())) {
        String requestDigestValue = entry.getValue();
        VerificationResult digestVerificationResult = verifyDigestAgainstAlgorithm(algorithm,
            requestDigestValue, bodyBytes);
        if (digestVerificationResult.isSuccess()) {
          return digestVerificationResult;
        }
      }
    }

    return VerificationResult.createFailure("No valid digest value found");
  }

  private static VerificationResult verifyDigestAgainstAlgorithm(
      String algorithm, String digestValue, byte[] bodyBytes) {

    LOGGER.debug(
        "Attempting to verify digest of body '" + new String(bodyBytes) + "'" + "' (in bytes: "
            + Arrays.toString(bodyBytes)
            + ")");

    byte[] digest;
    try {
      digest = MessageDigest.getInstance(algorithm).digest(bodyBytes);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("No such algorithm", e);
      return VerificationResult.createFailure("No such algorithm: " + algorithm);
    }
    String digestValueCalculated = new String(Base64.encodeBase64(digest));

    if (!digestValueCalculated.equals(digestValue)) {
      LOGGER.debug("Failed digest verification: request body = '" + new String(bodyBytes)
          + "'; provided digest = '" + digestValue + "'; calculated digest = '"
          + digestValueCalculated);
      return VerificationResult.createFailure(
          "Digest mismatch! calculated for algorithm "
              + algorithm
              + " (body length: "
              + bodyBytes.length
              + "): "
              + digestValueCalculated
              + ", provided: "
              + digestValue);
    }

    return VerificationResult.createSuccess();
  }

  public static String generateSignatureValue(
      KeyStoreService keyStoreService, List<String> requiredSignatureHeaderNames, String
      method,
      URI requestUri, HttpHeaders headers)
      throws IOException {
    DecodedCertificateAndKey decodedCertificateAndKey =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();
    Signature signature =
        new Signature(
            decodedCertificateAndKey.getPublicKeyFingerprint(),
            null,
            Algorithm.RSA_SHA256,
            null,
            null,
            requiredSignatureHeaderNames);
    Key key = decodedCertificateAndKey.getPrivateKey();

    Signer signer = new Signer(key, signature);
    String queryParams = requestUri.getRawQuery() == null ? "" : "?" + requestUri.getRawQuery();
    Map<String, String> headersMapWithHostHeader = HttpUtils.toHeadersMap(headers);
    headersMapWithHostHeader.put(HttpHeaders.HOST, HttpUtils.getHostHeaderValue(requestUri));
    Signature signed =
        signer.sign(method, requestUri.getPath() + queryParams, headersMapWithHostHeader);

    return signed.toString();
  }

  public static class VerificationResult {

    private final boolean success;
    private final String message;

    protected VerificationResult(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() {
      return success;
    }

    public boolean isFailure() {
      return !success;
    }

    public String getMessage() {
      return message;
    }

    public static VerificationResult createSuccess() {
      return new VerificationResult(true, null);
    }

    public static VerificationResult createFailure(String message) {
      return new VerificationResult(false, message);
    }
  }

}
