package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.AbstractEwpOperationResult;

public abstract class AbstractErrorEwpOperationResult extends AbstractEwpOperationResult {

  private final EwpOperationResultErrorType errorType;

  protected AbstractErrorEwpOperationResult(
      EwpOperationResultErrorType errorType, Builder<?> builder) {
    super(AbstractEwpOperationResult.EwpOperationResultType.ERROR, builder);
    this.errorType = errorType;
  }

  public EwpOperationResultErrorType getErrorType() {
    return errorType;
  }

  public abstract AbstractEwpClientErrorException toClientException();

  public EwpProcessorErrorOperationResult asProcessorError() {
    assert errorType == EwpOperationResultErrorType.PROCESSOR_ERROR;
    return (EwpProcessorErrorOperationResult) this;
  }

  public EwpResponseAuthenticationErrorOperationResult asResponseAuthenticationError() {
    assert errorType == EwpOperationResultErrorType.RESPONSE_AUTHENTICATION_ERROR;
    return (EwpResponseAuthenticationErrorOperationResult) this;
  }

  public EwpErrorResponseOperationResult asErrorResponse() {
    assert errorType == EwpOperationResultErrorType.ERROR_RESPONSE;
    return (EwpErrorResponseOperationResult) this;
  }

  public EwpUnknownErrorResponseOperationResult asUnknownErrorResponse() {
    assert errorType == EwpOperationResultErrorType.UNKNOWN_ERROR_RESPONSE;
    return (EwpUnknownErrorResponseOperationResult) this;
  }

  public abstract static class Builder<T extends Builder<T>>
      extends AbstractEwpOperationResult.Builder<T> {}

  public enum EwpOperationResultErrorType {
    PROCESSOR_ERROR,
    RESPONSE_AUTHENTICATION_ERROR,
    ERROR_RESPONSE,
    UNKNOWN_ERROR_RESPONSE
  }
}
