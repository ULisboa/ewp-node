package pt.ulisboa.ewp.node.domain.dto.communication.log.http;

import java.util.Collection;

public class HttpRequestLogDto {

  private String method;
  private String url;
  private Collection<HttpHeaderLogDto> headers;
  private String body;

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Collection<HttpHeaderLogDto> getHeaders() {
    return headers;
  }

  public void setHeaders(Collection<HttpHeaderLogDto> headers) {
    this.headers = headers;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
