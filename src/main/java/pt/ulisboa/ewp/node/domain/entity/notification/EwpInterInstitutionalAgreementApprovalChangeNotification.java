package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("IIA_APPROVAL")
public class EwpInterInstitutionalAgreementApprovalChangeNotification extends
    EwpChangeNotification {

  private String approvingHeiId;
  private String ownerHeiId;
  private String partnerHeiId;
  private String iiaId;

  public EwpInterInstitutionalAgreementApprovalChangeNotification() {
  }

  public EwpInterInstitutionalAgreementApprovalChangeNotification(String sendingHeiId,
      String partnerHeiId, String ownerHeiId, String iiaId) {
    super();
    this.approvingHeiId = sendingHeiId;
    this.partnerHeiId = partnerHeiId;
    this.ownerHeiId = ownerHeiId;
    this.iiaId = iiaId;
  }

  public EwpInterInstitutionalAgreementApprovalChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status, String sendingHeiId,
      String partnerHeiId, String ownerHeiId, String iiaId) {
    super(attemptNumber, scheduledDateTime, status);
    this.approvingHeiId = sendingHeiId;
    this.partnerHeiId = partnerHeiId;
    this.ownerHeiId = ownerHeiId;
    this.iiaId = iiaId;
  }

  @Column(name = "approving_hei_id")
  public String getApprovingHeiId() {
    return approvingHeiId;
  }

  public void setApprovingHeiId(String approvingHeiId) {
    this.approvingHeiId = approvingHeiId;
  }

  @Column(name = "owner_hei_id")
  public String getOwnerHeiId() {
    return ownerHeiId;
  }

  public void setOwnerHeiId(String ownerHeiId) {
    this.ownerHeiId = ownerHeiId;
  }

  @Column(name = "partner_hei_id")
  public String getPartnerHeiId() {
    return partnerHeiId;
  }

  public void setPartnerHeiId(String partnerHeiId) {
    this.partnerHeiId = partnerHeiId;
  }

  @Column(name = "iia_id")
  public String getIiaId() {
    return iiaId;
  }

  public void setIiaId(String iiaId) {
    this.iiaId = iiaId;
  }

  @Override
  public boolean canBeMergedInto(EwpChangeNotification o) {
    if (!(o instanceof EwpInterInstitutionalAgreementApprovalChangeNotification)) {
      return false;
    }

    EwpInterInstitutionalAgreementApprovalChangeNotification otherChangeNotification = (EwpInterInstitutionalAgreementApprovalChangeNotification) o;
    return approvingHeiId.equals(otherChangeNotification.approvingHeiId) &&
        partnerHeiId.equals(otherChangeNotification.partnerHeiId) &&
        ownerHeiId.equals(otherChangeNotification.ownerHeiId) && iiaId.equals(
        otherChangeNotification.iiaId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EwpInterInstitutionalAgreementApprovalChangeNotification)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpInterInstitutionalAgreementApprovalChangeNotification that = (EwpInterInstitutionalAgreementApprovalChangeNotification) o;
    return Objects.equals(getApprovingHeiId(), that.getApprovingHeiId())
        && Objects.equals(getOwnerHeiId(), that.getOwnerHeiId())
        && Objects.equals(getPartnerHeiId(), that.getPartnerHeiId())
        && Objects.equals(getIiaId(), that.getIiaId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getApprovingHeiId(), getOwnerHeiId(), getPartnerHeiId(),
        getIiaId());
  }

  @Override
  public String toString() {
    return "EwpInterInstitutionalAgreementApprovalChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", approvingHeiId='" + approvingHeiId + '\'' +
        ", partnerHeiId='" + partnerHeiId + '\'' +
        ", ownerHeiId='" + ownerHeiId + '\'' +
        ", iiaId='" + iiaId + '\'' +
        '}';
  }
}
