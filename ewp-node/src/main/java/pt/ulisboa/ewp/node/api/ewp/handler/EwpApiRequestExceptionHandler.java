package pt.ulisboa.ewp.node.api.ewp.handler;

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
import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import eu.erasmuswithoutpaper.api.architecture.MultilineString;

@Component
public class EwpApiRequestExceptionHandler extends DefaultHandlerExceptionResolver {

  @Autowired private Jaxb2Marshaller jaxb2Marshaller;

  public EwpApiRequestExceptionHandler() {
    setOrder(Integer.MIN_VALUE);
  }

  @Override
  protected ModelAndView doResolveException(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    if (request.getRequestURI().startsWith(EwpApiConstants.EWP_API_BASE_URI)) {
      HttpServletResponseWithCustomSendError responseWithCustomSendError =
          new HttpServletResponseWithCustomSendError(response);
      super.doResolveException(request, responseWithCustomSendError, handler, ex);

      if (responseWithCustomSendError.getStatus() == HttpStatus.OK.value()) {
        responseWithCustomSendError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      }

      return createModelAndViewFromException(ex);
    }
    return null;
  }

  private ModelAndView createModelAndViewFromException(Exception ex) {
    ModelAndView modelAndView = new ModelAndView();

    MarshallingView marshallingView = new MarshallingView();
    marshallingView.setMarshaller(jaxb2Marshaller);
    modelAndView.setView(marshallingView);

    modelAndView.addObject(createErrorResponse(ex));
    return modelAndView;
  }

  private ErrorResponse createErrorResponse(Exception exception) {
    logger.error("Handling unknown exception", exception);

    ErrorResponse errorResponse = new ErrorResponse();
    MultilineString message = new MultilineString();

    if (exception.getMessage() != null) {
      message.setValue(exception.getMessage());
    } else {
      message.setValue("Unknown internal server error");
    }

    errorResponse.setDeveloperMessage(message);

    return errorResponse;
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
