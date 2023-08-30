package pt.ulisboa.ewp.node.domain.dto.communication.log.http;

import java.util.Collection;

public class HttpResponseLogDto {

  private int statusCode;
  private Collection<HttpHeaderLogDto> headers;
  private String body;

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
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
