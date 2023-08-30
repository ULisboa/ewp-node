package pt.ulisboa.ewp.node.domain.dto.communication.log.http;

import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogDetailDto;

public class HttpCommunicationLogDetailDto extends CommunicationLogDetailDto {

  private HttpRequestLogDto request;
  private HttpResponseLogDto response;

  public HttpRequestLogDto getRequest() {
    return request;
  }

  public void setRequest(HttpRequestLogDto request) {
    this.request = request;
  }

  public HttpResponseLogDto getResponse() {
    return response;
  }

  public void setResponse(HttpResponseLogDto response) {
    this.response = response;
  }
}
