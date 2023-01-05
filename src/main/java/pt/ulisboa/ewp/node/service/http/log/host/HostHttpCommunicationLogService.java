package pt.ulisboa.ewp.node.service.http.log.host;

import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.host.HttpCommunicationFromHostLogRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.http.log.HttpCommunicationLogService;

@Service
@Transactional
public class HostHttpCommunicationLogService extends HttpCommunicationLogService {

  @Autowired
  private HttpCommunicationFromHostLogRepository httpCommunicationFromHostLogRepository;

  public HttpCommunicationFromHostLog logCommunicationFromHost(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws DomainException {
    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);

    return httpCommunicationFromHostLogRepository.create(
        host,
        hostForwardEwpApiClient,
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations, parentCommunication);
  }

  public boolean persist(HttpCommunicationFromHostLog httpCommunicationFromHostLog) {
    return httpCommunicationFromHostLogRepository.persist(httpCommunicationFromHostLog);
  }
}
