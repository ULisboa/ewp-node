package pt.ulisboa.ewp.node.domain.dto.notification;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogSummaryDto;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.Status;

public class EwpChangeNotificationDto {

  private long id;
  private ZonedDateTime creationDateTime;
  private List<CommunicationLogSummaryDto> sortedCommunicationLogs;
  private int attemptNumber;
  private ZonedDateTime nextAttemptDateTime;
  private Status status;
  private EwpChangeNotificationDto mergedInto;

  // NOTE: used to store actual CNR variables (e.g. iia_id)
  private List<ExtraVariableEntryDto> extraVariables = new ArrayList<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ZonedDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(ZonedDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public List<CommunicationLogSummaryDto> getSortedCommunicationLogs() {
    return sortedCommunicationLogs;
  }

  public void setSortedCommunicationLogs(List<CommunicationLogSummaryDto> sortedCommunicationLogs) {
    this.sortedCommunicationLogs = sortedCommunicationLogs;
  }

  public int getAttemptNumber() {
    return attemptNumber;
  }

  public void setAttemptNumber(int attemptNumber) {
    this.attemptNumber = attemptNumber;
  }

  public ZonedDateTime getNextAttemptDateTime() {
    return nextAttemptDateTime;
  }

  public void setNextAttemptDateTime(ZonedDateTime nextAttemptDateTime) {
    this.nextAttemptDateTime = nextAttemptDateTime;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  /**
   * If the change notification was merged into some other change notification, then this provides
   * that change notification.
   *
   * @return The change notification into which the current change notification was merged into.
   */
  public EwpChangeNotificationDto getMergedInto() {
    return mergedInto;
  }

  public void setMergedInto(EwpChangeNotificationDto mergedInto) {
    this.mergedInto = mergedInto;
  }

  public List<ExtraVariableEntryDto> getExtraVariables() {
    return extraVariables;
  }

  public void setExtraVariables(List<ExtraVariableEntryDto> extraVariables) {
    this.extraVariables = extraVariables;
  }

  public static class ExtraVariableEntryDto {

    private String key;
    private String value;

    public ExtraVariableEntryDto() {}

    public ExtraVariableEntryDto(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
