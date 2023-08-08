package pt.ulisboa.ewp.node.domain.utils;

public class HibernateProperties {

  private String dialect;
  private String hbm2ddlAuto;
  private String timeZone;

  public String getDialect() {
    return dialect;
  }

  public void setDialect(String dialect) {
    this.dialect = dialect;
  }

  public String getHbm2ddlAuto() {
    return hbm2ddlAuto;
  }

  public void setHbm2ddlAuto(String hbm2ddlAuto) {
    this.hbm2ddlAuto = hbm2ddlAuto;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }
}
