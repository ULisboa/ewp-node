package pt.ulisboa.ewp.node.service.communication.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.CommunicationLogRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;

@Service
@Transactional
public class CommunicationLogService {

  private static final Logger LOG = LoggerFactory.getLogger(CommunicationLogService.class);

  private final CommunicationLogRepository repository;

  public CommunicationLogService(CommunicationLogRepository repository) {
    this.repository = repository;
  }

  public void registerExceptionInCurrentCommunication(Exception exception) {
    if (CommunicationContextHolder.getContext().getCurrentCommunicationLog() != null) {
      this.registerException(CommunicationContextHolder.getContext().getCurrentCommunicationLog(), exception);
    } else {
      LOG.warn("No current communication found, ignoring exception...");
    }
  }

  public void registerException(CommunicationLog communicationLog, Exception exception) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    exception.printStackTrace(printWriter);

    communicationLog.setExceptionStacktrace(stringWriter.toString());
    repository.persist(communicationLog);
  }
}
