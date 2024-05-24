package pt.ulisboa.ewp.node.client.ewp.iias.approval;

import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementApprovals;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementsApprovalsV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpInterInstitutionalAgreementsApprovalsV2Client(
      RegistryClient registryClient, EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO
      getApiSpecification(String heiId) {
    EwpInterInstitutionalAgreementApprovalApiConfiguration apiConfiguration =
        getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO(
        apiConfiguration.getMaxIiaIds().intValueExact());
  }

  public EwpSuccessOperationResult<IiasApprovalResponseV2> getApprovals(
      String heiId, List<String> iiaIds) throws EwpClientErrorException {
    EwpInterInstitutionalAgreementApprovalApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(IiasApprovalResponseV2.class));
  }

  protected EwpInterInstitutionalAgreementApprovalApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return InterInstitutionalAgreementApprovals.V2.getConfigurationForHeiId(registryClient, heiId);
  }
}
