package pt.ulisboa.ewp.node.domain.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import pt.ulisboa.ewp.node.domain.listener.EntityAuditListener;

@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "USER_PROFILE")
public class UserProfile {

  private long id;
  private String username;
  private UserRole role;

  protected UserProfile() {}

  protected UserProfile(String username, UserRole role) {
    this.username = username;
    this.role = role;
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

  @Column(name = "username", unique = true, nullable = false)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public static UserProfile create(String username, UserRole role) {
    return new UserProfile(username, role);
  }

  @Override
  public String toString() {
    return String.format("UserProfile(username = %s; role = %s)", username, role);
  }
}
