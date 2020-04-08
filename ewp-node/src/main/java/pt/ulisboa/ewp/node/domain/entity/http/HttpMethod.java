package pt.ulisboa.ewp.node.domain.entity.http;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  PATCH,
  DELETE,
  UNKNOWN;

  public static HttpMethod fromString(String text) {
    for (HttpMethod method : values()) {
      if (method.name().equalsIgnoreCase(text)) {
        return method;
      }
    }
    return UNKNOWN;
  }
}
