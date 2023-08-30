package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpCommunicationLogDetailDto;

public class EwpHttpCommunicationLogDetailDto extends HttpCommunicationLogDetailDto {

  private String authenticationMethod;

  public String getAuthenticationMethod() {
    return authenticationMethod;
  }

  public void setAuthenticationMethod(String authenticationMethod) {
    this.authenticationMethod = authenticationMethod;
  }
}
