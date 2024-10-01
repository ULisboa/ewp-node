package pt.ulisboa.ewp.node.domain.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@DiscriminatorValue("IIA")
public class EwpInterInstitutionalAgreementChangeNotification extends EwpChangeNotification {

  private String notifierHeiId;
  private String partnerHeiId;
  private String iiaId;

  public EwpInterInstitutionalAgreementChangeNotification() {
  }

  public EwpInterInstitutionalAgreementChangeNotification(
      CommunicationLog originCommunicationLog,
      String sendingHeiId,
      String partnerHeiId,
      String iiaId) {
    super(originCommunicationLog);
    this.notifierHeiId = sendingHeiId;
    this.partnerHeiId = partnerHeiId;
    this.iiaId = iiaId;
  }

  public EwpInterInstitutionalAgreementChangeNotification(
      CommunicationLog originCommunicationLog,
      int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status,
      String sendingHeiId,
      String partnerHeiId,
      String iiaId) {
    super(originCommunicationLog, attemptNumber, scheduledDateTime, status);
    this.notifierHeiId = sendingHeiId;
    this.partnerHeiId = partnerHeiId;
    this.iiaId = iiaId;
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
    return notifierHeiId.equals(otherChangeNotification.notifierHeiId)
        && partnerHeiId.equals(otherChangeNotification.partnerHeiId)
        && iiaId.equals(otherChangeNotification.iiaId)
        && super.canBeMergedInto(o);
  }

  @Override
  @Transient
  public List<ExtraVariableEntry> getExtraVariables() {
    List<ExtraVariableEntry> result = new ArrayList<>();
    result.add(new ExtraVariableEntry("notifier_hei_id", getNotifierHeiId()));
    result.add(new ExtraVariableEntry("partner_hei_id", getPartnerHeiId()));
    result.add(new ExtraVariableEntry("iia_id", getIiaId()));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EwpInterInstitutionalAgreementChangeNotification)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpInterInstitutionalAgreementChangeNotification that = (EwpInterInstitutionalAgreementChangeNotification) o;
    return Objects.equals(getNotifierHeiId(), that.getNotifierHeiId())
        && Objects.equals(getPartnerHeiId(), that.getPartnerHeiId())
        && Objects.equals(getIiaId(), that.getIiaId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getNotifierHeiId(), getPartnerHeiId(), getIiaId());
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
