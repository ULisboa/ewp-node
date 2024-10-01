package pt.ulisboa.ewp.node.domain.entity.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "EWP_INTER_INSTITUTIONAL_AGREEMENT_MAPPING")
public class EwpInterInstitutionalAgreementMapping {

  private long id;
  private String heiId;
  private String ounitId;
  private String iiaId;

  protected EwpInterInstitutionalAgreementMapping() {
  }

  protected EwpInterInstitutionalAgreementMapping(String heiId, String ounitId, String iiaId) {
    this.heiId = heiId;
    this.ounitId = ounitId;
    this.iiaId = iiaId;
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

  @Column(name = "iia_id", nullable = false)
  public String getIiaId() {
    return iiaId;
  }

  public void setIiaId(String iiaId) {
    this.iiaId = iiaId;
  }

  public static EwpInterInstitutionalAgreementMapping create(
      String heiId, String ounitId, String iiaId) {
    return new EwpInterInstitutionalAgreementMapping(heiId, ounitId, iiaId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EwpInterInstitutionalAgreementMapping that = (EwpInterInstitutionalAgreementMapping) o;
    return id == that.id
        && Objects.equals(heiId, that.heiId)
        && Objects.equals(ounitId, that.ounitId)
        && Objects.equals(iiaId, that.iiaId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, heiId, ounitId, iiaId);
  }

  @Override
  public String toString() {
    return "EwpInterInstitutionalAgreementMapping{" +
        "id=" + id +
        ", heiId='" + heiId + '\'' +
        ", ounitId='" + ounitId + '\'' +
        ", iiaId='" + iiaId + '\'' +
        '}';
  }
}
