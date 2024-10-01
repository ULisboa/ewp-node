package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog_;

public class HttpCommunicationToApiEndpointFilterDto extends FilterDto<CommunicationLog> {

  private String apiName;
  private String apiVersion;
  private String endpointName;

  public HttpCommunicationToApiEndpointFilterDto() {}

  public HttpCommunicationToApiEndpointFilterDto(
      String apiName, String apiVersion, String endpointName) {
    this.apiName = apiName;
    this.apiVersion = apiVersion;
    this.endpointName = endpointName;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
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
    List<Predicate> predicateAlternatives = new ArrayList<>();

    predicateAlternatives.add(
        createPredicateForHttpCommunicationToEwpNodeLog(criteriaBuilder, selection));
    predicateAlternatives.add(
        createPredicateForHttpCommunicationFromEwpNodeLog(criteriaBuilder, selection));
    predicateAlternatives.add(
        createPredicateForHttpCommunicationFromHostLog(criteriaBuilder, selection));

    return criteriaBuilder.or(predicateAlternatives.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpCommunicationToEwpNodeLog(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(criteriaBuilder.equal(selection.type(), HttpCommunicationToEwpNodeLog.class));
    Root<HttpCommunicationToEwpNodeLog> httpCommunicationToEwpNodeLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationToEwpNodeLog.class);

    if (!StringUtils.isEmpty(getApiName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.apiName),
              criteriaBuilder.literal("%" + getApiName() + "%"));
      predicates.add(predicate);
    }

    if (!StringUtils.isEmpty(getApiVersion())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.apiVersion),
              criteriaBuilder.literal(getApiVersion() + "%"));
      predicates.add(predicate);
    }

    if (!StringUtils.isEmpty(getEndpointName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.endpointName),
              criteriaBuilder.literal("%" + getEndpointName() + "%"));
      predicates.add(predicate);
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpCommunicationFromEwpNodeLog(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(criteriaBuilder.equal(selection.type(), HttpCommunicationFromEwpNodeLog.class));
    Root<HttpCommunicationFromEwpNodeLog> httpCommunicationFromEwpNodeLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationFromEwpNodeLog.class);

    if (!StringUtils.isEmpty(getApiName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationFromEwpNodeLogRoot.get(HttpCommunicationFromEwpNodeLog_.apiName),
              criteriaBuilder.literal("%" + getApiName() + "%"));
      predicates.add(predicate);
    }

    if (!StringUtils.isEmpty(getApiVersion())) {
      Predicate predicate =
          criteriaBuilder.equal(
              criteriaBuilder.concat(
                  criteriaBuilder.literal(""),
                  httpCommunicationFromEwpNodeLogRoot
                      .get(HttpCommunicationFromEwpNodeLog_.apiMajorVersion)
                      .as(String.class)),
              criteriaBuilder.literal(getApiVersion()));
      predicates.add(predicate);
    }

    if (!StringUtils.isEmpty(getEndpointName())) {
      Predicate predicate =
          criteriaBuilder.like(
              httpCommunicationFromEwpNodeLogRoot.get(
                  HttpCommunicationFromEwpNodeLog_.endpointName),
              criteriaBuilder.literal("%" + getEndpointName() + "%"));
      predicates.add(predicate);
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpCommunicationFromHostLog(
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

    if (!StringUtils.isEmpty(getApiVersion())) {
      Predicate predicate =
          criteriaBuilder.equal(
              criteriaBuilder.concat(
                  criteriaBuilder.literal(""),
                  httpCommunicationFromHostLogRoot
                      .get(HttpCommunicationFromHostLog_.apiMajorVersion)
                      .as(String.class)),
              criteriaBuilder.literal(getApiVersion()));
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
