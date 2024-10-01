package pt.ulisboa.ewp.node.domain.repository.communication.log.host.plugin;

import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HostPluginFunctionCallCommunicationLogRepository
    extends AbstractRepository<HostPluginFunctionCallCommunicationLog> {

  @Autowired @Lazy private MessageResolver messages;

  protected HostPluginFunctionCallCommunicationLogRepository(SessionFactory sessionFactory) {
    super(HostPluginFunctionCallCommunicationLog.class, sessionFactory);
  }

  public HostPluginFunctionCallCommunicationLog create(
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication,
      String hostPluginId,
      String className,
      String method,
      List<Object> arguments)
      throws DomainException {
    HostPluginFunctionCallCommunicationLog hostPluginFunctionCallCommunicationLog =
        new HostPluginFunctionCallCommunicationLog(
            startProcessingDateTime,
            endProcessingDateTime,
            observations,
            parentCommunication,
            hostPluginId,
            className,
            method,
            arguments);

    if (persist(hostPluginFunctionCallCommunicationLog)) {
      return hostPluginFunctionCallCommunicationLog;
    } else {
      throw new DomainException("Failed to create host plugin call communication log");
    }
  }

  @Override
  protected boolean checkDomainConstraints(HostPluginFunctionCallCommunicationLog entity)
      throws DomainException {
    if (entity.getHostPluginId() == null) {
      throw new DomainException(
          messages.get(
              "error.communication.log.hostPluginFunctionCall.hostPluginId.must.be.defined"));
    }

    if (entity.getClassName() == null) {
      throw new DomainException(
          messages.get("error.communication.log.hostPluginFunctionCall.className.must.be.defined"));
    }

    if (entity.getMethod() == null) {
      throw new DomainException(
          messages.get("error.communication.log.hostPluginFunctionCall.method.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.communication.log.hostPluginFunctionCall.startProcessingDateTime.must.be.defined"));
    }

    return true;
  }
}
