package pt.ulisboa.ewp.node.client.ewp.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import java.util.Collections;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Institutions;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInstitutionsV2Client extends EwpApiClient<EwpInstitutionApiConfiguration> {

  public EwpInstitutionsV2Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<InstitutionsResponseV2> find(String heiId)
      throws EwpClientErrorException {
    EwpInstitutionApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));

    EwpRequest request = EwpRequest.createGet(api, api.getUrl(), queryParams);
    return ewpClient.executeAndLog(request, InstitutionsResponseV2.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpInstitutionApiConfiguration>
  getApiVersionSpecification() {
    return Institutions.V2;
  }
}
