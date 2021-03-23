package pt.ulisboa.ewp.node.client.ewp.ounits;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import java.util.Collection;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOrganizationalUnitsV2Client
    extends EwpApiClient<EwpOrganizationalUnitApiConfiguration> {

  public EwpOrganizationalUnitsV2Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOrganizationalUnitApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO(
        apiConfiguration.getMaxOunitIds().intValueExact(),
        apiConfiguration.getMaxOunitCodes().intValueExact());
  }

  public EwpSuccessOperationResult<OunitsResponseV2> findByOunitIds(
      String heiId, Collection<String> organizationalUnitIds)
      throws AbstractEwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfigurationForHeiId(heiId);

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
    EwpOrganizationalUnitApiConfiguration api = getApiConfigurationForHeiId(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, organizationalUnitCodes);
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OunitsResponseV2.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpOrganizationalUnitApiConfiguration>
  getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.ORGANIZATIONAL_UNITS_V2;
  }
}
