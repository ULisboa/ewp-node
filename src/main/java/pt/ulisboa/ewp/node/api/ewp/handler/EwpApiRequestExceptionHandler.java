package pt.ulisboa.ewp.node.api.ewp.handler;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.xml.MarshallingView;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;

@Component
public class EwpApiRequestExceptionHandler extends DefaultHandlerExceptionResolver {

  @Autowired private Jaxb2Marshaller jaxb2Marshaller;

  public EwpApiRequestExceptionHandler() {
    setOrder(Integer.MIN_VALUE);
  }

  @Override
  protected ModelAndView doResolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    if (request.getRequestURI().startsWith(EwpApiConstants.API_BASE_URI)) {
      HttpServletResponseWithCustomSendError responseWithCustomSendError =
          new HttpServletResponseWithCustomSendError(response);
      ModelAndView modelAndView =
          super.doResolveException(request, responseWithCustomSendError, handler, ex);
      if (modelAndView == null) {
        if (ex instanceof EwpBadRequestException) {
          modelAndView = handleEwpBadRequestException((EwpBadRequestException) ex, response);
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

  private ModelAndView handleEwpBadRequestException(
      EwpBadRequestException exception, HttpServletResponse response) {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
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
    marshallingView.setMarshaller(jaxb2Marshaller);
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
