package pt.ulisboa.ewp.node.utils;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;

public class ConnectorUtils {

  private ConnectorUtils() {}

  public static SSLHostConfig getOrCreateSSLHostConfig(Connector connector) {
    SSLHostConfig[] sslHostConfigs = connector.findSslHostConfigs();
    if (sslHostConfigs.length == 0) {
      return new SSLHostConfig();
    } else {
      return sslHostConfigs[0];
    }
  }
}
