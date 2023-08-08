package pt.ulisboa.ewp.node.client.ewp.operation.response;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

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
    return status != null && status.is2xxSuccessful();
  }

  public boolean isClientError() {
    return status != null && status.is4xxClientError();
  }

  public boolean isServerError() {
    return status != null && status.is5xxServerError();
  }

  public static EwpResponse create(Response response) {
    HttpUtils.sanitizeResponse(response);

    EwpResponse.Builder responseBuilder =
        new EwpResponse.Builder(HttpStatus.resolve(response.getStatus()));
    responseBuilder.mediaType(response.getMediaType().toString());

    response
        .getHeaders()
        .forEach(
            (headerName, headerValues) ->
                responseBuilder.header(
                    headerName,
                    headerValues.stream().map(String::valueOf).collect(Collectors.toList())));

    if (response.hasEntity()) {
      response.bufferEntity();

      responseBuilder.rawBody(response.readEntity(String.class));
    }

    return responseBuilder.build();
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
