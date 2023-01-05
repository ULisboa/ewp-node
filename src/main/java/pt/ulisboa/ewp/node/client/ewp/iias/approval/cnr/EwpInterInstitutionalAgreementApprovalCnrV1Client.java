package pt.ulisboa.ewp.node.client.ewp.iias.approval.cnr;

import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrResponseV1;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementApprovalCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementApprovalCnrV1Client
    extends EwpApiClient<EwpInterInstitutionalAgreementApprovalCnrApiConfiguration> {

  public EwpInterInstitutionalAgreementApprovalCnrV1Client(RegistryClient registryClient,
      EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<IiaApprovalCnrResponseV1> sendChangeNotification(
      String approvingHeiId, String partnerHeiId, String ownerHerId, String iiaId)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApprovalCnrApiConfiguration api = getApiConfigurationForHeiId(
        partnerHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    bodyParams.param(EwpApiParamConstants.OWNER_HEI_ID, ownerHerId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaId);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.execute(request, IiaApprovalCnrResponseV1.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpInterInstitutionalAgreementApprovalCnrApiConfiguration>
  getApiVersionSpecification() {
    return InterInstitutionalAgreementApprovalCnr.V1;
  }
}
