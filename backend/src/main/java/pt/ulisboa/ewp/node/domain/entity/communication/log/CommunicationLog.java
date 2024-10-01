package pt.ulisboa.ewp.node.domain.entity.communication.log;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.proxy.HibernateProxy;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.utils.DomainConstants;
import pt.ulisboa.ewp.node.domain.utils.communication.log.CommunicationLogWarningCode;
import pt.ulisboa.ewp.node.utils.StringUtils;

@Entity
@Table(name = "COMMUNICATION_LOG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "communication_type", discriminatorType = DiscriminatorType.STRING)
public class CommunicationLog {

  public static final int MAX_NUMBER_OF_STACK_TRACE_LINES_PER_LEVEL = 15;

  private long id;
  private Status status;
  private ZonedDateTime startProcessingDateTime;
  private ZonedDateTime endProcessingDateTime;
  private String exceptionStacktrace;
  private String observations;
  private CommunicationLog parentCommunication;
  private Set<CommunicationLog> childrenCommunications = new HashSet<>();
  private Collection<EwpChangeNotification> ewpChangeNotificationsAsOrigin = new HashSet<>();
  private Collection<EwpChangeNotification> ewpChangeNotifications = new HashSet<>();

  protected CommunicationLog() {}

  protected CommunicationLog(
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication) {
    this.status = Status.INCOMPLETE;
    this.startProcessingDateTime = startProcessingDateTime;
    this.endProcessingDateTime = endProcessingDateTime;
    setObservations(observations);
    if (parentCommunication != null) {
      parentCommunication.addChildCommunication(this);
    }
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

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    if (getEndProcessingDateTime() == null) {
      return Status.INCOMPLETE;
    }
    if (getExceptionStacktrace() != null && !getExceptionStacktrace().isEmpty()) {
      return Status.FAILURE;
    }
    return Status.SUCCESS;
  }

  /** This method does nothing. Status is calculated on runtime when calling getStatus(). */
  public void setStatus(Status status) {
    // NOTE: status is ignored as it is calculated on runtime
    // It is only implemented due to Hibernate requiring a setter method
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

  @Column(name = "exception_stacktrace", columnDefinition = "TEXT")
  public String getExceptionStacktrace() {
    return exceptionStacktrace;
  }

  public void setExceptionStacktrace(String exceptionStacktrace) {
    this.exceptionStacktrace =
        StringUtils.truncateWithSuffix(
            exceptionStacktrace, DomainConstants.MAX_TEXT_COLUMN_TEXT_LENGTH, "====TRUNCATED====");
    ;
  }

  @Column(name = "observations", nullable = true, columnDefinition = "TEXT")
  public String getObservations() {
    return this.observations;
  }

  public void setObservations(String observations) {
    this.observations =
        StringUtils.truncateWithSuffix(
            observations, DomainConstants.MAX_TEXT_COLUMN_TEXT_LENGTH, "====TRUNCATED====");
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_communication_id")
  public CommunicationLog getParentCommunication() {
    return parentCommunication;
  }

  public void setParentCommunication(CommunicationLog parentCommunication) {
    this.parentCommunication = parentCommunication;
  }

  @Transient
  public Collection<CommunicationLog> getSortedChildrenCommunications() {
    return getChildrenCommunications().stream()
        .sorted(
            Comparator.comparing(CommunicationLog::getStartProcessingDateTime)
                .thenComparing(CommunicationLog::getId))
        .collect(Collectors.toList());
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCommunication", cascade = CascadeType.ALL)
  public Set<CommunicationLog> getChildrenCommunications() {
    return childrenCommunications;
  }

  public void setChildrenCommunications(Set<CommunicationLog> childrenCommunications) {
    this.childrenCommunications = childrenCommunications;
  }

  public void addChildCommunication(CommunicationLog childCommunication) {
    this.childrenCommunications.add(childCommunication);
    childCommunication.setParentCommunication(this);
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "originCommunicationLog", cascade = CascadeType.ALL)
  public Collection<EwpChangeNotification> getEwpChangeNotificationsAsOrigin() {
    return ewpChangeNotificationsAsOrigin;
  }

  public void setEwpChangeNotificationsAsOrigin(
      Collection<EwpChangeNotification> ewpChangeNotificationsAsOrigin) {
    this.ewpChangeNotificationsAsOrigin = ewpChangeNotificationsAsOrigin;
  }

  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(
      name = "CommunicationLog_EwpChangeNotification",
      joinColumns = {@JoinColumn(name = "communication_log_id")},
      inverseJoinColumns = {@JoinColumn(name = "ewp_change_notification_id")})
  public Collection<EwpChangeNotification> getEwpChangeNotifications() {
    return ewpChangeNotifications;
  }

  public void setEwpChangeNotifications(Collection<EwpChangeNotification> ewpChangeNotifications) {
    this.ewpChangeNotifications = ewpChangeNotifications;
  }

  @Transient
  public final String getType() {
    Class<?> clazz = this.getClass();
    if (this instanceof HibernateProxy) {
      clazz = ((HibernateProxy) this).getHibernateLazyInitializer().getImplementation().getClass();
    }
    DiscriminatorValue discriminatorValue = clazz.getAnnotation(DiscriminatorValue.class);
    return discriminatorValue != null ? discriminatorValue.value() : null;
  }

  @Transient
  public String getSource() {
    if (this.parentCommunication == null) {
      return "Unknown";
    }
    return this.parentCommunication.getTarget();
  }

  @Transient
  public String getTarget() {
    return "Unknown";
  }

  @Transient
  public List<CommunicationLogWarningCode> getWarningCodes() {
    return new ArrayList<>();
  }

  public enum Status {
    INCOMPLETE,
    SUCCESS,
    FAILURE
  }
}
