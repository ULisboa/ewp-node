package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasIndexResponseV4;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.utils.ewp.EwpApiUtils;

@Service
public class EwpInterInstitutionalAgreementsClient {

  private final RegistryClient registryClient;
  private final EwpClient ewpClient;

  public EwpInterInstitutionalAgreementsClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<IiasIndexResponseV4> findAllByHeiIds(
      String heiId,
      String partnerHeiId,
      List<String> receivingAcademicYearIds,
      ZonedDateTime modifiedSince)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getIndexUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));

    if (partnerHeiId != null) {
      queryParams.put(
          EwpClientConstants.QUERY_PARTNER_HEI_ID, Collections.singletonList(partnerHeiId));
    }

    if (receivingAcademicYearIds != null) {
      queryParams.put(
          EwpClientConstants.QUERY_RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearIds);
    }

    if (modifiedSince != null) {
      queryParams.put(
          EwpClientConstants.QUERY_MODIFIED_SINCE,
          Collections.singletonList(DateTimeFormatter.ISO_DATE_TIME.format(modifiedSince)));
    }

    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasIndexResponseV4.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV4> findByHeiIdAndIiaIds(
      String heiId, Collection<String> iiaIds, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getGetUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(EwpClientConstants.QUERY_IIA_ID, new ArrayList<>(iiaIds));

    if (sendPdf != null) {
      queryParams.put(
          EwpClientConstants.QUERY_SEND_PDF, Collections.singletonList(sendPdf.toString()));
    }

    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasGetResponseV4.class);
  }

  public EwpSuccessOperationResult<IiasGetResponseV4> findByHeiIdAndIiaCodes(
      String heiId, Collection<String> iiaCodes, Boolean sendPdf)
      throws AbstractEwpClientErrorException {
    EwpInterinstitutionalAgreementApiConfiguration api = getApiConfiguration(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getGetUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(EwpClientConstants.QUERY_IIA_CODE, new ArrayList<>(iiaCodes));

    if (sendPdf != null) {
      queryParams.put(
          EwpClientConstants.QUERY_SEND_PDF, Collections.singletonList(sendPdf.toString()));
    }

    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, IiasGetResponseV4.class);
  }

  protected EwpInterinstitutionalAgreementApiConfiguration getApiConfiguration(String heiId) {
    Optional<EwpInterinstitutionalAgreementApiConfiguration> apiOptional =
        EwpApiUtils.getInterinstitutionalAgreementApiConfiguration(registryClient, heiId);
    if (apiOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdException(
          heiId, EwpInterinstitutionalAgreementApiConfiguration.API_NAME);
    }
    return apiOptional.get();
  }
}
