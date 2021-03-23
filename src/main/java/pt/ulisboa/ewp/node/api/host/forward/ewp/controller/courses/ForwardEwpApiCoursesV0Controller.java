package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.courses;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiCoursesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses.CoursesRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.courses.EwpCoursesV0Client;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "courses/v0")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiCoursesV0Controller extends AbstractForwardEwpApiController {

  private final EwpCoursesV0Client client;

  public ForwardEwpApiCoursesV0Controller(RegistryClient registryClient,
      EwpCoursesV0Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Returns the specification for the API when considering a given HEI ID.",
      description =
          "The specification returned contains the maximum number of LOS IDs and codes that the target HEI ID accepts per request.",
      tags = {"Courses"})
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiCoursesApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    ForwardEwpApiCoursesApiSpecificationResponseDTO apiSpecification =
        client.getApiSpecification(heiId);
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(apiSpecification));
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Courses"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CoursesResponseV0>> coursesGet(
      @Valid @ParameterObject @RequestParam CoursesRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getCourses(
        requestDto.getHeiId(),
        requestDto.getLosIds(),
        requestDto.getLosCodes(),
        requestDto.getLoisBefore(),
        requestDto.getLoisAfter(),
        requestDto.getLosAtDate());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Courses Forward API.",
      tags = {"Courses"})
  public ResponseEntity<ForwardEwpApiResponseWithData<CoursesResponseV0>> coursesPost(
      @Valid CoursesRequestDto requestDto) throws AbstractEwpClientErrorException {
    return getCourses(
        requestDto.getHeiId(),
        requestDto.getLosIds(),
        requestDto.getLosCodes(),
        requestDto.getLoisBefore(),
        requestDto.getLoisAfter(),
        requestDto.getLosAtDate());
  }

  // NOTE: currently only allows to obtain by LOS IDs or LOS codes (not both simultaneously)
  private ResponseEntity<ForwardEwpApiResponseWithData<CoursesResponseV0>> getCourses(
      String heiId,
      List<String> losIds,
      List<String> losCodes,
      LocalDate loisBefore,
      LocalDate loisAfter,
      LocalDate losAtDate)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<CoursesResponseV0> coursesResponse;
    if (!losIds.isEmpty()) {
      coursesResponse = client.findByLosIds(heiId, losIds, loisBefore, loisAfter, losAtDate);
    } else {
      coursesResponse = client.findByLosCodes(heiId, losCodes, loisBefore, loisAfter, losAtDate);
    }
    return createResponseEntityFromOperationResult(coursesResponse);
  }

  @Override
  public String getApiLocalName() {
    throw new UnsupportedOperationException();
  }
}
