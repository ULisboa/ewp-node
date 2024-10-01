package pt.ulisboa.ewp.node.service.communication.log.http.host;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.http.host.HttpCommunicationFromHostLogRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.log.http.HttpCommunicationLogService;

@Service
@Transactional
public class HostHttpCommunicationLogService extends HttpCommunicationLogService {

  @Autowired
  private HttpCommunicationFromHostLogRepository httpCommunicationFromHostLogRepository;

  public HttpCommunicationFromHostLog logCommunicationFromHost(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      ForwardEwpApiEndpoint forwardEwpApiEndpoint,
      String targetHeiId,
      ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws DomainException, IOException {
    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);

    return httpCommunicationFromHostLogRepository.create(
        host,
        hostForwardEwpApiClient,
        forwardEwpApiEndpoint != null ? forwardEwpApiEndpoint.api() : null,
        forwardEwpApiEndpoint != null && forwardEwpApiEndpoint.apiMajorVersion() != -1
            ? forwardEwpApiEndpoint.apiMajorVersion()
            : null,
        forwardEwpApiEndpoint != null ? forwardEwpApiEndpoint.endpoint() : null,
        targetHeiId,
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
  }

  public boolean persist(HttpCommunicationFromHostLog httpCommunicationFromHostLog) {
    return httpCommunicationFromHostLogRepository.persist(httpCommunicationFromHostLog);
  }
}
