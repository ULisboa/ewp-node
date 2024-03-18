package pt.ulisboa.ewp.node.service.communication.log.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    byte[] responseBodyAsBytes;
    if (isContentTypeOfBodyWhiteListedToLog(response.getContentType())) {
      responseBodyAsBytes = response.getContentAsByteArray();
    } else {
      responseBodyAsBytes =
          ("Bodies of content type '"
                  + response.getContentType()
                  + "' are not admissible to be logged")
              .getBytes(StandardCharsets.UTF_8);
    }

    HttpResponseLog responseLog =
        HttpResponseLog.create(
            response.getStatus(), toHttpHeaderCollection(response), responseBodyAsBytes);
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

  protected boolean isContentTypeOfBodyWhiteListedToLog(String contentType) {
    List<String> whitelistedContentTypes =
        List.of(
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    return contentType != null
        && whitelistedContentTypes.stream()
            .anyMatch(wct -> contentType.toLowerCase().contains(wct.toLowerCase()));
  }
}
