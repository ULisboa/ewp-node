package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog_;

public class HttpCommunicationResponseWithStatusCodeFilterDto extends FilterDto<CommunicationLog> {

  private int value;

  public HttpCommunicationResponseWithStatusCodeFilterDto() {}

  public HttpCommunicationResponseWithStatusCodeFilterDto(int value) {
    this.value = value;
  }

  public int getStatusCode() {
    return getValue();
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    Path<Integer> responseStatusCodePath =
        criteriaBuilder
            .treat(selection, HttpCommunicationLog.class)
            .get(HttpCommunicationLog_.response)
            .get(HttpResponseLog_.statusCode);

    return criteriaBuilder.equal(responseStatusCodePath, criteriaBuilder.literal(getStatusCode()));
  }
}
