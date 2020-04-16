package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;

public abstract class AbstractForwardEwpApiController {

  protected <T> ResponseEntity<ForwardEwpApiResponse> createResponseEntityFromOperationResult(
      EwpSuccessOperationResult<T> successOperationResult) {
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        successOperationResult.getResponse(),
        successOperationResult.getResponseAuthenticationResult(),
        successOperationResult.getResponseBody());
  }
}
