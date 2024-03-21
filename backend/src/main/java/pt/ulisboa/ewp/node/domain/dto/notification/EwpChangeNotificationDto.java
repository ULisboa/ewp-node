package pt.ulisboa.ewp.node.domain.dto.notification;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class EwpChangeNotificationDto {

  private long id;
  private ZonedDateTime creationDateTime;
  private int attemptNumber;
  private ZonedDateTime scheduledDateTime;
  private String status;

  // NOTE: used to store actual CNR variables (e.g. iia_id)
  private Map<String, String> extraVariables = new HashMap<>();

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

  public int getAttemptNumber() {
    return attemptNumber;
  }

  public void setAttemptNumber(int attemptNumber) {
    this.attemptNumber = attemptNumber;
  }

  public ZonedDateTime getScheduledDateTime() {
    return scheduledDateTime;
  }

  public void setScheduledDateTime(ZonedDateTime scheduledDateTime) {
    this.scheduledDateTime = scheduledDateTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Map<String, String> getExtraVariables() {
    return extraVariables;
  }

  public void setExtraVariables(Map<String, String> extraVariables) {
    this.extraVariables = extraVariables;
  }
}
