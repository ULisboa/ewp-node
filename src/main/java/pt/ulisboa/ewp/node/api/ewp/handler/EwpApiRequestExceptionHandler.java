package pt.ulisboa.ewp.node.api.ewp.handler;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.xml.MarshallingView;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpNotFoundException;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.CommunicationLogService;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@Component
public class EwpApiRequestExceptionHandler extends DefaultHandlerExceptionResolver {

  private final CommunicationLogService communicationLogService;
  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;

  public EwpApiRequestExceptionHandler(
      CommunicationLogService communicationLogService,
      Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.communicationLogService = communicationLogService;
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
    setOrder(Integer.MIN_VALUE);
  }

  @Override
  protected ModelAndView doResolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    if (request.getRequestURI().startsWith(EwpApiConstants.API_BASE_URI)) {
      this.communicationLogService.registerException(
          CommunicationContextHolder.getContext().getCurrentCommunicationLog(), ex);

      HttpServletResponseWithCustomSendError responseWithCustomSendError =
          new HttpServletResponseWithCustomSendError(response);

      ModelAndView modelAndView =
          super.doResolveException(request, responseWithCustomSendError, handler, ex);

      if (ex instanceof HttpMessageNotReadableException) {
        modelAndView =
            handleHttpMessageNotReadableExceptionException(
                (HttpMessageNotReadableException) ex, response);

      } else if (modelAndView == null) {
        if (ex instanceof EwpBadRequestException) {
          modelAndView = handleEwpBadRequestException((EwpBadRequestException) ex, response);
        } else if (ex instanceof EwpNotFoundException) {
          modelAndView = handleEwpNotFoundException((EwpNotFoundException) ex, response);
        } else {
          modelAndView = handleUnknownException(ex, response);
        }

      } else {
        fillModelAndViewWithException(modelAndView, ex);
      }
      return modelAndView;
    }
    return null;
  }

  private ModelAndView handleHttpMessageNotReadableExceptionException(
      HttpMessageNotReadableException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    return createModelAndViewFromException(
        new IllegalArgumentException("Required request body is missing"));
  }

  private ModelAndView handleEwpBadRequestException(
      EwpBadRequestException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    return createModelAndViewFromException(exception);
  }

  private ModelAndView handleEwpNotFoundException(
      EwpNotFoundException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.NOT_FOUND.value());
    return createModelAndViewFromException(exception);
  }

  private ModelAndView handleUnknownException(Exception exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    return createModelAndViewFromException(exception);
  }

  private ModelAndView createModelAndViewFromException(Exception ex) {
    ModelAndView modelAndView = new ModelAndView();
    fillModelAndViewWithException(modelAndView, ex);
    return modelAndView;
  }

  private void fillModelAndViewWithException(ModelAndView modelAndView, Exception ex) {
    MarshallingView marshallingView = new MarshallingView();
    marshallingView.setMarshaller(jaxb2HttpMessageConverter);
    modelAndView.setView(marshallingView);

    modelAndView.addObject(createErrorResponse(ex));
  }

  private ErrorResponseV1 createErrorResponse(Exception exception) {
    logger.error("Handling unknown exception", exception);

    String developerMessage;
    if (exception.getMessage() != null) {
      developerMessage = exception.getMessage();
    } else {
      developerMessage = "Unknown internal server error";
    }

    return EwpApiUtils.createErrorResponseWithDeveloperMessage(developerMessage);
  }

  /**
   * Custom HttpServletResponse where sendError methods only set the status code, ignoring the
   * message. This way it is possible to use original DefaultHandlerExceptionResolver and at the
   * same time pass a specific ModelAndView response.
   */
  private static class HttpServletResponseWithCustomSendError extends HttpServletResponseWrapper {

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public HttpServletResponseWithCustomSendError(HttpServletResponse response) {
      super(response);
    }

    @Override
    public void sendError(int sc) {
      super.setStatus(sc);
    }

    @Override
    public void sendError(int sc, String msg) {
      super.setStatus(sc);
      // NOTE: message is ignored
    }
  }
}
