package pt.ulisboa.ewp.node.api.ewp.controller.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FilesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.MockFilesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

public class EwpApiFilesV1ControllerIntegrationTest extends AbstractEwpControllerIntegrationTest {

  @MockBean private RegistryClient registryClient;

  @Autowired private HostPluginManager hostPluginManager;

  @Test
  public void testFileRetrieval_UnknownFileId_NotFoundReturned() throws Exception {
    String heiId = UUID.randomUUID().toString();
    String unknownFileId = UUID.randomUUID().toString();

    MockFilesV1HostProvider provider1 = new MockFilesV1HostProvider();

    Mockito
        .when(hostPluginManager.getAllProvidersOfType(heiId,
            FilesV1HostProvider.class))
        .thenReturn(List.of(provider1));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.FILE_ID, unknownFileId);

    assertNotFound(
        registryClient,
        HttpMethod.GET,
        EwpApiConstants.API_BASE_URI + EwpApiFilesV1Controller.BASE_PATH + "/" + heiId,
        queryParams,
        "File ID is unknown: " + unknownFileId);
  }

  @Test
  public void testFileRetrieval_ValidFileIdCorrespondingToPdfFile_PdfFileReturned()
      throws Exception {
    String heiId = UUID.randomUUID().toString();
    String fileId = UUID.randomUUID().toString();

    MockFilesV1HostProvider provider1 = new MockFilesV1HostProvider();

    FileResponse fileResponse = new FileResponse(MediaType.APPLICATION_PDF_VALUE,
        "PDF_TEST_CONTENT".getBytes(StandardCharsets.UTF_8));
    provider1.registerFile(fileId, fileResponse);

    Mockito
        .when(hostPluginManager.getAllProvidersOfType(heiId,
            FilesV1HostProvider.class))
        .thenReturn(List.of(provider1));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.FILE_ID, fileId);

    MockHttpServletResponse response =
        executeRequest(
                registryClient,
                HttpMethod.GET,
                EwpApiConstants.API_BASE_URI + EwpApiFilesV1Controller.BASE_PATH + "/" + heiId,
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
    byte[] resultFileContents = response.getContentAsByteArray();

    assertThat(response.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(fileResponse.getMediaType());

    assertThat(resultFileContents).isNotNull();
    assertThat(resultFileContents).isEqualTo(fileResponse.getData());
  }

}
