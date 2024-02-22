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
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpResponseLogDto;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationResultDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.mapper.communication.log.CommunicationLogMapper;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.domain.repository.communication.log.CommunicationLogRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.http.validator.HttpResponseLogBodyValidator;
import pt.ulisboa.ewp.node.utils.ExceptionUtils;

@Service
@Transactional
public class CommunicationLogService {

  private static final Logger LOG = LoggerFactory.getLogger(CommunicationLogService.class);

  private static final int MAX_NUMBER_OF_STACK_TRACE_LINES_PER_LEVEL = 15;

  private final CommunicationLogRepository repository;
  private final HttpResponseLogBodyValidator httpResponseLogBodyValidator;

  public CommunicationLogService(
      CommunicationLogRepository repository,
      HttpResponseLogBodyValidator httpResponseLogBodyValidator) {
    this.repository = repository;
    this.httpResponseLogBodyValidator = httpResponseLogBodyValidator;
  }

  public CommunicationLogDetailDto findById(long id) {
    CommunicationLogMapper mapper = CommunicationLogMapper.INSTANCE;
    Optional<CommunicationLog> communicationLogOptional = this.repository.findById(id);
    if (communicationLogOptional.isEmpty()) {
      throw new IllegalArgumentException("There is no communication log with ID: " + id);
    }
    CommunicationLogDetailDto result =
        mapper.communicationLogToCommunicationLogDetailDto(communicationLogOptional.get());
    if (result instanceof HttpCommunicationLogDetailDto) {
      fillHttpCommunicationLogDetail((HttpCommunicationLogDetailDto) result);
    }
    return result;
  }

  private void fillHttpCommunicationLogDetail(
      HttpCommunicationLogDetailDto httpCommunicationLogDetailDto) {
    HttpResponseLogDto httpResponse = httpCommunicationLogDetailDto.getResponse();
    if (httpResponse != null && httpResponse.getBody() != null) {
      ValidationResultDto validationResult =
          this.httpResponseLogBodyValidator.validate(httpResponse);
      httpResponse.setBodyValidation(validationResult);
    }
  }

  public Collection<CommunicationLogSummaryDto> findByFilter(FilterDto<CommunicationLog> filter, int offset, int limit) {
    CommunicationLogMapper mapper = CommunicationLogMapper.INSTANCE;
    return this.repository.findByFilter(filter, offset, limit).stream()
        .map(mapper::communicationLogToCommunicationLogSummaryDto)
        .collect(Collectors.toList());
  }

  public long countByFilter(FilterDto<CommunicationLog> filter) {
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
