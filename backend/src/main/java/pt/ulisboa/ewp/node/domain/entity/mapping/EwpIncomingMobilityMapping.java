package pt.ulisboa.ewp.node.domain.entity.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EWP_INCOMING_MOBILITY_MAPPING")
public class EwpIncomingMobilityMapping {

  private long id;
  private String receivingHeiId;
  private String receivingOunitId;
  private String omobilityId;

  protected EwpIncomingMobilityMapping() {
  }

  protected EwpIncomingMobilityMapping(String receivingHeiId, String receivingOunitId, String omobilityId) {
    this.receivingHeiId = receivingHeiId;
    this.receivingOunitId = receivingOunitId;
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

  @Column(name = "receiving_hei_id", nullable = false)
  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
  }

  @Column(name = "receiving_ounit_id", nullable = true)
  public String getReceivingOunitId() {
    return receivingOunitId;
  }

  public void setReceivingOunitId(String receivingOunitId) {
    this.receivingOunitId = receivingOunitId;
  }

  @Column(name = "omobility_id", nullable = false)
  public String getOmobilityId() {
    return omobilityId;
  }

  public void setOmobilityId(String omobilityId) {
    this.omobilityId = omobilityId;
  }

  public static EwpIncomingMobilityMapping create(String receivingHeiId, String receivingOunitId,
      String omobilityId) {
    return new EwpIncomingMobilityMapping(receivingHeiId, receivingOunitId, omobilityId);
  }

  @Override
  public String toString() {
    return "EwpIncomingMobilityAgreementMapping{" +
        "id=" + id +
        ", receivingHeiId='" + receivingHeiId + '\'' +
        ", receivingOunitId='" + receivingOunitId + '\'' +
        ", omobilityId='" + omobilityId + '\'' +
        '}';
  }
}
