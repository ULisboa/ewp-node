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
@DiscriminatorValue("OMOBILITY")
public class EwpOutgoingMobilityChangeNotification extends EwpChangeNotification {

  private String sendingHeiId;
  private String receivingHeiId;
  private String outgoingMobilityId;

  public EwpOutgoingMobilityChangeNotification() {
  }

  public EwpOutgoingMobilityChangeNotification(
      CommunicationLog originCommunicationLog,
      String sendingHeiId,
      String receivingHeiId,
      String outgoingMobilityId) {
    super(originCommunicationLog);
    this.sendingHeiId = sendingHeiId;
    this.receivingHeiId = receivingHeiId;
    this.outgoingMobilityId = outgoingMobilityId;
  }

  public EwpOutgoingMobilityChangeNotification(
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
    if (!(o instanceof EwpOutgoingMobilityChangeNotification)) {
      return false;
    }

    EwpOutgoingMobilityChangeNotification otherChangeNotification = (EwpOutgoingMobilityChangeNotification) o;
    return sendingHeiId.equals(otherChangeNotification.sendingHeiId)
        && receivingHeiId.equals(otherChangeNotification.receivingHeiId)
        && outgoingMobilityId.equals(otherChangeNotification.outgoingMobilityId)
        && super.canBeMergedInto(o);
  }

  @Override
  @Transient
  public List<ExtraVariableEntry> getExtraVariables() {
    List<ExtraVariableEntry> result = new ArrayList<>();
    result.add(new ExtraVariableEntry("sending_hei_id", getSendingHeiId()));
    result.add(new ExtraVariableEntry("receiving_hei_id", getReceivingHeiId()));
    result.add(new ExtraVariableEntry("omobility_id", getOutgoingMobilityId()));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EwpOutgoingMobilityChangeNotification)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpOutgoingMobilityChangeNotification that = (EwpOutgoingMobilityChangeNotification) o;
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
    return "EwpOutgoingMobilityChangeNotification{" +
        "super='" + super.toString() + '\'' +
        ", sendingHeiId='" + sendingHeiId + '\'' +
        ", receivingHeiId='" + receivingHeiId + '\'' +
        ", outgoingMobilityId='" + outgoingMobilityId + '\'' +
        '}';
  }
}
