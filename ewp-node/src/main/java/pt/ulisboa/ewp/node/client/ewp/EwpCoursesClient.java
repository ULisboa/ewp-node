package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.courses.CoursesResponse;
import java.time.LocalDate;
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
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.utils.ewp.EwpApiUtils;

@Service
public class EwpCoursesClient {

  private RegistryClient registryClient;
  private EwpClient ewpClient;

  public EwpCoursesClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<CoursesResponse> findByLosIds(
      String heiId,
      Collection<String> losIds,
      LocalDate loisBeforeDate,
      LocalDate loisAfterDate,
      LocalDate loisAtDate)
      throws AbstractEwpClientErrorException {
    Optional<EwpCourseApiConfiguration> apiOptional =
        EwpApiUtils.getCourseApiConfiguration(registryClient, heiId);
    if (!apiOptional.isPresent()) {
      throw new NoEwpApiForHeiIdException(heiId, EwpCourseApiConfiguration.API_NAME);
    }
    EwpCourseApiConfiguration api = apiOptional.get();

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(EwpClientConstants.QUERY_LOS_ID, new ArrayList<>(losIds));
    if (loisBeforeDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_BEFORE,
          Collections.singletonList(loisBeforeDate.toString()));
    }
    if (loisAfterDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_AFTER, Collections.singletonList(loisAfterDate.toString()));
    }
    if (loisAtDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_AT_DATE, Collections.singletonList(loisAtDate.toString()));
    }
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, CoursesResponse.class);
  }

  public EwpSuccessOperationResult<CoursesResponse> findByLosCodes(
      String heiId,
      Collection<String> losCodes,
      LocalDate loisBeforeDate,
      LocalDate loisAfterDate,
      LocalDate loisAtDate)
      throws AbstractEwpClientErrorException {
    Optional<EwpCourseApiConfiguration> apiOptional =
        EwpApiUtils.getCourseApiConfiguration(registryClient, heiId);
    if (!apiOptional.isPresent()) {
      throw new NoEwpApiForHeiIdException(heiId, EwpCourseApiConfiguration.API_NAME);
    }
    EwpCourseApiConfiguration api = apiOptional.get();

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    queryParams.put(EwpClientConstants.QUERY_LOS_CODE, new ArrayList<>(losCodes));
    if (loisBeforeDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_BEFORE,
          Collections.singletonList(loisBeforeDate.toString()));
    }
    if (loisAfterDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_AFTER, Collections.singletonList(loisAfterDate.toString()));
    }
    if (loisAtDate != null) {
      queryParams.put(
          EwpClientConstants.QUERY_LOIS_AT_DATE, Collections.singletonList(loisAtDate.toString()));
    }
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, CoursesResponse.class);
  }
}
