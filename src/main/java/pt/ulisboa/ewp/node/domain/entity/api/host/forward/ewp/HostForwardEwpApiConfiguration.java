package pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp;

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
@Table(name = "HOST_FORWARD_EWP_API_CONFIGURATION")
public class HostForwardEwpApiConfiguration {

  private long id;
  private String secret;

  private Host host;

  protected HostForwardEwpApiConfiguration() {}

  protected HostForwardEwpApiConfiguration(Host host, String secret) {
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

  public static HostForwardEwpApiConfiguration create(Host host, String secret) {
    return new HostForwardEwpApiConfiguration(host, secret);
  }

  @Override
  public String toString() {
    return String.format(
        "EwpHostInternalApiConfiguration(host description = %s; secret = PROTECTED)",
        host.getDescription());
  }
}
