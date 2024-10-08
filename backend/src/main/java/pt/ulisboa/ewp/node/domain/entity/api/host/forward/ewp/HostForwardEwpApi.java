package pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HOST_FORWARD_EWP_API")
public class HostForwardEwpApi {

  private long id;

  private Host host;
  private Collection<HostForwardEwpApiClient> clients;

  protected HostForwardEwpApi() {
  }

  protected HostForwardEwpApi(Host host) {
    this.host = host;
    this.clients = new ArrayList<>();
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

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "forwardEwpApi", cascade = CascadeType.ALL)
  public Collection<HostForwardEwpApiClient> getClients() {
    return clients;
  }

  public void setClients(
      Collection<HostForwardEwpApiClient> clients) {
    this.clients = clients;
  }

  @Transient
  public Optional<HostForwardEwpApiClient> getActiveClientById(String clientId) {
    return getClientById(clientId).filter(HostForwardEwpApiClient::isActive);
  }

  @Transient
  public Optional<HostForwardEwpApiClient> getClientById(String clientId) {
    return this.clients.stream().filter(c -> c.getId().equals(clientId)).findFirst();
  }

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "forwardEwpApi")
  public Host getHost() {
    return host;
  }

  public void setHost(Host host) {
    this.host = host;
  }

  public static HostForwardEwpApi create(Host host) {
    return new HostForwardEwpApi(host);
  }

  @Override
  public String toString() {
    return "HostForwardEwpApi{" +
        "id=" + id +
        ", host=" + host +
        ", clients=" + clients +
        '}';
  }
}
