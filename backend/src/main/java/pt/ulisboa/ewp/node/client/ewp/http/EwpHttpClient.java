package pt.ulisboa.ewp.node.client.ewp.http;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.Collection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientConflictException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.http.interceptor.EwpHttpClientInterceptor;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestSerializableBody;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.exception.ewp.EwpClientAuthenticationFailedException;
import pt.ulisboa.ewp.node.exception.ewp.EwpServerAuthenticationFailedException;
import pt.ulisboa.ewp.node.exception.ewp.EwpServerException;
import pt.ulisboa.ewp.node.service.ewp.security.signer.request.RequestAuthenticationSigner;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.response.ResponseAuthenticationVerifier;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.SecurityUtils;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EwpHttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(EwpHttpClient.class);

  private final Collection<EwpHttpClientInterceptor> interceptors;
  private final KeyStoreService keystoreService;
  private final RequestAuthenticationSigner requestSigner;
  private final ResponseAuthenticationVerifier responseVerifier;
  private final Jaxb2Marshaller jaxb2Marshaller;

  public EwpHttpClient(Collection<EwpHttpClientInterceptor> interceptors,
      KeyStoreService keystoreService,
      RequestAuthenticationSigner requestSigner,
      ResponseAuthenticationVerifier responseVerifier,
      Jaxb2Marshaller jaxb2Marshaller) {
    this.interceptors = interceptors;
    this.keystoreService = keystoreService;
    this.requestSigner = requestSigner;
    this.responseVerifier = responseVerifier;
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
  public <T extends Serializable> EwpSuccessOperationResult<T> execute(EwpRequest request,
      Class<T> responseBodyType) throws EwpClientErrorException {
    this.interceptors.forEach(i -> i.onPreparing(request));
    try {
      EwpSuccessOperationResult<T> operationResult = executeInternal(request, responseBodyType);
      this.interceptors.forEach(i -> i.onSuccess(request, operationResult));
      return operationResult;

    } catch (EwpClientErrorException e) {
      this.interceptors.forEach(i -> i.onError(request, e));
      throw e;
    }
  }

  protected <T extends Serializable> EwpSuccessOperationResult<T> executeInternal(
      EwpRequest request,
      Class<T> expectedResponseBodyType) throws EwpClientErrorException {
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

      responseAuthenticationResult = responseVerifier.verifyAgainstMethod(request, response);
      if (!responseAuthenticationResult.isValid()) {
        throw new EwpServerAuthenticationFailedException(request, response,
            responseAuthenticationResult);
      }

      return resolveResponseToSuccessOperationStatus(request, expectedResponseBodyType, response,
          responseAuthenticationResult);

    } catch (EwpServerAuthenticationFailedException | XmlCannotUnmarshallToTypeException e) {
      LOGGER.error("Invalid server's response", e);
      throw new EwpClientInvalidResponseException(request, response, responseAuthenticationResult,
          e);

    } catch (EwpClientErrorException e) {
      throw e;

    } catch (Exception e) {
      LOGGER.error("Failed to execute request", e);
      throw new EwpClientProcessorException(request, response, e);
    }
  }

  private <T extends Serializable> EwpSuccessOperationResult<T> resolveResponseToSuccessOperationStatus(
      EwpRequest request, Class<T> expectedResponseBodyType, EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult)
      throws XmlCannotUnmarshallToTypeException, EwpClientErrorException {

    if (response.isSuccess()) {
      if (response.isXmlResponse()) {
        // NOTE: deserialize as XML
        T responseBody =
            XmlUtils.unmarshall(jaxb2Marshaller, response.getRawBody(), expectedResponseBodyType);
        return new EwpSuccessOperationResult.Builder<T>()
            .request(request)
            .response(response)
            .responseAuthenticationResult(responseAuthenticationResult)
            .responseBody(responseBody)
            .build();

      } else if (byte[].class.isAssignableFrom(expectedResponseBodyType)) {
        // NOTE: deserialize as byte array
        return new EwpSuccessOperationResult.Builder<T>()
            .request(request)
            .response(response)
            .responseAuthenticationResult(responseAuthenticationResult)
            .responseBody((T) response.getRawBody())
            .build();

      } else {
        throw new EwpClientProcessorException(
            request,
            response,
            new IllegalArgumentException(
                "Failed to process response's body of type: " + response.getMediaType()));
      }

    } else {
      throw createClientErrorExceptionFromResponse(request, response, responseAuthenticationResult);
    }
  }

  private EwpClientErrorException createClientErrorExceptionFromResponse(EwpRequest request,
      EwpResponse response, EwpAuthenticationResult responseAuthenticationResult)
      throws XmlCannotUnmarshallToTypeException {

    if (response.isClientError()) {
      ErrorResponseV1 errorResponse = XmlUtils.unmarshall(jaxb2Marshaller, response.getRawBody(),
          ErrorResponseV1.class);
      if (HttpStatus.UNAUTHORIZED.equals(response.getStatus())
          || HttpStatus.FORBIDDEN.equals(response.getStatus())) {
        return new EwpClientProcessorException(
            request,
            response,
            new EwpClientAuthenticationFailedException(
                request, response, errorResponse.getDeveloperMessage().getValue()));

      } else if (HttpStatus.CONFLICT.equals(response.getStatus())) {
        return new EwpClientConflictException(
            request, response, responseAuthenticationResult, errorResponse);

      } else {
        return new EwpClientErrorResponseException(
            request, response, responseAuthenticationResult, errorResponse);
      }

    } else if (response.isServerError()) {
      return new EwpClientInvalidResponseException(request, response, responseAuthenticationResult,
          new EwpServerException(request, response));
    }

    return new EwpClientProcessorException(request, response,
        new IllegalStateException("Unknown response status code: " + response.getStatus()));
  }

  private Client getClient()
      throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
    DecodedKeystore decodedKeystore = keystoreService.getDecodedKeyStoreFromStorage();
    SSLContext sslContext = SecurityUtils.createSecurityContext(decodedKeystore.getKeyStore(), null,
        decodedKeystore.getKeyStorePassword());
    return ClientBuilder.newBuilder().sslContext(sslContext)
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
        return requestBuilder.build(request.getMethod().name(),
            createBodyEntity(request.getBody()));

      default:
        throw new IllegalArgumentException("Unsupported method: " + request.getMethod().name());
    }
  }

  private void setRequestHeaders(Invocation.Builder requestBuilder, EwpRequest request) {
    HttpUtils.toHeadersMap(request.getHeaders()).forEach(requestBuilder::header);
  }

  private Entity<?> createBodyEntity(EwpRequestBody body) {
    if (body instanceof EwpRequestFormDataUrlEncodedBody) {
      return createFormDataEntity(((EwpRequestFormDataUrlEncodedBody) body));
    } else if (body instanceof EwpRequestSerializableBody) {
      return createSerializableEntity((EwpRequestSerializableBody) body);
    } else {
      throw new IllegalArgumentException(
          "Unknown request body type: " + body.getClass().getSimpleName());
    }
  }

  private Entity<String> createFormDataEntity(EwpRequestFormDataUrlEncodedBody body) {
    String formDataAsString = HttpUtils.serializeFormDataUrlEncoded(body.getFormData().asMap());
    Variant variant =
        Variant.mediaTypes(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE)
            .build()
            .get(0);
    return Entity.entity(formDataAsString, variant);
  }

  private Entity<Serializable> createSerializableEntity(EwpRequestSerializableBody body) {
    Variant variant = Variant.mediaTypes(javax.ws.rs.core.MediaType.TEXT_XML_TYPE).build().get(0);
    return Entity.entity(body.serialize(), variant);
  }
}
