package pt.ulisboa.ewp.node.domain.utils;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "db")
public class DatabaseProperties {

  private String driverClassName;
  private String url;
  private String username;
  private String password;
  private HibernateProperties hibernate;

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public HibernateProperties getHibernate() {
    return hibernate;
  }

  public void setHibernate(HibernateProperties hibernate) {
    this.hibernate = hibernate;
  }
}
