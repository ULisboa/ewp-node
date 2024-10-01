package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.web.util.UriUtils;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog_;

public class HttpCommunicationFormParameterStartsWithValueFilterDto
    extends HttpCommunicationFormParameterFilterDto {

  private String value;

  public HttpCommunicationFormParameterStartsWithValueFilterDto() {}

  public HttpCommunicationFormParameterStartsWithValueFilterDto(String parameter, String value) {
    super(parameter);
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
      CriteriaBuilder criteriaBuilder, Root<HttpCommunicationLog> selection) {
    Path<HttpRequestLog> requestPath =
        criteriaBuilder
            .treat(selection, HttpCommunicationLog.class)
            .get(HttpCommunicationLog_.request);

    return criteriaBuilder.or(
        criteriaBuilder.like(
            requestPath.get(HttpRequestLog_.url),
            "%"
                + UriUtils.encodeQuery(getParameter(), "UTF-8")
                + "="
                + UriUtils.encodeQuery(value, "UTF-8")
                + "%"),
        criteriaBuilder.like(
            requestPath.get(HttpRequestLog_.body),
            "%"
                + UriUtils.encodeQuery(getParameter(), "UTF-8")
                + "="
                + UriUtils.encodeQuery(value, "UTF-8")
                + "%"));
  }
}
