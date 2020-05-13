package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.ounits.OunitsResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.utils.ewp.EwpApiUtils;

@Service
public class EwpOrganizationalUnitsClient {

  private RegistryClient registryClient;
  private EwpClient ewpClient;

  public EwpOrganizationalUnitsClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOrganizationalUnitApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return new ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO(
        apiConfiguration.getMaxOunitIds().intValueExact(),
        apiConfiguration.getMaxOunitCodes().intValueExact());
  }

  public EwpSuccessOperationResult<OunitsResponse> findByOunitIds(
      String heiId, Collection<String> organizationalUnitIds)
      throws AbstractEwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(
        EwpClientConstants.QUERY_ORGANIZATIONAL_UNIT_ID, new ArrayList<>(organizationalUnitIds));
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OunitsResponse.class);
  }

  public EwpSuccessOperationResult<OunitsResponse> findByOunitCodes(
      String heiId, Collection<String> organizationalUnitCodes)
      throws AbstractEwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(
        EwpClientConstants.QUERY_ORGANIZATIONAL_UNIT_CODE,
        new ArrayList<>(organizationalUnitCodes));
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OunitsResponse.class);
  }

  protected EwpOrganizationalUnitApiConfiguration getApiConfiguration(String heiId) {
    Optional<EwpOrganizationalUnitApiConfiguration> apiOptional =
        EwpApiUtils.getOrganizationalUnitApiConfiguration(registryClient, heiId);
    if (apiOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdException(heiId, EwpOrganizationalUnitApiConfiguration.API_NAME);
    }
    return apiOptional.get();
  }
}
