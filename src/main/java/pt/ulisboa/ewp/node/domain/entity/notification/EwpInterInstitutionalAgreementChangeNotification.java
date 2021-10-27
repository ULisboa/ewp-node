package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("IIA")
public class EwpInterInstitutionalAgreementChangeNotification extends EwpChangeNotification {

  private String notifierHeiId;
  private String partnerHeiId;
  private String iiaId;

  public EwpInterInstitutionalAgreementChangeNotification() {
  }

  public EwpInterInstitutionalAgreementChangeNotification(String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super();
    this.notifierHeiId = sendingHeiId;
    this.partnerHeiId = receivingHeiId;
    this.iiaId = outgoingMobilityId;
  }

  public EwpInterInstitutionalAgreementChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status, String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super(attemptNumber, scheduledDateTime, status);
    this.notifierHeiId = sendingHeiId;
    this.partnerHeiId = receivingHeiId;
    this.iiaId = outgoingMobilityId;
  }

  @Column(name = "notifier_hei_id")
  public String getNotifierHeiId() {
    return notifierHeiId;
  }

  public void setNotifierHeiId(String notifierHeiId) {
    this.notifierHeiId = notifierHeiId;
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
    if (!(o instanceof EwpInterInstitutionalAgreementChangeNotification)) {
      return false;
    }

    EwpInterInstitutionalAgreementChangeNotification otherChangeNotification = (EwpInterInstitutionalAgreementChangeNotification) o;
    return notifierHeiId.equals(otherChangeNotification.notifierHeiId) &&
        partnerHeiId.equals(otherChangeNotification.partnerHeiId) && iiaId.equals(
        otherChangeNotification.iiaId);
  }

  @Override
  public String toString() {
    return "EwpInterInstitutionalAgreementChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", notifierHeiId='" + notifierHeiId + '\'' +
        ", partnerHeiId='" + partnerHeiId + '\'' +
        ", iiaId='" + iiaId + '\'' +
        '}';
  }
}
