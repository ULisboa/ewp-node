package pt.ulisboa.ewp.node.api.host.forward.ewp.utils;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.AuthenticationAlgorithm;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse.AuthenticationResult;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse.Body;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OtherMessages;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.ProcessingError;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.RequestError;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.MessageSeverity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ResultType;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

public class ForwardEwpApiResponseUtils {

  private ForwardEwpApiResponseUtils() {}

  public static <T> ResponseEntity<ForwardEwpApiResponse> toSuccessResponseEntity(
      EwpResponse ewpResponse,
      EwpAuthenticationResult responseAuthenticationResult,
      T responseBody) {
    ForwardEwpApiResponse response =
        toSuccessForwardEwpApiResponse(ewpResponse, responseAuthenticationResult, responseBody);

    return ResponseEntity.status(HttpStatus.resolve(ewpResponse.getStatusCode()))
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toRequestErrorResponseEntity(
      Collection<String> errorMessages) {
    ForwardEwpApiResponse responseBody = createResponseWithMessages(ResultType.REQUEST_ERROR);

    RequestError requestError = new RequestError();
    requestError.getErrorMessage().addAll(errorMessages);
    responseBody.setRequestError(requestError);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_XML)
        .body(responseBody);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toProcessorErrorResponseEntity(
      String errorMessage) {
    ForwardEwpApiResponse response = createResponseWithMessages(ResultType.PROCESSOR_ERROR);

    ProcessingError processingError = new ProcessingError();
    processingError.setErrorMessage(errorMessage);
    response.setProcessingError(processingError);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toResponseAuthenticationErrorResponseEntity(
      EwpResponse ewpResponse, EwpAuthenticationResult responseAuthenticationResult) {
    ForwardEwpApiResponse response =
        createResponseWithMessages(ResultType.RESPONSE_AUTHENTICATION_ERROR);

    OriginalResponse originalResponse = new OriginalResponse();
    originalResponse.setStatusCode(BigInteger.valueOf(ewpResponse.getStatusCode()));
    originalResponse.setAuthenticationResult(toAuthenticationResult(responseAuthenticationResult));
    response.setOriginalResponse(originalResponse);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toErrorResponseEntity(
      EwpResponse ewpResponse,
      EwpAuthenticationResult responseAuthenticationResult,
      ErrorResponse errorResponse) {
    ForwardEwpApiResponse response = createResponseWithMessages(ResultType.ERROR_RESPONSE);

    OriginalResponse originalResponse = new OriginalResponse();
    originalResponse.setStatusCode(BigInteger.valueOf(ewpResponse.getStatusCode()));
    originalResponse.setAuthenticationResult(toAuthenticationResult(responseAuthenticationResult));
    ForwardEwpApiResponse.OriginalResponse.Body responseBody = new Body();
    responseBody.setAny(errorResponse);
    originalResponse.setBody(responseBody);
    response.setOriginalResponse(originalResponse);

    return ResponseEntity.status(HttpStatus.resolve(ewpResponse.getStatusCode()))
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toUnknownErrorResponseEntity(
      EwpResponse ewpResponse, EwpAuthenticationResult responseAuthenticationResult, String error) {
    ForwardEwpApiResponse response = createResponseWithMessages(ResultType.UNKNOWN_ERROR_RESPONSE);

    OriginalResponse originalResponse = new OriginalResponse();
    originalResponse.setStatusCode(BigInteger.valueOf(ewpResponse.getStatusCode()));
    originalResponse.setAuthenticationResult(toAuthenticationResult(responseAuthenticationResult));
    originalResponse.setUnknownRawBody(error);
    response.setOriginalResponse(originalResponse);

    return ResponseEntity.status(HttpStatus.resolve(ewpResponse.getStatusCode()))
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static <T> ForwardEwpApiResponse toSuccessForwardEwpApiResponse(
      EwpResponse ewpResponse,
      EwpAuthenticationResult responseAuthenticationResult,
      T responseBody) {
    ForwardEwpApiResponse response = createResponseWithMessages(ResultType.SUCCESS);

    OriginalResponse originalResponse = new OriginalResponse();
    originalResponse.setStatusCode(BigInteger.valueOf(ewpResponse.getStatusCode()));
    originalResponse.setAuthenticationResult(
        ForwardEwpApiResponseUtils.toAuthenticationResult(responseAuthenticationResult));
    Body body = new Body();
    body.setAny(responseBody);
    originalResponse.setBody(body);

    response.setOriginalResponse(originalResponse);

    return response;
  }

  public static AuthenticationResult toAuthenticationResult(
      EwpAuthenticationResult securityVerificationResult) {
    AuthenticationResult result = new AuthenticationResult();
    result.setAlgorithm(toAuthenticationAlgorithm(securityVerificationResult.getMethod()));
    result.setValid(securityVerificationResult.isValid());
    result.setErrorMessage(securityVerificationResult.getErrorMessage());
    return result;
  }

  public static AuthenticationAlgorithm toAuthenticationAlgorithm(
      EwpAuthenticationMethod authenticationMethod) {
    switch (authenticationMethod) {
      case HTTP_SIGNATURE:
        return AuthenticationAlgorithm.HTTP_SIGNATURE;

      case TLS:
        return AuthenticationAlgorithm.TLS;

      case ANONYMOUS:
        return AuthenticationAlgorithm.ANONYMOUS;

      default:
        throw new IllegalArgumentException(
            "Unknown authentication method: " + authenticationMethod.name());
    }
  }

  public static ForwardEwpApiResponse createResponseWithMessages(ResultType resultType) {
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(resultType);
    decorateResponseWithOtherMessages(response);
    return response;
  }

  public static void decorateResponseWithOtherMessages(ForwardEwpApiResponse response) {
    Collection<Message> messagesToAdd = MessageService.getInstance().consumeMessages();
    OtherMessages messages = new OtherMessages();
    messagesToAdd.forEach(messageToAdd -> messages.getMessage().add(toMessage(messageToAdd)));
    response.setOtherMessages(messages);
  }

  private static OtherMessages.Message toMessage(Message message) {
    OtherMessages.Message result = new OtherMessages.Message();
    result.setContext(message.getContext());
    result.setSeverity(toMessageSeverity(message.getSeverity()));
    result.setSummary(message.getSummary());
    return result;
  }

  private static MessageSeverity toMessageSeverity(Severity severity) {
    switch (severity) {
      case FATAL:
        return MessageSeverity.FATAL;
      case ERROR:
        return MessageSeverity.ERROR;
      case WARN:
        return MessageSeverity.WARN;
      case INFO:
        return MessageSeverity.INFO;
      default:
        throw new IllegalArgumentException("Unknown severity: " + severity.name());
    }
  }
}
