package pt.ulisboa.ewp.node.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HEI")
public class Hei {

  private long id;
  private String schacCode;
  private Map<Locale, String> name = new HashMap<>();

  private Host host;
  private Collection<OtherHeiId> otherHeiIds = new HashSet<>();

  protected Hei() {}

  protected Hei(Host host, String schacCode, Map<Locale, String> name) {
    this.host = host;
    this.schacCode = schacCode;
    this.name = name;
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

  @Column(name = "schac_code", nullable = false, unique = true)
  public String getSchacCode() {
    return schacCode;
  }

  public void setSchacCode(String schacCode) {
    this.schacCode = schacCode;
  }

  @ElementCollection
  @MapKeyColumn(name = "locale")
  @Column(name = "name")
  @CollectionTable(name = "EWP_HEI_NAMES", joinColumns = @JoinColumn(name = "hei_id"))
  public Map<Locale, String> getName() {
    return name;
  }

  public void setName(Map<Locale, String> name) {
    this.name = name;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id")
  public Host getHost() {
    return host;
  }

  public void setHost(Host host) {
    this.host = host;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "hei", cascade = CascadeType.ALL, orphanRemoval = true)
  public Collection<OtherHeiId> getOtherHeiIds() {
    return otherHeiIds;
  }

  public void setOtherHeiIds(Collection<OtherHeiId> otherHeiIds) {
    this.otherHeiIds = otherHeiIds;
  }

  public Optional<OtherHeiId> getOtherHeiIdByType(String type) {
    return this.otherHeiIds.stream().filter(ohi -> ohi.getType().equals(type)).findFirst();
  }

  public void update(Map<Locale, String> name) {
    this.name = name;
  }

  public static Hei create(Host host, String id, Map<Locale, String> name) {
    return new Hei(host, id, name);
  }

  @Override
  public String toString() {
    return String.format(
        "Hei(host description = %s; id = %s; name = %s; other hei ids = %s)",
        host.getDescription(), id, name, otherHeiIds);
  }
}
