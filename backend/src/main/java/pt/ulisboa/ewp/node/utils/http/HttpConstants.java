package pt.ulisboa.ewp.node.utils.http;

public class HttpConstants {

  private HttpConstants() {
  }

  public static final String HEADER_ACCEPT_SIGNATURE = "Accept-Signature";
  public static final String HEADER_DIGEST = "Digest";
  public static final String HEADER_ORIGINAL_DATE = "Original-Date";
  public static final String HEADER_SIGNATURE = "Signature";
  public static final String HEADER_WANT_DIGEST = "Want-Digest";
  public static final String HEADER_X_EWP_NODE_COMMUNICATION_ID = "X-EWP-Node-Communication-ID";
  public static final String HEADER_X_REQUEST_ID = "X-Request-Id";
  public static final String HEADER_X_REQUEST_SIGNATURE = "X-Request-Signature";

  /**
   * Used to identify if the response's body has a data object associated
   */
  public static final String HEADER_X_HAS_DATA_OBJECT = "X-Has-Data-Object";

  public static final String HEADERS_COMMA_SEPARATED_LIST_TOKEN = ", ";

  public static final String PROTOCOL_HTTPS = "https";
}
