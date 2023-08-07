package pt.ulisboa.ewp.node.service.communication.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
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

  public static <T extends CommunicationLog> void registerException(
      AbstractRepository<T> repository, T communicationLog, Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);

    communicationLog.setExceptionStacktrace(stringWriter.toString());
    repository.persist(communicationLog);
  }

  public void registerExceptionInCurrentCommunication(Throwable throwable) {
    if (CommunicationContextHolder.getContext().getCurrentCommunicationLog() != null) {
      this.registerException(
          CommunicationContextHolder.getContext().getCurrentCommunicationLog(), throwable);
    } else {
      LOG.warn("No current communication found, ignoring exception...");
    }
  }

  public void registerException(CommunicationLog communicationLog, Throwable throwable) {
    registerException(repository, communicationLog, throwable);
  }
}
