package pt.ulisboa.ewp.node.client.ewp.iias.cnr;

import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrResponseV3;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementCnrV3Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpInterInstitutionalAgreementCnrV3Client(
      RegistryClient registryClient, EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<IiaCnrResponseV3> sendChangeNotification(String partnerHeiId, List<String> iiaIds)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementCnrApiConfiguration api =
        getApiConfigurationForHeiId(partnerHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createWithOptionalType(IiaCnrResponseV3.class));
  }

  protected EwpInterInstitutionalAgreementCnrApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return InterInstitutionalAgreementCnr.V3.getConfigurationForHeiId(registryClient, heiId);
  }
}
