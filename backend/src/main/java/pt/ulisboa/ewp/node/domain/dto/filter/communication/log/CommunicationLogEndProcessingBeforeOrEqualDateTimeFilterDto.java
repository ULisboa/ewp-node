package pt.ulisboa.ewp.node.domain.dto.filter.communication.log;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog_;

public class CommunicationLogEndProcessingBeforeOrEqualDateTimeFilterDto
    extends FilterDto<CommunicationLog> {

  private ZonedDateTime value;

  public CommunicationLogEndProcessingBeforeOrEqualDateTimeFilterDto() {}

  public CommunicationLogEndProcessingBeforeOrEqualDateTimeFilterDto(ZonedDateTime value) {
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
    return criteriaBuilder.lessThanOrEqualTo(
        selection.get(CommunicationLog_.END_PROCESSING_DATE_TIME), value);
  }
}
