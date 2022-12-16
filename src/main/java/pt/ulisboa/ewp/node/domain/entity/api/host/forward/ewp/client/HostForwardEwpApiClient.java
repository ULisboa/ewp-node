package pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApi;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HOST_FORWARD_EWP_API_CLIENT")
public class HostForwardEwpApiClient {

  private String id;
  private HostForwardEwpApi forwardEwpApi;
  private String secret;

  protected HostForwardEwpApiClient() {
  }

  protected HostForwardEwpApiClient(HostForwardEwpApi forwardEwpApi, String id, String secret) {
    this.forwardEwpApi = forwardEwpApi;
    this.id = id;
    this.secret = secret;
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

  @Transient
  public Host getHost() {
    return forwardEwpApi.getHost();
  }

  public void update(String secret) {
    this.secret = secret;
  }

  public static HostForwardEwpApiClient create(HostForwardEwpApi forwardEwpApi, String id,
      String secret) {
    return new HostForwardEwpApiClient(forwardEwpApi, id, secret);
  }

  @Override
  public String toString() {
    return "HostForwardEwpApiClient{" +
        "id='" + id + '\'' +
        ", secret=REDACTED" +
        '}';
  }
}
