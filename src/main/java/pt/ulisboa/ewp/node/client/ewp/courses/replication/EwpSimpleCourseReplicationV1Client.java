package pt.ulisboa.ewp.node.client.ewp.courses.replication;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import java.time.ZonedDateTime;
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
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpSimpleCourseReplicationV1Client
    extends EwpApiClient<EwpSimpleCourseReplicationApiConfiguration> {

  public EwpSimpleCourseReplicationV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<CourseReplicationResponseV1> findAllCourses(
      String heiId, ZonedDateTime modifiedSince) throws AbstractEwpClientErrorException {
    EwpSimpleCourseReplicationApiConfiguration api = getApiConfigurationForHeiId(heiId);

    EwpRequest request = new EwpRequest(HttpMethod.GET, api.getUrl());
    request.authenticationMethod(EwpApiUtils.getBestSupportedApiAuthenticationMethod(api));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);
    request.queryParams(queryParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, CourseReplicationResponseV1.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpSimpleCourseReplicationApiConfiguration>
  getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.SIMPLE_COURSE_REPLICATION_V1;
  }
}
