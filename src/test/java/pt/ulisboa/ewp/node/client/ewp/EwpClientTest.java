package pt.ulisboa.ewp.node.client.ewp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import eu.erasmuswithoutpaper.api.discovery.v5.ManifestV5;
import eu.erasmuswithoutpaper.api.echo.v2.ResponseV2;
import eu.erasmuswithoutpaper.api.registry.v1.CatalogueV1;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

public class EwpClientTest extends AbstractIntegrationTest {

  @LocalServerPort
  private int serverPort;

  @Autowired
  private EwpClient ewpClient;

  @Autowired
  private RegistryClient registryClient;

  @Autowired
  private KeyStoreService keyStoreService;

  @Test
  public void testGetDevRegistryCatalogue() throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<CatalogueV1> result =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(
                HttpMethod.GET, "https://dev-registry.erasmuswithoutpaper.eu/catalogue-v1.xml"),
            CatalogueV1.class);
    assertThat(result.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(result.getResponseBody(), notNullValue());
    assertThat(result.getResponseBody().getHost(), notNullValue());
  }

  @Test
  public void testGetDevRegistryManifestAnonymous() throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<ManifestV5> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(
                HttpMethod.GET, "https://dev-registry.erasmuswithoutpaper.eu/manifest.xml"),
            ManifestV5.class);
    assertThat(clientResponse.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(clientResponse.getResponseBody(), notNullValue());
    assertThat(clientResponse.getResponseBody().getHost(), notNullValue());
  }

  @Test
  public void testGetOwnEchoResourceWithoutAuthentication() throws AbstractEwpClientErrorException {
    HttpParams params = new HttpParams();
    params.param(EwpApiParamConstants.ECHO, "abc");
    assertThrows(
        EwpClientProcessorException.class,
        () -> {
          ewpClient.executeWithLoggingExpectingSuccess(
              new EwpRequest(HttpMethod.GET, "http://localhost:" + serverPort + "/api/ewp/echo")
                  .queryParams(params)
                  .authenticationMethod(EwpAuthenticationMethod.ANONYMOUS),
              ResponseV2.class);
        });
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
    HttpParams params = new HttpParams();
    params.param(EwpApiParamConstants.ECHO, testEchoValue);
    EwpSuccessOperationResult<ResponseV2> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(HttpMethod.GET, "http://localhost:" + serverPort + "/api/ewp/echo")
                .queryParams(params)
                .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE),
            ResponseV2.class);
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
    doReturn(Collections.singletonList(UUID.randomUUID().toString())).when(registryClient)
        .getHeisCoveredByClientKey(ArgumentMatchers.any());

    String testEchoValue = "abc";
    HttpParams params = new HttpParams();
    params.param(EwpApiParamConstants.ECHO, testEchoValue);
    EwpSuccessOperationResult<ResponseV2> clientResponse =
        ewpClient.executeWithLoggingExpectingSuccess(
            new EwpRequest(HttpMethod.POST, "http://localhost:" + serverPort + "/api/ewp/echo")
                .bodyParams(params)
                .authenticationMethod(EwpAuthenticationMethod.HTTP_SIGNATURE),
            ResponseV2.class);
    assertThat(clientResponse.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(clientResponse.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(clientResponse.getResponseBody().getEcho().get(0), equalTo(testEchoValue));
  }
}
