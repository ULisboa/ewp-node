package pt.ulisboa.ewp.node.service.http.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.domain.entity.http.HttpHeader;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

@Service
@Transactional
public class HttpCommunicationLogService {

  public HttpRequestLog toHttpRequestLog(ContentCachingRequestWrapper request) {
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethod.fromString(request.getMethod()),
            request.getRequestURL().toString(),
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

  protected Collection<HttpHeader> toHttpHeaderCollection(ContentCachingRequestWrapper request) {
    Collection<HttpHeader> headers = new ArrayList<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      Enumeration<String> headerValues = request.getHeaders(headerName);
      while (headerValues.hasMoreElements()) {
        String headerValue = headerValues.nextElement();
        headers.add(HttpHeader.create(headerName, headerValue));
      }
    }
    return headers;
  }

  protected Collection<HttpHeader> toHttpHeaderCollection(ContentCachingResponseWrapper response) {
    Collection<HttpHeader> headers = new ArrayList<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(
        headerName ->
            response
                .getHeaders(headerName)
                .forEach(headerValue -> headers.add(HttpHeader.create(headerName, headerValue))));
    return headers;
  }

  protected Collection<HttpHeader> toHttpHeaderCollection(HttpHeaders headers) {
    Collection<HttpHeader> result = new ArrayList<>();
    headers.forEach(
        (headerName, headerValues) ->
            result.add(
                HttpHeader.create(
                    headerName,
                    String.join(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, headerValues))));
    return result;
  }
}
