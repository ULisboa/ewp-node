package pt.ulisboa.ewp.node.client.ewp.operation.response;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

public class EwpResponse implements Serializable {

  private static final Logger LOG = LoggerFactory.getLogger(EwpResponse.class);

  private HttpStatus status;
  private String mediaType;
  private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
  private byte[] rawBody = new byte[0];

  protected EwpResponse(Builder builder) {
    this.status = builder.status;
    this.mediaType = builder.mediaType;
    this.headers = builder.headers;
    this.rawBody = builder.rawBody;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public boolean isXmlResponse() {
    return getMediaType() != null
        && (getMediaType().contains(MediaType.APPLICATION_XML_VALUE)
            || getMediaType().contains(MediaType.TEXT_XML_VALUE));
  }

  public String getMediaType() {
    return mediaType;
  }

  /** Returns the header value of EWP Node communication ID, if existing. */
  public Optional<Long> getEwpNodeCommunicationId() {
    List<String> headerValues = headers.get(HttpConstants.HEADER_X_EWP_NODE_COMMUNICATION_ID);
    if (headerValues == null || headerValues.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(Long.valueOf(headerValues.iterator().next()));
    } catch (NumberFormatException e) {
      LOG.warn(
          "Invalid EWP Node communication ID #" + headerValues.iterator().next() + ", ignoring it");
      return Optional.empty();
    }
  }

  public ExtendedHttpHeaders getHeaders() {
    return headers;
  }

  public String getRawBodyAsString() {
    return new String(this.rawBody);
  }

  public byte[] getRawBody() {
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

  public String getServerDeveloperMessage() {
    try {
      // NOTE: attempt to parse an error response
      ErrorResponseV1 errorResponse =
          XmlUtils.unmarshall(getRawBodyAsString(), ErrorResponseV1.class);
      return errorResponse.getDeveloperMessage().getValue();

    } catch (XmlCannotUnmarshallToTypeException e) {
      return null;
    }
  }

  public static EwpResponse create(Response response) {
    HttpUtils.sanitizeResponse(response);

    EwpResponse.Builder responseBuilder =
        new EwpResponse.Builder(HttpStatus.resolve(response.getStatus()));

    if (response.getMediaType() != null) {
      responseBuilder.mediaType(response.getMediaType().toString());
    }

    response
        .getHeaders()
        .forEach(
            (headerName, headerValues) ->
                responseBuilder.header(
                    headerName,
                    headerValues.stream().map(String::valueOf).collect(Collectors.toList())));

    if (response.hasEntity()) {
      response.bufferEntity();

      responseBuilder.rawBody(response.readEntity(byte[].class));
    }

    return responseBuilder.build();
  }

  public static class Builder {

    private HttpStatus status;
    private String mediaType;
    private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
    private byte[] rawBody = new byte[0];

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

    public byte[] rawBody() {
      return rawBody;
    }

    public Builder rawBody(byte[] rawBody) {
      this.rawBody = rawBody;
      return this;
    }

    public EwpResponse build() {
      return new EwpResponse(this);
    }
  }
}
