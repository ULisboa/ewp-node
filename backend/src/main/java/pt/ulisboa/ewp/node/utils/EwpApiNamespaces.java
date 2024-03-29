package pt.ulisboa.ewp.node.utils;

public enum EwpApiNamespaces {
  IIAS_V3_GET_RESPONSE(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v3/endpoints/get-response.xsd"),
  IIAS_V4_GET_RESPONSE(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v4/endpoints/get-response.xsd"),
  IIAS_V6_GET_RESPONSE(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v6/endpoints/get-response.xsd"),
  IIAS_V7_GET_RESPONSE(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v7/endpoints/get-response.xsd");

  private final String namespaceUrl;

  EwpApiNamespaces(String namespaceUrl) {
    this.namespaceUrl = namespaceUrl;
  }

  public String getNamespaceUrl() {
    return namespaceUrl;
  }
}
