package pt.ulisboa.ewp.node.client.ewp.files;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFilesApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Files;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpFilesV1Client extends
    EwpApiClient<EwpFilesApiConfiguration> {

  public EwpFilesV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<FileResponse> getFile(String heiId, String fileId)
      throws EwpClientErrorException {
    EwpFilesApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.FILE_ID, fileId);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    EwpSuccessOperationResult<byte[]> successOperationResult = ewpClient.execute(
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

  @Override
  public EwpApiVersionSpecification<?, EwpFilesApiConfiguration> getApiVersionSpecification() {
    return Files.V1;
  }
}
