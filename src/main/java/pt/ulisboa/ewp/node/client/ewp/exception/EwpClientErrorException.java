package pt.ulisboa.ewp.node.client.ewp.exception;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;

public abstract class EwpClientErrorException extends Exception {

  private final EwpRequest request;
  private final EwpResponse response;

  protected EwpClientErrorException(
      EwpRequest request, EwpResponse response) {
    this.request = request;
    this.response = response;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public String getDetailedMessage() {
    return getMessage();
  }
}
