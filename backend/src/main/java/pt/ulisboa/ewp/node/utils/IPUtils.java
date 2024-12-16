package pt.ulisboa.ewp.node.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtils {

  private static final Logger LOG = LoggerFactory.getLogger(IPUtils.class);

  private IPUtils() {}

  public static String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      LOG.error("Failed to determine local hostname", e);
      return "unknown";
    }
  }
}
