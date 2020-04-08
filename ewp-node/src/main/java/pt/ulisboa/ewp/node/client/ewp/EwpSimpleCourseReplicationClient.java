package pt.ulisboa.ewp.node.client.ewp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.utils.ewp.EwpApiUtils;
import eu.erasmuswithoutpaper.api.courses.replication.CourseReplicationResponse;

@Service
public class EwpSimpleCourseReplicationClient {

  private RegistryClient registryClient;
  private EwpClient ewpClient;

  public EwpSimpleCourseReplicationClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<CourseReplicationResponse> findAllCourses(
      String heiId, String modifiedSince)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    Optional<EwpSimpleCourseReplicationApiConfiguration> apiOptional =
        EwpApiUtils.getSimpleCourseReplicationApiConfiguration(registryClient, heiId);
    if (!apiOptional.isPresent()) {
      throw new NoEwpApiForHeiIdException(
          heiId, EwpSimpleCourseReplicationApiConfiguration.API_NAME);
    }
    EwpSimpleCourseReplicationApiConfiguration api = apiOptional.get();

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HashMap<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(EwpClientConstants.QUERY_HEI_ID, Collections.singletonList(heiId));
    if (modifiedSince != null) {
      queryParams.put(
          EwpClientConstants.QUERY_MODIFIED_SINCE, Collections.singletonList(modifiedSince));
    }
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, CourseReplicationResponse.class);
  }
}
