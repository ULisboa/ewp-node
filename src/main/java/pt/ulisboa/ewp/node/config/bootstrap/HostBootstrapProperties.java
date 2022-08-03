package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.List;

public class HostBootstrapProperties {

  private String code;
  private String description;
  private String adminEmail;
  private String adminNotes;
  private String adminProvider;
  private HostForwardEwpApiBootstrapProperties forwardEwpApi;
  private List<HostCoveredHeiBootstrapProperties> coveredHeis;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAdminEmail() {
    return adminEmail;
  }

  public void setAdminEmail(String adminEmail) {
    this.adminEmail = adminEmail;
  }

  public String getAdminNotes() {
    return adminNotes;
  }

  public void setAdminNotes(String adminNotes) {
    this.adminNotes = adminNotes;
  }

  public String getAdminProvider() {
    return adminProvider;
  }

  public void setAdminProvider(String adminProvider) {
    this.adminProvider = adminProvider;
  }

  public HostForwardEwpApiBootstrapProperties getForwardEwpApi() {
    return forwardEwpApi;
  }

  public void setForwardEwpApi(HostForwardEwpApiBootstrapProperties forwardEwpApi) {
    this.forwardEwpApi = forwardEwpApi;
  }

  public List<HostCoveredHeiBootstrapProperties> getCoveredHeis() {
    return coveredHeis;
  }

  public void setCoveredHeis(List<HostCoveredHeiBootstrapProperties> coveredHeis) {
    this.coveredHeis = coveredHeis;
  }
}
