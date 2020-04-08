package pt.ulisboa.ewp.node.domain.entity.api.host.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HOST_NOTIFICATION_API_CONFIGURATION")
public class HostNotificationApiConfiguration {

  private long id;
  private String baseUrl;
  private String secret;

  private Host host;

  protected HostNotificationApiConfiguration() {}

  protected HostNotificationApiConfiguration(Host host, String secret) {
    this.host = host;
    this.secret = secret;
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

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "forwardEwpApiConfiguration")
  public Host getHost() {
    return host;
  }

  public void setHost(Host host) {
    this.host = host;
  }

  public static HostNotificationApiConfiguration create(Host host, String secret) {
    return new HostNotificationApiConfiguration(host, secret);
  }

  @Override
  public String toString() {
    return String.format(
        "EwpHostInternalApiConfiguration(host description = %s; secret = PROTECTED)",
        host.getDescription());
  }
}
