package pt.ulisboa.ewp.node.client.ewp.iias;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

    HashMap<String, List<String>> bodyParams = new HashMap<>();
    bodyParams.put(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));

    if (partnerHeiId != null) {
      bodyParams.put(EwpApiParamConstants.PARTNER_HEI_ID, Collections.singletonList(partnerHeiId));
    }

    if (receivingAcademicYearIds != null) {
      bodyParams.put(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearIds);
    }

    if (modifiedSince != null) {
      bodyParams.put(
          EwpApiParamConstants.MODIFIED_SINCE,
          Collections.singletonList(DateTimeFormatter.ISO_DATE_TIME.format(modifiedSince)));
    }

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

    HashMap<String, List<String>> bodyParams = new HashMap<>();
    bodyParams.put(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));
    bodyParams.put(EwpApiParamConstants.IIA_ID, new ArrayList<>(iiaIds));

    if (sendPdf != null) {
      bodyParams.put(EwpApiParamConstants.SEND_PDF, Collections.singletonList(sendPdf.toString()));
    }

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

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));
    queryParams.put(EwpApiParamConstants.IIA_CODE, new ArrayList<>(iiaCodes));

    if (sendPdf != null) {
      queryParams.put(EwpApiParamConstants.SEND_PDF, Collections.singletonList(sendPdf.toString()));
    }

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
