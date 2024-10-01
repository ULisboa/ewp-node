package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"changeNotificationIds"})
@XmlRootElement(name = "cnr-submission")
public class ForwardEwpApiCnrSubmissionResponseDTO {

  @XmlElement(name = "change-notification-id", required = true)
  private Collection<Long> changeNotificationIds;

  public ForwardEwpApiCnrSubmissionResponseDTO() {}

  public ForwardEwpApiCnrSubmissionResponseDTO(Collection<Long> changeNotificationIds) {
    this.changeNotificationIds = changeNotificationIds;
  }

  /**
   * IDs of the submitted change notifications, per order of submission.
   *
   * @return IDs of the submitted change notifications.
   */
  public Collection<Long> getChangeNotificationIds() {
    return changeNotificationIds;
  }

  public void setChangeNotificationIds(Collection<Long> changeNotificationIds) {
    this.changeNotificationIds = changeNotificationIds;
  }
}
