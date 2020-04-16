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

  public EwpInternalErrorOperationResult asProcessorError() {
    assert errorType == EwpOperationResultErrorType.INTERNAL_ERROR;
    return (EwpInternalErrorOperationResult) this;
  }

  public EwpErrorResponseOperationResult asErrorResponse() {
    assert errorType == EwpOperationResultErrorType.ERROR_RESPONSE;
    return (EwpErrorResponseOperationResult) this;
  }

  public EwpInvalidResponseOperationResult asInvalidServerResponse() {
    assert errorType == EwpOperationResultErrorType.INVALID_RESPONSE;
    return (EwpInvalidResponseOperationResult) this;
  }

  public abstract static class Builder<T extends Builder<T>>
      extends AbstractEwpOperationResult.Builder<T> {}

  public enum EwpOperationResultErrorType {
    INTERNAL_ERROR,
    RESPONSE_AUTHENTICATION_ERROR,
    ERROR_RESPONSE,
    INVALID_RESPONSE
  }
}
