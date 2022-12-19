package pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client;

import java.util.Collection;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApi;
import pt.ulisboa.ewp.node.domain.entity.http.log.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

/**
 * Specifies a Host Forward EWP API client. A client must never be deleted. Instead, it should be
 * marked as inactive. This is necessary to guarantee the persistence of old HTTP communication logs
 * to that client.
 */
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HOST_FORWARD_EWP_API_CLIENT")
public class HostForwardEwpApiClient {

  private String id;
  private HostForwardEwpApi forwardEwpApi;
  private String secret;
  private boolean active;
  private Collection<HttpCommunicationFromHostLog> httpCommunicationFromHostLogs = new HashSet<>();

  protected HostForwardEwpApiClient() {
  }

  protected HostForwardEwpApiClient(HostForwardEwpApi forwardEwpApi, String id, String secret,
      boolean active) {
    this.forwardEwpApi = forwardEwpApi;
    this.id = id;
    this.secret = secret;
    this.active = active;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "host_forward_ewp_api_id")
  public HostForwardEwpApi getForwardEwpApi() {
    return forwardEwpApi;
  }

  public void setForwardEwpApi(
      HostForwardEwpApi forwardEwpApi) {
    this.forwardEwpApi = forwardEwpApi;
  }

  @Column(name = "secret", unique = false, nullable = false)
  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Transient
  public Host getHost() {
    return forwardEwpApi.getHost();
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "hostForwardEwpApiClient", cascade = CascadeType.ALL)
  public Collection<HttpCommunicationFromHostLog> getHttpCommunicationFromHostLogs() {
    return httpCommunicationFromHostLogs;
  }

  public void setHttpCommunicationFromHostLogs(
      Collection<HttpCommunicationFromHostLog> httpCommunicationFromHostLogs) {
    this.httpCommunicationFromHostLogs = httpCommunicationFromHostLogs;
  }

  public static HostForwardEwpApiClient create(HostForwardEwpApi forwardEwpApi, String id,
      String secret, boolean active) {
    return new HostForwardEwpApiClient(forwardEwpApi, id, secret, active);
  }

  @Override
  public String toString() {
    return "HostForwardEwpApiClient{" +
        "id='" + id + '\'' +
        ", host=" + forwardEwpApi.getHost().getCode() +
        ", secret=REDACTED" +
        ", active=" + active +
        '}';
  }
}
