package pt.ulisboa.ewp.node.domain.entity.mapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EWP_OUTGOING_MOBILITY_MAPPING")
public class EwpOutgoingMobilityMapping {

  private long id;
  private String heiId;
  private String ounitId;
  private String omobilityId;

  protected EwpOutgoingMobilityMapping() {
  }

  protected EwpOutgoingMobilityMapping(String heiId, String ounitId, String omobilityId) {
    this.heiId = heiId;
    this.ounitId = ounitId;
    this.omobilityId = omobilityId;
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

  @Column(name = "hei_id", nullable = false)
  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
  }

  @Column(name = "ounit_id", nullable = true)
  public String getOunitId() {
    return ounitId;
  }

  public void setOunitId(String ounitId) {
    this.ounitId = ounitId;
  }

  @Column(name = "omobility_id", nullable = false)
  public String getOmobilityId() {
    return omobilityId;
  }

  public void setOmobilityId(String omobilityId) {
    this.omobilityId = omobilityId;
  }

  public static EwpOutgoingMobilityMapping create(String heiId, String ounitId,
      String omobilityId) {
    return new EwpOutgoingMobilityMapping(heiId, ounitId, omobilityId);
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityAgreementMapping{" +
        "id=" + id +
        ", heiId='" + heiId + '\'' +
        ", ounitId='" + ounitId + '\'' +
        ", omobilityId='" + omobilityId + '\'' +
        '}';
  }
}
