package pt.ulisboa.ewp.node.client.ewp.iias.cnr;

import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrResponseV2;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.InterInstitutionalAgreementCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInterInstitutionalAgreementCnrV2Client
    extends EwpApiClient<EwpInterInstitutionalAgreementCnrApiConfiguration> {

  public EwpInterInstitutionalAgreementCnrV2Client(RegistryClient registryClient,
      EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<IiaCnrResponseV2> sendChangeNotification(
      String notifierHeiId, String partnerHeiId, List<String> iiaIds)
      throws EwpClientErrorException {
    EwpInterInstitutionalAgreementCnrApiConfiguration api = getApiConfigurationForHeiId(
        partnerHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.NOTIFIER_HEI_ID, notifierHeiId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataBody(bodyParams));
    return ewpClient.executeAndLog(request, IiaCnrResponseV2.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpInterInstitutionalAgreementCnrApiConfiguration>
  getApiVersionSpecification() {
    return InterInstitutionalAgreementCnr.V2;
  }
}
