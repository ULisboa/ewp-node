package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("OMOBILITY_LA")
public class EwpOutgoingMobilityLearningAgreementChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String outgoingMobilityId;

  public EwpOutgoingMobilityLearningAgreementChangeNotification() {
  }

  public EwpOutgoingMobilityLearningAgreementChangeNotification(String sendingHeiId,
      String outgoingMobilityId) {
    super(1, ZonedDateTime.now(), Status.PENDING);
    this.sendingHeiId = sendingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpOutgoingMobilityLearningAgreementChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status, String sendingHeiId,
      String outgoingMobilityId) {
    super(attemptNumber, scheduledDateTime, status);
    this.sendingHeiId = sendingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  @Column(name = "sending_hei_id", nullable = false)
  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  @Column(name = "outgoing_mobility_id", nullable = false)
  public String getOutgoingMobilityId() {
    return outgoingMobilityId;
  }

  public void setOutgoingMobilityId(String outgoingMobilityId) {
    this.outgoingMobilityId = outgoingMobilityId;
  }

  @Override
  public boolean canBeMergedInto(EwpChangeNotification o) {
    if (!(o instanceof EwpOutgoingMobilityLearningAgreementChangeNotification)) {
      return false;
    }

    EwpOutgoingMobilityLearningAgreementChangeNotification otherChangeNotification = (EwpOutgoingMobilityLearningAgreementChangeNotification) o;
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId) && outgoingMobilityId.equals(
        otherChangeNotification.outgoingMobilityId);
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityLearningAgreementChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
