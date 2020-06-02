package pt.ulisboa.ewp.node.domain.entity.http;

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
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.utils.StringUtils;

@Entity
@Table(name = "HTTP_REQUEST_LOG")
public class HttpRequestLog {

  private static final int MAX_BODY_LENGTH = (int) Math.pow(2, 15);

  private long id;
  private HttpCommunicationLog communication;
  private HttpMethod method;
  private String url;
  private Collection<HttpHeader> headers;
  private String body;

  protected HttpRequestLog() {}

  protected HttpRequestLog(
      HttpMethod method, String url, Collection<HttpHeader> headers, String body) {
    this.method = method;
    this.url = url;
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

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "request")
  public HttpCommunicationLog getCommunication() {
    return communication;
  }

  public void setCommunication(HttpCommunicationLog communication) {
    this.communication = communication;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "method", nullable = false)
  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  @Column(name = "url", nullable = false, length = 2048)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "requestLog", cascade = CascadeType.ALL)
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

  public static HttpRequestLog create(
      HttpMethod method, String url, Collection<HttpHeader> headers, String body) {
    return new HttpRequestLog(method, url, headers, body);
  }
}
