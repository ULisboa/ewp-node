package pt.ulisboa.ewp.node.api.ewp.handler;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.xml.MarshallingView;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpNotFoundException;
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
      this.communicationLogService.registerExceptionInCurrentCommunication(ex);

      HttpServletResponseWithCustomSendError responseWithCustomSendError =
          new HttpServletResponseWithCustomSendError(response);

      ModelAndView modelAndView =
          super.doResolveException(request, responseWithCustomSendError, handler, ex);

      if (ex instanceof HttpMessageNotReadableException) {
        modelAndView =
            handleHttpMessageNotReadableExceptionException(
                (HttpMessageNotReadableException) ex, response);

      } else if (ex instanceof MissingServletRequestParameterException) {
        modelAndView =
            handleMissingServletRequestParameterException(
                (MissingServletRequestParameterException) ex, response);

      } else if (modelAndView == null) {
        if (ex instanceof EwpBadRequestException) {
          modelAndView = handleEwpBadRequestException((EwpBadRequestException) ex, response);
        } else if (ex instanceof EwpNotFoundException) {
          modelAndView = handleEwpNotFoundException((EwpNotFoundException) ex, response);
        } else {
          modelAndView = handleUnknownException(ex, response);
        }

      } else {
        fillModelAndViewWithException(modelAndView, null, getDeveloperMessageFromException(ex));
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

  private ModelAndView handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    return createModelAndViewFromException(
        new IllegalArgumentException(
            "Required parameter '" + exception.getParameterName() + "' is missing"));
  }

  private ModelAndView handleEwpBadRequestException(
      EwpBadRequestException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    return createModelAndView(exception.getUserMessage(), exception.getDeveloperMessage());
  }

  private ModelAndView handleEwpNotFoundException(
      EwpNotFoundException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.NOT_FOUND.value());
    return createModelAndViewFromException(exception);
  }

  private ModelAndView handleUnknownException(Exception exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    return createModelAndView(null, getDeveloperMessageFromException(exception));
  }

  private ModelAndView createModelAndViewFromException(Exception exception) {
    String developerMessage;
    if (exception.getMessage() != null) {
      developerMessage = exception.getMessage();
    } else {
      developerMessage = "Unknown error";
    }

    return createModelAndView(null, developerMessage);
  }

  private String getDeveloperMessageFromException(Exception exception) {
    if (exception instanceof HttpRequestMethodNotSupportedException) {
      return exception.getMessage();
    } else {
      return "Unknown error";
    }
  }

  private ModelAndView createModelAndView(String userMessage, String developerMessage) {
    ModelAndView modelAndView = new ModelAndView();
    fillModelAndViewWithException(modelAndView, userMessage, developerMessage);
    return modelAndView;
  }

  private void fillModelAndViewWithException(
      ModelAndView modelAndView, String userMessage, String developerMessage) {
    MarshallingView marshallingView = new MarshallingView();
    marshallingView.setMarshaller(jaxb2HttpMessageConverter);
    modelAndView.setView(marshallingView);

    modelAndView.addObject(createErrorResponse(userMessage, developerMessage));
  }

  private ErrorResponseV1 createErrorResponse(String userMessage, String developerMessage) {
    return EwpApiUtils.createErrorResponseWithUserAndDeveloperMessage(
        userMessage, developerMessage);
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
