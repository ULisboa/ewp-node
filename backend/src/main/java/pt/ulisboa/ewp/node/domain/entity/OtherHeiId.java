package pt.ulisboa.ewp.node.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(
    name = "OTHER_HEI_ID",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"hei_id", "type"})})
public class OtherHeiId {

  private long id;
  private String type;
  private String value;

  private Hei hei;

  protected OtherHeiId() {}

  protected OtherHeiId(Hei hei, String type, String value) {
    this.hei = hei;
    this.type = type;
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

  @Column(name = "type", nullable = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "id_value", nullable = false)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hei_id")
  public Hei getHei() {
    return hei;
  }

  public void setHei(Hei hei) {
    this.hei = hei;
  }

  public static OtherHeiId create(Hei hei, String type, String value) {
    return new OtherHeiId(hei, type, value);
  }

  @Override
  public String toString() {
    return String.format(
        "OtherHeiId(hei schac code = %s; type = %s; value = %s)",
        hei != null ? hei.getSchacCode() : null, type, value);
  }
}
