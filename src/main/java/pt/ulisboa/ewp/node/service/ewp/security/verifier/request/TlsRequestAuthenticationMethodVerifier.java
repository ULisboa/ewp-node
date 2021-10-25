package pt.ulisboa.ewp.node.service.ewp.security.verifier.request;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.CertificateUtils;

@Service
public class TlsRequestAuthenticationMethodVerifier
    implements AbstractRequestAuthenticationMethodVerifier {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(TlsRequestAuthenticationMethodVerifier.class);

  private final SecurityProperties securityProperties;
  private final RegistryClient registryClient;

  public TlsRequestAuthenticationMethodVerifier(
      SecurityProperties securityProperties,
      RegistryClient registryClient) {
    this.securityProperties = securityProperties;
    this.registryClient = registryClient;
  }

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.TLS;
  }

  @Override
  public EwpApiAuthenticateMethodResponse verify(EwpApiHttpRequestWrapper request) {
    X509Certificate[] certificates;
    try {
      certificates = parseCertificatesFromRequest(request);
    } catch (Exception e) {
      LOGGER.error("Failed to parse client certificate from request", e);
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.TLS, "Failed to parse client certificate")
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    if (certificates == null && !securityProperties.isAllowMissingClientCertificate()) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.TLS, "Request is not using authentication method")
          .notUsingMethod()
          .withRequiredMethodInfoFulfilled(false)
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

    CertificateUtils.logCertificates(certificates);

    X509Certificate certificate = registryClient.getCertificateKnownInEwpNetwork(certificates);
    if (certificate == null && !securityProperties.isAllowMissingClientCertificate()) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
          EwpAuthenticationMethod.TLS,
          "None of the client certificates is valid in the EWP network")
          .withResponseCode(HttpStatus.FORBIDDEN)
          .build();
    }

    return EwpApiAuthenticateMethodResponse.successBuilder(
        EwpAuthenticationMethod.TLS, registryClient.getHeisCoveredByCertificate(certificate))
        .build();
  }

  private X509Certificate[] parseCertificatesFromRequest(EwpApiHttpRequestWrapper request)
      throws IOException, CertificateException, DecoderException {
    X509Certificate[] certificates = null;
    if (securityProperties.getClientTls().getHeaderName() != null) {
      String clientTlsHeaderName = securityProperties.getClientTls().getHeaderName();
      String encodedCertificateString = request.getHeader(clientTlsHeaderName);
      if (StringUtils.isNotEmpty(encodedCertificateString)) {
        LOGGER.debug(
            "Received header {} with value: \n{}", clientTlsHeaderName, encodedCertificateString);
        String certificateString =
            URLDecoder.decode(encodedCertificateString, StandardCharsets.UTF_8.name())
                .replaceAll("[\r\n\t]", "");
        X509Certificate certificate =
            CertificateUtils.decodeCertificate(
                certificateString, securityProperties.getClientTls().getEncoding());
        certificates = new X509Certificate[]{certificate};
      }
    }

    if (certificates == null) {
      certificates =
          (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    }

    return certificates;
  }
}
