package pt.ulisboa.ewp.node.exception.ewp;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;

public class EwpServerException extends Exception {

  private EwpRequest request;
  private EwpResponse response;

  public EwpServerException(EwpRequest request, EwpResponse response) {
    this.request = request;
    this.response = response;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  @Override
  public String getMessage() {
    return "Server exception: " + response.getStatus().getReasonPhrase();
  }
}
