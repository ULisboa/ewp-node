package pt.ulisboa.ewp.node.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApi;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HostHttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "HOST")
public class Host {

  private long id;
  private String code;
  private String description;
  private String adminEmail;
  private String adminNotes;
  private String adminProvider;
  private boolean ounitIdInObjectsRequired;
  private String ounitIdInObjectsRequiredErrorMessage;

  private HostForwardEwpApi forwardEwpApi;
  private Set<Hei> coveredHeis = new HashSet<>();
  private Collection<HostHttpCommunicationLog> httpCommunicationLogs = new HashSet<>();

  protected Host() {
  }

  protected Host(
      String code,
      String description,
      String adminEmail,
      String adminNotes,
      String adminProvider,
      boolean ounitIdInObjectsRequired,
      String ounitIdInObjectsRequiredErrorMessage) {
    this.code = code;
    this.description = description;
    this.adminEmail = adminEmail;
    this.adminNotes = adminNotes;
    this.adminProvider = adminProvider;
    this.ounitIdInObjectsRequired = ounitIdInObjectsRequired;
    this.ounitIdInObjectsRequiredErrorMessage = ounitIdInObjectsRequiredErrorMessage;
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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "admin_email")
  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  @Column(name = "admin_notes")
  public String getAdminNotes() {
    return adminNotes;
  }

  public void setAdminNotes(String adminNotes) {
    this.adminNotes = adminNotes;
  }

  @Column(name = "admin_provider")
  public String getAdminProvider() {
    return adminProvider;
  }

  public void setAdminProvider(String adminProvider) {
    this.adminProvider = adminProvider;
  }

  @Column(name = "ounit_id_in_objects_required")
  public boolean isOunitIdInObjectsRequired() {
    return ounitIdInObjectsRequired;
  }

  public void setOunitIdInObjectsRequired(boolean ounitIdInObjectsRequired) {
    this.ounitIdInObjectsRequired = ounitIdInObjectsRequired;
  }

  @Column(
      name = "ounit_id_in_objects_required_error_message",
      columnDefinition = "TEXT",
      nullable = true)
  public String getOunitIdInObjectsRequiredErrorMessage() {
    return ounitIdInObjectsRequiredErrorMessage;
  }

  public void setOunitIdInObjectsRequiredErrorMessage(String ounitIdInObjectsRequiredErrorMessage) {
    this.ounitIdInObjectsRequiredErrorMessage = ounitIdInObjectsRequiredErrorMessage;
  }

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "forward_ewp_api_id")
  public HostForwardEwpApi getForwardEwpApi() {
    return forwardEwpApi;
  }

  public void setForwardEwpApi(
      HostForwardEwpApi forwardEwpApi) {
    this.forwardEwpApi = forwardEwpApi;
  }

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "host", cascade = CascadeType.ALL)
  public Set<Hei> getCoveredHeis() {
    return coveredHeis;
  }

  @Transient
  public Optional<Hei> getCoveredHei(String schacCode) {
    return coveredHeis.stream().filter(ch -> ch.getSchacCode().equals(schacCode)).findFirst();
  }

  public void setCoveredHeis(Set<Hei> coveredHeis) {
    this.coveredHeis = coveredHeis;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "host", cascade = CascadeType.ALL)
  public Collection<HostHttpCommunicationLog> getHttpCommunicationLogs() {
    return httpCommunicationLogs;
  }

  public void setHttpCommunicationLogs(Collection<HostHttpCommunicationLog> httpCommunicationLogs) {
    this.httpCommunicationLogs = httpCommunicationLogs;
  }

  public void update(
      String description,
      String adminEmail,
      String adminNotes,
      String adminProvider,
      boolean ounitIdInObjectsRequired,
      String ounitIdInObjectsRequiredErrorMessage) {
    this.description = description;
    this.adminEmail = adminEmail;
    this.adminNotes = adminNotes;
    this.adminProvider = adminProvider;
    this.ounitIdInObjectsRequired = ounitIdInObjectsRequired;
    this.ounitIdInObjectsRequiredErrorMessage = ounitIdInObjectsRequiredErrorMessage;
  }

  public static Host create(
      String code,
      String description,
      String adminEmail,
      String adminNotes,
      String adminProvider,
      boolean ounitIdInObjectsRequired,
      String ounitIdInObjectsRequiredErrorMessage) {
    return new Host(
        code,
        description,
        adminEmail,
        adminNotes,
        adminProvider,
        ounitIdInObjectsRequired,
        ounitIdInObjectsRequiredErrorMessage);
  }

  @Override
  public String toString() {
    return "Host{"
        + "id="
        + id
        + ", code='"
        + code
        + '\''
        + ", description='"
        + description
        + '\''
        + ", adminEmail='"
        + adminEmail
        + '\''
        + ", adminNotes='"
        + adminNotes
        + '\''
        + ", adminProvider='"
        + adminProvider
        + '\''
        + ", ounitIdInObjectsRequired="
        + ounitIdInObjectsRequired
        + ", ounitIdInObjectsRequiredErrorMessage='"
        + ounitIdInObjectsRequiredErrorMessage
        + '\''
        + '}';
  }
}
