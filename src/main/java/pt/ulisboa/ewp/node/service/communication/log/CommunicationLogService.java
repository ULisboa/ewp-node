package pt.ulisboa.ewp.node.service.communication.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.CommunicationLogRepository;

@Service
@Transactional
public class CommunicationLogService {

  private final CommunicationLogRepository repository;

  public CommunicationLogService(CommunicationLogRepository repository) {
    this.repository = repository;
  }

  public void registerException(CommunicationLog communicationLog, Exception exception) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    exception.printStackTrace(printWriter);

    communicationLog.setExceptionStacktrace(stringWriter.toString());
    repository.persist(communicationLog);
  }
}
