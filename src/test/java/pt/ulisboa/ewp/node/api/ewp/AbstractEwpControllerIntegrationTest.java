package pt.ulisboa.ewp.node.api.ewp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import pt.ulisboa.ewp.node.api.AbstractResourceIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.filter.EwpApiCommunicationLoggerFilter;
import pt.ulisboa.ewp.node.api.ewp.filter.EwpApiRequestAndResponseWrapperFilter;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.XmlValidator;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

public abstract class AbstractEwpControllerIntegrationTest extends AbstractResourceIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private XmlValidator xmlValidator;

  @Autowired
  private EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(
                new EwpApiRequestAndResponseWrapperFilter(),
                new EwpApiCommunicationLoggerFilter(ewpHttpCommunicationLogService))
            .apply(springSecurity())
            .build();
  }

  protected void assertBadRequest(
      RegistryClient registryClient,
      HttpMethod method,
      String uri,
      HttpParams params,
      String errorMessage)
      throws Exception {
    assertErrorRequest(registryClient, method, uri, params, HttpStatus.BAD_REQUEST,
        new Condition<>(errorResponse -> errorResponse.getDeveloperMessage().getValue()
            .equalsIgnoreCase(errorMessage), "valid developer message"));
  }

  protected void assertNotFound(
      RegistryClient registryClient,
      HttpMethod method,
      String uri,
      HttpParams params,
      String errorMessage)
      throws Exception {
    assertErrorRequest(registryClient, method, uri, params, HttpStatus.NOT_FOUND,
        new Condition<>(errorResponse -> errorResponse.getDeveloperMessage().getValue()
            .equalsIgnoreCase(errorMessage), "valid developer message"));
  }

  protected void assertErrorRequest(
      RegistryClient registryClient,
      HttpMethod method,
      String uri,
      HttpParams params,
      HttpStatus expectedHttpStatus,
      Condition<ErrorResponseV1> errorResponseCondition)
      throws Exception {
    MvcResult mvcResult =
        executeRequest(registryClient, method, uri, params)
            .andExpect(status().is(expectedHttpStatus.value()))
            .andReturn();

    ErrorResponseV1 errorResponseV1 = (ErrorResponseV1) mvcResult.getModelAndView().getModel()
        .values().iterator().next();
    assertThat(errorResponseV1).satisfies(errorResponseCondition);

    validateXml(mvcResult.getResponse().getContentAsString(), "xsd/ewp/common-types.xsd");
  }

  protected ResultActions executeRequest(
      RegistryClient registryClient, HttpMethod method, String uri, HttpParams params)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(method, uri).with(httpParamsProcessor(params));

    return executeRequest(registryClient, requestBuilder);
  }

  protected ResultActions executeRequest(
      RegistryClient registryClient, HttpMethod method, String uri, Serializable body)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(method, uri).with(serializableBodyProcessor(body));

    return executeRequest(registryClient, requestBuilder);
  }

  protected ResultActions executeRequest(
      RegistryClient registryClient, MockHttpServletRequestBuilder requestBuilder)
      throws Exception {
    RequestPostProcessor securityRequestProcessor =
        httpSignatureRequestProcessor(registryClient,
            Collections.singletonList(UUID.randomUUID().toString()));

    return executeRequest(registryClient, requestBuilder, securityRequestProcessor);
  }

  protected ResultActions executeRequest(
      RegistryClient registryClient, MockHttpServletRequestBuilder requestBuilder,
      RequestPostProcessor requestPostProcessor)
      throws Exception {
    requestBuilder = requestBuilder.with(requestPostProcessor);

    return this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print());
  }

  protected void assertErrorRequest(
      MockHttpServletRequestBuilder requestBuilder,
      RequestPostProcessor securityMethodProcessor,
      HttpStatus expectedHttpStatus,
      String errorMessage)
      throws Exception {
    requestBuilder
        .with(securityMethodProcessor)
        .header("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()))
        .header("X-Request-ID", UUID.randomUUID().toString())
        .accept(MediaType.APPLICATION_XML);

    MvcResult mvcResult =
        this.mockMvc
            .perform(requestBuilder)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(expectedHttpStatus.value()))
            .andExpect(xpath("/error-response/developer-message").string(errorMessage))
            .andReturn();

    validateXml(mvcResult.getResponse().getContentAsString(), "xsd/ewp/common-types.xsd");
  }

  protected RequestPostProcessor serializableBodyProcessor(Serializable serializable) {
    return request -> {
      request.setCharacterEncoding("UTF-8");
      request.setContentType(MediaType.TEXT_XML_VALUE);
      request.setContent(XmlUtils.marshall(serializable).getBytes(StandardCharsets.UTF_8));
      return request;
    };
  }

  protected RequestPostProcessor httpParamsProcessor(HttpParams params) {
    return request -> {
      switch (request.getMethod()) {
        case "POST":
        case "PUT":
          request.setContentType(MediaType.APPLICATION_FORM_URLENCODED.toString());
          break;
      }
      params
          .asMap()
          .forEach((key, value) -> request.addParameter(key, value.toArray(new String[0])));
      return request;
    };
  }

  /**
   * Modifies an HTTP request to include valid HTTP Signature headers. Also, configures required
   * mocks in order for HTTP Signature authentication to succeed.
   */
  protected RequestPostProcessor httpSignatureRequestProcessor(
      RegistryClient registryClient, Collection<String> clientCoveredHeiIds) {
    return request -> {
      HttpHeaders headers = HttpUtils.toExtendedHttpHeaders(request);

      request.addHeader(HttpHeaders.HOST, "localhost");
      request.addHeader(HttpHeaders.DATE,
          DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
      request.addHeader(HttpConstants.HEADER_X_REQUEST_ID, UUID.randomUUID().toString());

      addRequestDigestHeader(request);

      String keyId = UUID.randomUUID().toString();
      Algorithm algorithm = Algorithm.RSA_SHA256;

      TreeSet<String> signatureHeadersSet = new TreeSet<>(headers.keySet());
      signatureHeadersSet.add(HttpSignatureUtils.HEADER_REQUEST_TARGET);
      signatureHeadersSet.add(HttpHeaders.HOST);
      signatureHeadersSet.add(HttpHeaders.DATE);
      signatureHeadersSet.add(HttpConstants.HEADER_X_REQUEST_ID);
      signatureHeadersSet.add(HttpConstants.HEADER_DIGEST);
      String[] signatureHeaders = signatureHeadersSet.toArray(new String[0]);

      KeyPair keyPair = createKeyPair();

      Signature signature = generateSignatureForRequest(request, keyPair, keyId, algorithm,
          signatureHeaders);
      request.addHeader("Authorization", signature);

      request.addHeader(
          HttpConstants.HEADER_ACCEPT_SIGNATURE, Algorithm.RSA_SHA256.getPortableName());
      request.addHeader(HttpConstants.HEADER_WANT_DIGEST, "SHA-256");

      doReturn(keyPair.getPublic())
          .when(registryClient)
          .findClientRsaPublicKey(keyId);
      doReturn(clientCoveredHeiIds)
          .when(registryClient)
          .getHeisCoveredByClientKey((RSAPublicKey) keyPair.getPublic());

      return request;
    };
  }

  protected RequestPostProcessor httpSignatureRequestProcessor(
      RegistryClient registryClient,
      Collection<String> clientCoveredHeiIds,
      String keyId,
      Algorithm algorithm,
      String signature,
      boolean validKeyId) {
    return request -> {
      HttpHeaders headers = HttpUtils.toExtendedHttpHeaders(request);

      TreeSet<String> signatureHeadersSet = new TreeSet<>(headers.keySet());
      signatureHeadersSet.add(HttpSignatureUtils.HEADER_REQUEST_TARGET);
      String[] signatureHeaders = signatureHeadersSet.toArray(new String[0]);

      return signRequestWithHttpSignature(
          request,
          registryClient,
          clientCoveredHeiIds,
          keyId,
          algorithm,
          signatureHeaders,
          signature,
          validKeyId);
    };
  }

  protected RequestPostProcessor httpSignatureRequestProcessor(
      RegistryClient registryClient,
      Collection<String> clientCoveredHeiIds,
      String keyId,
      Algorithm algorithm,
      String[] signatureHeaders,
      String signature,
      boolean validKeyId) {
    return request ->
        signRequestWithHttpSignature(
            request,
            registryClient,
            clientCoveredHeiIds,
            keyId,
            algorithm,
            signatureHeaders,
            signature,
            validKeyId);
  }

  private MockHttpServletRequest signRequestWithHttpSignature(
      MockHttpServletRequest request,
      RegistryClient registryClient,
      Collection<String> clientCoveredHeiIds,
      String keyId,
      Algorithm algorithm,
      String[] signatureHeaders,
      String signature,
      boolean validKeyId) {
    addRequestDigestHeader(request);

    request.addHeader(
        HttpConstants.HEADER_ACCEPT_SIGNATURE, Algorithm.RSA_SHA256.getPortableName());
    request.addHeader(HttpConstants.HEADER_WANT_DIGEST, "SHA-256");

    HttpHeaders headers = HttpUtils.toExtendedHttpHeaders(request);

    KeyPair keyPair = createKeyPair();

    addRequestAuthorizationSignatureHeader(
        keyId, algorithm, signature, signatureHeaders, request, headers, keyPair);

    doReturn(validKeyId ? keyPair.getPublic() : null)
        .when(registryClient)
        .findClientRsaPublicKey(keyId);
    doReturn(clientCoveredHeiIds)
        .when(registryClient)
        .getHeisCoveredByClientKey((RSAPublicKey) keyPair.getPublic());

    return request;
  }

  private void addRequestAuthorizationSignatureHeader(
      String keyId,
      Algorithm algorithm,
      String signatureParameter,
      String[] signatureHeaders,
      MockHttpServletRequest request,
      HttpHeaders headers,
      KeyPair keyPair) {
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(keyId, null, algorithm, null, null, List.of(signatureHeaders)));
    try {
      String queryParams = request.getQueryString();
      String requestString = request.getPathInfo() + (queryParams == null ? "" : "?" + queryParams);
      Signature signature;
      if (signatureParameter == null) {
        signature =
            signer.sign(
                request.getMethod().toLowerCase(), requestString, HttpUtils.toHeadersMap(headers));
      } else {
        signature =
            new Signature(
                keyId, null, algorithm, null, signatureParameter, List.of(signatureHeaders));
      }

      request.addHeader("Authorization", signature);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Signature generateSignatureForRequest(HttpServletRequest request, KeyPair keyPair,
      String keyId,
      Algorithm algorithm, String[] signatureHeaders) {
    Signer signer =
        new Signer(keyPair.getPrivate(), new Signature(keyId, null, algorithm, null, null, List.of(signatureHeaders)));

    String queryParams = request.getQueryString();
    String requestString = request.getPathInfo() + (queryParams == null ? "" : "?" + queryParams);
    try {
      return signer.sign(
          request.getMethod().toLowerCase(), requestString,
          HttpUtils.toHeadersMap(HttpUtils.toExtendedHttpHeaders(request)));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void addRequestDigestHeader(MockHttpServletRequest request) {
    try {
      request.addHeader("Digest", "SHA-256=" + getDigest(request));
    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  private String getDigest(@NotNull MockHttpServletRequest httpServletRequest)
      throws IOException, NoSuchAlgorithmException {
    String body = "";
    switch (httpServletRequest.getMethod()) {
      case "POST":
      case "PUT":
        if (httpServletRequest.getContentAsByteArray() != null
            && httpServletRequest.getCharacterEncoding() != null) {
          body = httpServletRequest.getContentAsString();
        } else {
          body = httpServletRequest.getParameterMap().entrySet().stream()
              .flatMap(e -> Arrays.stream(e.getValue())
                  .map(
                      v -> UriUtils.encodeQuery(e.getKey(), "UTF-8") + "=" + UriUtils.encodeQuery(v,
                          "UTF-8"))
              )
              .collect(Collectors.joining("&"));
        }
        break;
    }
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    byte[] digest = messageDigest.digest(body.getBytes(StandardCharsets.UTF_8));
    return new String(Base64.getEncoder().encode(digest));
  }

  private KeyPair createKeyPair() {
    KeyPair keyPair = null;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(1024);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return keyPair;
  }
}
