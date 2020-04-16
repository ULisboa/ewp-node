package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import eu.erasmuswithoutpaper.api.courses.CoursesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpCoursesClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiCoursesController extends AbstractForwardEwpApiController {

  @Autowired private EwpCoursesClient client;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<ForwardEwpApiResponse> coursesGet(
      @Valid @ParameterObject @RequestParam CoursesRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getCourses(
        requestDto.getHeiId(),
        requestDto.getLosIds(),
        requestDto.getLosCodes(),
        requestDto.getLoisBefore(),
        requestDto.getLoisAfter(),
        requestDto.getLoisAtDate());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<ForwardEwpApiResponse> coursesPost(@Valid CoursesRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getCourses(
        requestDto.getHeiId(),
        requestDto.getLosIds(),
        requestDto.getLosCodes(),
        requestDto.getLoisBefore(),
        requestDto.getLoisAfter(),
        requestDto.getLoisAtDate());
  }

  // NOTE: currently only allows to obtain by LOS IDs or LOS codes (not both simultaneously)
  private ResponseEntity<ForwardEwpApiResponse> getCourses(
      String heiId,
      List<String> losIds,
      List<String> losCodes,
      LocalDate loisBefore,
      LocalDate loisAfter,
      LocalDate loisAtDate)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<CoursesResponse> coursesResponse;
    if (!losIds.isEmpty()) {
      coursesResponse = client.findByLosIds(heiId, losIds, loisBefore, loisAfter, loisAtDate);
    } else {
      coursesResponse = client.findByLosCodes(heiId, losCodes, loisBefore, loisAfter, loisAtDate);
    }
    return createResponseEntityFromOperationResult(coursesResponse);
  }

  private static class CoursesRequestDto {

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

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID,
        description =
            "Must be set if no "
                + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE
                + " is provided.")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID,
        description =
            "Must be set if no "
                + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE
                + " is provided.")
    private List<String> losIds = new ArrayList<>();

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE,
        description =
            "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID + " is provided.")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE,
        description =
            "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID + " is provided.")
    private List<String> losCodes = new ArrayList<>();

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE,
        description = "Look up LOIS before a given date")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE,
        description = "Look up LOIS before a given date")
    @DateTimeFormat(iso = DATE)
    private LocalDate loisBefore;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER,
        description = "Look up LOIS after a given date")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER,
        description = "Look up LOIS after a given date")
    @DateTimeFormat(iso = DATE)
    private LocalDate loisAfter;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AT_DATE)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AT_DATE,
        description = "Look up LOIS at a given date")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AT_DATE,
        description = "Look up LOIS at a given date")
    @DateTimeFormat(iso = DATE)
    private LocalDate loisAtDate;

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }

    public List<String> getLosIds() {
      return losIds;
    }

    public void setLosIds(List<String> losIds) {
      this.losIds = losIds;
    }

    public List<String> getLosCodes() {
      return losCodes;
    }

    public void setLosCodes(List<String> losCodes) {
      this.losCodes = losCodes;
    }

    public LocalDate getLoisBefore() {
      return loisBefore;
    }

    public void setLoisBefore(LocalDate loisBefore) {
      this.loisBefore = loisBefore;
    }

    public LocalDate getLoisAfter() {
      return loisAfter;
    }

    public void setLoisAfter(LocalDate loisAfter) {
      this.loisAfter = loisAfter;
    }

    public LocalDate getLoisAtDate() {
      return loisAtDate;
    }

    public void setLoisAtDate(LocalDate loisAtDate) {
      this.loisAtDate = loisAtDate;
    }
  }
}
