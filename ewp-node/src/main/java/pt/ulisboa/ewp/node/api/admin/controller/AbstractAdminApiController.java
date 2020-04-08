package pt.ulisboa.ewp.node.api.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

public abstract class AbstractAdminApiController {

  @Autowired protected MessageResolver messages;

  protected ResponseEntity<ApiOperationStatusDTO> getCreateEntityApiResponse(boolean success) {
    return getCrudOperationApiResponse(
        success, "crud.create.success.message", "crud.create.failure.message");
  }

  protected ResponseEntity<ApiOperationStatusDTO> getUpdateEntityApiResponse(boolean success) {
    return getCrudOperationApiResponse(
        success, "crud.update.success.message", "crud.update.failure.message");
  }

  protected ResponseEntity<ApiOperationStatusDTO> getDeleteEntityApiResponse(boolean success) {
    return getCrudOperationApiResponse(
        success, "crud.delete.success.message", "crud.delete.failure.message");
  }

  protected ResponseEntity<ApiOperationStatusDTO> getOperationApiResponse(
      boolean success, String failureMessageCode) {
    return getCrudOperationApiResponse(success, "info.operation.success", failureMessageCode);
  }

  private ResponseEntity<ApiOperationStatusDTO> getCrudOperationApiResponse(
      boolean success, String successMessageCode, String failureMessageCode) {
    if (success) {
      MessageService.getInstance().add(messages.get(successMessageCode));
      return ResponseEntity.ok(new ApiOperationStatusDTO(true));
    } else {
      MessageService.getInstance().add(Severity.ERROR, messages.get(failureMessageCode));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiOperationStatusDTO(false));
    }
  }
}
