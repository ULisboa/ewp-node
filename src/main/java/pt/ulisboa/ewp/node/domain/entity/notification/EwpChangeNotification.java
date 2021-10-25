package pt.ulisboa.ewp.node.domain.entity.notification;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "EWP_CHANGE_NOTIFICATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class EwpChangeNotification {

  private long id;
  private ZonedDateTime creationDateTime;
  private int attemptNumber;
  private ZonedDateTime scheduledDateTime;
  private Status status;

  protected EwpChangeNotification() {

  }

  protected EwpChangeNotification(int attemptNumber,
      ZonedDateTime scheduledDateTime,
      Status status) {
    this.creationDateTime = ZonedDateTime.now();
    this.attemptNumber = attemptNumber;
    this.scheduledDateTime = scheduledDateTime;
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

  @Column(name = "attempt_number", nullable = false)
  public int getAttemptNumber() {
    return this.attemptNumber;
  }

  public void setAttemptNumber(int attemptNumber) {
    this.attemptNumber = attemptNumber;
  }

  @Column(name = "scheduled_date_time", nullable = false)
  public ZonedDateTime getScheduledDateTime() {
    return this.scheduledDateTime;
  }

  public void setScheduledDateTime(ZonedDateTime scheduledDateTime) {
    this.scheduledDateTime = scheduledDateTime;
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
  public boolean hasFailed() {
    return getStatus() == Status.FAILED;
  }

  @Column(name = "status", nullable = false)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Transient
  public void scheduleNewAttempt() {
    BigInteger newDelayInMinutes = BigInteger.TWO.pow(this.attemptNumber);

    this.attemptNumber++;
    this.scheduledDateTime = this.scheduledDateTime.plusMinutes(newDelayInMinutes.longValueExact());
  }

  @Transient
  public void markAsSuccess() {
    this.status = Status.SUCCESS;
  }

  @Transient
  public void markAsFailed() {
    this.status = Status.FAILED;
  }

  @Transient
  public void markAsMerged() {
    this.status = Status.MERGED;
  }

  @Transient
  public boolean canBeMergedInto(EwpChangeNotification other) {
    return isPending() && other.isPending() && getCreationDateTime().isBefore(
        other.getCreationDateTime());
  }

  @Override
  public String toString() {
    return "EwpChangeNotification{" +
        "id=" + id +
        ", creationDateTime=" + creationDateTime +
        ", attemptNumber=" + attemptNumber +
        ", scheduledDateTime=" + scheduledDateTime +
        ", status=" + status +
        '}';
  }

  public enum Status {
    PENDING,
    SUCCESS,
    FAILED,
    MERGED;
  }
}
