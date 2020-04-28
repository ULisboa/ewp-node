package pt.ulisboa.ewp.node.client.ewp.operation.response;

import java.io.Serializable;
import java.util.List;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;

public class EwpResponse implements Serializable {

  private HttpStatus status;
  private String mediaType;
  private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
  private String rawBody = "";

  protected EwpResponse(Builder builder) {
    this.status = builder.status;
    this.mediaType = builder.mediaType;
    this.headers = builder.headers;
    this.rawBody = builder.rawBody;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMediaType() {
    return mediaType;
  }

  public ExtendedHttpHeaders getHeaders() {
    return headers;
  }

  public String getRawBody() {
    return rawBody;
  }

  public boolean isSuccess() {
    return status.is2xxSuccessful();
  }

  public boolean isClientError() {
    return status.is4xxClientError();
  }

  public boolean isServerError() {
    return status.is5xxServerError();
  }

  public static class Builder {

    private HttpStatus status;
    private String mediaType;
    private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
    private String rawBody = "";

    public Builder(HttpStatus status) {
      this.status = status;
    }

    public Builder mediaType(String mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public String mediaType() {
      return mediaType;
    }

    public Builder headers(ExtendedHttpHeaders headers) {
      this.headers = headers;
      return this;
    }

    public ExtendedHttpHeaders headers() {
      return headers;
    }

    public Builder header(String key, List<String> values) {
      headers.put(key, values);
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
