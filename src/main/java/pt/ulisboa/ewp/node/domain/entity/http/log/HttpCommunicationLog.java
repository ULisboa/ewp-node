package pt.ulisboa.ewp.node.domain.entity.http.log;

import java.time.ZonedDateTime;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import javax.persistence.Transient;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.utils.StringUtils;

@Entity
@Table(name = "HTTP_COMMUNICATION_LOG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "communication_type", discriminatorType = DiscriminatorType.STRING)
public class HttpCommunicationLog {

  private static final int MAX_OBSERVATIONS_LENGTH = 10000;

  private long id;
  private HttpRequestLog request;
  private HttpResponseLog response;
  private ZonedDateTime startProcessingDateTime;
  private ZonedDateTime endProcessingDateTime;
  private String observations;
  private HttpCommunicationLog parentCommunication;
  private Set<HttpCommunicationLog> childrenCommunications;

  protected HttpCommunicationLog() {
  }

  protected HttpCommunicationLog(
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) {
    this.request = request;
    this.response = response;
    this.startProcessingDateTime = startProcessingDateTime;
    this.endProcessingDateTime = endProcessingDateTime;
    this.observations = StringUtils.truncateWithSuffix(observations, MAX_OBSERVATIONS_LENGTH - "====TRUNCATED====".length(),
            "====TRUNCATED====");
    this.parentCommunication = parentCommunication;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "request_log")
  public HttpRequestLog getRequest() {
    return request;
  }

  public void setRequest(HttpRequestLog request) {
    this.request = request;
  }

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "response_log")
  public HttpResponseLog getResponse() {
    return response;
  }

  public void setResponse(HttpResponseLog response) {
    this.response = response;
  }

  @Column(name = "start_processing_date_time", nullable = false)
  public ZonedDateTime getStartProcessingDateTime() {
    return startProcessingDateTime;
  }

  public void setStartProcessingDateTime(ZonedDateTime startProcessingDateTime) {
    this.startProcessingDateTime = startProcessingDateTime;
  }

  @Column(name = "end_processing_date_time")
  public ZonedDateTime getEndProcessingDateTime() {
    return endProcessingDateTime;
  }

  public void setEndProcessingDateTime(ZonedDateTime endProcessingDateTime) {
    this.endProcessingDateTime = endProcessingDateTime;
  }

  @Column(name = "observations", nullable = true, length = 10000)
  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_communication_id")
  public HttpCommunicationLog getParentCommunication() {
    return parentCommunication;
  }

  public void setParentCommunication(
      HttpCommunicationLog parentCommunication) {
    this.parentCommunication = parentCommunication;
  }

  @Transient
  public Collection<HttpCommunicationLog> getSortedChildrenCommunications() {
    return getChildrenCommunications().stream()
        .sorted(Comparator.comparing(HttpCommunicationLog::getStartProcessingDateTime))
        .collect(Collectors.toList());
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCommunication", cascade = CascadeType.ALL)
  public Set<HttpCommunicationLog> getChildrenCommunications() {
    return childrenCommunications;
  }

  public void setChildrenCommunications(
      Set<HttpCommunicationLog> childrenCommunications) {
    this.childrenCommunications = childrenCommunications;
  }
}
