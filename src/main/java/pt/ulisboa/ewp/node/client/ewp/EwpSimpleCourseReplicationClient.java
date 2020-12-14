package pt.ulisboa.ewp.node.client.ewp;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;

@Service
public class EwpSimpleCourseReplicationClient {

  private RegistryClient registryClient;
  private EwpClient ewpClient;

  public EwpSimpleCourseReplicationClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public EwpSuccessOperationResult<CourseReplicationResponseV1> findAllCourses(
      String heiId, ZonedDateTime modifiedSince) throws AbstractEwpClientErrorException {
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
    queryParams.put(EwpApiParamConstants.HEI_ID, Collections.singletonList(heiId));
    if (modifiedSince != null) {
      queryParams.put(
          EwpApiParamConstants.MODIFIED_SINCE,
          Collections.singletonList(DateTimeFormatter.ISO_DATE_TIME.format(modifiedSince)));
    }
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, CourseReplicationResponseV1.class);
  }
}
