package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.courses.replication.CourseReplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.time.ZonedDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpSimpleCourseReplicationClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/replication")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiSimpleCourseReplicationController
    extends AbstractForwardEwpApiController {

  @Autowired private EwpSimpleCourseReplicationClient client;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> simpleCourseReplicationGet(
      @Valid @RequestParam SimpleCourseReplicationRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getCourses(requestDto.getHeiId(), requestDto.getModifiedSince());
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> simpleCourseReplicationPost(
      @Valid @RequestParam SimpleCourseReplicationRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getCourses(requestDto.getHeiId(), requestDto.getModifiedSince());
  }

  private ResponseEntity<?> getCourses(String heiId, ZonedDateTime modifiedSince)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<CourseReplicationResponse> courseReplicationResponse =
        client.findAllCourses(heiId, modifiedSince);
    return createResponseEntityFromOperationResult(courseReplicationResponse);
  }

  private static class SimpleCourseReplicationRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @NotNull
    @Size(min = 1)
    private String heiId;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE)
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
