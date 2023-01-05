package pt.ulisboa.ewp.node.client.ewp.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult.Builder;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFilesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationHttpSignatureConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationHttpSignatureConfiguration;

class EwpFilesV1ClientUnitTest {

  @Test
  public void testGetFile_ValidHeiIdAndFileId_ValidFileResponseReturned()
      throws EwpClientErrorException {
    // Given
    String heiId = UUID.randomUUID().toString();
    String fileId = UUID.randomUUID().toString();

    String mediaType = "application/pdf";
    byte[] data = "PDF_TEST_CONTENT".getBytes(StandardCharsets.UTF_8);

    EwpFilesApiConfiguration apiConfiguration = new EwpFilesApiConfiguration(
        "https://www.example.org",
        List.of(new EwpClientAuthenticationHttpSignatureConfiguration()),
        List.of(new EwpServerAuthenticationHttpSignatureConfiguration()));

    EwpResponse ewpResponse = new EwpResponse.Builder(HttpStatus.OK).mediaType(mediaType).build();

    EwpSuccessOperationResult<Serializable> successOperationResult = new Builder<>().responseBody(
        data).response(ewpResponse).build();

    EwpClient ewpClient = Mockito.mock(EwpClient.class);
    doReturn(successOperationResult).when(ewpClient).execute(Mockito.any(), Mockito.any());

    EwpFilesV1Client client = Mockito.spy(new EwpFilesV1Client(null, ewpClient));
    doReturn(apiConfiguration).when(client).getApiConfigurationForHeiId(heiId);

    // When
    EwpSuccessOperationResult<FileResponse> response = client.getFile(heiId, fileId);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getResponseBody()).isNotNull();
    assertThat(response.getResponseBody().getMediaType()).isEqualTo(mediaType);
    assertThat(response.getResponseBody().getData()).isEqualTo(data);
  }

}