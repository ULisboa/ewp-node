package pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
