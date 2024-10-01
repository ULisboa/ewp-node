package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {
      "id",
      "creationDateTime",
      "attemptNumber",
      "nextAttemptDateTime",
      "status",
      "mergedInto",
      "extraVariables"
    })
@XmlRootElement(name = "cnr-status")
public class ForwardEwpApiCnrStatusResponseDTO {

  @XmlElement(name = "id", required = true)
  private long id;

  @XmlElement(name = "creation-date-time", required = true)
  private XMLGregorianCalendar creationDateTime;

  @XmlElement(name = "attempt-number", required = true)
  private int attemptNumber;

  @XmlElement(name = "next-attempt-date-time")
  private XMLGregorianCalendar nextAttemptDateTime;

  @XmlElement(name = "status", required = true)
  private StatusDto status;

  @XmlElement(name = "merged-into", required = true)
  private ForwardEwpApiCnrStatusResponseDTO mergedInto;

  @XmlElement(name = "extra-variables", required = true)
  private List<ExtraVariableEntryDto> extraVariables = new ArrayList<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public XMLGregorianCalendar getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(XMLGregorianCalendar creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public int getAttemptNumber() {
    return attemptNumber;
  }

  public void setAttemptNumber(int attemptNumber) {
    this.attemptNumber = attemptNumber;
  }

  public XMLGregorianCalendar getNextAttemptDateTime() {
    return nextAttemptDateTime;
  }

  public void setNextAttemptDateTime(XMLGregorianCalendar nextAttemptDateTime) {
    this.nextAttemptDateTime = nextAttemptDateTime;
  }

  /**
   * The change notification is pending delivery. There might have been already some delivery
   * attempt, but neither it was success nor fatal error (e.g. target institution does not implement
   * the CNR API).
   *
   * @return Whether change notification is pending delivery.
   */
  public boolean isPending() {
    return getStatus() == StatusDto.PENDING;
  }

  /**
   * The change notification was successfully delivered.
   *
   * @return Whether change notification was successfully delivered.
   */
  public boolean wasSuccess() {
    return getStatus() == StatusDto.SUCCESS;
  }

  /**
   * The change notification was merged into some other change notification. When a change
   * notification is scheduled and is the same as an already existing pending change notification
   * then that original change notification is merged into the new change notification.
   *
   * @return Whether change notification was merged into some other change notification.
   */
  public boolean wasMerged() {
    return getStatus() == StatusDto.MERGED;
  }

  /**
   * The change notification has failed due to reaching the maximum number of attempts.
   *
   * @return Whether change notification has failed due to reaching the maximum number of attempts.
   */
  public boolean hasFailedDueToMaxAttempts() {
    return getStatus() == StatusDto.FAILED_MAX_ATTEMPTS;
  }

  /**
   * The change notification has failed due to the target institution not implementing the CNR API.
   *
   * @return Whether change notification has failed due to the target institution not implementing
   *     the CNR API.
   */
  public boolean hasFailedDueToNoCnrApiAvailable() {
    return getStatus() == StatusDto.FAILED_NO_CNR_API_AVAILABLE;
  }

  public StatusDto getStatus() {
    return status;
  }

  public void setStatus(StatusDto status) {
    this.status = status;
  }

  /**
   * If the change notification was merged into some other change notification, then this provides
   * that change notification.
   *
   * @return The change notification into which the current change notification was merged into.
   */
  public ForwardEwpApiCnrStatusResponseDTO getMergedInto() {
    return mergedInto;
  }

  public void setMergedInto(ForwardEwpApiCnrStatusResponseDTO mergedInto) {
    this.mergedInto = mergedInto;
  }

  /**
   * Returns the list of variables of the CNR itself. For example, it may include variables like
   * iia_id or omobility_id.
   *
   * @return The list of variables of the CNR itself.
   */
  public List<ExtraVariableEntryDto> getExtraVariables() {
    return extraVariables;
  }

  public void setExtraVariables(List<ExtraVariableEntryDto> extraVariables) {
    this.extraVariables = extraVariables;
  }

  public enum StatusDto {
    PENDING,
    SUCCESS,
    FAILED_MAX_ATTEMPTS,
    FAILED_NO_CNR_API_AVAILABLE,
    MERGED;
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
