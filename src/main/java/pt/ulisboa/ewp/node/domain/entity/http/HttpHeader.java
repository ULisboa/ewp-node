package pt.ulisboa.ewp.node.domain.entity.http;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HTTP_HEADER")
public class HttpHeader {

  private long id;
  private HttpRequestLog requestLog;
  private HttpResponseLog responseLog;
  private String name;
  private String value;

  protected HttpHeader() {}

  protected HttpHeader(String name, String value) {
    this.name = name;
    this.value = value;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_log_id")
  public HttpRequestLog getRequestLog() {
    return requestLog;
  }

  public void setRequestLog(HttpRequestLog requestLog) {
    this.requestLog = requestLog;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "response_log_id")
  public HttpResponseLog getResponseLog() {
    return responseLog;
  }

  public void setResponseLog(HttpResponseLog responseLog) {
    this.responseLog = responseLog;
  }

  @Column(name = "name", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String key) {
    this.name = key;
  }

  @Column(name = "value", nullable = false, columnDefinition = "TEXT")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public static HttpHeader create(String key, String value) {
    return new HttpHeader(key, value);
  }
}
