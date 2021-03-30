package pt.ulisboa.ewp.node.service.security.ewp.verifier.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.config.security.SecurityClientTlsEncoding;
import pt.ulisboa.ewp.node.config.security.SecurityClientTlsProperties;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreGenerator;

class TlsRequestAuthenticationMethodVerifierTest extends AbstractTest {

  @Test
  void testGetAuthenticationMethod() {
    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        null, null);
    assertThat(verifier.getAuthenticationMethod())
        .isEqualTo(EwpAuthenticationMethod.TLS);
  }

  @Test
  void testVerify_NoCertificateProvided_ReturnFailure()
      throws IOException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityProperties.setClientTls(securityClientTlsProperties);

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, null);

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.TLS, "Request is not using authentication method")
        .notUsingMethod()
        .withRequiredMethodInfoFulfilled(false)
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_UnknownCertificateProvided_ReturnFailure()
      throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, OperatorCreationException, NoSuchProviderException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityProperties.setClientTls(securityClientTlsProperties);

    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, registryClient);

    DecodedKeystore keystore = KeyStoreGenerator.generate("", "1");
    DecodedCertificateAndKey certificateAndKey = keystore.getDecodedCertificateAndKey("1");

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest
        .setAttribute("javax.servlet.request.X509Certificate",
            new X509Certificate[]{(X509Certificate) certificateAndKey.getCertificate()});

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(null).when(registryClient).getCertificateKnownInEwpNetwork(ArgumentMatchers.any());

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.TLS,
            "None of the client certificates is valid in the EWP network")
        .withResponseCode(HttpStatus.FORBIDDEN)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_InvalidCertificateProvidedByHeader_ReturnFailure()
      throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, OperatorCreationException, NoSuchProviderException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityClientTlsProperties.setEncoding(SecurityClientTlsEncoding.HEX);
    securityProperties.setClientTls(securityClientTlsProperties);

    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, registryClient);

    DecodedKeystore keystore = KeyStoreGenerator.generate("", "1");
    DecodedCertificateAndKey certificateAndKey = keystore.getDecodedCertificateAndKey("1");
    X509Certificate[] certificates = {(X509Certificate) certificateAndKey.getCertificate()};

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.addHeader("X-CERTIFICATE", "invalid");

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(certificateAndKey.getCertificate()).when(registryClient)
        .getCertificateKnownInEwpNetwork(certificates);

    Collection<String> heiIdsCoveredByCertificate = Collections
        .singletonList(UUID.randomUUID().toString());
    doReturn(heiIdsCoveredByCertificate).when(registryClient)
        .getHeisCoveredByCertificate(certificates[0]);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .failureBuilder(
            EwpAuthenticationMethod.TLS, "Failed to parse client certificate")
        .withResponseCode(HttpStatus.BAD_REQUEST)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_ValidCertificateProvidedByAttribute_ReturnSuccess()
      throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, OperatorCreationException, NoSuchProviderException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityProperties.setClientTls(securityClientTlsProperties);

    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, registryClient);

    DecodedKeystore keystore = KeyStoreGenerator.generate("", "1");
    DecodedCertificateAndKey certificateAndKey = keystore.getDecodedCertificateAndKey("1");
    X509Certificate[] certificates = {(X509Certificate) certificateAndKey.getCertificate()};

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest
        .setAttribute("javax.servlet.request.X509Certificate", certificates);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(certificateAndKey.getCertificate()).when(registryClient)
        .getCertificateKnownInEwpNetwork(certificates);

    Collection<String> heiIdsCoveredByCertificate = Collections
        .singletonList(UUID.randomUUID().toString());
    doReturn(heiIdsCoveredByCertificate).when(registryClient)
        .getHeisCoveredByCertificate(certificates[0]);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .successBuilder(
            EwpAuthenticationMethod.TLS, heiIdsCoveredByCertificate)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_ValidCertificateProvidedByHeaderEncodedInHex_ReturnSuccess()
      throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, OperatorCreationException, NoSuchProviderException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityClientTlsProperties.setEncoding(SecurityClientTlsEncoding.HEX);
    securityProperties.setClientTls(securityClientTlsProperties);

    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, registryClient);

    DecodedKeystore keystore = KeyStoreGenerator.generate("", "1");
    DecodedCertificateAndKey certificateAndKey = keystore.getDecodedCertificateAndKey("1");
    X509Certificate[] certificates = {(X509Certificate) certificateAndKey.getCertificate()};

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    String certificateEncoded = new String(
        Hex.encode(Base64.decodeBase64(
            certificateAndKey.getFormattedCertificate().getBytes(StandardCharsets.UTF_8))));
    mockHttpServletRequest.addHeader("X-CERTIFICATE", certificateEncoded);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(certificateAndKey.getCertificate()).when(registryClient)
        .getCertificateKnownInEwpNetwork(certificates);

    Collection<String> heiIdsCoveredByCertificate = Collections
        .singletonList(UUID.randomUUID().toString());
    doReturn(heiIdsCoveredByCertificate).when(registryClient)
        .getHeisCoveredByCertificate(certificates[0]);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .successBuilder(
            EwpAuthenticationMethod.TLS, heiIdsCoveredByCertificate)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

  @Test
  void testVerify_ValidCertificateProvidedByHeaderEncodedInBase64_ReturnSuccess()
      throws IOException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, OperatorCreationException, NoSuchProviderException {
    SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setAllowMissingClientCertificate(false);
    SecurityClientTlsProperties securityClientTlsProperties = new SecurityClientTlsProperties();
    securityClientTlsProperties.setHeaderName("X-CERTIFICATE");
    securityClientTlsProperties.setEncoding(SecurityClientTlsEncoding.BASE64);
    securityProperties.setClientTls(securityClientTlsProperties);

    RegistryClient registryClient = Mockito.spy(new RegistryClient(new RegistryProperties()));

    TlsRequestAuthenticationMethodVerifier verifier = new TlsRequestAuthenticationMethodVerifier(
        securityProperties, registryClient);

    DecodedKeystore keystore = KeyStoreGenerator.generate("", "1");
    DecodedCertificateAndKey certificateAndKey = keystore.getDecodedCertificateAndKey("1");
    X509Certificate[] certificates = {(X509Certificate) certificateAndKey.getCertificate()};

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    String certificateEncoded = URLEncoder
        .encode(certificateAndKey.getFormattedCertificate(), StandardCharsets.UTF_8.toString());
    mockHttpServletRequest.addHeader("X-CERTIFICATE", certificateEncoded);

    EwpApiHttpRequestWrapper request = new EwpApiHttpRequestWrapper(
        mockHttpServletRequest);

    doReturn(certificateAndKey.getCertificate()).when(registryClient)
        .getCertificateKnownInEwpNetwork(certificates);

    Collection<String> heiIdsCoveredByCertificate = Collections
        .singletonList(UUID.randomUUID().toString());
    doReturn(heiIdsCoveredByCertificate).when(registryClient)
        .getHeisCoveredByCertificate(certificates[0]);

    EwpApiAuthenticateMethodResponse expectedResult = EwpApiAuthenticateMethodResponse
        .successBuilder(
            EwpAuthenticationMethod.TLS, heiIdsCoveredByCertificate)
        .build();
    assertThat(verifier.verify(request)).isEqualTo(expectedResult);
  }

}
