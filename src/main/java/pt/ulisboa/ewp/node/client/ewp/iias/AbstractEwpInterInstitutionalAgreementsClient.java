package pt.ulisboa.ewp.node.client.ewp.iias;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdAndMajorVersionException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public abstract class AbstractEwpInterInstitutionalAgreementsClient {

  private final RegistryClient registryClient;
  private final EwpClient ewpClient;

  public AbstractEwpInterInstitutionalAgreementsClient(
      RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  protected <T extends Serializable> EwpSuccessOperationResult<T> findAllByHeiIds(
      String heiId,
      String partnerHeiId,
      List<String> receivingAcademicYearIds,
      ZonedDateTime modifiedSince,
      EwpInterinstitutionalAgreementApiConfiguration apiConfiguration,
      Class<T> responseType)
      throws AbstractEwpClientErrorException {
    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getIndexUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.PARTNER_HEI_ID, partnerHeiId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearIds);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);
    request.bodyParams(bodyParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, responseType);
  }

  protected <T extends Serializable> EwpSuccessOperationResult<T> findByHeiIdAndIiaIds(
      String heiId,
      Collection<String> iiaIds,
      Boolean sendPdf,
      EwpInterinstitutionalAgreementApiConfiguration apiConfiguration,
      Class<T> responseType)
      throws AbstractEwpClientErrorException {
    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getGetUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);
    bodyParams.param(EwpApiParamConstants.IIA_ID, iiaIds);
    bodyParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);
    request.bodyParams(bodyParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, responseType);
  }

  protected <T extends Serializable> EwpSuccessOperationResult<T> findByHeiIdAndIiaCodes(
      String heiId,
      Collection<String> iiaCodes,
      Boolean sendPdf,
      EwpInterinstitutionalAgreementApiConfiguration apiConfiguration,
      Class<T> responseType)
      throws AbstractEwpClientErrorException {
    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getGetUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.IIA_CODE, iiaCodes);
    queryParams.param(EwpApiParamConstants.SEND_PDF, sendPdf);
    request.bodyParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, responseType);
  }

  public <T> EwpInterinstitutionalAgreementApiConfiguration getApiConfiguration(
      String heiId,
      int wantedMajorVersion,
      Class<T> apiConfigurationElementClassType,
      Function<T, EwpInterinstitutionalAgreementApiConfiguration> apiConfigurationTransformer) {
    Optional<T> apiElementOptional =
        EwpApiUtils.getApiElement(
            registryClient,
            heiId,
            EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME,
            wantedMajorVersion,
            apiConfigurationElementClassType);
    if (apiElementOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdAndMajorVersionException(
          heiId, EwpInterinstitutionalAgreementApiConfiguration.API_NAME, wantedMajorVersion);
    }
    return apiConfigurationTransformer.apply(apiElementOptional.get());
  }
}
