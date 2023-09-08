package pt.ulisboa.ewp.node.domain.utils.communication.log;

public enum CommunicationLogWarningCode {
  ERROR_NOT_REPORTED_TO_MONITORING;

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
