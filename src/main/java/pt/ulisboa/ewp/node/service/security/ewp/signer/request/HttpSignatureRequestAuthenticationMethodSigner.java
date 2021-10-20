package pt.ulisboa.ewp.node.service.security.ewp.signer.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Base64;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.DateUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;

@Service
public class HttpSignatureRequestAuthenticationMethodSigner
    implements AbstractRequestAuthenticationMethodSigner {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(HttpSignatureRequestAuthenticationMethodSigner.class);

  private final KeyStoreService keyStoreService;

  public HttpSignatureRequestAuthenticationMethodSigner(
      KeyStoreService keyStoreService) {
    this.keyStoreService = keyStoreService;
  }

  @Override
  public boolean supports(EwpAuthenticationMethod method) {
    return EwpAuthenticationMethod.HTTP_SIGNATURE.equals(method);
  }

  @Override
  public void sign(EwpRequest request) {
    try {
      final HttpHeaders headers = new HttpHeaders();

      headers.set(HttpConstants.HEADER_WANT_DIGEST, HttpSignatureUtils.SHA_256);

      headers.set(HttpConstants.HEADER_ACCEPT_SIGNATURE, Algorithm.RSA_SHA256.getPortableName());

      headers.set(HttpConstants.HEADER_X_REQUEST_ID, request.getId());

      headers.set(
          HttpConstants.HEADER_ORIGINAL_DATE,
          DateUtils.toStringAsGMT(new Date(), HttpSignatureUtils.DATETIME_WITH_TIMEZONE_FORMAT));

      String formData = request.getBody().serialize();
      byte[] bodyBytes = formData.getBytes();
      byte[] digest = MessageDigest.getInstance(HttpSignatureUtils.SHA_256).digest(bodyBytes);
      String digestHeader =
          HttpSignatureUtils.SHA_256 + "=" + new String(Base64.encodeBase64(digest));
      headers.set(HttpConstants.HEADER_DIGEST, digestHeader);

      List<String> requiredSignatureHeaderNames = new ArrayList<>();
      requiredSignatureHeaderNames.add(HttpSignatureUtils.HEADER_REQUEST_TARGET);
      requiredSignatureHeaderNames.add(HttpHeaders.HOST);
      headers.forEach(
          (key, value) -> {
            requiredSignatureHeaderNames.add(key);
            request.header(key, value);
          });

      String signatureValue =
          HttpSignatureUtils.generateSignatureValue(keyStoreService, requiredSignatureHeaderNames,
              request.getMethod().name(),
              new URI(request.getUrl()), headers);
      request.header(HttpHeaders.AUTHORIZATION, Collections.singletonList(signatureValue));
    } catch (IOException | NoSuchAlgorithmException | URISyntaxException e) {
      LOGGER.error("Can't sign request", e);
    }
  }
}
