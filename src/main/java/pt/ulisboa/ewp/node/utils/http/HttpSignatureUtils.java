package pt.ulisboa.ewp.node.utils.http;

import static pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService.DATE_THRESHOLD_IN_MILLISECONDS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.MissingRequiredHeaderException;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Verifier;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class HttpSignatureUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpSignatureUtils.class);

  private static final String DATETIME_WITH_TIMEZONE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

  private static final String MESSAGE_NO_SUCH_ALGORITHM = "No such algorithm";
  private static final String MESSAGE_MISSING_REQUIRED_HEADER = "Missing required header";

  private static final String SHA_256 = "SHA-256";

  public static final String HEADER_REQUEST_TARGET = "(request-target)";

  private HttpSignatureUtils() {
  }

  public static Optional<EwpApiAuthenticateMethodResponse> checkRequiredSignedHeaders(
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

  public static boolean verifyDate(HttpHeaders headers) {
    if (headers.containsKey(HttpHeaders.DATE)
        && !isDateWithinTimeThreshold(headers.getFirst(HttpHeaders.DATE))) {
      return false;
    }

    return !headers.containsKey(HttpConstants.HEADER_ORIGINAL_DATE)
        || isDateWithinTimeThreshold(headers.getFirst(HttpConstants.HEADER_ORIGINAL_DATE));
  }

  public static Optional<EwpApiAuthenticateMethodResponse> verifyDigest(
      ExtendedHttpHeaders headers, byte[] bodyBytes) {
    if (headers.containsKey(HttpConstants.HEADER_DIGEST)) {
      return verifyDigestAgainstAlgorithm(headers, SHA_256, bodyBytes);
    } else {
      // DOUBT: Digest header is required or not?
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, "Digest header is missing")
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
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

  public static Optional<EwpApiAuthenticateMethodResponse> verifySignature(
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
        return Optional.of(
            EwpApiAuthenticateMethodResponse.failureBuilder(
                EwpAuthenticationMethod.HTTP_SIGNATURE, "Signature verification failed")
                .withResponseCode(HttpStatus.BAD_REQUEST)
                .build());
      }
    } catch (NoSuchAlgorithmException e) {
      LOGGER.warn(String.format("%s: %s", MESSAGE_NO_SUCH_ALGORITHM, e.getMessage()));
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, MESSAGE_NO_SUCH_ALGORITHM)
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (MissingRequiredHeaderException e) {
      LOGGER.warn(String.format("%s: %s", MESSAGE_MISSING_REQUIRED_HEADER, e.getMessage()));
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              MESSAGE_MISSING_REQUIRED_HEADER + ": " + e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (IOException e) {
      LOGGER.warn(String.format("Error reading: %s", e.getMessage()));
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    } catch (SignatureException e) {
      LOGGER.warn(String.format("Signature error: %s", e.getMessage()));
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, e.getMessage())
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    }

    return Optional.empty();
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

  private static Optional<EwpApiAuthenticateMethodResponse> verifyDigestAgainstAlgorithm(
      ExtendedHttpHeaders headers, String algorithm, byte[] bodyBytes) {
    byte[] digest;
    try {
      digest = MessageDigest.getInstance(algorithm).digest(bodyBytes);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error(MESSAGE_NO_SUCH_ALGORITHM, e);
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE, MESSAGE_NO_SUCH_ALGORITHM)
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    }
    String digestValueCalculated = new String(Base64.encodeBase64(digest));

    String requestDigestValue = headers.getDigestValue(algorithm);
    if (!digestValueCalculated.equals(requestDigestValue)) {
      return Optional.of(
          EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              "Digest mismatch! calculated for algorithm "
                  + algorithm
                  + " (body length: "
                  + bodyBytes.length
                  + "): "
                  + digestValueCalculated
                  + ", provided: "
                  + requestDigestValue)
              .withResponseCode(HttpStatus.BAD_REQUEST)
              .build());
    }

    return Optional.empty();
  }

}
