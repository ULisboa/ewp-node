package pt.ulisboa.ewp.node.client.ewp.http.interceptor;

import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;

/**
 * Interceptor that allows to receive events before/after requests to EWP.
 */
public interface EwpHttpClientInterceptor {

  /**
   * Called before sending a request.
   *
   * @param request EWP request
   */
  void onPreparing(EwpRequest request);

  /**
   * Called upon a success response.
   *
   * @param request                EWP request
   * @param successOperationResult Result of the successful operation
   */
  void onSuccess(EwpRequest request, EwpSuccessOperationResult<?> successOperationResult);

  /**
   * Called upon an error response.
   *
   * @param request EWP request
   * @param e       Error exception
   */
  void onError(EwpRequest request, EwpClientErrorException e);

}
