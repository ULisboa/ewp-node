package pt.ulisboa.ewp.node.service.http.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.domain.entity.http.HttpHeader;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.utils.http.HttpHeadersMap;

@Service
@Transactional
public class HttpCommunicationLogService {

  protected HttpRequestLog toHttpRequestLog(ContentCachingRequestWrapper request) {
    HttpRequestLog requestLog =
        HttpRequestLog.create(
            HttpMethod.fromString(request.getMethod()),
            request.getRequestURL().toString(),
            getHttpHeadersCollection(request),
            new String(request.getContentAsByteArray()));
    requestLog.getHeaders().forEach(header -> header.setRequestLog(requestLog));
    return requestLog;
  }

  protected HttpResponseLog toHttpResponseLog(ContentCachingResponseWrapper response) {
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

  protected Collection<HttpHeader> getHttpHeadersCollection(ContentCachingRequestWrapper request) {
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

  protected Collection<HttpHeader> getHttpHeadersCollection(
      ContentCachingResponseWrapper response) {
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

  protected Collection<HttpHeader> getHttpHeadersCollection(HttpHeadersMap headersMap) {
    Collection<HttpHeader> headers = new ArrayList<>();
    headersMap.forEach(
        (headerName, headerValue) -> {
          headers.add(HttpHeader.create(headerName, headerValue));
        });
    return headers;
  }

}
