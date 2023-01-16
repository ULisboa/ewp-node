package pt.ulisboa.ewp.node.client.ewp.http.interceptor;

import java.time.ZonedDateTime;
import java.util.WeakHashMap;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.HttpCommunicationLogRepository;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;

@Component
@Transactional
public class EwpHttpClientLoggerInterceptor implements EwpHttpClientInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(EwpHttpClientLoggerInterceptor.class);

  private final EwpHttpCommunicationLogService ewpHttpCommunicationLogService;
  private final HttpCommunicationLogRepository httpCommunicationLogRepository;

  private final WeakHashMap<EwpRequest, EwpCommunicationContext> requestToCommunicationContextMap = new WeakHashMap<>();

  public EwpHttpClientLoggerInterceptor(
      EwpHttpCommunicationLogService ewpHttpCommunicationLogService,
      HttpCommunicationLogRepository httpCommunicationLogRepository) {
    this.ewpHttpCommunicationLogService = ewpHttpCommunicationLogService;
    this.httpCommunicationLogRepository = httpCommunicationLogRepository;
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

    ewpHttpCommunicationLogService.logCommunicationToEwpNode(successOperationResult,
        communicationContext.startProcessingDateTime, ZonedDateTime.now(), parentCommunication);
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

    this.ewpHttpCommunicationLogService.logCommunicationToEwpNode(e,
        communicationContext.startProcessingDateTime,
        ZonedDateTime.now(), parentCommunication);
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
