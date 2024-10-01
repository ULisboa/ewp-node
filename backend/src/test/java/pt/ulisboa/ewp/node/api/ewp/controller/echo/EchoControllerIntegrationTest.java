package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.tomitribe.auth.signatures.Algorithm;
import org.tomitribe.auth.signatures.Signature;
import org.tomitribe.auth.signatures.Signer;
import org.tomitribe.auth.signatures.Verifier;
import org.w3c.dom.Node;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.HttpSignatureUtils;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

public class EchoControllerIntegrationTest extends AbstractEwpControllerIntegrationTest {

  private static final String[] EXPECTED_SIGNATURE_HEADERS_WITH_DATE = {
      HttpSignatureUtils.HEADER_REQUEST_TARGET,
      HttpHeaders.HOST,
      HttpHeaders.DATE,
      HttpConstants.HEADER_DIGEST,
      HttpConstants.HEADER_X_REQUEST_ID,
      HttpConstants.HEADER_ACCEPT_SIGNATURE,
      HttpConstants.HEADER_WANT_DIGEST
  };

  private static final Collection<String> EXPECTED_HEI_IDS =
      Arrays.asList("myInstId1", "myInstId2");

  @MockBean private RegistryClient registryClient;

  @Autowired private KeyStoreService keyStoreService;

