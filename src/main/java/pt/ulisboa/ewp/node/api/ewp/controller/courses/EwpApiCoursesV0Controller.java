package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.CoursesV0HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiCoursesV0Controller.BASE_PATH)
public class EwpApiCoursesV0Controller {

  public static final String BASE_PATH = "courses/v0";

  private final HostPluginManager hostPluginManager;

  public EwpApiCoursesV0Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Courses API.",
      tags = {"ewp"})
  public ResponseEntity<CoursesResponseV0> coursesGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.LOS_ID, required = false)
          List<String> losIds,
      @RequestParam(value = EwpApiParamConstants.LOS_CODE, required = false)
          List<String> losCodes,
      @RequestParam(value = EwpApiParamConstants.LOIS_BEFORE, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate loisBefore,
      @RequestParam(value = EwpApiParamConstants.LOIS_AFTER, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate loisAfter,
      @RequestParam(value = EwpApiParamConstants.LOS_AT_DATE, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate losAtDate) {
    return courses(heiId, losIds, losCodes, loisBefore, loisAfter, losAtDate);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Courses API.",
      tags = {"ewp"})
  public ResponseEntity<CoursesResponseV0> coursesPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.LOS_ID, required = false)
          List<String> losIds,
      @RequestParam(value = EwpApiParamConstants.LOS_CODE, required = false)
          List<String> losCodes,
      @RequestParam(value = EwpApiParamConstants.LOIS_BEFORE, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate loisBefore,
      @RequestParam(value = EwpApiParamConstants.LOIS_AFTER, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate loisAfter,
      @RequestParam(value = EwpApiParamConstants.LOS_AT_DATE, required = false)
      @DateTimeFormat(iso = DATE)
          LocalDate losAtDate) {
    return courses(heiId, losIds, losCodes, loisBefore, loisAfter, losAtDate);
  }

  private ResponseEntity<CoursesResponseV0> courses(String heiId, List<String> losIds,
      List<String> losCodes, LocalDate loisBefore, LocalDate loisAfter, LocalDate losAtDate) {
    losIds = losIds != null ? losIds : Collections.emptyList();
    losCodes = losCodes != null ? losCodes : Collections.emptyList();

    Optional<CoursesV0HostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, CoursesV0HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    CoursesV0HostProvider provider = providerOptional.get();

    if (!losIds.isEmpty() && !losCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only LOS IDs or codes are accepted, not both simultaneously");
    }

    if (losIds.isEmpty() && losCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some LOS ID or code must be provided");
    }

    if (!losIds.isEmpty()) {
      return coursesByIds(provider, heiId, losIds, loisBefore, loisAfter, losAtDate);
    } else {
      return coursesByCodes(provider, heiId, losCodes, loisBefore, loisAfter, losAtDate);
    }
  }

  private ResponseEntity<CoursesResponseV0> coursesByIds(
      CoursesV0HostProvider provider, String heiId, List<String> losIds, LocalDate loisBefore,
      LocalDate loisAfter, LocalDate losAtDate) {
    if (losIds.size() > provider.getMaxLosIdsPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid LOS IDs per request is "
              + provider.getMaxLosIdsPerRequest());
    }

    CoursesResponseV0 response = new CoursesResponseV0();
    response.getLearningOpportunitySpecification()
        .addAll(provider.findByHeiIdAndLosIds(heiId, losIds, loisBefore, loisAfter, losAtDate));
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<CoursesResponseV0> coursesByCodes(
      CoursesV0HostProvider provider, String heiId, List<String> losCodes, LocalDate loisBefore,
      LocalDate loisAfter, LocalDate losAtDate) {
    if (losCodes.size() > provider.getMaxLosCodesPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid LOS codes per request is "
              + provider.getMaxLosCodesPerRequest());
    }

    CoursesResponseV0 response = new CoursesResponseV0();
    response.getLearningOpportunitySpecification()
        .addAll(provider.findByHeiIdAndLosCodes(heiId, losCodes, loisBefore, loisAfter, losAtDate));
    return ResponseEntity.ok(response);
  }
}
