package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.time.ZonedDateTime;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.exception.ewp.EwpClientAuthenticationFailedException;
import pt.ulisboa.ewp.node.exception.ewp.EwpServerAuthenticationFailedException;
import pt.ulisboa.ewp.node.exception.ewp.EwpServerException;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.service.security.ewp.signer.request.RequestAuthenticationSigner;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.response.ResponseAuthenticationVerifier;
import pt.ulisboa.ewp.node.utils.SecurityUtils;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EwpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(EwpClient.class);

  private final KeyStoreService keystoreService;
  private final RequestAuthenticationSigner requestSigner;
  private final ResponseAuthenticationVerifier responseVerifier;
  private final EwpHttpCommunicationLogService ewpHttpCommunicationLogService;
  private final Jaxb2Marshaller jaxb2Marshaller;

  public EwpClient(KeyStoreService keystoreService,
      RequestAuthenticationSigner requestSigner,
      ResponseAuthenticationVerifier responseVerifier,
      EwpHttpCommunicationLogService ewpHttpCommunicationLogService,
      Jaxb2Marshaller jaxb2Marshaller) {
    this.keystoreService = keystoreService;
    this.requestSigner = requestSigner;
    this.responseVerifier = responseVerifier;
    this.ewpHttpCommunicationLogService = ewpHttpCommunicationLogService;
    this.jaxb2Marshaller = jaxb2Marshaller;
  }

  /**
   * Sends a request to the target API, resolving its response, returning it only upon success. If a
   * request fails or the response obtained indicates an error then a corresponding exception is
   * thrown.
   *
   * @param request          Request to send
   * @param responseBodyType Expected response's body class type upon success.
   * @return The result of a successful operation.
   * @throws EwpClientErrorException The request failed for some reason.
   */
  public <T extends Serializable> EwpSuccessOperationResult<T> executeAndLog(
      EwpRequest request, Class<T> responseBodyType) throws EwpClientErrorException {
    ZonedDateTime startProcessingDateTime = ZonedDateTime.now();
    try {
      EwpSuccessOperationResult<T> operationResult = execute(request, responseBodyType);
      ewpHttpCommunicationLogService.logCommunicationToEwpNode(
          operationResult, startProcessingDateTime, ZonedDateTime.now());
      return operationResult;

    } catch (EwpClientErrorException e) {
      ewpHttpCommunicationLogService
          .logCommunicationToEwpNode(e, startProcessingDateTime, ZonedDateTime.now());
      throw e;
    }
  }

  protected <T extends Serializable> EwpSuccessOperationResult<T> execute(
      EwpRequest request, Class<T> expectedResponseBodyType) throws EwpClientErrorException {
    EwpResponse response = null;
    EwpAuthenticationResult responseAuthenticationResult = null;
    try {
      Client client = getClient();

      requestSigner.sign(request);

      WebTarget target = client.target(request.getUrl());
      target.property("http.autoredirect", true);

      Invocation invocation = buildRequest(request, target);

      LOGGER.info("Sending EWP request to: {}", request.getUrl());

      response = EwpResponse.create(invocation.invoke());

      responseAuthenticationResult =
          responseVerifier.verifyAgainstMethod(request, response);
      if (!responseAuthenticationResult.isValid()) {
        throw new EwpServerAuthenticationFailedException(
            request, response, responseAuthenticationResult);
      }

      return resolveResponseToSuccessOperationStatus(
          request, expectedResponseBodyType, response, responseAuthenticationResult);

    } catch (EwpServerAuthenticationFailedException | XmlCannotUnmarshallToTypeException e) {
      LOGGER.error("Invalid server's response", e);
      throw new EwpClientInvalidResponseException(request, response,
          responseAuthenticationResult, e);

    } catch (EwpClientErrorException e) {
      throw e;

    } catch (Exception e) {
      LOGGER.error("Failed to execute request", e);
      throw new EwpClientProcessorException(request, response, e);
    }
  }

  private <T extends Serializable> EwpSuccessOperationResult<T> resolveResponseToSuccessOperationStatus(
      EwpRequest request,
      Class<T> expectedResponseBodyType,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult)
      throws XmlCannotUnmarshallToTypeException, EwpClientErrorException {

    if (response.isSuccess()) {
      T responseBody = XmlUtils
          .unmarshall(jaxb2Marshaller, response.getRawBody(), expectedResponseBodyType);
      return new EwpSuccessOperationResult.Builder<T>()
          .request(request)
          .response(response)
          .responseAuthenticationResult(responseAuthenticationResult)
          .responseBody(responseBody)
          .build();

    } else {
      throw createClientErrorExceptionFromResponse(request, response, responseAuthenticationResult);
    }
  }

  private EwpClientErrorException createClientErrorExceptionFromResponse(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult)
      throws XmlCannotUnmarshallToTypeException {

    if (response.isClientError()) {
      ErrorResponseV1 errorResponse =
          XmlUtils.unmarshall(jaxb2Marshaller, response.getRawBody(), ErrorResponseV1.class);
      if (HttpStatus.BAD_REQUEST.equals(response.getStatus())) {
        return new EwpClientErrorResponseException(request, response, responseAuthenticationResult,
            errorResponse);

      } else {
        return new EwpClientProcessorException(request, response,
            new EwpClientAuthenticationFailedException(
                request, response, errorResponse.getDeveloperMessage().getValue()));
      }

    } else if (response.isServerError()) {
      return new EwpClientInvalidResponseException(request, response, responseAuthenticationResult,
          new EwpServerException(request, response));
    }

    return new EwpClientProcessorException(request, response, new IllegalStateException(
        "Unknown response status code: " + response.getStatus()));
  }

  private Client getClient()
      throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException,
      UnrecoverableKeyException, KeyManagementException {
    DecodedKeystore decodedKeystore = keystoreService.getDecodedKeyStoreFromStorage();
    SSLContext sslContext =
        SecurityUtils.createSecurityContext(
            decodedKeystore.getKeyStore(), null, decodedKeystore.getKeyStorePassword());
    return ClientBuilder.newBuilder()
        .sslContext(sslContext)
        .hostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost()))
        .build();
  }

  private Invocation buildRequest(EwpRequest request, WebTarget target) {
    Invocation.Builder requestBuilder = target.request();
    setRequestHeaders(requestBuilder, request);

    switch (request.getMethod()) {
      case GET:
      case DELETE:
        return requestBuilder.build(request.getMethod().name());

      case POST:
      case PUT:
        return requestBuilder
            .build(request.getMethod().name(), createFormDataEntity(request.getBodyParams()));

      default:
        throw new IllegalArgumentException("Unsupported method: " + request.getMethod().name());
    }
  }

  private void setRequestHeaders(Invocation.Builder requestBuilder, EwpRequest request) {
    HttpUtils.toHeadersMap(request.getHeaders()).forEach(requestBuilder::header);
  }

  private Entity<String> createFormDataEntity(HttpParams formData) {
    String formDataAsString = HttpUtils.serializeFormData(formData.asMap());
    return Entity.entity(formDataAsString, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
  }
}