  @Test
  public void testEchoPutAnonymous() throws Exception {
    this.mockMvc
        .perform(put(EwpApiConstants.API_BASE_URI + "echo").accept(MediaType.APPLICATION_XML))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testEchoPutWithTLS() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
            this::putRequest,
            HttpStatus.METHOD_NOT_ALLOWED,
            "Request method 'PUT' is not supported"));
  }

  @Test
  public void testEchoGetAnonymous() throws Exception {
    this.mockMvc
        .perform(get(EwpApiConstants.API_BASE_URI + "echo").accept(MediaType.APPLICATION_XML))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidAlgorithm() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.UNAUTHORIZED,
                "Only signature algorithm rsa-sha256 is supported.")
            .algorithm(Algorithm.RSA_SHA512));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndMissingRequestTargetHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "Missing required signed header '(request-target)'")
            .signatureHeaders());
  }

  @Test
  public void testEchoGetWithHttpSignatureAndMissingHostHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest, HttpStatus.BAD_REQUEST, "Missing required signed header 'host'")
            .signatureHeaders("(request-target)"));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndMissingDateOrOriginalDateHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "Missing required signed header 'date|original-date'")
            .signatureHeaders("(request-target)", "host"));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndMissingDigestHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest, HttpStatus.BAD_REQUEST, "Missing required signed header 'digest'")
            .signatureHeaders("(request-target)", "host", "date"));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndMissingXRequestIdHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "Missing required signed header 'x-request-id'")
            .signatureHeaders("(request-target)", "host", "date", "digest"));
  }

  //    @Test
  //    public void testEchoGetWithHttpSignatureAndInvalidHostHeader() throws Exception {
  //
  // assertUnsuccessfulEchoRequest(InvalidEchoHttpSignatureTestDataWrapper.create(this::getRequest,
  // HttpStatus.BAD_REQUEST,
  //                "Host does not match").host("invalid.host"));
  //    }

  @Test
  public void testEchoGetWithHttpSignatureAndUnknownKeyIdHeader() throws Exception {
    String keyId = "test";
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest, HttpStatus.FORBIDDEN, "Key not found for fingerprint: " + keyId)
            .keyId(keyId)
            .validKeyId(false));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidDateHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.")
            .date(ZonedDateTime.now().minusYears(1L)));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidOriginalDateHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.")
            .originalDate(ZonedDateTime.now().minusYears(1L)));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidXRequestIdHeader() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest,
                HttpStatus.BAD_REQUEST,
                "Authentication with non-canonical X-Request-ID")
            .xRequestId("@@@invalid@@@"));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidSignature() throws Exception {
    String base64SignedSignature =
        "jHis/Laxm2ZcsE9xJuA5S8ngNch6uk5EJDaGgLLkdAF9vvnh1a29BkduLK23fIzFBxWss3gSjexpWe94n279fAJdcEs7diMkjQf2nRLF1m7gdZ14Lo5ays0CIvorMw/ME/wEbvrry1wyqHPnn3iYzWD9PgzRF8217DnIRTP7dRw=";
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::getRequest, HttpStatus.BAD_REQUEST, "Signature verification failed")
            .signature(base64SignedSignature));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndInvalidDigest() throws Exception {
    assertUnsuccessfulEchoRequest(
        InvalidEchoHttpSignatureTestDataWrapper.create(
                this::postRequest,
                HttpStatus.BAD_REQUEST,
                "Digest mismatch! calculated for algorithm SHA-256 (body length: 7): 7K89JMP/+DCmiM5iRh4iK6B8VZm8pqDY4yv8tTumlfo=, provided: @@@invalid@@@")
            .echo(Collections.singletonList("a1"))
            .digest("SHA-256=@@@invalid@@@"));
  }

  @Test
  public void testEchoGetWithHttpSignatureAndNoParameters() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Collections.emptyList(),
        this::getRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  @Test
  public void testEchoGetWithHttpSignatureAndOneParameter() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Collections.singletonList("a1"),
        this::getRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  @Test
  public void testEchoGetWithHttpSignatureAndTwoParameters() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Arrays.asList("a1", "b2"),
        this::getRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  @Test
  public void testEchoPostAnonymous() throws Exception {
    this.mockMvc
        .perform(
            post(EwpApiConstants.API_BASE_URI + "echo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_XML))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testEchoPostWithHttpSignatureAndNoParameters() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Collections.emptyList(),
        this::postRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  @Test
  public void testEchoPostWithHttpSignatureAndOneParameter() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Collections.singletonList("a1"),
        this::postRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  @Test
  public void testEchoPostWithHttpSignatureAndTwoParameters() throws Exception {
    assertSuccessfulEchoRequestWithHttpSignature(
        Arrays.asList("a1", "b2"),
        this::postRequest,
        httpSignatureRequestProcessor(
            registryClient,
            EXPECTED_HEI_IDS,
            "test",
            Algorithm.RSA_SHA256,
            EXPECTED_SIGNATURE_HEADERS_WITH_DATE,
            null,
            true),
        createRequiredHttpSignatureCommonHeaders());
  }

  private void assertSuccessfulEchoRequest(
      List<String> echo,
      Function<List<String>, MockHttpServletRequestBuilder> requestBuilder,
      RequestPostProcessor securityMethodProcessor,
      HttpHeaders additionalHeaders)
      throws Exception {
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                requestBuilder.apply(echo).with(securityMethodProcessor).headers(additionalHeaders))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())

            // Assert response's echo values
            .andExpect(xpath("/*[local-name()='response']/echo").nodeCount(echo.size()))
            .andExpect(xpath("/*[local-name()='response']").node(validChildrenEchoNodes(echo)))

            // Assert response's hei ids
            .andExpect(
                xpath("/*[local-name()='response']/hei-id").nodeCount(EXPECTED_HEI_IDS.size()))
            .andExpect(xpath("/*[local-name()='response']").node(validChildrenHeiIdsNodes()))
            .andReturn();

    validateXml(mvcResult.getResponse().getContentAsString());
  }

  private void assertSuccessfulEchoRequestWithHttpSignature(
      List<String> echo,
      Function<List<String>, MockHttpServletRequestBuilder> requestBuilder,
      RequestPostProcessor securityMethodProcessor,
      HttpHeaders additionalHeaders)
      throws Exception {
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                requestBuilder.apply(echo).with(securityMethodProcessor).headers(additionalHeaders))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(validResponseDigest())
            .andExpect(validResponseXRequestId())
            .andExpect(validResponseXRequestSignature())
            .andExpect(validResponseSignature())

            // Assert response's echo values
            .andExpect(xpath("/*[local-name()='response']/echo").nodeCount(echo.size()))
            .andExpect(xpath("/*[local-name()='response']").node(validChildrenEchoNodes(echo)))

            // Assert response's hei ids
            .andExpect(
                xpath("/*[local-name()='response']/hei-id").nodeCount(EXPECTED_HEI_IDS.size()))
            .andExpect(xpath("/*[local-name()='response']").node(validChildrenHeiIdsNodes()))
            .andReturn();

    validateXml(mvcResult.getResponse().getContentAsString());
  }

  private void assertUnsuccessfulEchoRequest(InvalidEchoHttpSignatureTestDataWrapper dataWrapper)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder =
        dataWrapper
            .requestBuilder
            .apply(dataWrapper.echo)
            .with(
                httpSignatureRequestProcessor(
                    registryClient,
                    EXPECTED_HEI_IDS,
                    dataWrapper.keyId,
                    dataWrapper.algorithm,
                    dataWrapper.signatureHeaders,
                    dataWrapper.signatureParameter,
                    dataWrapper.validKeyId))
            .header("Host", dataWrapper.host)
            .header("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(dataWrapper.date))
            .header(
                HttpConstants.HEADER_ORIGINAL_DATE,
                DateTimeFormatter.RFC_1123_DATE_TIME.format(dataWrapper.originalDate))
            .header("X-Request-ID", dataWrapper.xRequestId)
            .headers(dataWrapper.additionalHeaders);

    if (dataWrapper.digest != null) {
      requestBuilder = requestBuilder.header("Digest", dataWrapper.digest);
    }

    MvcResult mvcResult =
        this.mockMvc
            .perform(requestBuilder)
            .andExpect(status().is(dataWrapper.expectedStatus.value()))
            .andExpect(
                xpath("/error-response/developer-message")
                    .string(new StringContains(dataWrapper.errorMessage)))
            .andReturn();

    validateXml(mvcResult.getResponse().getContentAsString());
  }

  private MockHttpServletRequestBuilder postRequest(List<String> echo) {
    return post(EwpApiConstants.API_BASE_URI + "echo")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .content(echo.stream().map(e -> "echo=" + e).collect(Collectors.joining("&")))
        .accept(MediaType.APPLICATION_XML);
  }

  private MockHttpServletRequestBuilder getRequest(List<String> echo) {
    return get(EwpApiConstants.API_BASE_URI
            + "echo?"
            + echo.stream().map(e -> "echo=" + e).collect(Collectors.joining("&")))
        .accept(MediaType.APPLICATION_XML);
  }

  private MockHttpServletRequestBuilder putRequest(List<String> echo) {
    return put(EwpApiConstants.API_BASE_URI + "echo")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .content(echo.stream().map(e -> "echo=" + e).collect(Collectors.joining("&")))
        .accept(MediaType.APPLICATION_XML);
  }

  private void addRequestAuthorizationSignatureHeader(
      String keyId,
      Algorithm algorithm,
      String signatureParameter,
      String[] signatureHeaders,
      MockHttpServletRequest request,
      Map<String, String> headers,
      KeyPair keyPair) {
    Signer signer =
        new Signer(keyPair.getPrivate(), new Signature(keyId, null, algorithm, null, null, List.of(signatureHeaders)));
    try {
      String queryParams = request.getQueryString();
      String requestString = request.getPathInfo() + (queryParams == null ? "" : "?" + queryParams);
      Signature signature;
      if (signatureParameter == null) {
        signature = signer.sign(request.getMethod().toLowerCase(), requestString, headers);
      } else {
        signature = new Signature(keyId, null, algorithm, null, signatureParameter, List.of(signatureHeaders));
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

  private HttpHeaders createRequiredHttpSignatureCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Host", "localhost");
    headers.add("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.add("X-Request-Id", UUID.randomUUID().toString());
    return headers;
  }

  private Matcher<Node> validChildrenEchoNodes(Collection<String> expectedEcho) {
    return new BaseMatcher<Node>() {
      @Override
      public boolean matches(Object item) {
        Node node = (Node) item;
        Collection<String> obtainedEcho = getChildrenTextContents(node, "echo");
        assertThat(obtainedEcho, equalTo(expectedEcho));
        return true;
      }

      @Override
      public void describeTo(Description description) {}
    };
  }

  private Matcher<Node> validChildrenHeiIdsNodes() {
    return new BaseMatcher<Node>() {
      @Override
      public boolean matches(Object item) {
        Node node = (Node) item;
        Collection<String> obtainedHeiIds = getChildrenTextContents(node, "hei-id");
        assertThat(obtainedHeiIds, equalTo(EchoControllerIntegrationTest.EXPECTED_HEI_IDS));
        return true;
      }

      @Override
      public void describeTo(Description description) {}
    };
  }

  private ResultMatcher validResponseDigest() {
    return result -> {
      byte[] bodyBytes = result.getResponse().getContentAsString().getBytes();
      byte[] digest = MessageDigest.getInstance("SHA-256").digest(bodyBytes);
      String base64 = new String(Base64.getEncoder().encode(digest));
      assertEquals(
          "Digest",
          "SHA-256=" + base64,
          result.getResponse().getHeader(HttpConstants.HEADER_DIGEST));
    };
  }

  private ResultMatcher validResponseXRequestId() {
    return result ->
        assertEquals(
            "X-Request-Id",
            result.getRequest().getHeader(HttpConstants.HEADER_X_REQUEST_ID),
            result.getResponse().getHeader(HttpConstants.HEADER_X_REQUEST_ID));
  }

  private ResultMatcher validResponseXRequestSignature() {
    return result ->
        assertEquals(
            "X-Request-Signature",
            Signature.fromString(result.getRequest().getHeader(HttpHeaders.AUTHORIZATION))
                .getSignature(),
            result.getResponse().getHeader(HttpConstants.HEADER_X_REQUEST_SIGNATURE));
  }

  private ResultMatcher validResponseSignature() {
    DecodedCertificateAndKey decodedCertificateAndKey =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();

    return result -> {
      Signature signature =
          Signature.fromString(result.getResponse().getHeader(HttpConstants.HEADER_SIGNATURE));
      boolean valid =
          new Verifier(decodedCertificateAndKey.getRsaPublicKey(), signature)
              .verify("", "", HttpUtils.toHeadersMap(result.getResponse()));
      assertTrue("Signature", valid);
    };
  }

  private Collection<String> getChildrenTextContents(Node node, String childTag) {
    Collection<String> textContents = new ArrayList<>();
    int numberChildNodes = node.getChildNodes().getLength();
    for (int index = 0; index < numberChildNodes; index++) {
      Node childNode = node.getChildNodes().item(index);
      String nodeName = childNode.getNodeName();
      if (nodeName.contains(":")) {
        nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
      }
      if (nodeName.equals(childTag)) {
        textContents.add(childNode.getTextContent());
      }
    }
    return textContents;
  }

  private static class InvalidEchoHttpSignatureTestDataWrapper {

    private HttpHeaders additionalHeaders = new HttpHeaders();
    private List<String> echo = Collections.emptyList();
    private Function<List<String>, MockHttpServletRequestBuilder> requestBuilder;

    private String keyId = UUID.randomUUID().toString();
    private Algorithm algorithm = Algorithm.RSA_SHA256;
    private String signatureParameter;
    private String[] signatureHeaders = EXPECTED_SIGNATURE_HEADERS_WITH_DATE;

    private HttpStatus expectedStatus;
    private String errorMessage;

    private String host = "localhost";
    private ZonedDateTime date = ZonedDateTime.now();
    private ZonedDateTime originalDate = ZonedDateTime.now();
    private String xRequestId = UUID.randomUUID().toString();
    private String digest;

    private boolean validKeyId = true;

    private InvalidEchoHttpSignatureTestDataWrapper(
        Function<List<String>, MockHttpServletRequestBuilder> requestBuilder,
        HttpStatus expectedStatus,
        String errorMessage) {
      this.requestBuilder = requestBuilder;
      this.expectedStatus = expectedStatus;
      this.errorMessage = errorMessage;
    }

    static InvalidEchoHttpSignatureTestDataWrapper create(
        Function<List<String>, MockHttpServletRequestBuilder> requestBuilder,
        HttpStatus expectedStatus,
        String errorMessage) {
      return new InvalidEchoHttpSignatureTestDataWrapper(
          requestBuilder, expectedStatus, errorMessage);
    }

    InvalidEchoHttpSignatureTestDataWrapper echo(List<String> echo) {
      this.echo = echo;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper keyId(String keyId) {
      this.keyId = keyId;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper algorithm(Algorithm algorithm) {
      this.algorithm = algorithm;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper signature(String signature) {
      this.signatureParameter = signature;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper signatureHeaders(String... signatureHeaders) {
      this.signatureHeaders = signatureHeaders;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper host(String host) {
      this.host = host;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper date(ZonedDateTime date) {
      this.date = date;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper originalDate(ZonedDateTime originalDate) {
      this.originalDate = originalDate;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper xRequestId(String xRequestId) {
      this.xRequestId = xRequestId;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper digest(String digest) {
      this.digest = digest;
      return this;
    }

    InvalidEchoHttpSignatureTestDataWrapper validKeyId(boolean validKeyId) {
      this.validKeyId = validKeyId;
      return this;
    }
  }
}
