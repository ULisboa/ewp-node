package pt.ulisboa.ewp.node.client.ewp.operation.result;

import java.io.Serializable;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public abstract class EwpOperationResult implements Serializable {

  private final EwpRequest request;
  private final EwpResponse response;
  private final EwpAuthenticationResult responseAuthenticationResult;

  protected EwpOperationResult(Builder<?> builder) {
    this.request = builder.request;
    this.response = builder.response;
    this.responseAuthenticationResult = builder.responseAuthenticationResult;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public EwpAuthenticationResult getResponseAuthenticationResult() {
    return responseAuthenticationResult;
  }

  public abstract static class Builder<T extends Builder<T>> {

    private EwpRequest request;
    private EwpResponse response;
    private EwpAuthenticationResult responseAuthenticationResult;

    public EwpRequest request() {
      return request;
    }

    public T request(EwpRequest request) {
      this.request = request;
      return getThis();
    }

    public EwpResponse response() {
      return response;
    }

    public T response(EwpResponse response) {
      this.response = response;
      return getThis();
    }

    public EwpAuthenticationResult responseAuthenticationResult() {
      return responseAuthenticationResult;
    }

    public T responseAuthenticationResult(EwpAuthenticationResult securityVerificationResult) {
      this.responseAuthenticationResult = securityVerificationResult;
      return getThis();
    }

    public abstract T getThis();
  }
}
