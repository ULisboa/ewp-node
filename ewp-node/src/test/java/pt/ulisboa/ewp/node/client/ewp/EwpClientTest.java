package pt.ulisboa.ewp.node.client.ewp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import eu.erasmuswithoutpaper.api.discovery.Manifest;
import eu.erasmuswithoutpaper.api.echo.Response;
import eu.erasmuswithoutpaper.api.registry.Catalogue;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

@SpringBootTest(
    classes = {EwpNodeApplication.class, EwpClientTest.Config.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EwpClientTest extends AbstractTest {

  @LocalServerPort private int serverPort;

  @Autowired private EwpClient ewpClient;

  @Autowired private RegistryClient registryClient;

  @Autowired private KeyStoreService keyStoreService;

  @Configuration
  static class Config {

    @Bean
    @Primary
    public RegistryClient getRegistryClient() {
      return spy(RegistryClient.class);
    }
  }

  @Test
  public void testGetDevRegistryCatalogue() throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<Catalogue> result =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(
                HttpMethod.GET, "https://dev-registry.erasmuswithoutpaper.eu/catalogue-v1.xml"),
            Catalogue.class);
    assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(result.getResponseBody(), notNullValue());
    assertThat(result.getResponseBody().getHost(), notNullValue());
  }

  @Test
  public void testGetDevRegistryManifestAnonymous() throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<Manifest> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(
                HttpMethod.GET, "https://dev-registry.erasmuswithoutpaper.eu/manifest.xml"),
            Manifest.class);
    assertThat(clientResponse.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(clientResponse.getResponseBody(), notNullValue());
    assertThat(clientResponse.getResponseBody().getHost(), notNullValue());
  }

  @Test(expected = EwpClientProcessorException.class)
  public void testGetOwnEchoResourceWithoutAuthentication() throws AbstractEwpClientErrorException {
    Map<String, List<String>> params = new HashMap<>();
    params.put(EwpApiParamConstants.PARAM_NAME_ECHO, Collections.singletonList("abc"));
    ewpClient.executeWithLoggingExpectingSuccess(
        new EwpRequest(HttpMethod.GET, "http://localhost:" + serverPort + "/rest/ewp/echo")
            .queryParams(params)
            .authenticationMethod(EwpAuthenticationMethod.ANONYMOUS),
        Response.class);
  }

  @Test
  public void testGetOwnEchoResourceWithHttpSignature()
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
          KeyStoreException, IOException, AbstractEwpClientErrorException {
    DecodedCertificateAndKey decodedCertificateAndKey =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();
    doReturn(decodedCertificateAndKey.getRsaPublicKey())
        .when(registryClient)
        .findClientRsaPublicKey(decodedCertificateAndKey.getPublicKeyFingerprint());
    doReturn(decodedCertificateAndKey.getRsaPublicKey())
        .when(registryClient)
        .findRsaPublicKey(decodedCertificateAndKey.getPublicKeyFingerprint());

    String testEchoValue = "abc";
    Map<String, List<String>> params = new HashMap<>();
    params.put(EwpApiParamConstants.PARAM_NAME_ECHO, Collections.singletonList(testEchoValue));
    EwpSuccessOperationResult<Response> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(HttpMethod.GET, "http://localhost:" + serverPort + "/rest/ewp/echo")
                .queryParams(params)
                .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE),
            Response.class);
    assertThat(clientResponse.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(clientResponse.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(clientResponse.getResponseBody().getEcho().get(0), equalTo(testEchoValue));
  }

  @Test
  public void testPostOwnEchoResourceWithHttpSignature()
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
          KeyStoreException, IOException, AbstractEwpClientErrorException {
    DecodedCertificateAndKey decodedCertificateAndKey =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();
    doReturn(decodedCertificateAndKey.getRsaPublicKey())
        .when(registryClient)
        .findClientRsaPublicKey(decodedCertificateAndKey.getPublicKeyFingerprint());
    doReturn(decodedCertificateAndKey.getRsaPublicKey())
        .when(registryClient)
        .findRsaPublicKey(decodedCertificateAndKey.getPublicKeyFingerprint());

    String testEchoValue = "abc";
    Map<String, List<String>> params = new HashMap<>();
    params.put(EwpApiParamConstants.PARAM_NAME_ECHO, Collections.singletonList(testEchoValue));
    EwpSuccessOperationResult<Response> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(HttpMethod.POST, "http://localhost:" + serverPort + "/rest/ewp/echo")
                .bodyParams(params)
                .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE),
            Response.class);
    assertThat(clientResponse.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(clientResponse.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(clientResponse.getResponseBody().getEcho().get(0), equalTo(testEchoValue));
  }
}
