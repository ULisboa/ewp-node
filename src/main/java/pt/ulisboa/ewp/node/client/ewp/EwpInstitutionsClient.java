package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;

@Service
public class EwpInstitutionsClient {

  private final RegistryClient registryClient;
  private final EwpClient ewpClient;

  public EwpInstitutionsClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<InstitutionsResponseV2> find(String heiId)
      throws AbstractEwpClientErrorException {
    Optional<EwpInstitutionApiConfiguration> apiOptional =
        EwpApiUtils.getInstitutionApiConfiguration(registryClient, heiId);
    if (apiOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdException(heiId, EwpInstitutionApiConfiguration.API_NAME);
    }
    EwpInstitutionApiConfiguration api = apiOptional.get();

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, InstitutionsResponseV2.class);
  }
}
