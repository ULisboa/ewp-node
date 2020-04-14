package pt.ulisboa.ewp.node.client.ewp.operation.result;

import java.io.Serializable;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.error.AbstractErrorEwpOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public abstract class AbstractEwpOperationResult implements Serializable {

  private final EwpOperationResultType resultType;
  private final EwpRequest request;
  private final EwpResponse response;
  private final EwpAuthenticationResult responseAuthenticationResult;

  protected AbstractEwpOperationResult(EwpOperationResultType resultType, Builder<?> builder) {
    this.resultType = resultType;
    this.request = builder.request;
    this.response = builder.response;
    this.responseAuthenticationResult = builder.responseAuthenticationResult;
  }

  public EwpOperationResultType getResultType() {
    return resultType;
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

  public boolean isSuccess() {
    return EwpOperationResultType.SUCCESS.equals(resultType);
  }

  /**
   * Returns a summary of the operation result. This should be succinct, as it may be used for
   * logging purposes.
   *
   * @return A succinct summary of the operation result.
   */
  public abstract String getSummary();

  @SuppressWarnings("unchecked")
  public <T> EwpSuccessOperationResult<T> asSuccess(Class<T> responseBodyType) {
    assert resultType == EwpOperationResultType.SUCCESS;
    return (EwpSuccessOperationResult<T>) this;
  }

  public boolean isError() {
    return EwpOperationResultType.ERROR.equals(resultType);
  }

  public AbstractErrorEwpOperationResult asError() {
    assert resultType == EwpOperationResultType.ERROR;
    return (AbstractErrorEwpOperationResult) this;
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

  public enum EwpOperationResultType {
    SUCCESS,
    ERROR
  }
}
