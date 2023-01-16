package pt.ulisboa.ewp.node.client.ewp.courses.replication;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.SimpleCourseReplication;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpSimpleCourseReplicationV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpSimpleCourseReplicationV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<CourseReplicationResponseV1> findAllCourses(
      String heiId, ZonedDateTime modifiedSince) throws EwpClientErrorException {
    EwpSimpleCourseReplicationApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createGet(api, api.getUrl(), queryParams);
    return ewpHttpClient.execute(request, CourseReplicationResponseV1.class);
  }

  protected EwpSimpleCourseReplicationApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return SimpleCourseReplication.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
