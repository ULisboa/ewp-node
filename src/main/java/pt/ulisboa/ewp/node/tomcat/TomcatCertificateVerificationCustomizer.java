package pt.ulisboa.ewp.node.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfig.CertificateVerification;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/** Customizer that configures Tomcat to expect client certificate with(out) CA, optionally. */
@Component
public class TomcatCertificateVerificationCustomizer
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

  @Autowired private Logger log;

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(
        connector -> {
          SSLHostConfig sslHostConfig = getOrCreateSSLHostConfig(connector);
          sslHostConfig.setCertificateVerification(CertificateVerification.OPTIONAL_NO_CA.name());

          ((Http11NioProtocol) connector.getProtocolHandler())
              .setTrustManagerClassName(AllClientsPermissiveTrustManager.class.getName());

          log.info(
              "Configured SSL host config's certificate verification attribute: {}",
              sslHostConfig.getCertificateVerificationAsString());
        });
  }

  private SSLHostConfig getOrCreateSSLHostConfig(Connector connector) {
    SSLHostConfig[] sslHostConfigs = connector.findSslHostConfigs();
    if (sslHostConfigs.length == 0) {
      return new SSLHostConfig();
    } else {
      return sslHostConfigs[0];
    }
  }
}
