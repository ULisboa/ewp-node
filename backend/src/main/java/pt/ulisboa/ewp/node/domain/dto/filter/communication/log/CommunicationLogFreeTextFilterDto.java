package pt.ulisboa.ewp.node.domain.dto.filter.communication.log;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpHeaderLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpHeaderLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog_;

public class CommunicationLogFreeTextFilterDto extends FilterDto<CommunicationLog> {

  private String value;

  public CommunicationLogFreeTextFilterDto() {}

  public CommunicationLogFreeTextFilterDto(String value) {
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
    List<Predicate> predicateAlternatives = new ArrayList<>();

    predicateAlternatives.add(createPredicateForHttpCommunication(criteriaBuilder, selection));

    return criteriaBuilder.or(predicateAlternatives.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpCommunication(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    Root<HttpCommunicationLog> httpCommunicationLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationLog.class);
    predicates.add(
        createPredicateForHttpRequestLog(
            criteriaBuilder, httpCommunicationLogRoot.get(HttpCommunicationLog_.REQUEST)));
    predicates.add(
        createPredicateForHttpResponseLog(
            criteriaBuilder, httpCommunicationLogRoot.get(HttpCommunicationLog_.RESPONSE)));

    predicates.add(createPredicateForHttpCommunicationFromEwpNodeLog(criteriaBuilder, selection));

    predicates.add(createPredicateForHttpCommunicationToEwpNodeLog(criteriaBuilder, selection));

    predicates.add(createPredicateForHttpCommunicationFromHostLog(criteriaBuilder, selection));

    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpRequestLog(
      CriteriaBuilder criteriaBuilder, Path<HttpRequestLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    // NOTE: Value is contained on HTTP request's method
    predicates.add(
        criteriaBuilder.like(
            selection.get(HttpRequestLog_.method).as(String.class),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on HTTP request's URL
    predicates.add(
        criteriaBuilder.like(
            selection.get(HttpRequestLog_.url).as(String.class),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on some HTTP request's headers name
    Subquery<Long> headerNameSubquery =
        criteriaBuilder.createQuery(HttpCommunicationLog.class).subquery(Long.class);
    Root<HttpHeaderLog> httpHeaderLogNameSubqueryRoot =
        headerNameSubquery.from(HttpHeaderLog.class);
    Predicate headerNameSubqueryPredicate =
        criteriaBuilder.and(
            criteriaBuilder.equal(
                httpHeaderLogNameSubqueryRoot
                    .get(HttpHeaderLog_.requestLog)
                    .get(HttpRequestLog_.id),
                selection.get(HttpRequestLog_.id)),
            criteriaBuilder.like(
                httpHeaderLogNameSubqueryRoot.get(HttpHeaderLog_.name), "%" + getValue() + "%"));
    headerNameSubquery
        .select(httpHeaderLogNameSubqueryRoot.get(HttpHeaderLog_.id))
        .where(headerNameSubqueryPredicate);
    predicates.add(criteriaBuilder.exists(headerNameSubquery));

    // NOTE: Value is contained on some HTTP request's headers value
    Subquery<Long> headerValueSubquery =
        criteriaBuilder.createQuery(HttpCommunicationLog.class).subquery(Long.class);
    Root<HttpHeaderLog> httpHeaderLogValueSubqueryRoot =
        headerValueSubquery.from(HttpHeaderLog.class);
    Predicate headerValueSubqueryPredicate =
        criteriaBuilder.and(
            criteriaBuilder.equal(
                httpHeaderLogValueSubqueryRoot
                    .get(HttpHeaderLog_.requestLog)
                    .get(HttpRequestLog_.id),
                selection.get(HttpRequestLog_.id)),
            criteriaBuilder.like(
                httpHeaderLogValueSubqueryRoot.get(HttpHeaderLog_.value), "%" + getValue() + "%"));
    headerValueSubquery
        .select(httpHeaderLogValueSubqueryRoot.get(HttpHeaderLog_.id))
        .where(headerValueSubqueryPredicate);
    predicates.add(criteriaBuilder.exists(headerValueSubquery));

    // NOTE: Value is contained on HTTP response's body
    predicates.add(
        criteriaBuilder.like(
            selection.get(HttpRequestLog_.body), criteriaBuilder.literal("%" + getValue() + "%")));

    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpResponseLog(
      CriteriaBuilder criteriaBuilder, Path<HttpResponseLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    // NOTE: Value is contained on HTTP response's status code
    predicates.add(
        criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.literal(""),
                selection.get(HttpResponseLog_.statusCode).as(String.class)),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on some HTTP request's headers name
    Subquery<Long> headerNameSubquery =
        criteriaBuilder.createQuery(HttpCommunicationLog.class).subquery(Long.class);
    Root<HttpHeaderLog> httpHeaderLogNameSubqueryRoot =
        headerNameSubquery.from(HttpHeaderLog.class);
    Predicate headerNameSubqueryPredicate =
        criteriaBuilder.and(
            criteriaBuilder.equal(
                httpHeaderLogNameSubqueryRoot
                    .get(HttpHeaderLog_.responseLog)
                    .get(HttpResponseLog_.id),
                selection.get(HttpResponseLog_.id)),
            criteriaBuilder.like(
                httpHeaderLogNameSubqueryRoot.get(HttpHeaderLog_.name), "%" + getValue() + "%"));
    headerNameSubquery
        .select(httpHeaderLogNameSubqueryRoot.get(HttpHeaderLog_.id))
        .where(headerNameSubqueryPredicate);
    predicates.add(criteriaBuilder.exists(headerNameSubquery));

    // NOTE: Value is contained on some HTTP request's headers value
    Subquery<Long> headerValueSubquery =
        criteriaBuilder.createQuery(HttpCommunicationLog.class).subquery(Long.class);
    Root<HttpHeaderLog> httpHeaderLogValueSubqueryRoot =
        headerValueSubquery.from(HttpHeaderLog.class);
    Predicate headerValueSubqueryPredicate =
        criteriaBuilder.and(
            criteriaBuilder.equal(
                httpHeaderLogValueSubqueryRoot
                    .get(HttpHeaderLog_.responseLog)
                    .get(HttpResponseLog_.id),
                selection.get(HttpResponseLog_.id)),
            criteriaBuilder.like(
                httpHeaderLogValueSubqueryRoot.get(HttpHeaderLog_.value), "%" + getValue() + "%"));
    headerValueSubquery
        .select(httpHeaderLogValueSubqueryRoot.get(HttpHeaderLog_.id))
        .where(headerValueSubqueryPredicate);
    predicates.add(criteriaBuilder.exists(headerValueSubquery));

    // NOTE: checking for HTTP response's body is not yet supported

    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
  }

  private Predicate createPredicateForHttpCommunicationFromEwpNodeLog(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    Root<HttpCommunicationFromEwpNodeLog> httpCommunicationFromEwpNodeLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationFromEwpNodeLog.class);

    // NOTE: Value is contained on HEI IDs covered by client
    predicates.add(
        criteriaBuilder.isMember(
            getValue(),
            httpCommunicationFromEwpNodeLogRoot.get(
                HttpCommunicationFromEwpNodeLog_.heiIdsCoveredByClient)));

    // NOTE: Value is contained on API name
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationFromEwpNodeLogRoot.get(HttpCommunicationFromEwpNodeLog_.apiName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API major version
    predicates.add(
        criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.literal(""),
                httpCommunicationFromEwpNodeLogRoot
                    .get(HttpCommunicationFromEwpNodeLog_.apiMajorVersion)
                    .as(String.class)),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API endpoint
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationFromEwpNodeLogRoot.get(HttpCommunicationFromEwpNodeLog_.endpointName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    return criteriaBuilder.and(
        criteriaBuilder.equal(selection.type(), HttpCommunicationFromEwpNodeLog.class),
        criteriaBuilder.or(predicates.toArray(new Predicate[0])));
  }

  private Predicate createPredicateForHttpCommunicationToEwpNodeLog(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    Root<HttpCommunicationToEwpNodeLog> httpCommunicationToEwpNodeLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationToEwpNodeLog.class);

    // NOTE: Value is contained on target HEI ID
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.targetHeiId),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API name
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.apiName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API version
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.apiVersion),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API endpoint
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationToEwpNodeLogRoot.get(HttpCommunicationToEwpNodeLog_.endpointName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on server developer message
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationToEwpNodeLogRoot.get(
                HttpCommunicationToEwpNodeLog_.serverDeveloperMessage),
            criteriaBuilder.literal("%" + getValue() + "%")));

    return criteriaBuilder.and(
        criteriaBuilder.equal(selection.type(), HttpCommunicationToEwpNodeLog.class),
        criteriaBuilder.or(predicates.toArray(new Predicate[0])));
  }

  private Predicate createPredicateForHttpCommunicationFromHostLog(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    List<Predicate> predicates = new ArrayList<>();

    Root<HttpCommunicationFromHostLog> httpCommunicationFromHostLogRoot =
        criteriaBuilder.treat(selection, HttpCommunicationFromHostLog.class);

    // NOTE: Value is contained on API name
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.apiName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API major version
    predicates.add(
        criteriaBuilder.like(
            criteriaBuilder.concat(
                criteriaBuilder.literal(""),
                httpCommunicationFromHostLogRoot
                    .get(HttpCommunicationFromHostLog_.apiMajorVersion)
                    .as(String.class)),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on API endpoint
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.endpointName),
            criteriaBuilder.literal("%" + getValue() + "%")));

    // NOTE: Value is contained on target HEI ID
    predicates.add(
        criteriaBuilder.like(
            httpCommunicationFromHostLogRoot.get(HttpCommunicationFromHostLog_.targetHeiId),
            criteriaBuilder.literal("%" + getValue() + "%")));

    return criteriaBuilder.and(
        criteriaBuilder.equal(selection.type(), HttpCommunicationFromHostLog.class),
        criteriaBuilder.or(predicates.toArray(new Predicate[0])));
  }
}
