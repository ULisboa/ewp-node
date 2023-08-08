package pt.ulisboa.ewp.node.client.ewp.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import java.util.Collections;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Institutions;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInstitutionsV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpInstitutionsV2Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<InstitutionsResponseV2> find(String heiId)
      throws EwpClientErrorException {
    EwpInstitutionApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));

    EwpRequest request = EwpRequest.createGet(api, api.getUrl(), queryParams);
    return ewpHttpClient.execute(request, InstitutionsResponseV2.class);
  }

  protected EwpInstitutionApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return Institutions.V2.getConfigurationForHeiId(registryClient, heiId);
  }
}
