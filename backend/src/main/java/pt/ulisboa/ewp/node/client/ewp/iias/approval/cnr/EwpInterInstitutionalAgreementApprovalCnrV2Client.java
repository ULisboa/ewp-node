package pt.ulisboa.ewp.node.client.ewp.iias.approval.cnr;

import eu.erasmuswithoutpaper.api.iias.approval.cnr.v2.IiaApprovalCnrResponseV2;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementApprovalCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementApprovalCnrV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpInterInstitutionalAgreementApprovalCnrV2Client(
      RegistryClient registryClient, EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<IiaApprovalCnrResponseV2> sendChangeNotification(
      String heiId, String iiaId) throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApprovalCnrApiConfiguration api =
        getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaId);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, IiaApprovalCnrResponseV2.class);
  }

  protected EwpInterInstitutionalAgreementApprovalCnrApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return InterInstitutionalAgreementApprovalCnr.V2.getConfigurationForHeiId(
        registryClient, heiId);
  }
}
