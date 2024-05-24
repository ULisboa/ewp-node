package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog_;

public class HttpCommunicationToEwpNodeIsToHeiIdFilterDto extends FilterDto<CommunicationLog> {

  private String value;

  public HttpCommunicationToEwpNodeIsToHeiIdFilterDto() {}

  public HttpCommunicationToEwpNodeIsToHeiIdFilterDto(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    return criteriaBuilder.or(
        criteriaBuilder.equal(
            criteriaBuilder
                .treat(selection, HttpCommunicationToEwpNodeLog.class)
                .get(HttpCommunicationToEwpNodeLog_.targetHeiId),
            criteriaBuilder.literal(value)),
        criteriaBuilder.equal(
            criteriaBuilder
                .treat(selection, HttpCommunicationFromHostLog.class)
                .get(HttpCommunicationFromHostLog_.targetHeiId),
            criteriaBuilder.literal(value)));
  }
}
