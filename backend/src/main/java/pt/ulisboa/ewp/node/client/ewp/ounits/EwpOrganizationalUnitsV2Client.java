package pt.ulisboa.ewp.node.client.ewp.ounits;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ounits.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OrganizationalUnits;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOrganizationalUnitsV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpOrganizationalUnitsV2Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
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
      throws EwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.OUNIT_ID, organizationalUnitIds);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(OunitsResponseV2.class));
  }

  public EwpSuccessOperationResult<OunitsResponseV2> findByOunitCodes(
      String heiId, Collection<String> organizationalUnitCodes)
      throws EwpClientErrorException {
    EwpOrganizationalUnitApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.OUNIT_CODE, organizationalUnitCodes);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(OunitsResponseV2.class));
  }

  protected EwpOrganizationalUnitApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return OrganizationalUnits.V2.getConfigurationForHeiId(registryClient, heiId);
  }
}
