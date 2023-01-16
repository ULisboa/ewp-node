package pt.ulisboa.ewp.node.client.ewp.files;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFilesApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Files;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpFilesV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpFilesV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<FileResponse> getFile(String heiId, String fileId)
      throws EwpClientErrorException {
    EwpFilesApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.FILE_ID, fileId);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    EwpSuccessOperationResult<byte[]> successOperationResult = ewpHttpClient.execute(
        request, byte[].class);

    FileResponse fileResponse = createFileResponse(successOperationResult);
    return new EwpSuccessOperationResult.Builder<FileResponse>().responseBody(fileResponse).build();
  }

  private FileResponse createFileResponse(
      EwpSuccessOperationResult<byte[]> successOperationResult) {

    String mediaType = successOperationResult.getResponse().getMediaType();
    byte[] data = successOperationResult.getResponseBody();

    return new FileResponse(mediaType, data);
  }

  protected EwpFilesApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return Files.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
