package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("OMOBILITY_LA")
public class EwpOutgoingMobilityLearningAgreementChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String receivingHeiId;
  private String outgoingMobilityId;

  public EwpOutgoingMobilityLearningAgreementChangeNotification() {
  }

  public EwpOutgoingMobilityLearningAgreementChangeNotification(String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super(1, ZonedDateTime.now(), Status.PENDING);
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpOutgoingMobilityLearningAgreementChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status, String sendingHeiId,
      String receivingHeiId, String outgoingMobilityId) {
    super(attemptNumber, scheduledDateTime, status);
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  @Column(name = "sending_hei_id")
  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  @Column(name = "receiving_hei_id")
  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
  }

  @Column(name = "outgoing_mobility_id")
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
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId)
        && receivingHeiId.equals(otherChangeNotification.receivingHeiId)
        && outgoingMobilityId.equals(otherChangeNotification.outgoingMobilityId)
        && super.canBeMergedInto(o);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EwpOutgoingMobilityLearningAgreementChangeNotification)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpOutgoingMobilityLearningAgreementChangeNotification that = (EwpOutgoingMobilityLearningAgreementChangeNotification) o;
    return Objects.equals(getSendingHeiId(), that.getSendingHeiId())
        && Objects.equals(getReceivingHeiId(), that.getReceivingHeiId())
        && Objects.equals(getOutgoingMobilityId(), that.getOutgoingMobilityId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSendingHeiId(), getReceivingHeiId(),
        getOutgoingMobilityId());
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityLearningAgreementChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", receivingHeiId='" + receivingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
