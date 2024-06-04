package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.host;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog_;

public class HttpCommunicationToForwardEwpApiEndpointFilterDto extends FilterDto<CommunicationLog> {

  private String apiName;
  private Integer apiMajorVersion;
  private String endpointName;

  public HttpCommunicationToForwardEwpApiEndpointFilterDto() {}

  public HttpCommunicationToForwardEwpApiEndpointFilterDto(
      String apiName, Integer apiMajorVersion, String endpointName) {
    this.apiName = apiName;
    this.apiMajorVersion = apiMajorVersion;
    this.endpointName = endpointName;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public Integer getApiMajorVersion() {
    return apiMajorVersion;
  }

  public void setApiMajorVersion(Integer apiMajorVersion) {
    this.apiMajorVersion = apiMajorVersion;
  }

  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(criteriaBuilder.equal(selection.type(), HttpCommunicationFromHostLog.class));
    Root<HttpCommunicationFromHostLog> httpCommunicationFromHostLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationFromHostLog.class);

    if (!StringUtils.isEmpty(getApiName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.apiName),
              criteriaBuilder.literal("%" + getApiName() + "%"));
      predicates.add(predicate);
    }

    if (getApiMajorVersion() != null) {
      Predicate predicate =
          criteriaBuilder.equal(
              httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.apiMajorVersion),
              criteriaBuilder.literal(getApiMajorVersion()));
      predicates.add(predicate);
    }

    if (!StringUtils.isEmpty(getEndpointName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.endpointName),
              criteriaBuilder.literal("%" + getEndpointName() + "%"));
      predicates.add(predicate);
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }
}
