package pt.ulisboa.ewp.node.service.http.log.ewp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.AbstractEwpOperationResult;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpHeader;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.ewp.HttpCommunicationFromEwpNodeLogRepository;
import pt.ulisboa.ewp.node.domain.repository.http.log.ewp.HttpCommunicationToEwpNodeLogRepository;
import pt.ulisboa.ewp.node.utils.http.HttpHeadersMap;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

@Service
@Transactional
public class EwpHttpCommunicationLogService {

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
            getHttpHeadersCollection(request),
            request.getBody());
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  private HttpRequestLog toHttpRequestLog(EwpRequest request) {
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethod.fromString(request.getMethod().name()),
            request.getUrl(),
            getHttpHeadersCollection(request.getHeaders()),
            HttpUtils.serializeFormData(request.getBodyParams()));
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  private HttpResponseLog toHttpResponseLog(ContentCachingResponseWrapper response) {
    if (response == null) {
      return null;
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatusCode(),
            getHttpHeadersCollection(response),
            new String(response.getContentAsByteArray()));
    responseLog.getHeaders().forEach(header -> header.setResponseLog(responseLog));
    return responseLog;
  }

  private HttpResponseLog toHttpResponseLog(EwpResponse response) {
    if (response == null) {
      return null;
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatusCode(),
            getHttpHeadersCollection(response.getHeaders()),
            response.getRawBody());
    responseLog.getHeaders().forEach(header -> header.setResponseLog(responseLog));
    return responseLog;
  }

  private Collection<HttpHeader> getHttpHeadersCollection(EwpApiHttpRequestWrapper request) {
    Collection<HttpHeader> headers = new ArrayList<>();
    Enumeration<String> headerNamesEnumeration = request.getHeaderNames();
    while (headerNamesEnumeration.hasMoreElements()) {
      String headerName = headerNamesEnumeration.nextElement();
      Enumeration<String> headerValuesEnumeration = request.getHeaders(headerName);
      while (headerValuesEnumeration.hasMoreElements()) {
        String headerValue = headerValuesEnumeration.nextElement();
        headers.add(HttpHeader.create(headerName, headerValue));
      }
    }
    return headers;
  }

  private Collection<HttpHeader> getHttpHeadersCollection(ContentCachingResponseWrapper response) {
    Collection<HttpHeader> headers = new ArrayList<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(
        headerName -> {
          response
              .getHeaders(headerName)
              .forEach(
                  headerValue -> {
                    headers.add(HttpHeader.create(headerName, headerValue));
                  });
        });
    return headers;
  }

  private Collection<HttpHeader> getHttpHeadersCollection(HttpHeadersMap headersMap) {
    Collection<HttpHeader> headers = new ArrayList<>();
    headersMap.forEach(
        (headerName, headerValue) -> {
          headers.add(HttpHeader.create(headerName, headerValue));
        });
    return headers;
  }
}
