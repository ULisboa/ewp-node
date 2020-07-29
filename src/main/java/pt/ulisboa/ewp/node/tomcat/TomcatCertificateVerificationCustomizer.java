package pt.ulisboa.ewp.node.tomcat;

import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfig.CertificateVerification;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.utils.ConnectorUtils;

/** Customizer that configures Tomcat to expect client certificate with(out) CA, optionally. */
@Component
public class TomcatCertificateVerificationCustomizer
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

  @Autowired private Logger log;

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(
        connector -> {
          SSLHostConfig sslHostConfig = ConnectorUtils.getOrCreateSSLHostConfig(connector);
          sslHostConfig.setCertificateVerification(CertificateVerification.OPTIONAL_NO_CA.name());

          ((Http11NioProtocol) connector.getProtocolHandler())
              .setTrustManagerClassName(AllClientsPermissiveTrustManager.class.getName());

          log.info(
              "Configured SSL host config's certificate verification attribute: {}",
              sslHostConfig.getCertificateVerificationAsString());
        });
  }
}
