package pt.ulisboa.ewp.node.client.ewp.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v1.OmobilitiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesIndexResponseV1;
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
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdAndMajorVersionException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilitiesApiConfiguration;

@Service
public class EwpOutgoingMobilitiesV1Client {

  private static final int API_MAJOR_VERSION = 1;

  private final RegistryClient registryClient;
  private final EwpClient ewpClient;

  public EwpOutgoingMobilitiesV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilitiesApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return new ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilitiesIndexResponseV1> findAllBySendingHeiId(
      String sendingHeiId,
      List<String> receivingHeiIds,
      String receivingAcademicYearId,
      ZonedDateTime modifiedSince)
      throws AbstractEwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration apiConfiguration = getApiConfiguration(sendingHeiId);

    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getIndexUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HashMap<String, List<String>> bodyParams = new HashMap<>();
    bodyParams.put(EwpApiParamConstants.SENDING_HEI_ID, Collections.singletonList(sendingHeiId));

    if (receivingHeiIds != null && !receivingHeiIds.isEmpty()) {
      bodyParams.put(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiIds);
    }

    if (receivingAcademicYearId != null) {
      bodyParams.put(
          EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID,
          Collections.singletonList(receivingAcademicYearId));
    }

    if (modifiedSince != null) {
      bodyParams.put(
          EwpApiParamConstants.MODIFIED_SINCE,
          Collections.singletonList(DateTimeFormatter.ISO_DATE_TIME.format(modifiedSince)));
    }

    request.bodyParams(bodyParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OmobilitiesIndexResponseV1.class);
  }

  public EwpSuccessOperationResult<OmobilitiesGetResponseV1> findBySendingHeiIdAndOmobilityIds(
      String sendingHeiId, Collection<String> omobilityIds) throws AbstractEwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration apiConfiguration = getApiConfiguration(sendingHeiId);

    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getGetUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HashMap<String, List<String>> bodyParams = new HashMap<>();
    bodyParams.put(EwpApiParamConstants.SENDING_HEI_ID, Collections.singletonList(sendingHeiId));
    bodyParams.put(EwpApiParamConstants.OMOBILITY_ID, new ArrayList<>(omobilityIds));

    request.bodyParams(bodyParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, OmobilitiesGetResponseV1.class);
  }

  public EwpOutgoingMobilitiesApiConfiguration getApiConfiguration(String heiId) {
    Optional<OmobilitiesV1> apiElementOptional =
        EwpApiUtils.getApiElement(
            registryClient,
            heiId,
            EwpApiConstants.API_OUTGOING_MOBILITIES_NAME,
            API_MAJOR_VERSION,
            OmobilitiesV1.class);
    if (apiElementOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdAndMajorVersionException(
          heiId, EwpInterinstitutionalAgreementApiConfiguration.API_NAME, API_MAJOR_VERSION);
    }
    OmobilitiesV1 apiElement = apiElementOptional.get();

    return new EwpOutgoingMobilitiesApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds(),
        apiElement.getSendsNotifications() != null);
  }
}
