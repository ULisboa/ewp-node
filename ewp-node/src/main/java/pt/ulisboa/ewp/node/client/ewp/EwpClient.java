package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.AbstractEwpOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpErrorResponseOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpProcessorErrorOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpResponseAuthenticationErrorOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpUnknownErrorResponseOperationResult;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.exception.ewp.EwpResponseBodyCannotBeCastToException;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.utils.ewp.EwpResponseUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreUtil;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EwpClient {

  @Autowired private Logger log;

  @Autowired private KeyStoreService keystoreService;

  @Autowired private HttpSignatureService httpSignatureService;

  @Autowired private EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  /**
   * Sends a request to the target API, resolving its response, returning it only upon success. If a
   * request fails or the response obtained indicates an error then a corresponding exception is
   * thrown.
   *
   * @param request Request to send
   * @param responseBodyType Expected response's body class type upon success.
   * @return
   * @throws EwpClientProcessorException Request/Response processing failed for some reason.
   * @throws EwpClientErrorResponseException Target API returned an error response (see {@see
   *     eu.erasmuswithoutpaper.api.architecture.ErrorResponse}).
   * @throws EwpClientUnknownErrorResponseException Target API returned an unknown error response.
   * @throws EwpClientResponseAuthenticationFailedException The obtained response failed
   *     authentication
   */
  @SuppressWarnings("unchecked")
  public <T> EwpSuccessOperationResult<T> executeWithLoggingExpectingSuccess(
      EwpRequest request, Class<T> responseBodyType)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    AbstractEwpOperationResult operationResult = executeWithLogging(request, responseBodyType);
    return getSuccessOperationResult(responseBodyType, operationResult);
  }

  private <T> EwpSuccessOperationResult<T> getSuccessOperationResult(
      Class<T> responseBodyType, AbstractEwpOperationResult operationResult)
      throws EwpClientProcessorException, EwpClientResponseAuthenticationFailedException,
          EwpClientErrorResponseException, EwpClientUnknownErrorResponseException {
    switch (operationResult.getResultType()) {
      case PROCESSOR_ERROR:
        EwpProcessorErrorOperationResult processorErrorOperationResult =
            operationResult.asProcessorError();
        throw new EwpClientProcessorException(processorErrorOperationResult.getException());

      case SUCCESS:
        return operationResult.asSuccess(responseBodyType);

      case RESPONSE_AUTHENTICATION_ERROR:
        EwpResponseAuthenticationErrorOperationResult responseAuthenticationErrorOperationResult =
            operationResult.asResponseAuthenticationError();
        throw new EwpClientResponseAuthenticationFailedException(
            responseAuthenticationErrorOperationResult.getRequest(),
            responseAuthenticationErrorOperationResult.getResponse(),
            responseAuthenticationErrorOperationResult.getResponseAuthenticationResult());

      case ERROR_RESPONSE:
        EwpErrorResponseOperationResult errorResponseOperationResult =
            operationResult.asErrorResponse();
        throw new EwpClientErrorResponseException(
            errorResponseOperationResult.getRequest(),
            errorResponseOperationResult.getResponse(),
            errorResponseOperationResult.getResponseAuthenticationResult(),
            errorResponseOperationResult.getErrorResponse());

      case UNKNOWN_ERROR_RESPONSE:
        EwpUnknownErrorResponseOperationResult unknownErrorResponseOperationResult =
            operationResult.asUnknownErrorResponse();
        throw new EwpClientUnknownErrorResponseException(
            unknownErrorResponseOperationResult.getRequest(),
            unknownErrorResponseOperationResult.getResponse(),
            unknownErrorResponseOperationResult.getResponseAuthenticationResult(),
            unknownErrorResponseOperationResult.getError());

      default:
        throw new IllegalStateException(
            "Unknown result type: " + operationResult.getResultType().name());
    }
  }

  private <T> AbstractEwpOperationResult executeWithLogging(
      EwpRequest request, Class<T> responseBodyType) {
    ZonedDateTime startProcessingDateTime = ZonedDateTime.now();
    AbstractEwpOperationResult operationResult = execute(request, responseBodyType);
    ZonedDateTime endProcessingDateTime = ZonedDateTime.now();
    ewpHttpCommunicationLogService.logCommunicationToEwpNode(
        operationResult,
        startProcessingDateTime,
        endProcessingDateTime,
        getOperationObservations(operationResult));

    return operationResult;
  }

  protected <T> AbstractEwpOperationResult execute(
      EwpRequest request, Class<T> expectedResponseBodyType) {
    EwpResponse ewpResponse = null;
    EwpAuthenticationResult responseAuthenticationResult = null;
    try {
      Client client;
      try {
        client = getClient();
      } catch (NoSuchAlgorithmException
          | KeyManagementException
          | UnrecoverableKeyException
          | KeyStoreException
          | NoSuchProviderException e) {
        log.error("Failed to initialize EWP client", e);
        return new EwpProcessorErrorOperationResult.Builder().request(request).exception(e).build();
      }

      WebTarget target = client.target(request.getUrl());
      target.property("http.autoredirect", true);

      signRequest(request, target);
      Invocation invocation = buildRequest(request, target);

      log.info("Sending EWP request to: {}", request.getUrl());

      Response response = invocation.invoke();

      sanitizeResponse(response);

      EwpResponse.Builder responseBuilder = new EwpResponse.Builder();
      responseBuilder.statusCode(response.getStatus());
      responseBuilder.mediaType(response.getMediaType().toString());

      response
          .getHeaders()
          .forEach(
              (headerName, headerValues) ->
                  responseBuilder.header(
                      headerName,
                      headerValues.stream().map(String::valueOf).collect(Collectors.toList())));

      if (response.hasEntity()) {
        response.bufferEntity();

        responseBuilder.rawBody(response.readEntity(String.class));
      }

      ewpResponse = responseBuilder.build();

      responseAuthenticationResult = authenticateResponse(request, ewpResponse);
      if (!responseAuthenticationResult.isValid()) {
        return new EwpResponseAuthenticationErrorOperationResult.Builder()
            .request(request)
            .response(ewpResponse)
            .responseAuthenticationResult(responseAuthenticationResult)
            .build();
      }

      if (!ewpResponse.isOk()) {
        try {
          ErrorResponse errorResponse =
              EwpResponseUtils.readResponseBody(ewpResponse, ErrorResponse.class);
          return new EwpErrorResponseOperationResult.Builder()
              .request(request)
              .response(ewpResponse)
              .responseAuthenticationResult(responseAuthenticationResult)
              .errorResponse(errorResponse)
              .build();

        } catch (EwpResponseBodyCannotBeCastToException e) {
          log.error("Failed to read error response's body", e);
          return new EwpUnknownErrorResponseOperationResult.Builder()
              .request(request)
              .response(ewpResponse)
              .responseAuthenticationResult(responseAuthenticationResult)
              .error(ewpResponse.getRawBody())
              .build();
        }
      }

      T responseBody;
      try {
        responseBody = EwpResponseUtils.readResponseBody(ewpResponse, expectedResponseBodyType);
      } catch (EwpResponseBodyCannotBeCastToException e) {
        return new EwpProcessorErrorOperationResult.Builder()
            .request(request)
            .response(ewpResponse)
            .responseAuthenticationResult(responseAuthenticationResult)
            .exception(e)
            .build();
      }

      return new EwpSuccessOperationResult.Builder<T>()
          .request(request)
          .response(ewpResponse)
          .responseAuthenticationResult(responseAuthenticationResult)
          .responseBody(responseBody)
          .build();

    } catch (Exception e) {
      log.error("Failed to execute request", e);
      return new EwpProcessorErrorOperationResult.Builder()
          .request(request)
          .response(ewpResponse)
          .responseAuthenticationResult(responseAuthenticationResult)
          .exception(e)
          .build();
    }
  }

  private String getOperationObservations(AbstractEwpOperationResult result) {
    switch (result.getResultType()) {
      case PROCESSOR_ERROR:
        return toString(result.asProcessorError().getException()).substring(0, 1000);

      case SUCCESS:
      case ERROR_RESPONSE:
      case UNKNOWN_ERROR_RESPONSE:
        return "";

      case RESPONSE_AUTHENTICATION_ERROR:
        return result
            .asResponseAuthenticationError()
            .getResponseAuthenticationResult()
            .getErrorMessage();

      default:
        throw new IllegalStateException("Unknown result type: " + result.getResultType().name());
    }
  }

  private String toString(Exception exception) {
    StringWriter result = new StringWriter();
    exception.printStackTrace(new PrintWriter(result));
    return result.toString();
  }

  private EwpAuthenticationResult authenticateResponse(EwpRequest request, EwpResponse response) {
    if (request.getAuthenticationMethod().equals(EwpAuthenticationMethod.HTTP_SIGNATURE)) {
      return httpSignatureService.verifyHttpSignatureResponse(
          request.getMethod().name(),
          request.getUrlWithoutQueryParams(),
          response.getHeaders(),
          response.getRawBody(),
          request.getId());

    } else if (request.getAuthenticationMethod().equals(EwpAuthenticationMethod.TLS)) {
      try {
        boolean valid =
            new URL(request.getUrl()).getProtocol().equalsIgnoreCase(HttpConstants.PROTOCOL_HTTPS);
        if (valid) {
          return EwpAuthenticationResult.createValid(EwpAuthenticationMethod.TLS);
        } else {
          return EwpAuthenticationResult.createInvalid(
              EwpAuthenticationMethod.TLS,
              "Request URL is not using " + HttpConstants.PROTOCOL_HTTPS + " protocol");
        }
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException(e);
      }

    } else if (request.getAuthenticationMethod().equals(EwpAuthenticationMethod.ANONYMOUS)) {
      return EwpAuthenticationResult.createValid(EwpAuthenticationMethod.ANONYMOUS);

    } else {
      throw new IllegalStateException(
          "Communication to EWP servers must use one authentication method");
    }
  }

  private void sanitizeResponse(Response response) {
    // NOTE: sanitize possibly wrong XML content type header
    // namely, some servers respond with a Content-Type like "xml;charset=ISO-8859-1" which is not
    // considered corrected for Jersey since it contains only the subtype and not the type
    String contentType = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
    if (contentType.matches("[ \t]*xml[ \t]*;[ \t]*charset=.*")) {
      String correctContentType = contentType.replace("xml", "application/xml");
      response.getMetadata().putSingle(HttpHeaders.CONTENT_TYPE, correctContentType);
    }
  }

  private Client getClient()
      throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException,
          UnrecoverableKeyException, KeyManagementException {
    DecodedKeystore decodedKeystore = keystoreService.getDecodedKeyStoreFromStorage();
    SSLContext sslContext =
        createSecurityContext(
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
        return buildGetRequest(requestBuilder);

      case POST:
        return buildPostRequest(requestBuilder, request);

      default:
        throw new IllegalArgumentException("Unsupported method: " + request.getMethod().name());
    }
  }

  private void setRequestHeaders(Invocation.Builder requestBuilder, EwpRequest request) {
    HttpUtils.toHeadersMap(request.getHeaders()).forEach(requestBuilder::header);
  }

  private Invocation buildGetRequest(Invocation.Builder requestBuilder) {
    return requestBuilder.buildGet();
  }

  private Invocation buildPostRequest(Invocation.Builder requestBuilder, EwpRequest request) {
    String formData = HttpUtils.serializeFormData(request.getBodyParams());
    Entity<String> entity = Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    return requestBuilder.buildPost(entity);
  }

  private void signRequest(EwpRequest request, WebTarget target) {
    if (request.getAuthenticationMethod().equals(EwpAuthenticationMethod.HTTP_SIGNATURE)) {
      httpSignatureService.signRequest(
          request.getMethod().name(),
          target.getUri(),
          HttpUtils.serializeFormData(request.getBodyParams()),
          request.getId(),
          request::header);
    }
  }

  private static SSLContext createSecurityContext(
      KeyStore keyStore, KeyStore trustStore, String password)
      throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException,
          KeyStoreException, KeyManagementException {
    KeyManager[] keyManagers = null;
    if (!KeyStoreUtil.isSelfIssued(
        keyStore, (X509Certificate) keyStore.getCertificate(keyStore.aliases().nextElement()))) {
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
      keyManagerFactory.init(keyStore, password.toCharArray());
      keyManagers = keyManagerFactory.getKeyManagers();
    }

    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);

    SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
    context.init(
        keyManagers, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
    return context;
  }
}
