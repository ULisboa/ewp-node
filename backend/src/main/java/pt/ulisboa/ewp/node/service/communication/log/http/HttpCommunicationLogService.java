package pt.ulisboa.ewp.node.service.communication.log.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpHeaderLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpMethodLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

@Service
@Transactional
public class HttpCommunicationLogService {

  public HttpRequestLog toHttpRequestLog(ContentCachingRequestWrapper request) {
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(request.getRequestURL().toString());
    if (!StringUtils.isEmpty(request.getQueryString())) {
      urlBuilder.append("?");
      urlBuilder.append(request.getQueryString());
    }

    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethodLog.fromString(request.getMethod()),
            urlBuilder.toString(),
            toHttpHeaderCollection(request),
            new String(request.getContentAsByteArray()));
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  public HttpResponseLog toHttpResponseLog(ContentCachingResponseWrapper response) {
    if (response == null) {
      return null;
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatus(),
            toHttpHeaderCollection(response),
            new String(response.getContentAsByteArray()));
    responseLog.getHeaders().forEach(header -> header.setResponseLog(responseLog));
    return responseLog;
  }

  protected Collection<HttpHeaderLog> toHttpHeaderCollection(ContentCachingRequestWrapper request) {
    Collection<HttpHeaderLog> headers = new ArrayList<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      Enumeration<String> headerValues = request.getHeaders(headerName);
      while (headerValues.hasMoreElements()) {
        String headerValue = headerValues.nextElement();
        headers.add(HttpHeaderLog.create(headerName, headerValue));
      }
    }
    return headers;
  }

  protected Collection<HttpHeaderLog> toHttpHeaderCollection(ContentCachingResponseWrapper response) {
    Collection<HttpHeaderLog> headers = new ArrayList<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(
        headerName ->
            response
                .getHeaders(headerName)
                .forEach(headerValue -> headers.add(HttpHeaderLog.create(headerName, headerValue))));
    return headers;
  }

  protected Collection<HttpHeaderLog> toHttpHeaderCollection(HttpHeaders headers) {
    Collection<HttpHeaderLog> result = new ArrayList<>();
    headers.forEach(
        (headerName, headerValues) ->
            result.add(
                    HttpHeaderLog.create(
                    headerName,
                    String.join(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, headerValues))));
    return result;
  }
}
