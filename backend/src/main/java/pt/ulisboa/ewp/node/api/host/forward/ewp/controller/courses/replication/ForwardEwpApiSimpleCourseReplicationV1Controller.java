package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.courses.replication;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses.replication.SimpleCourseReplicationRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.courses.replication.EwpSimpleCourseReplicationV1Client;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.SIMPLE_COURSE_REPLICATION)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/replication/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiSimpleCourseReplicationV1Controller
    extends AbstractForwardEwpApiController {

  private final EwpSimpleCourseReplicationV1Client client;

  public ForwardEwpApiSimpleCourseReplicationV1Controller(
      RegistryClient registryClient, EwpSimpleCourseReplicationV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "simple-course-replication",
      apiMajorVersion = 1,
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Simple Course Replication"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>>
      simpleCourseReplicationGet(
          @Valid @ParameterObject @RequestParam SimpleCourseReplicationRequestDto requestDto)
          throws EwpClientErrorException {
    return getCourses(requestDto);
  }

  @ForwardEwpApiEndpoint(
      api = "simple-course-replication",
      apiMajorVersion = 1,
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Simple Course Replication"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>>
      simpleCourseReplicationPost(@Valid SimpleCourseReplicationRequestDto requestDto)
          throws EwpClientErrorException {
    return getCourses(requestDto);
  }

  private ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>> getCourses(
      SimpleCourseReplicationRequestDto requestDto) throws EwpClientErrorException {
    EwpSuccessOperationResult<CourseReplicationResponseV1> courseReplicationResponse =
        client.findAllCourses(requestDto.getHeiId(), requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(courseReplicationResponse);
  }
}
