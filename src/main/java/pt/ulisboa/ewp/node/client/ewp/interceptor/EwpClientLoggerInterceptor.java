package pt.ulisboa.ewp.node.client.ewp.interceptor;

import java.time.ZonedDateTime;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;

@Component
public class EwpClientLoggerInterceptor implements EwpClientInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(EwpClientLoggerInterceptor.class);

  private final EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  private final WeakHashMap<EwpRequest, EwpCommunicationContext> requestToCommunicationContextMap = new WeakHashMap<>();

  public EwpClientLoggerInterceptor(EwpHttpCommunicationLogService ewpHttpCommunicationLogService) {
    this.ewpHttpCommunicationLogService = ewpHttpCommunicationLogService;
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
    ewpHttpCommunicationLogService.logCommunicationToEwpNode(successOperationResult,
        communicationContext.startProcessingDateTime, ZonedDateTime.now());
  }

  @Override
  public void onError(EwpRequest request, EwpClientErrorException e) {
    if (!this.requestToCommunicationContextMap.containsKey(request)) {
      LOG.error("Missing communication context for request: " + request.getId());
      return;
    }
    EwpCommunicationContext communicationContext = this.requestToCommunicationContextMap.get(
        request);
    this.ewpHttpCommunicationLogService.logCommunicationToEwpNode(e,
        communicationContext.startProcessingDateTime,
        ZonedDateTime.now());
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
