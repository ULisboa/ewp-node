package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.io.Serializable;
import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;

public abstract class AbstractForwardEwpApiController {

  protected <T extends Serializable>
      ResponseEntity<ForwardEwpApiResponseWithData<T>> createResponseEntityFromOperationResult(
          EwpSuccessOperationResult<T> successOperationResult) {
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        successOperationResult.getResponse(), successOperationResult.getResponseBody());
  }
}
