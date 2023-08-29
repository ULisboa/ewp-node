package pt.ulisboa.ewp.node.service.communication.log;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogSummaryDto;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.mapper.communication.log.CommunicationLogMapper;
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

  public CommunicationLogDetailDto findById(long id) {
    CommunicationLogMapper mapper = CommunicationLogMapper.INSTANCE;
    Optional<CommunicationLog> communicationLogOptional = this.repository.findById(id);
    if (communicationLogOptional.isEmpty()) {
      throw new IllegalArgumentException("There is no communication log with ID: " + id);
    }
    return mapper.communicationLogToCommunicationLogDetailDto(communicationLogOptional.get());
  }

  public Collection<CommunicationLogSummaryDto> findByFilter(FilterDto filter, int offset, int limit) {
    CommunicationLogMapper mapper = CommunicationLogMapper.INSTANCE;
    return this.repository.findByFilter(filter, offset, limit).stream()
        .map(mapper::communicationLogToCommunicationLogSummaryDto)
        .collect(Collectors.toList());
  }

  public long countByFilter(FilterDto filter) {
    return this.repository.countByFilter(filter);
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
