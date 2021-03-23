package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import java.util.Collection;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

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

  public EwpSuccessOperationResult<OunitsResponseV2> findByOunitIds(
      String heiId, Collection<String> organizationalUnitIds)
      throws AbstractEwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_ID, organizationalUnitIds);
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OunitsResponseV2.class);
  }

  public EwpSuccessOperationResult<OunitsResponseV2> findByOunitCodes(
      String heiId, Collection<String> organizationalUnitCodes)
      throws AbstractEwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, organizationalUnitCodes);
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OunitsResponseV2.class);
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
