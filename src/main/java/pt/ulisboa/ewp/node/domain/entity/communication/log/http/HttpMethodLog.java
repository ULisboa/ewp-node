package pt.ulisboa.ewp.node.domain.entity.communication.log.http;

public enum HttpMethodLog {
  GET,
  POST,
  PUT,
  PATCH,
  DELETE,
  UNKNOWN;

  public static HttpMethodLog fromString(String text) {
    for (HttpMethodLog method : values()) {
      if (method.name().equalsIgnoreCase(text)) {
        return method;
      }
    }
    return UNKNOWN;
  }
}
