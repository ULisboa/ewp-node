package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.courses.replication;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.time.ZonedDateTime;
import javax.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses.replication.SimpleCourseReplicationRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.courses.replication.EwpSimpleCourseReplicationV1Client;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/replication/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiSimpleCourseReplicationV1Controller
    extends AbstractForwardEwpApiController {

  private final EwpSimpleCourseReplicationV1Client client;

  public ForwardEwpApiSimpleCourseReplicationV1Controller(
      RegistryClient registryClient, EwpSimpleCourseReplicationV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Simple Course Replication"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>>
      simpleCourseReplicationGet(
          @Valid @ParameterObject @RequestParam SimpleCourseReplicationRequestDto requestDto)
          throws AbstractEwpClientErrorException {
    return getCourses(requestDto.getHeiId(), requestDto.getModifiedSince());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Simple Course Replication"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>>
      simpleCourseReplicationPost(@Valid SimpleCourseReplicationRequestDto requestDto)
          throws AbstractEwpClientErrorException {
    return getCourses(requestDto.getHeiId(), requestDto.getModifiedSince());
  }

  private ResponseEntity<ForwardEwpApiResponseWithData<CourseReplicationResponseV1>> getCourses(
      String heiId, ZonedDateTime modifiedSince) throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<CourseReplicationResponseV1> courseReplicationResponse =
        client.findAllCourses(heiId, modifiedSince);
    return createResponseEntityFromOperationResult(courseReplicationResponse);
  }

  @Override
  public String getApiLocalName() {
    return EwpApiConstants.API_SIMPLE_COURSE_REPLICATION_NAME;
  }
}
