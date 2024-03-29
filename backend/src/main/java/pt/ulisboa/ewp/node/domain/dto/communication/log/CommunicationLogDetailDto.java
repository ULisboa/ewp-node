package pt.ulisboa.ewp.node.domain.dto.communication.log;

import java.util.List;

public class CommunicationLogDetailDto extends CommunicationLogSummaryDto {

  private String exceptionStacktrace;

  private String observations;

  private List<CommunicationLogSummaryDto> sortedChildrenCommunications;
  private List<Long> ewpChangeNotificationIdsAsOrigin;

  public String getExceptionStacktrace() {
    return exceptionStacktrace;
  }

  public void setExceptionStacktrace(String exceptionStacktrace) {
    this.exceptionStacktrace = exceptionStacktrace;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public List<CommunicationLogSummaryDto> getSortedChildrenCommunications() {
    return sortedChildrenCommunications;
  }

  public void setSortedChildrenCommunications(
      List<CommunicationLogSummaryDto> sortedChildrenCommunications) {
    this.sortedChildrenCommunications = sortedChildrenCommunications;
  }

  public List<Long> getEwpChangeNotificationIdsAsOrigin() {
    return ewpChangeNotificationIdsAsOrigin;
  }

  public void setEwpChangeNotificationIdsAsOrigin(List<Long> ewpChangeNotificationIdsAsOrigin) {
    this.ewpChangeNotificationIdsAsOrigin = ewpChangeNotificationIdsAsOrigin;
  }
}
