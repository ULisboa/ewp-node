package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("OMOBILITY")
public class EwpOutgoingMobilityChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String outgoingMobilityId;

  public EwpOutgoingMobilityChangeNotification() {
  }

  public EwpOutgoingMobilityChangeNotification(String sendingHeiId,
      String outgoingMobilityId) {
    super();
    this.sendingHeiId = sendingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpOutgoingMobilityChangeNotification(int attemptNumber,
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
    if (!(o instanceof EwpOutgoingMobilityChangeNotification)) {
      return false;
    }

    EwpOutgoingMobilityChangeNotification otherChangeNotification = (EwpOutgoingMobilityChangeNotification) o;
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId) && outgoingMobilityId.equals(
        otherChangeNotification.outgoingMobilityId);
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
