package pt.ulisboa.ewp.node.domain.entity.communication.log.http;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.utils.DomainConstants;
import pt.ulisboa.ewp.node.utils.ByteArrayUtils;

@Entity
@Table(name = "HTTP_RESPONSE_LOG")
public class HttpResponseLog {

  private long id;
  private HttpCommunicationLog communication;
  private int statusCode;
  private Collection<HttpHeaderLog> headers;
  private byte[] body;

  protected HttpResponseLog() {}

  protected HttpResponseLog(int statusCode, Collection<HttpHeaderLog> headers, byte[] body) {
    this.statusCode = statusCode;
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

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "response")
  public HttpCommunicationLog getCommunication() {
    return communication;
  }

  public void setCommunication(HttpCommunicationLog communication) {
    this.communication = communication;
  }

  @Transient
  public boolean isErrorCode() {
    return 400 <= statusCode && statusCode < 600;
  }

  @Column(name = "status_code", nullable = false)
  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public static HttpResponseLog create(
      int statusCode, Collection<HttpHeaderLog> headers, byte[] body) {
    return new HttpResponseLog(statusCode, headers, body);
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "responseLog", cascade = CascadeType.ALL)
  public Collection<HttpHeaderLog> getHeaders() {
    return headers;
  }

  @Column(name = "body", nullable = true, columnDefinition = "MEDIUMBLOB")
  @Lob
  @Basic(fetch = FetchType.EAGER)
  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body =
        ByteArrayUtils.truncateWithSuffix(
            body,
            DomainConstants.MAX_TEXT_COLUMN_TEXT_LENGTH,
            "====TRUNCATED====".getBytes(StandardCharsets.UTF_8));
  }

  public void setHeaders(Collection<HttpHeaderLog> headers) {
    this.headers = headers;
  }

}
