package pt.ulisboa.ewp.node.client.ewp.http.interceptor;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.HttpCommunicationLogRepository;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;

@Component
@Transactional
public class EwpHttpClientLoggerInterceptor implements EwpHttpClientInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(EwpHttpClientLoggerInterceptor.class);

  private final EwpHttpCommunicationLogService ewpHttpCommunicationLogService;
  private final HttpCommunicationLogRepository httpCommunicationLogRepository;
  private final HostRepository hostRepository;

  private final WeakHashMap<EwpRequest, EwpCommunicationContext> requestToCommunicationContextMap = new WeakHashMap<>();

  public EwpHttpClientLoggerInterceptor(
      EwpHttpCommunicationLogService ewpHttpCommunicationLogService,
      HttpCommunicationLogRepository httpCommunicationLogRepository,
      HostRepository hostRepository) {
    this.ewpHttpCommunicationLogService = ewpHttpCommunicationLogService;
    this.httpCommunicationLogRepository = httpCommunicationLogRepository;
    this.hostRepository = hostRepository;
  }

  @Override
  public void onPreparing(EwpRequest request) {
    EwpCommunicationContext communicationContext = new EwpCommunicationContext(request,
        ZonedDateTime.now());
    this.requestToCommunicationContextMap.put(request, communicationContext);
  }

  @Override
  public void onSuccess(EwpRequest request, EwpSuccessOperationResult<?> successOperationResult) {
    if (!this.requestToCommunicationContextMap.containsKey(request)) {
      LOG.error("Missing communication context for request: " + request.getId());
      return;
    }
    EwpCommunicationContext communicationContext = this.requestToCommunicationContextMap.get(
        request);

    HttpCommunicationLog parentCommunication = this.httpCommunicationLogRepository.findById(
        request.getParentCommunicationId()).orElse(null);

    try {
      HttpCommunicationToEwpNodeLog communicationLog =
          ewpHttpCommunicationLogService.logCommunicationToEwpNode(
              successOperationResult,
              communicationContext.startProcessingDateTime,
              ZonedDateTime.now(),
              parentCommunication,
              request.getEwpChangeNotifications());

      Optional<Long> ewpNodeCommunicationId =
          successOperationResult.getResponse().getEwpNodeCommunicationId();
      if (ewpNodeCommunicationId.isPresent()) {
        associateCommunicationLogToParent(
            request, ewpNodeCommunicationId.get(), communicationLog.getId());
      }

    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void onError(EwpRequest request, EwpClientErrorException e) {
    if (!this.requestToCommunicationContextMap.containsKey(request)) {
      LOG.error("Missing communication context for request: " + request.getId());
      return;
    }
    EwpCommunicationContext communicationContext = this.requestToCommunicationContextMap.get(
        request);

    HttpCommunicationLog parentCommunication = this.httpCommunicationLogRepository.findById(
        request.getParentCommunicationId()).orElse(null);

    try {
      HttpCommunicationToEwpNodeLog communicationLog =
          this.ewpHttpCommunicationLogService.logCommunicationToEwpNode(
              e,
              communicationContext.startProcessingDateTime,
              ZonedDateTime.now(),
              parentCommunication,
              request.getEwpChangeNotifications());

      if (e.getResponse() != null) {
        Optional<Long> ewpNodeCommunicationId = e.getResponse().getEwpNodeCommunicationId();
        if (ewpNodeCommunicationId.isPresent()) {
          associateCommunicationLogToParent(
              request, ewpNodeCommunicationId.get(), communicationLog.getId());
        }
      }
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private void associateCommunicationLogToParent(
      EwpRequest request, Long communicationId, Long parentCommunicationId) {
    // NOTE: If the requested HEI ID is not locally covered then the request may have been sent to
    // some other EWP Node that returns also a header with the communication ID.
    if (hostRepository.findByCoveredHeiId(request.getEndpointInformation().getHeiId()).isEmpty()) {
      return;
    }
    ewpHttpCommunicationLogService.setParentCommunicationLogOf(
        communicationId, parentCommunicationId);
  }

  private static class EwpCommunicationContext {

    private final EwpRequest request;

    private final ZonedDateTime startProcessingDateTime;

    private EwpCommunicationContext(EwpRequest request, ZonedDateTime startProcessingDateTime) {
      this.request = request;
      this.startProcessingDateTime = startProcessingDateTime;
    }
  }
}
