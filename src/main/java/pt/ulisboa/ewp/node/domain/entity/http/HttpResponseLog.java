package pt.ulisboa.ewp.node.domain.entity.http;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.utils.StringUtils;

@Entity
@Table(name = "HTTP_RESPONSE_LOG")
public class HttpResponseLog {

  private static final int MAX_BODY_LENGTH = (int) Math.pow(2, 15);

  private long id;
  private HttpCommunicationLog communication;
  private int statusCode;
  private Collection<HttpHeader> headers;
  private String body;

  protected HttpResponseLog() {}

  protected HttpResponseLog(int statusCode, Collection<HttpHeader> headers, String body) {
    this.statusCode = statusCode;
    this.headers = headers;
    this.body = StringUtils.truncateWithSuffix(body, MAX_BODY_LENGTH, "====TRUNCATED====");
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "response")
  public HttpCommunicationLog getCommunication() {
    return communication;
  }

  public void setCommunication(HttpCommunicationLog communication) {
    this.communication = communication;
  }

  @Column(name = "status_code", nullable = false)
  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "responseLog", cascade = CascadeType.ALL)
  public Collection<HttpHeader> getHeaders() {
    return headers;
  }

  public void setHeaders(Collection<HttpHeader> headers) {
    this.headers = headers;
  }

  @Column(name = "body", nullable = true, columnDefinition = "TEXT")
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public static HttpResponseLog create(
      int statusCode, Collection<HttpHeader> headers, String body) {
    return new HttpResponseLog(statusCode, headers, body);
  }

  public String toRawString(int maximumBodyLineLength) {
    StringBuilder result = new StringBuilder();
    HttpStatus httpStatus = HttpStatus.resolve(getStatusCode());
    result.append("HTTP/1.1 ").append(getStatusCode()).append(" ")
        .append(httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown")
        .append(System.lineSeparator());
    for (HttpHeader header : getHeaders()) {
      String headerLine = header.getName() + ": " + header.getValue();
      result.append(StringUtils.breakTextWithLineLengthLimit(headerLine, System.lineSeparator(),
          maximumBodyLineLength)).append(System.lineSeparator());
    }
    result.append(System.lineSeparator());
    result.append(StringUtils.breakTextWithLineLengthLimit(getBody(), System.lineSeparator(),
        maximumBodyLineLength)).append(System.lineSeparator());
    return result.toString();
  }
}
