package pt.ulisboa.ewp.node.client.ewp.courses;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses.ForwardEwpApiCoursesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Courses;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpCoursesV0Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpCoursesV0Client(RegistryClient registryClient, EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiCoursesApiSpecificationResponseDTO getApiSpecification(String heiId) {
    EwpCourseApiConfiguration api = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiCoursesApiSpecificationResponseDTO(
        api.getMaxLosIds().intValueExact(), api.getMaxLosCodes().intValueExact());
  }

  public EwpSuccessOperationResult<CoursesResponseV0> findByLosIds(
      String heiId,
      Collection<String> losIds,
      LocalDate loisBeforeDate,
      LocalDate loisAfterDate,
      LocalDate losAtDate)
      throws EwpClientErrorException {
    EwpCourseApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.LOS_ID, losIds);
    queryParams.param(EwpApiParamConstants.LOIS_BEFORE, loisBeforeDate);
    queryParams.param(EwpApiParamConstants.LOIS_AFTER, loisAfterDate);
    queryParams.param(EwpApiParamConstants.LOS_AT_DATE, losAtDate);

    EwpRequest request = EwpRequest.createGet(api, "", api.getUrl(), queryParams);
    return ewpHttpClient.execute(request, CoursesResponseV0.class);
  }

  public EwpSuccessOperationResult<CoursesResponseV0> findByLosCodes(
      String heiId,
      Collection<String> losCodes,
      LocalDate loisBeforeDate,
      LocalDate loisAfterDate,
      LocalDate losAtDate)
      throws EwpClientErrorException {
    EwpCourseApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.LOS_CODE, losCodes);
    queryParams.param(EwpApiParamConstants.LOIS_BEFORE, loisBeforeDate);
    queryParams.param(EwpApiParamConstants.LOIS_AFTER, loisAfterDate);
    queryParams.param(EwpApiParamConstants.LOS_AT_DATE, losAtDate);

    EwpRequest request = EwpRequest.createGet(api, "", api.getUrl(), queryParams);
    return ewpHttpClient.execute(request, CoursesResponseV0.class);
  }

  protected EwpCourseApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return Courses.V0.getConfigurationForHeiId(registryClient, heiId);
  }
}
