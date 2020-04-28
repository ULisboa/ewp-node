package pt.ulisboa.ewp.node.service.http.log.host;

import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.host.HttpCommunicationFromHostLogRepository;
import pt.ulisboa.ewp.node.service.http.log.HttpCommunicationLogService;

@Service
@Transactional
public class HostHttpCommunicationLogService extends HttpCommunicationLogService {

  @Autowired
  private HttpCommunicationFromHostLogRepository httpCommunicationFromHostLogRepository;

  public void logCommunicationFromHost(
      Host host,
      ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    HttpRequestLog requestLog = toHttpRequestLog(request);
    HttpResponseLog responseLog = toHttpResponseLog(response);

    httpCommunicationFromHostLogRepository.create(
        host,
        requestLog,
        responseLog,
        startProcessingDateTime,
        endProcessingDateTime,
        observations);
  }
}
