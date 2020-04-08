package pt.ulisboa.ewp.node.api.host.forward.ewp.utils;

import java.math.BigInteger;
import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.AuthenticationAlgorithm;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ResultType;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.ProcessingError;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.RequestError;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse.AuthenticationResult;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.OriginalResponse.Body;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;
import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;

public class ForwardEwpApiResponseUtils {

  private ForwardEwpApiResponseUtils() {}

  public static <T> ResponseEntity<ForwardEwpApiResponse> toRequestErrorResponseEntity(
      Collection<String> errorMessages) {
    ForwardEwpApiResponse responseBody = new ForwardEwpApiResponse();
    responseBody.setResultType(ResultType.REQUEST_ERROR);

    RequestError requestError = new RequestError();
    requestError.getErrorMessage().addAll(errorMessages);
    responseBody.setRequestError(requestError);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_XML)
        .body(responseBody);
  }

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

  public static ResponseEntity<ForwardEwpApiResponse> toProcessorErrorResponseEntity(
      String errorMessage) {
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(ResultType.PROCESSOR_ERROR);

    ProcessingError processingError = new ProcessingError();
    processingError.setErrorMessage(errorMessage);
    response.setProcessingError(processingError);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toResponseAuthenticationErrorResponseEntity(
      EwpResponse ewpResponse, EwpAuthenticationResult responseAuthenticationResult) {
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(ResultType.RESPONSE_AUTHENTICATION_ERROR);

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
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(ResultType.ERROR_RESPONSE);

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
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(ResultType.UNKNOWN_ERROR_RESPONSE);

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
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    response.setResultType(ResultType.SUCCESS);

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
}
