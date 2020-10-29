package pt.ulisboa.ewp.node.api.ewp.utils;

public class EwpApiConstants {

  private EwpApiConstants() {}

  public static final String API_BASE_URI = "/api/ewp/";

  /** Should be used only for excecional backwards compatibility. */
  public static final String REST_BASE_URI = "/rest/ewp/";

  public static final String DISCOVERY_VERSION = "5.0.0";
  public static final String ECHO_VERSION = "2.0.1";
  public static final String INSTITUTIONS_VERSION = "2.1.0";
  public static final String ORGANIZATIONAL_UNITS_VERSION = "2.1.1";

  public static final int MAX_HEI_IDS = 1;
}
