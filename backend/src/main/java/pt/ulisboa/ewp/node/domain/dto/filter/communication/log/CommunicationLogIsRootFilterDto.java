package pt.ulisboa.ewp.node.domain.dto.filter.communication.log;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog_;

public class CommunicationLogIsRootFilterDto extends FilterDto<CommunicationLog> {

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    return criteriaBuilder.isNull(selection.get(CommunicationLog_.parentCommunication));
  }
}
