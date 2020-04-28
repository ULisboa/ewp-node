package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.utils.ewp.EwpApiUtils;

@Service
public class EwpInstitutionsClient {

  private RegistryClient registryClient;
  private EwpClient ewpClient;

  public EwpInstitutionsClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<InstitutionsResponse> find(String heiId)
      throws AbstractEwpClientErrorException {
    Optional<EwpInstitutionApiConfiguration> apiOptional =
        EwpApiUtils.getInstitutionApiConfiguration(registryClient, heiId);
    if (!apiOptional.isPresent()) {
      throw new NoEwpApiForHeiIdException(heiId, EwpInstitutionApiConfiguration.API_NAME);
    }
    EwpInstitutionApiConfiguration api = apiOptional.get();

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, InstitutionsResponse.class);
  }
}
