package pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http;

import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;

public abstract class HttpCommunicationFormParameterFilterDto
    extends FilterDto<HttpCommunicationLog> {

  private final String parameter;

  public HttpCommunicationFormParameterFilterDto(String parameter) {
    this.parameter = parameter;
  }

  public String getParameter() {
    return parameter;
  }
}
