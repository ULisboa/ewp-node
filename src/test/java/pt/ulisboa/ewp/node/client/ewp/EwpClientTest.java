package pt.ulisboa.ewp.node.client.ewp;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringWithOptionalLangV1;
import eu.erasmuswithoutpaper.api.echo.v2.ResponseV2;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.Marshaller;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.ewp.security.signer.request.RequestAuthenticationSigner;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.response.ResponseAuthenticationVerifier;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreGenerator;

@ExtendWith(MockServerExtension.class)
class EwpClientTest extends AbstractTest {

  private ClientAndServer mockServer;

  private KeyStoreService keyStoreService;
  private RequestAuthenticationSigner requestSigner;
  private ResponseAuthenticationVerifier responseVerifier;

  private EwpClient client;

  @BeforeEach
  public void beforeEach(ClientAndServer mockServer) {
    this.mockServer = mockServer;
    this.mockServer.reset();

    this.keyStoreService = mock(KeyStoreService.class);
    this.requestSigner = mock(RequestAuthenticationSigner.class);
    this.responseVerifier = mock(ResponseAuthenticationVerifier.class);
    EwpHttpCommunicationLogService ewpHttpCommunicationLogService = mock(
        EwpHttpCommunicationLogService.class);
    this.client = new EwpClient(keyStoreService, requestSigner, responseVerifier,
        ewpHttpCommunicationLogService, createJaxb2Marshaller());
  }

