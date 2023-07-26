package pt.ulisboa.ewp.node.service.ewp.security.verifier.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.tomitribe.auth.signatures.*;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

class HttpSignatureRequestAuthenticationMethodVerifierTest extends AbstractTest {

  @Test
  void testGetAuthenticationMethod() {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);
    assertThat(verifier.getAuthenticationMethod())
        .isEqualTo(EwpAuthenticationMethod.HTTP_SIGNATURE);
  }

  @Test
  void testVerify_MissingAuthorizationHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE, "Request is not using authentication method")
        .notUsingMethod()
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.UNAUTHORIZED)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_InvalidSignatureAlgorithm_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA512,
                null,
                null,
                Collections.emptyList()));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Only signature algorithm rsa-sha256 is supported.")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.UNAUTHORIZED)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MissingRequestTargetSignatureHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Collections.emptyList()));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Missing required signed header '(request-target)'")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MissingHostSignatureHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Collections.singletonList("(request-target)")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Missing required signed header 'host'")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MissingDateOrOriginalDateSignatureHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Missing required signed header 'date|original-date'")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MissingDigestSignatureHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Missing required signed header 'digest'")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MissingXRequestIdSignatureHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Missing required signed header 'x-request-id'")
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MismatchedHostHeader_ReturnFailure()
      throws IOException {
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, "");
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE, "Host does not match")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_UnknownPublicKey_ReturnFailure()
      throws IOException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setServerName("example.com");

    Signer signer =
        new Signer(
            createKeyPair().getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, "");
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(null).when(registryClient).findClientRsaPublicKey(ArgumentMatchers.anyString());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Key not found for fingerprint: " + signature.getKeyId())
        .withResponseCode(HttpStatus.FORBIDDEN)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_InvalidDate_ReturnFailure()
      throws IOException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setServerName("example.com");

    KeyPair keyPair = createKeyPair();
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now().minusDays(1L)));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, "");
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(keyPair.getPublic()).when(registryClient)
        .findClientRsaPublicKey(ArgumentMatchers.anyString());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "The date cannot be parsed or the date does not match your server clock within a certain threshold of timeDate.")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_InvalidXRequestId_ReturnFailure()
      throws IOException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setServerName("example.com");

    KeyPair keyPair = createKeyPair();
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, "");
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(keyPair.getPublic()).when(registryClient)
        .findClientRsaPublicKey(ArgumentMatchers.anyString());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Authentication with non-canonical X-Request-ID")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_InvalidSignature_ReturnFailure()
      throws IOException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setServerName("example.com");

    KeyPair keyPair = createKeyPair();
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    headers.put(HttpConstants.HEADER_DIGEST, "");
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, UUID.randomUUID().toString());
    Signature signature = signer.sign("", "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(createKeyPair().getPublic()).when(registryClient)
        .findClientRsaPublicKey(ArgumentMatchers.anyString());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Signature verification failed")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_MismatchedDigest_ReturnFailure()
      throws IOException, NoSuchAlgorithmException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setMethod(HttpMethod.POST.name());
    mockHttpServletRequest.setServerName("example.com");

    KeyPair keyPair = createKeyPair();
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    String digest = new String(
        Base64.encodeBase64(MessageDigest.getInstance("SHA-256").digest("c=d" .getBytes(
            StandardCharsets.UTF_8))));
    headers.put(HttpConstants.HEADER_DIGEST, "SHA-256=" + digest);
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, UUID.randomUUID().toString());
    Signature signature = signer.sign(mockHttpServletRequest.getMethod(), "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    mockHttpServletRequest.setContentType("application/x-www-form-urlencoded");
    mockHttpServletRequest.addParameter("a", "b");

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(keyPair.getPublic()).when(registryClient)
        .findClientRsaPublicKey(ArgumentMatchers.anyString());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.HTTP_SIGNATURE,
            "Digest mismatch! calculated for algorithm SHA-256 (body length: 3): QhRPOTnD/7vwv4sfEq/7XCOkxb1B4P9nLVSldU8GIFg=, provided: fFgX4QP5RZBCc+dNh3vWa+jwblaMmHJqZIIWJbnMJHU=")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_ValidRequest_ReturnSuccess()
      throws IOException, NoSuchAlgorithmException {
    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));
    HttpSignatureRequestAuthenticationMethodVerifier verifier = new HttpSignatureRequestAuthenticationMethodVerifier(
        registryClient);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setMethod(HttpMethod.POST.name());
    mockHttpServletRequest.setServerName("example.com");

    KeyPair keyPair = createKeyPair();
    Signer signer =
        new Signer(
            keyPair.getPrivate(),
            new Signature(
                UUID.randomUUID().toString(),
                null,
                Algorithm.RSA_SHA256,
                null,
                null,
                Arrays.asList("(request-target)", "host", "date", "digest", "x-request-id")));
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.HOST, "example.com");
    headers.put(HttpHeaders.DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    String digest = new String(
        Base64.encodeBase64(MessageDigest.getInstance("SHA-256").digest("a=b" .getBytes(
            StandardCharsets.UTF_8))));
    headers.put(HttpConstants.HEADER_DIGEST, "SHA-256=" + digest);
    headers.put(HttpConstants.HEADER_X_REQUEST_ID, UUID.randomUUID().toString());
    Signature signature = signer.sign(mockHttpServletRequest.getMethod(), "", headers);
    mockHttpServletRequest.addHeader("Authorization", signature);
    headers.forEach(mockHttpServletRequest::addHeader);

    mockHttpServletRequest.setContentType("application/x-www-form-urlencoded");
    mockHttpServletRequest.addParameter("a", "b");

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(keyPair.getPublic()).when(registryClient)
        .findClientRsaPublicKey(ArgumentMatchers.anyString());

    List<String> heiIdsCoveredByClient = Collections.singletonList(UUID.randomUUID().toString());
    doReturn(heiIdsCoveredByClient).when(registryClient)
        .getHeisCoveredByClientKey(ArgumentMatchers.any());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .successBuilder(EwpAuthenticationMethod.HTTP_SIGNATURE, heiIdsCoveredByClient)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
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
