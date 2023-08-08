package pt.ulisboa.ewp.node.config.security;

public class SecurityClientTlsProperties {

  private String headerName;
  private SecurityClientTlsEncoding encoding;

  public String getHeaderName() {
    return headerName;
  }

  public void setHeaderName(String headerName) {
    this.headerName = headerName;
  }

  public SecurityClientTlsEncoding getEncoding() {
    return encoding;
  }

  public void setEncoding(SecurityClientTlsEncoding encoding) {
    this.encoding = encoding;
  }
}
