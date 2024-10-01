package pt.ulisboa.ewp.node.domain.dto.filter.communication.log;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog_;

public class CommunicationLogStartProcessingAfterOrEqualDateTimeFilterDto
    extends FilterDto<CommunicationLog> {

  private ZonedDateTime value;

  public CommunicationLogStartProcessingAfterOrEqualDateTimeFilterDto() {}

  public CommunicationLogStartProcessingAfterOrEqualDateTimeFilterDto(ZonedDateTime value) {
    this.value = value;
  }

  public ZonedDateTime getValue() {
    return value;
  }

  public void setValue(ZonedDateTime value) {
    this.value = value;
  }

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    return criteriaBuilder.greaterThanOrEqualTo(
        selection.get(CommunicationLog_.START_PROCESSING_DATE_TIME), value);
  }
}
