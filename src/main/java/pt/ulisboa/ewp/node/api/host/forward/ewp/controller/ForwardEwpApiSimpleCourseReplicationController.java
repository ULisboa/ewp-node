package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpSimpleCourseReplicationClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/replication")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiSimpleCourseReplicationController
    extends AbstractForwardEwpApiController {

  @Autowired private EwpSimpleCourseReplicationClient client;

  public ForwardEwpApiSimpleCourseReplicationController(RegistryClient registryClient) {
    super(registryClient);
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
    return EwpClientConstants.API_SIMPLE_COURSE_REPLICATION_NAME;
  }

  private static class SimpleCourseReplicationRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID,
        description = "HEI ID (SCHAC code) to look up")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID,
        description = "HEI ID (SCHAC code) to look up")
    @NotNull
    @Size(min = 1)
    private String heiId;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE,
        description = "Find LOS modified since a given date")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE,
        description = "Find LOS modified since a given date")
    @DateTimeFormat(iso = DATE_TIME)
    private ZonedDateTime modifiedSince;

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }

    public ZonedDateTime getModifiedSince() {
      return modifiedSince;
    }

    public void setModifiedSince(ZonedDateTime modifiedSince) {
      this.modifiedSince = modifiedSince;
    }
  }
}
