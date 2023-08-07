package pt.ulisboa.ewp.node.service.communication.log;

import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.domain.repository.communication.log.CommunicationLogRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.utils.ExceptionUtils;

@Service
@Transactional
public class CommunicationLogService {

  private static final Logger LOG = LoggerFactory.getLogger(CommunicationLogService.class);

  private static final int MAX_NUMBER_OF_STACK_TRACE_LINES_PER_LEVEL = 15;

  private final CommunicationLogRepository repository;

  public CommunicationLogService(CommunicationLogRepository repository) {
    this.repository = repository;
  }

  public static <T extends CommunicationLog> void registerException(
      AbstractRepository<T> repository, T communicationLog, Throwable throwable) {
    String stackTraceAsString =
        ExceptionUtils.getStackTraceAsString(throwable, MAX_NUMBER_OF_STACK_TRACE_LINES_PER_LEVEL);
    communicationLog.setExceptionStacktrace(stackTraceAsString);
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
