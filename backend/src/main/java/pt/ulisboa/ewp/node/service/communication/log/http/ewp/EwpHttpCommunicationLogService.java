package pt.ulisboa.ewp.node.service.communication.log.http.ewp;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
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
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp.HttpCommunicationFromEwpNodeLogRepository;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp.HttpCommunicationToEwpNodeLogRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.log.http.HttpCommunicationLogService;

@Service
@Transactional
public class EwpHttpCommunicationLogService extends HttpCommunicationLogService {

  @Autowired
  private HttpCommunicationFromEwpNodeLogRepository httpCommunicationFromEwpNodeLogRepository;

  @Autowired
  private HttpCommunicationToEwpNodeLogRepository httpCommunicationToEwpNodeLogRepository;

  public Optional<HttpCommunicationToEwpNodeLog> findCommunicationToEwpNodeById(long id) {
    return httpCommunicationToEwpNodeLogRepository.findById(id);
  }

  public HttpCommunicationFromEwpNodeLog logCommunicationFromEwpNodeBeforeExecution(
      EwpApiHttpRequestWrapper request,
      ZonedDateTime startProcessingDateTime,
      EwpApiEndpoint ewpApiEndpoint,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException, DomainException {

    HttpRequestLog requestLog = toHttpRequestLog(request);

    EwpAuthenticationMethod authenticationMethod =
        request.getAuthenticationToken() != null
            ? request.getAuthenticationToken().getAuthenticationMethod()
            : EwpAuthenticationMethod.ANONYMOUS;
    Collection<String> heiIdsCoveredByClient =
        request.getAuthenticationToken() != null
            ? request.getAuthenticationToken().getPrincipal().getHeiIdsCoveredByClient()
            : Collections.emptyList();
    return httpCommunicationFromEwpNodeLogRepository.create(
        authenticationMethod,
        heiIdsCoveredByClient,
        ewpApiEndpoint != null ? ewpApiEndpoint.api() : null,
        ewpApiEndpoint != null ? ewpApiEndpoint.apiMajorVersion() : null,
        ewpApiEndpoint != null ? ewpApiEndpoint.endpoint() : null,
        requestLog,
        null,
        startProcessingDateTime,
        null,
        observations,
        parentCommunication);
  }

  public boolean updateCommunicationFromEwpNodeAuthenticationData(HttpCommunicationFromEwpNodeLog communicationLog,
                                                                      EwpApiHostAuthenticationToken authenticationToken) {
    if (authenticationToken != null) {
      communicationLog.updateAuthenticationData(authenticationToken);
      return httpCommunicationFromEwpNodeLogRepository.persist(communicationLog);
    }
    return true;
  }

  public boolean updateCommunicationFromEwpNodeAfterExecution(
          HttpCommunicationFromEwpNodeLog communicationLog,
          ContentCachingResponseWrapper response,
          ZonedDateTime endProcessingDateTime,
          String observations)
          throws IOException {

    // NOTE: Requests for manifest are not logged to avoid using too much log space
    // Therefore, maintain only failure communications, deleting the success ones
    if (isRequestForManifest(communicationLog.getRequest()) && response.getStatus() == HttpStatus.OK.value()) {
      httpCommunicationFromEwpNodeLogRepository.delete(communicationLog);
      return true;
    }

    communicationLog.setResponse(toHttpResponseLog(response));
    communicationLog.setEndProcessingDateTime(endProcessingDateTime);
    communicationLog.setObservations(observations);
    return httpCommunicationFromEwpNodeLogRepository.persist(communicationLog);
  }

  public boolean markCommunicationToEwpNodeAsReportedToMonitoring(HttpCommunicationToEwpNodeLog communicationLog) {
    communicationLog.setReportedToMonitoring(true);
    return httpCommunicationToEwpNodeLogRepository.persist(communicationLog);
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
        null,
        "",
        parentCommunication);
  }

  public void logCommunicationToEwpNode(
      EwpClientErrorException clientErrorException,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    String serverDeveloperMessage = null;
    if (clientErrorException.getResponse() != null) {
      serverDeveloperMessage = clientErrorException.getResponse().getServerDeveloperMessage();
    }
    logCommunicationToEwpNode(
        clientErrorException.getRequest(),
        clientErrorException.getResponse(),
        startProcessingDateTime,
        endProcessingDateTime,
        serverDeveloperMessage,
        clientErrorException.getDetailedMessage(),
        parentCommunication);
  }

  public void logCommunicationToEwpNode(
      EwpRequest request,
      EwpResponse response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String serverDeveloperMessage,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);
    httpCommunicationToEwpNodeLogRepository.create(
        request.getEndpointInformation().getHeiId(),
        request.getEndpointInformation().getApiName(),
        request.getEndpointInformation().getApiVersion(),
        request.getEndpointInformation().getEndpointName(),
        request.getAuthenticationMethod(),
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        serverDeveloperMessage,
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

    byte[] responseBodyAsBytes;
    if (isContentTypeOfBodyWhiteListedToLog(response.getMediaType())) {
      responseBodyAsBytes = response.getRawBody();
    } else {
      responseBodyAsBytes =
          ("Bodies of content type '"
                  + response.getMediaType()
                  + "' are not admissible to be logged")
              .getBytes(StandardCharsets.UTF_8);
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatus().value(),
            toHttpHeaderCollection(response.getHeaders()),
            responseBodyAsBytes);
    responseLog.getHeaders().forEach(header -> header.setResponseLog(responseLog));
    return responseLog;
  }

  private boolean isRequestForManifest(HttpRequestLog request) {
    return request.getUrl().endsWith("/manifest");
  }
}
