package pt.ulisboa.ewp.node.api.ewp;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.api.AbstractResourceTest;
import pt.ulisboa.ewp.node.api.ewp.filter.EwpApiRequestFilter;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.service.http.log.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;
import pt.ulisboa.ewp.node.utils.XmlValidator;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

@SpringBootTest(classes = {EwpNodeApplication.class})
public abstract class AbstractEwpControllerTest extends AbstractResourceTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private XmlValidator xmlValidator;

  @Autowired
  private EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  protected MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(new EwpApiRequestFilter(ewpHttpCommunicationLogService))
            .apply(springSecurity())
            .build();
  }

  protected ResultActions executeGetRequest(RegistryClient registryClient, String relativeUri,
      HttpParams params)
      throws Exception {
    RequestPostProcessor securityRequestProcessor =
        tlsRequestProcessor(
            registryClient, Collections.singletonList(UUID.randomUUID().toString()));

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(
            EwpApiConstants.API_BASE_URI + relativeUri + "?" + params.toString())
            .with(securityRequestProcessor);

    return this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print());
  }

  protected ResultActions executePostRequest(RegistryClient registryClient, String relativeUri,
      HttpParams params)
      throws Exception {
    RequestPostProcessor securityRequestProcessor =
        tlsRequestProcessor(
            registryClient, Collections.singletonList(UUID.randomUUID().toString()));

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(EwpApiConstants.API_BASE_URI + relativeUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(params.toString())
            .with(securityRequestProcessor);

    return this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print());
  }

  protected void assertBadRequest(RegistryClient registryClient,
      MockHttpServletRequestBuilder requestBuilder, String errorMessage)
      throws Exception {
    assertErrorRequest(
        requestBuilder,
        tlsRequestProcessor(
            registryClient, Collections.singletonList(UUID.randomUUID().toString())),
        HttpStatus.BAD_REQUEST,
        errorMessage);
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

  protected RequestPostProcessor tlsRequestProcessor(
      RegistryClient registryClient, Collection<String> clientCoveredHeiIds) {
    return request -> {
      X509Certificate mockedX509Certificate = mock(X509Certificate.class);

      doReturn(clientCoveredHeiIds)
          .when(registryClient)
          .getHeisCoveredByCertificate(mockedX509Certificate);
      doReturn(mockedX509Certificate)
          .when(registryClient)
          .getCertificateKnownInEwpNetwork(new X509Certificate[] {mockedX509Certificate});

      request.setAttribute(
          "javax.servlet.request.X509Certificate", new X509Certificate[] {mockedX509Certificate});
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
      signatureHeadersSet.add(HttpSignatureService.HEADER_REQUEST_TARGET);
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
        new Signer(keyPair.getPrivate(), new Signature(keyId, algorithm, null, signatureHeaders));
    try {
      String queryParams = request.getQueryString();
      String requestString = request.getPathInfo() + (queryParams == null ? "" : "?" + queryParams);
      Signature signature;
      if (signatureParameter == null) {
        signature =
            signer.sign(
                request.getMethod().toLowerCase(), requestString, HttpUtils.toHeadersMap(headers));
      } else {
        signature = new Signature(keyId, algorithm, signatureParameter, signatureHeaders);
      }

      request.addHeader("Authorization", signature);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addRequestDigestHeader(MockHttpServletRequest request) {
    try {
      request.addHeader("Digest", "SHA-256=" + getDigest(request));
    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  private String getDigest(HttpServletRequest httpServletRequest)
      throws IOException, NoSuchAlgorithmException {
    String body =
        httpServletRequest.getReader() != null
            ? IOUtils.toString(httpServletRequest.getReader())
            : "";
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
