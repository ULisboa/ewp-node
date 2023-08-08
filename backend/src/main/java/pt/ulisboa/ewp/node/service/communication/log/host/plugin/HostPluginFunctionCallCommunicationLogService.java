package pt.ulisboa.ewp.node.service.communication.log.host.plugin;

import java.time.ZonedDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.host.plugin.HostPluginFunctionCallCommunicationLogRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.log.CommunicationLogService;

@Service
@Transactional
public class HostPluginFunctionCallCommunicationLogService {

  private final HostPluginFunctionCallCommunicationLogRepository repository;

  public HostPluginFunctionCallCommunicationLogService(HostPluginFunctionCallCommunicationLogRepository repository) {
    this.repository = repository;
  }

  public HostPluginFunctionCallCommunicationLog logCommunicationBeforeExecution(
      ZonedDateTime startProcessingDateTime,
      CommunicationLog parentCommunication,
      String hostPluginId,
      String className,
      String method,
      List<Object> arguments)
      throws DomainException {

    return this.repository.create(
        startProcessingDateTime,
        null,
        "",
        parentCommunication,
        hostPluginId,
        className,
        method,
        arguments);
  }

  public boolean updateCommunicationAfterExecution(
      HostPluginFunctionCallCommunicationLog communicationLog, String resultType, String result) {
    communicationLog.editResult(resultType, result);
    return this.repository.persist(communicationLog);
  }

  public void registerException(
      HostPluginFunctionCallCommunicationLog communicationLog, Throwable throwable) {
    CommunicationLogService.registerException(repository, communicationLog, throwable);
  }
}
