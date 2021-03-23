package pt.ulisboa.ewp.node.client.ewp.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import java.util.Collections;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpInstitutionsV2Client extends EwpApiClient<EwpInstitutionApiConfiguration> {

  public EwpInstitutionsV2Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<InstitutionsResponseV2> find(String heiId)
      throws AbstractEwpClientErrorException {
    EwpInstitutionApiConfiguration api = getApiConfigurationForHeiId(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, InstitutionsResponseV2.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpInstitutionApiConfiguration> getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.INSTITUTIONS_V2;
  }
}
