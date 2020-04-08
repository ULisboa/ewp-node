package pt.ulisboa.ewp.node.client.ewp.operation.response;

import java.io.Serializable;

import pt.ulisboa.ewp.node.utils.http.HttpHeadersMap;

public class EwpResponse implements Serializable {

  private int statusCode = -1;
  private String mediaType;
  private HttpHeadersMap headers = new HttpHeadersMap();
  private String rawBody = "";

  protected EwpResponse(Builder builder) {
    this.statusCode = builder.statusCode;
    this.mediaType = builder.mediaType;
    this.headers = builder.headers;
    this.rawBody = builder.rawBody;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getMediaType() {
    return mediaType;
  }

  public HttpHeadersMap getHeaders() {
    return headers;
  }

  public String getRawBody() {
    return rawBody;
  }

  public boolean isOk() {
    return 100 <= statusCode && statusCode < 400;
  }

  public boolean isError() {
    return !isOk();
  }

  public static class Builder {

    private int statusCode = -1;
    private String mediaType;
    private HttpHeadersMap headers = new HttpHeadersMap();
    private String rawBody = "";

    public Builder statusCode(int statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    public int statusCode() {
      return statusCode;
    }

    public Builder mediaType(String mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public String mediaType() {
      return mediaType;
    }

    public Builder headers(HttpHeadersMap headers) {
      this.headers = headers;
      return this;
    }

    public HttpHeadersMap headers() {
      return headers;
    }

    public Builder header(String key, String value) {
      this.headers.put(key, value);
      return this;
    }

    public String rawBody() {
      return rawBody;
    }

    public Builder rawBody(String rawBody) {
      this.rawBody = rawBody;
      return this;
    }

    public EwpResponse build() {
      return new EwpResponse(this);
    }
  }
}
