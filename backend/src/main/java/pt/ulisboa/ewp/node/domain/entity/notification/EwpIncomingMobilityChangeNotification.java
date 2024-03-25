package pt.ulisboa.ewp.node.domain.entity.notification;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@DiscriminatorValue("IMOBILITY")
public class EwpIncomingMobilityChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String receivingHeiId;
  private String outgoingMobilityId;

  public EwpIncomingMobilityChangeNotification() {
  }

  public EwpIncomingMobilityChangeNotification(
      CommunicationLog originCommunicationLog,
      String sendingHeiId,
      String receivingHeiId,
      String outgoingMobilityId) {
    super(originCommunicationLog);
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpIncomingMobilityChangeNotification(
      CommunicationLog originCommunicationLog,
      int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status,
      String sendingHeiId,
      String receivingHeiId,
      String outgoingMobilityId) {
    super(originCommunicationLog, attemptNumber, scheduledDateTime, status);
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
    if (!(o instanceof EwpIncomingMobilityChangeNotification)) {
      return false;
    }

    EwpIncomingMobilityChangeNotification otherChangeNotification = (EwpIncomingMobilityChangeNotification) o;
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId)
        && receivingHeiId.equals(otherChangeNotification.receivingHeiId)
        && outgoingMobilityId.equals(otherChangeNotification.outgoingMobilityId)
        && super.canBeMergedInto(o);
  }

  @Override
  @Transient
  public List<ExtraVariableEntry> getExtraVariables() {
    List<ExtraVariableEntry> result = new ArrayList<>();
    result.add(new ExtraVariableEntry("sendingHeiId", getSendingHeiId()));
    result.add(new ExtraVariableEntry("receivingHeiId", getReceivingHeiId()));
    result.add(new ExtraVariableEntry("outgoingMobilityId", getOutgoingMobilityId()));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EwpIncomingMobilityChangeNotification)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpIncomingMobilityChangeNotification that = (EwpIncomingMobilityChangeNotification) o;
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
    return "EwpIncomingMobilityChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", receivingHeiId='" + receivingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
