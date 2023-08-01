package pt.ulisboa.ewp.node.domain.entity.communication.log.http;

import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import pt.ulisboa.ewp.node.domain.utils.DomainConstants;
import pt.ulisboa.ewp.node.utils.StringUtils;

@Entity
@Table(name = "HTTP_REQUEST_LOG")
public class HttpRequestLog {

  private long id;
  private HttpCommunicationLog communication;
  private HttpMethodLog method;
  private String url;
  private Collection<HttpHeaderLog> headers;
  private String body;

  protected HttpRequestLog() {}

  protected HttpRequestLog(
          HttpMethodLog method, String url, Collection<HttpHeaderLog> headers, String body) {
    this.method = method;
    this.url = url;
    this.headers = headers;
    setBody(body);
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

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "request")
  public HttpCommunicationLog getCommunication() {
    return communication;
  }

  public void setCommunication(HttpCommunicationLog communication) {
    this.communication = communication;
  }

  public static HttpRequestLog create(
          HttpMethodLog method, String url, Collection<HttpHeaderLog> headers, String body) {
    return new HttpRequestLog(method, url, headers, body);
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "method", nullable = false)
  public HttpMethodLog getMethod() {
    return method;
  }

  @Column(name = "url", nullable = false, length = 2048)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setMethod(HttpMethodLog method) {
    this.method = method;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "requestLog", cascade = CascadeType.ALL)
  public Collection<HttpHeaderLog> getHeaders() {
    return headers;
  }

  @Column(name = "body", nullable = true, columnDefinition = "TEXT")
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body =
            StringUtils.truncateWithSuffix(
                    body, DomainConstants.MAX_TEXT_COLUMN_TEXT_LENGTH, "====TRUNCATED====");
  }

  public void setHeaders(Collection<HttpHeaderLog> headers) {
    this.headers = headers;
  }

  public String toRawString(int maximumBodyLineLength) {
    StringBuilder result = new StringBuilder();
    result.append(getMethod().toString()).append(" ").append(getUrl())
        .append(" HTTP/1.1").append(System.lineSeparator());
    for (HttpHeaderLog header : getHeaders()) {
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
