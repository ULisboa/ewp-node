package pt.ulisboa.ewp.node.client.ewp.iias.approval;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementApprovals;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementsApprovalsV1Client
    extends EwpApiClient<EwpInterInstitutionalAgreementApprovalApiConfiguration> {

  public EwpInterInstitutionalAgreementsApprovalsV1Client(
      RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpInterInstitutionalAgreementApprovalApiConfiguration apiConfiguration =
        getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO(
        apiConfiguration.getMaxIiaIds().intValueExact());
  }

  public EwpSuccessOperationResult<IiasApprovalResponseV1> getApprovals(
      String approvingHeiId,
      String ownerHeiId,
      List<String> iiaIds,
      Boolean sendPdf)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApprovalApiConfiguration api = getApiConfigurationForHeiId(
        approvingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    bodyParams.param(EwpApiParamConstants.OWNER_HEI_ID, ownerHeiId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataBody(bodyParams));
    return ewpClient.executeAndLog(request, IiasApprovalResponseV1.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpInterInstitutionalAgreementApprovalApiConfiguration>
  getApiVersionSpecification() {
    return InterInstitutionalAgreementApprovals.V1;
  }
}
