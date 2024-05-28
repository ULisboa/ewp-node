package pt.ulisboa.ewp.node.domain.entity.notification;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@Table(name = "EWP_CHANGE_NOTIFICATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class EwpChangeNotification {

  private long id;
  private ZonedDateTime creationDateTime;
  private CommunicationLog originCommunicationLog;
  private Collection<CommunicationLog> communications = new HashSet<>();
  private int attemptNumber;
  private ZonedDateTime nextAttemptDateTime;
  private Status status;
  private EwpChangeNotification mergedIntoChangeNotification;
  private Collection<EwpChangeNotification> changeNotificationsAsAggregator = new HashSet<>();

  protected EwpChangeNotification() {}

  protected EwpChangeNotification(CommunicationLog originCommunicationLog) {
    this(originCommunicationLog, 0, ZonedDateTime.now(), Status.PENDING);
  }

  protected EwpChangeNotification(
      CommunicationLog originCommunicationLog,
      int attemptNumber,
      ZonedDateTime nextAttemptDateTime,
      Status status) {
    this.creationDateTime = ZonedDateTime.now();
    this.originCommunicationLog = originCommunicationLog;
    this.attemptNumber = attemptNumber;
    this.nextAttemptDateTime = nextAttemptDateTime;
    this.status = status;
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

  @Column(name = "creation_date_time", nullable = false)
  public ZonedDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(ZonedDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "origin_communication_log_id")
  public CommunicationLog getOriginCommunicationLog() {
    return originCommunicationLog;
  }

  public void setOriginCommunicationLog(CommunicationLog originCommunicationLog) {
    this.originCommunicationLog = originCommunicationLog;
  }

  @Transient
  public Collection<CommunicationLog> getSortedCommunications() {
    return getCommunications().stream()
        .sorted(
            Comparator.comparing(CommunicationLog::getStartProcessingDateTime)
                .thenComparing(CommunicationLog::getId))
        .collect(Collectors.toList());
  }

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "ewpChangeNotifications")
  public Collection<CommunicationLog> getCommunications() {
    return communications;
  }

  public void setCommunications(Collection<CommunicationLog> communications) {
    this.communications = communications;
  }

  @Column(name = "attempt_number", nullable = false)
  public int getAttemptNumber() {
    return this.attemptNumber;
  }

  public void incrementAttemptNumber() {
    this.attemptNumber++;
  }

  public void setAttemptNumber(int attemptNumber) {
    this.attemptNumber = attemptNumber;
  }

  @Column(name = "next_attempt_date_time")
  public ZonedDateTime getNextAttemptDateTime() {
    return this.nextAttemptDateTime;
  }

  public void setNextAttemptDateTime(ZonedDateTime nextAttemptDateTime) {
    this.nextAttemptDateTime = nextAttemptDateTime;
  }

  @Transient
  public boolean isPending() {
    return getStatus() == Status.PENDING;
  }

  @Transient
  public boolean wasSuccess() {
    return getStatus() == Status.SUCCESS;
  }

  @Transient
  public boolean wasMerged() {
    return getStatus() == Status.MERGED;
  }

  @Transient
  public boolean hasFailedDueToMaxAttempts() {
    return getStatus() == Status.FAILED_MAX_ATTEMPTS;
  }

  @Transient
  public boolean hasFailedDueToNoCnrApiAvailable() {
    return getStatus() == Status.FAILED_NO_CNR_API_AVAILABLE;
  }

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merged_into_change_notification_id")
  public EwpChangeNotification getMergedIntoChangeNotification() {
    return mergedIntoChangeNotification;
  }

  public void setMergedIntoChangeNotification(EwpChangeNotification mergedIntoChangeNotification) {
    this.mergedIntoChangeNotification = mergedIntoChangeNotification;
  }

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "mergedIntoChangeNotification",
      cascade = CascadeType.ALL)
  public Collection<EwpChangeNotification> getChangeNotificationsAsAggregator() {
    return changeNotificationsAsAggregator;
  }

  public void setChangeNotificationsAsAggregator(
      Collection<EwpChangeNotification> changeNotificationsAsAggregator) {
    this.changeNotificationsAsAggregator = changeNotificationsAsAggregator;
  }

  @Transient
  public void scheduleNewAttempt() {
    BigInteger newDelayInMinutes = BigInteger.TWO.pow(this.attemptNumber);

    this.nextAttemptDateTime =
        this.nextAttemptDateTime.plusMinutes(newDelayInMinutes.longValueExact());
  }

  @Transient
  public void markAsSuccess() {
    this.status = Status.SUCCESS;
    this.nextAttemptDateTime = null;
  }

  @Transient
  public void markAsFailedDueToMaxAttempts() {
    this.status = Status.FAILED_MAX_ATTEMPTS;
    this.nextAttemptDateTime = null;
  }

  @Transient
  public void markAsFailedDueToNoCnrApiAvailable() {
    this.status = Status.FAILED_NO_CNR_API_AVAILABLE;
    this.nextAttemptDateTime = null;
  }

  @Transient
  public void mergeInto(EwpChangeNotification mergedIntoChangeNotification) {
    this.status = Status.MERGED;
    this.nextAttemptDateTime = null;
    this.mergedIntoChangeNotification = mergedIntoChangeNotification;
  }

  @Transient
  public boolean canBeMergedInto(EwpChangeNotification other) {
    return isPending() && other.isPending() && getCreationDateTime().isBefore(
        other.getCreationDateTime());
  }

  /**
   * Returns a map of a key (label) to the actual value for every specific variable of the change
   * notification (e.g. iiaId).
   */
  @Transient
  public abstract List<ExtraVariableEntry> getExtraVariables();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EwpChangeNotification that = (EwpChangeNotification) o;
    return id == that.id
        && attemptNumber == that.attemptNumber
        && Objects.equals(creationDateTime, that.creationDateTime)
        && Objects.equals(originCommunicationLog, that.originCommunicationLog)
        && Objects.equals(nextAttemptDateTime, that.nextAttemptDateTime)
        && status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, creationDateTime, originCommunicationLog, attemptNumber, nextAttemptDateTime, status);
  }

  @Override
  public String toString() {
    return "EwpChangeNotification{"
        + "id="
        + id
        + ", creationDateTime="
        + creationDateTime
        + ", originCommunicationLog="
        + originCommunicationLog
        + ", attemptNumber="
        + attemptNumber
        + ", scheduledDateTime="
        + nextAttemptDateTime
        + ", status="
        + status
        + '}';
  }

  public enum Status {
    PENDING,
    SUCCESS,
    FAILED_MAX_ATTEMPTS,
    FAILED_NO_CNR_API_AVAILABLE,
    MERGED;
  }

  public static class ExtraVariableEntry {

    private final String key;
    private final String value;

    public ExtraVariableEntry(String key, String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }
}
