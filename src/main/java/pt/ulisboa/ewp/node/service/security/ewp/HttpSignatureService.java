package pt.ulisboa.ewp.node.service.security.ewp;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.DateUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

/**
 * Provides methods respecting
 * https://github.com/erasmus-without-paper/ewp-specs-sec-cliauth-httpsig .
 */
@Service
public class HttpSignatureService {

  private static final String DATETIME_WITH_TIMEZONE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

  private static final String SHA_256 = "SHA-256";

  public static final String HEADER_REQUEST_TARGET = "(request-target)";

  public static final int DATE_THRESHOLD_IN_MILLISECONDS = 5 * 60 * 1000;

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpSignatureService.class);

  private final KeyStoreService keyStoreService;

  public HttpSignatureService(KeyStoreService keyStoreService) {
    this.keyStoreService = keyStoreService;
  }

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

      HttpHeaders headers = new HttpHeaders();

      headers.set(HttpConstants.HEADER_ORIGINAL_DATE, stringToday);
      headers.set(HttpConstants.HEADER_DIGEST, digestHeader);

      if (requestId != null) {
        headers.set(HttpConstants.HEADER_X_REQUEST_ID, requestId);
      }

      if (requestSignature != null) {
        headers.set(HttpConstants.HEADER_X_REQUEST_SIGNATURE, requestSignature.getSignature());
      }

      // Update the response with the new headers
      headers.forEach(
          (headerName, headerValues) ->
              response.addHeader(
                  headerName,
                  String.join(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, headerValues)));

      List<String> headerNames = new ArrayList<>(new HashSet<>(response.getHeaderNames()));
      headerNames.remove(HttpHeaders.VARY);

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
      Signature signed = signer.sign("", "", HttpUtils.toHeadersMap(response));

      response.addHeader(
          HttpConstants.HEADER_SIGNATURE, signed.toString().replace("Signature ", ""));

    } catch (IOException | NoSuchAlgorithmException e) {
      LOGGER.error("Can't sign response", e);
    }
  }

}
