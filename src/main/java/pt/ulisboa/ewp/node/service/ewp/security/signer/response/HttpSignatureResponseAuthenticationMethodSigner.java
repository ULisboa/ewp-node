package pt.ulisboa.ewp.node.service.ewp.security.signer.response;

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
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.DateUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

@Service
public class HttpSignatureResponseAuthenticationMethodSigner
    implements AbstractResponseAuthenticationMethodSigner {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(HttpSignatureResponseAuthenticationMethodSigner.class);

  private final KeyStoreService keyStoreService;

  public HttpSignatureResponseAuthenticationMethodSigner(
      KeyStoreService keyStoreService) {
    this.keyStoreService = keyStoreService;
  }

  @Override
  public boolean supports(HttpServletRequest request) {
    return request.getHeader(HttpConstants.HEADER_ACCEPT_SIGNATURE) != null
        && Arrays.stream(request.getHeader(HttpConstants.HEADER_ACCEPT_SIGNATURE).split(",\\s?"))
        .anyMatch(m -> Algorithm.RSA_SHA256.getPortableName().equalsIgnoreCase(m));
  }

  @Override
  public void sign(HttpServletRequest request, HttpServletResponse response) {
    try {
      String requestId = request.getHeader(HttpConstants.HEADER_X_REQUEST_ID);
      String requestAuthorization = request.getHeader(HttpHeaders.AUTHORIZATION);
      Signature requestSignature = null;
      if (requestAuthorization != null) {
        requestSignature = Signature.fromString(requestAuthorization);
      }

      String stringToday = DateUtils
          .toStringAsGMT(new Date(), HttpSignatureUtils.DATETIME_WITH_TIMEZONE_FORMAT);

      byte[] bodyBytes = getResponseData(response);
      byte[] digest = MessageDigest.getInstance(HttpSignatureUtils.SHA_256).digest(bodyBytes);
      String digestHeader =
          HttpSignatureUtils.SHA_256 + "=" + new String(Base64.encodeBase64(digest));

      HttpHeaders headers = new HttpHeaders();

      headers.set(HttpConstants.HEADER_ORIGINAL_DATE, stringToday);
      headers.set(HttpConstants.HEADER_DIGEST, digestHeader);

      if (requestId != null) {
        headers.set(HttpConstants.HEADER_X_REQUEST_ID, requestId);
      }

      if (requestSignature != null) {
        headers.set(HttpConstants.HEADER_X_REQUEST_SIGNATURE, requestSignature.getSignature());
      }

      HttpUtils.setHeaders(response, headers);

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

  private static byte[] getResponseData(HttpServletResponse response) {
    ContentCachingResponseWrapper wrapper =
        WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    if (wrapper != null) {
      return wrapper.getContentAsByteArray();
    }
    return new byte[0];
  }
}
