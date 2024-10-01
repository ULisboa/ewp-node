package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog_;

public class HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto
    extends FilterDto<CommunicationLog> {

  private String value;

  public HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto() {}

  public HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto(String value) {
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
    return criteriaBuilder.isMember(
        value,
        criteriaBuilder
            .treat(selection, HttpCommunicationFromEwpNodeLog.class)
            .get(HttpCommunicationFromEwpNodeLog_.heiIdsCoveredByClient));
  }
}