  @Test
  void testExecute_ValidGetRequestAndSuccessResponse_ReturnSuccessOptionResult()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    HttpParams requestParams = new HttpParams();
    requestParams.param("echo", "test_echo");
    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE)
        .queryParams(requestParams);

    ResponseV2 expectedResponse = new ResponseV2();
    expectedResponse.getHeiId().add("test_heiid");
    expectedResponse.getEcho().add("test_echo");

    mockServer.when(request().withMethod("GET").withPath("/test")
        .withQueryStringParameter("echo", "test_echo"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_XML)
            .withBody(XmlUtils.marshall(createJaxb2Marshaller(), expectedResponse)));

    EwpSuccessOperationResult<ResponseV2> result = client
        .executeAndLog(request, ResponseV2.class);
    assertThat(result, notNullValue());
    assertThat(result.getResponseBody().getHeiId(),
        equalTo(Collections.singletonList("test_heiid")));
    assertThat(result.getResponseBody().getEcho(), equalTo(Collections.singletonList("test_echo")));
  }

  @Test
  void testExecute_ValidPostRequestAndSuccessResponse_ReturnSuccessOptionResult()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    HttpParams requestParams = new HttpParams();
    requestParams.param("echo", "test_echo");
    EwpRequest request = new EwpRequest(HttpMethod.POST,
        "http://localhost:" + mockServer.getPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE)
        .body(new EwpRequestFormDataUrlEncodedBody(requestParams));

    ResponseV2 expectedResponse = new ResponseV2();
    expectedResponse.getHeiId().add("test_heiid");
    expectedResponse.getEcho().add("test_echo");

    mockServer.when(request().withMethod("POST").withPath("/test")
        .withBody("echo=test_echo"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_XML)
            .withBody(XmlUtils.marshall(createJaxb2Marshaller(), expectedResponse)));

    EwpSuccessOperationResult<ResponseV2> result = client
        .executeAndLog(request, ResponseV2.class);
    assertThat(result, notNullValue());
    assertThat(result.getResponseBody().getHeiId(),
        equalTo(Collections.singletonList("test_heiid")));
    assertThat(result.getResponseBody().getEcho(), equalTo(Collections.singletonList("test_echo")));
  }

  @Test
  void testExecute_ValidRequestAndResponseNotSatisfyingSecurityRequirements_ThrowException()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createInvalid(EwpAuthenticationMethod.HTTP_SIGNATURE, "test");
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getLocalPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE);

    ResponseV2 expectedResponse = new ResponseV2();
    expectedResponse.getHeiId().add("test_heiid");
    expectedResponse.getEcho().add("test_echo");

    mockServer.when(request().withMethod("GET").withPath("/test"))
        .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_XML)
            .withBody(XmlUtils.marshall(createJaxb2Marshaller(), expectedResponse)));

    assertThatThrownBy(() -> client.executeAndLog(request, ResponseV2.class))
        .isInstanceOf(EwpClientInvalidResponseException.class)
        .hasMessage(
            "Server returned an invalid response: Server authentication failed for authentication method "
                + authenticationResult.getMethod() + ": " + authenticationResult.getErrorMessage());
  }

  @Test
  void testExecute_BadRequestAndErrorResponse_ThrowException()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getLocalPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE);

    ErrorResponseV1 errorResponse = new ErrorResponseV1();
    MultilineStringV1 developerMessage = new MultilineStringV1();
    developerMessage.setValue("developer message test");
    errorResponse.setDeveloperMessage(developerMessage);
    MultilineStringWithOptionalLangV1 userMessage = new MultilineStringWithOptionalLangV1();
    userMessage.setValue("user message test");
    errorResponse.getUserMessage().add(userMessage);

    mockServer.when(request().withMethod("GET").withPath("/test"))
        .respond(response().withStatusCode(400).withContentType(MediaType.APPLICATION_XML)
            .withBody(XmlUtils.marshall(createJaxb2Marshaller(), errorResponse)));

    assertThatThrownBy(() -> client.executeAndLog(request, ResponseV2.class))
        .isInstanceOf(EwpClientErrorResponseException.class)
        .hasMessage(
            "Error response obtained: user message test [developer message: developer message test]");
  }

  @Test
  void testExecute_UnauthorizedRequestAndErrorResponse_ThrowException()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getLocalPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE);

    ErrorResponseV1 errorResponse = new ErrorResponseV1();
    MultilineStringV1 developerMessage = new MultilineStringV1();
    developerMessage.setValue("developer message");
    errorResponse.setDeveloperMessage(developerMessage);
    MultilineStringWithOptionalLangV1 userMessage = new MultilineStringWithOptionalLangV1();
    userMessage.setValue("user message");
    errorResponse.getUserMessage().add(userMessage);

    mockServer.when(request().withMethod("GET").withPath("/test"))
        .respond(response().withStatusCode(401).withContentType(MediaType.APPLICATION_XML)
            .withBody(XmlUtils.marshall(createJaxb2Marshaller(), errorResponse)));

    assertThatThrownBy(() -> client.executeAndLog(request, ResponseV2.class))
        .isInstanceOf(EwpClientProcessorException.class)
        .hasMessage(
            "Processor error: Client authentication failed for authentication method HTTP_SIGNATURE: developer message");
  }

  @Test
  void testExecute_ValidRequestAndServerInternalError_ThrowException()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getLocalPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE);

    mockServer.when(request().withMethod("GET").withPath("/test"))
        .respond(response().withStatusCode(500).withContentType(MediaType.APPLICATION_XML)
            .withBody(""));

    assertThatThrownBy(() -> client.executeAndLog(request, ResponseV2.class))
        .isInstanceOf(EwpClientInvalidResponseException.class)
        .hasMessage("Server returned an invalid response: Server exception: Internal Server Error");
  }

  @Test
  void testExecute_ValidRequestAndServerUnknownStatusCode_ThrowException()
      throws EwpClientErrorException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException, NoSuchProviderException {

    // Mock dependencies
    doReturn(KeyStoreGenerator.generate("", "1")).when(keyStoreService)
        .getDecodedKeyStoreFromStorage();

    EwpAuthenticationResult authenticationResult = EwpAuthenticationResult
        .createValid(EwpAuthenticationMethod.HTTP_SIGNATURE);
    doReturn(authenticationResult).when(responseVerifier)
        .verifyAgainstMethod(ArgumentMatchers.any(), ArgumentMatchers.any());

    EwpRequest request = new EwpRequest(HttpMethod.GET,
        "http://localhost:" + mockServer.getLocalPort() + "/test")
        .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE);

    mockServer.when(request().withMethod("GET").withPath("/test"))
        .respond(response().withStatusCode(600).withContentType(MediaType.APPLICATION_XML)
            .withBody(""));

    assertThatThrownBy(() -> client.executeAndLog(request, ResponseV2.class))
        .isInstanceOf(EwpClientProcessorException.class)
        .hasMessage("Processor error: Unknown response status code: null");
  }

  private Jaxb2Marshaller createJaxb2Marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("eu.erasmuswithoutpaper.api", "pt.ulisboa.ewp.node");
    Map<String, Object> jaxbProperties = new HashMap<>();
    jaxbProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setMarshallerProperties(jaxbProperties);
    return marshaller;
  }
}
