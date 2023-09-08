package pt.ulisboa.ewp.node.service.ewp.monitoring;

import java.util.Optional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.monitoring.EwpMonitoringV1Client;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;

@Service
public class EwpMonitoringService {

  private final EwpMonitoringV1Client monitoringClient;
  private final EwpHttpCommunicationLogService communicationLogService;

  public EwpMonitoringService(
      EwpMonitoringV1Client monitoringClient,
      EwpHttpCommunicationLogService communicationLogService) {
    this.monitoringClient = monitoringClient;
    this.communicationLogService = communicationLogService;
  }

  public void reportCommunicationErrorToMonitoring(long communicationId, String clientMessage) {
    Optional<HttpCommunicationToEwpNodeLog> communicationLogOptional =
        this.communicationLogService.findCommunicationToEwpNodeById(communicationId);
    if (communicationLogOptional.isEmpty()) {
      throw new IllegalArgumentException(
          "There is no valid communication log with ID: " + communicationId);
    }
    HttpCommunicationToEwpNodeLog communicationLog = communicationLogOptional.get();

    if (communicationLog.isReportedToMonitoring()) {
        throw new IllegalArgumentException("Communication was already previously reported to monitoring");
    }

    String serverMessage = null;
    if (400 <= communicationLog.getResponse().getStatusCode()
        && communicationLog.getResponse().getStatusCode() < 600) {
      serverMessage = communicationLog.getServerDeveloperMessage();
    }

    try {
      this.monitoringClient.reportIssue(
          communicationLog.getTargetHeiId(),
          communicationLog.getApiName(),
          communicationLog.getEndpointName(),
          communicationLog.getResponse().getStatusCode(),
          serverMessage,
          clientMessage);
    } catch (EwpClientErrorException e) {
      throw new IllegalStateException("Failed to send report to monitoring", e);
    }

    communicationLogService.markCommunicationToEwpNodeAsReportedToMonitoring(communicationLog);
  }
}
