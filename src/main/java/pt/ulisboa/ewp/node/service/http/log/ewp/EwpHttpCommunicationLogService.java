package pt.ulisboa.ewp.node.service.http.log.ewp;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.AbstractEwpOperationResult;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.ewp.HttpCommunicationFromEwpNodeLogRepository;
import pt.ulisboa.ewp.node.domain.repository.http.log.ewp.HttpCommunicationToEwpNodeLogRepository;
import pt.ulisboa.ewp.node.service.http.log.HttpCommunicationLogService;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

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
      String observations) {
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
        observations);
  }

  public void logCommunicationToEwpNode(
      AbstractEwpOperationResult operationResult,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    HttpRequestLog requestLog = toHttpRequestLog(operationResult.getRequest());
    HttpResponseLog responseLog = toHttpResponseLog(operationResult.getResponse());
    httpCommunicationToEwpNodeLogRepository.create(
        operationResult.getRequest().getAuthenticationMethod(),
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations);
  }

  private HttpRequestLog toHttpRequestLog(EwpApiHttpRequestWrapper request) {
    StringBuilder url = new StringBuilder(request.getRequestURL().toString());
    if (request.getOriginalQueryString() != null) {
      url.append('?').append(request.getOriginalQueryString());
    }
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethod.fromString(request.getMethod()),
            url.toString(),
            toHttpHeaderCollection(request),
            request.getBody());
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  private HttpRequestLog toHttpRequestLog(EwpRequest request) {
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethod.fromString(request.getMethod().name()),
            request.getUrl(),
            toHttpHeaderCollection(request.getHeaders()),
            HttpUtils.serializeFormData(request.getBodyParams().asMap()));
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
}
