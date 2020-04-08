package pt.ulisboa.ewp.node.api.admin.wrapper;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.common.dto.ApiResponseBodyDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;

@ControllerAdvice(annotations = {AdminApiWithResponseBodyWrapper.class})
public class AdminApiResponseBodyWrapper implements ResponseBodyAdvice<Object> {

  @Autowired private Logger log;

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    log.debug("Responding with provider " + SecurityContextHolder.getContext());

    ApiResponseBodyDTO<Object> responseBody = new ApiResponseBodyDTO<>();
    responseBody.setMessages(MessageService.getInstance().consumeMessages());
    responseBody.setData(body);
    return responseBody;
  }
}
