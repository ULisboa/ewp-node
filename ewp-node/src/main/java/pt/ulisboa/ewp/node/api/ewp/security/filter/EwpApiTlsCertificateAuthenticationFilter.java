package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostPrincipal;
import pt.ulisboa.ewp.node.api.ewp.security.exception.EwpApiSecurityException;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.LoggerUtils;

/** Filter that attempts to authenticate a request by TLS Certificate. */
public class EwpApiTlsCertificateAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

  private SecurityProperties securityProperties;
  private RegistryClient registryClient;

  public EwpApiTlsCertificateAuthenticationFilter(
      SecurityProperties securityProperties, RegistryClient registryClient) {
    this.securityProperties = securityProperties;
    this.registryClient = registryClient;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (SecurityContextHolder.getContext().getAuthentication() == null
        || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      EwpApiHttpRequestWrapper ewpApiHttpRequestWrapper = (EwpApiHttpRequestWrapper) request;
      EwpApiAuthenticateMethodResponse result = authenticate((EwpApiHttpRequestWrapper) request);

      if (result.isRequiredMethodInfoFulfilled()) {
        if (!result.isOk()) {
          throw new EwpApiSecurityException(
              result.getErrorMessage(),
              result.getStatus(),
              EwpApiSecurityException.AuthMethod.TLSCERT);
        }
        EwpApiHostAuthenticationToken authentication =
            new EwpApiHostAuthenticationToken(
                EwpAuthenticationMethod.TLS,
                new EwpApiHostPrincipal(result.getHeiIdsCoveredByClient()));
        ewpApiHttpRequestWrapper.setAuthenticationToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoggerUtils.info(
            "Valid session for host through TLS Certificate (hei IDs: "
                + authentication.getName()
                + "; roles: "
                + authentication.getAuthorities()
                + ")",
            this.getClass().getCanonicalName());
      }
    }

    chain.doFilter(request, response);
  }

  private EwpApiAuthenticateMethodResponse authenticate(EwpApiHttpRequestWrapper request) {
    X509Certificate[] certificates = null;

    if (securityProperties.getClientTls().getHeaderName() != null) {
      String clientTlsHeaderName = securityProperties.getClientTls().getHeaderName();
      try {
        String encodedCertificateString = request.getHeader(clientTlsHeaderName);
        if (encodedCertificateString != null) {
          String certificateString =
              URLDecoder.decode(encodedCertificateString, StandardCharsets.UTF_8.name())
                  .replaceAll("[\r\n\t]", "");
          byte[] decodedCertificate =
              Base64.getDecoder()
                  .decode(
                      certificateString
                          .replace(BEGIN_CERTIFICATE, "")
                          .replace(END_CERTIFICATE, ""));
          X509Certificate certificate =
              (X509Certificate)
                  CertificateFactory.getInstance("X.509")
                      .generateCertificate(new ByteArrayInputStream(decodedCertificate));
          if (certificate != null) {
            certificates = new X509Certificate[] {certificate};
          }
        }
      } catch (Exception e) {
        logger.error("Failed to parse client certificate from request: " + e.getMessage());
        return EwpApiAuthenticateMethodResponse.failureBuilder(
                EwpAuthenticationMethod.TLS,
                "Failed to parse client certificate: " + e.getMessage())
            .withResponseCode(HttpStatus.BAD_REQUEST)
            .build();
      }
    }

    if (certificates == null) {
      certificates =
          (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    }

    if (certificates == null && !securityProperties.isAllowMissingClientCertificate()) {
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.TLS, "Request is not using authentication method")
          .notUsingMethod()
          .withRequiredMethodInfoFulfilled(false)
          .withResponseCode(HttpStatus.BAD_REQUEST)
          .build();
    }

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
}
