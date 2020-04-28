package pt.ulisboa.ewp.node.api.admin.security.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.api.common.dto.ApiResponseBodyDTO;
import pt.ulisboa.ewp.node.api.common.security.handler.AccessDeniedResponseHandler;
import pt.ulisboa.ewp.node.service.messaging.MessageService;

@Component
public class AdminApiAccessDeniedResponseHandler extends AccessDeniedResponseHandler {

  @Override
  protected Object getAccessDeniedResponseBody(
      HttpServletRequest request, AccessDeniedException accessDeniedException) {
    return new ApiResponseBodyDTO<>(
        MessageService.getInstance().consumeMessages(), new ApiOperationStatusDTO(false));
  }
}
