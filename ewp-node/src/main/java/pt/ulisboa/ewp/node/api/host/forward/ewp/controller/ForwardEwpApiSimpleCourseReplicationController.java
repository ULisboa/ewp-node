package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpSimpleCourseReplicationClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import eu.erasmuswithoutpaper.api.courses.replication.CourseReplicationResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/replication")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiSimpleCourseReplicationController
    extends AbstractForwardEwpApiController {

  @Autowired private EwpSimpleCourseReplicationClient client;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> simpleCourseReplicationGet(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE, required = false)
          String modifiedSince)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getCourses(heiId, modifiedSince);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Simple Course Replication Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> simpleCourseReplicationPost(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE, required = false)
          String modifiedSince)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getCourses(heiId, modifiedSince);
  }

  private ResponseEntity<?> getCourses(String heiId, String modifiedSince)
      throws EwpClientResponseAuthenticationFailedException, EwpClientProcessorException,
          EwpClientUnknownErrorResponseException, EwpClientErrorResponseException {
    EwpSuccessOperationResult<CourseReplicationResponse> courseReplicationResponse =
        client.findAllCourses(heiId, modifiedSince);
    return createResponseEntityFromOperationResult(courseReplicationResponse);
  }
}
