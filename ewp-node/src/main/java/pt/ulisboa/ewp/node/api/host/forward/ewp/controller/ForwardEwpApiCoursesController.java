package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.util.ArrayList;
import java.util.List;

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
import pt.ulisboa.ewp.node.client.ewp.EwpCoursesClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import eu.erasmuswithoutpaper.api.courses.CoursesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiCoursesController extends AbstractForwardEwpApiController {

  @Autowired private EwpCoursesClient client;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> coursesGet(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID, defaultValue = "")
          List<String> losIds,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE, defaultValue = "")
          List<String> losCodes,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE, required = false)
          String loisBefore,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER, required = false)
          String loisAfter,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AT_DATE, required = false)
          String loisAtDate)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getCourses(heiId, losIds, losCodes, loisBefore, loisAfter, loisAtDate);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> coursesPost(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID, required = false)
          List<String> losIds,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE, required = false)
          List<String> losCodes,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE, required = false)
          String loisBefore,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER, required = false)
          String loisAfter,
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AT_DATE, required = false)
          String loisAtDate)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {

    if (losIds == null) {
      losIds = new ArrayList<>();
    }
    if (losCodes == null) {
      losCodes = new ArrayList<>();
    }
    return getCourses(heiId, losIds, losCodes, loisBefore, loisAfter, loisAtDate);
  }

  // NOTE: currently only allows to obtain by LOS IDs or LOS codes (not both simultaneously)
  private ResponseEntity<?> getCourses(
      String heiId,
      List<String> losIds,
      List<String> losCodes,
      String loisBefore,
      String loisAfter,
      String loisAtDate)
      throws EwpClientResponseAuthenticationFailedException, EwpClientProcessorException,
          EwpClientUnknownErrorResponseException, EwpClientErrorResponseException {
    EwpSuccessOperationResult<CoursesResponse> coursesResponse;
    if (!losIds.isEmpty()) {
      coursesResponse = client.findByLosIds(heiId, losIds, loisBefore, loisAfter, loisAtDate);
    } else {
      coursesResponse = client.findByLosCodes(heiId, losCodes, loisBefore, loisAfter, loisAtDate);
    }
    return createResponseEntityFromOperationResult(coursesResponse);
  }
}
