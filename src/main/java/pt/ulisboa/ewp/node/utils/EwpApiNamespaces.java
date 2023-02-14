package pt.ulisboa.ewp.node.utils;

public enum EwpApiNamespaces {
  IIAS_V6_GET_RESPONSE(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v6/endpoints/get-response.xsd");

  private final String namespaceUrl;

  EwpApiNamespaces(String namespaceUrl) {
    this.namespaceUrl = namespaceUrl;
  }

  public String getNamespaceUrl() {
    return namespaceUrl;
  }
}
