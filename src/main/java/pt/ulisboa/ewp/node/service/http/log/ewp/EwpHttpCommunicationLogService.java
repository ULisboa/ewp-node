package pt.ulisboa.ewp.node.service.http.log.ewp;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpMethodLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp.HttpCommunicationFromEwpNodeLogRepository;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp.HttpCommunicationToEwpNodeLogRepository;
import pt.ulisboa.ewp.node.service.http.log.HttpCommunicationLogService;

@Service
@Transactional
public class EwpHttpCommunicationLogService extends HttpCommunicationLogService {

  @Autowired
  private HttpCommunicationFromEwpNodeLogRepository httpCommunicationFromEwpNodeLogRepository;

  @Autowired
  private HttpCommunicationToEwpNodeLogRepository httpCommunicationToEwpNodeLogRepository;

  public void logCommunicationFromEwpNode(
      EwpApiHttpRequestWrapper request,
      ContentCachingResponseWrapper response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException {

    // NOTE: Requests for manifest are not logged to avoid using too much log space
    if (isRequestForManifest(request) && response.getStatus() == HttpStatus.OK.value()) {
      return;
    }

    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);

    EwpAuthenticationMethod authenticationMethod =
        request.getAuthenticationToken() != null
            ? request.getAuthenticationToken().getAuthenticationMethod()
            : EwpAuthenticationMethod.ANONYMOUS;
    Collection<String> heiIdsCoveredByClient =
        request.getAuthenticationToken() != null
            ? request.getAuthenticationToken().getPrincipal().getHeiIdsCoveredByClient()
            : Collections.emptyList();
    httpCommunicationFromEwpNodeLogRepository.create(
        authenticationMethod,
        heiIdsCoveredByClient,
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
  }

  public <T extends Serializable> void logCommunicationToEwpNode(
      EwpSuccessOperationResult<T> successOperationResult,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    logCommunicationToEwpNode(
        successOperationResult.getRequest(),
        successOperationResult.getResponse(),
        startProcessingDateTime,
        endProcessingDateTime,
        "",
        parentCommunication);
  }

  public void logCommunicationToEwpNode(
      EwpClientErrorException clientErrorException,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    logCommunicationToEwpNode(
        clientErrorException.getRequest(),
        clientErrorException.getResponse(),
        startProcessingDateTime,
        endProcessingDateTime,
        clientErrorException.getDetailedMessage(),
        parentCommunication);
  }

  public void logCommunicationToEwpNode(
      EwpRequest request,
      EwpResponse response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);
    httpCommunicationToEwpNodeLogRepository.create(
        request.getAuthenticationMethod(),
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
  }

  private HttpRequestLog toHttpRequestLog(EwpApiHttpRequestWrapper request) {
    StringBuilder url = new StringBuilder(request.getRequestURL().toString());
    if (request.getOriginalQueryString() != null) {
      url.append('?').append(request.getOriginalQueryString());
    }
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethodLog.fromString(request.getMethod()),
            url.toString(),
            toHttpHeaderCollection(request),
            request.getBody());
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  private HttpRequestLog toHttpRequestLog(EwpRequest request) {
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethodLog.fromString(request.getMethod().name()),
            request.getUrl(),
            toHttpHeaderCollection(request.getHeaders()),
            request.getBody().serialize());
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  private HttpResponseLog toHttpResponseLog(EwpResponse response) {
    if (response == null) {
      return null;
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatus().value(),
            toHttpHeaderCollection(response.getHeaders()),
            response.getRawBody());
    responseLog.getHeaders().forEach(header -> header.setResponseLog(responseLog));
    return responseLog;
  }

  private boolean isRequestForManifest(EwpApiHttpRequestWrapper request) {
    return request.getRequestURI().endsWith("/manifest");
  }
}
