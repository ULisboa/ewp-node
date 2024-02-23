package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0.LearningOpportunitySpecification;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.CoursesV0HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
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

  @EwpApiEndpoint(api = "courses", apiMajorVersion = 0)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Courses API.",
      tags = {"ewp"})
  public ResponseEntity<CoursesResponseV0> courses(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.LOS_ID, required = false) List<String> losIds,
      @RequestParam(value = EwpApiParamConstants.LOS_CODE, required = false) List<String> losCodes,
      @RequestParam(value = EwpApiParamConstants.LOIS_BEFORE, required = false)
          @DateTimeFormat(iso = DATE)
          LocalDate loisBefore,
      @RequestParam(value = EwpApiParamConstants.LOIS_AFTER, required = false)
          @DateTimeFormat(iso = DATE)
          LocalDate loisAfter,
      @RequestParam(value = EwpApiParamConstants.LOS_AT_DATE, required = false)
          @DateTimeFormat(iso = DATE)
          LocalDate losAtDate) {

    losIds = losIds != null ? losIds : Collections.emptyList();
    losCodes = losCodes != null ? losCodes : Collections.emptyList();

    if (!hostPluginManager.hasHostProvider(heiId, CoursesV0HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }

    if (!losIds.isEmpty() && !losCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only LOS IDs or codes are accepted, not both simultaneously");
    }

    if (losIds.isEmpty() && losCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some LOS ID or code must be provided");
    }

    Collection<CoursesV0HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        heiId, CoursesV0HostProvider.class);

    if (!losIds.isEmpty()) {
      return coursesByIds(providers, heiId, losIds, loisBefore, loisAfter, losAtDate);
    } else {
      return coursesByCodes(providers, heiId, losCodes, loisBefore, loisAfter, losAtDate);
    }
  }

  private ResponseEntity<CoursesResponseV0> coursesByIds(
      Collection<CoursesV0HostProvider> providers, String heiId, List<String> losIds,
      LocalDate loisBefore,
      LocalDate loisAfter, LocalDate losAtDate) {

    int maxLosIdsPerRequest = providers.stream().mapToInt(
        CoursesV0HostProvider::getMaxLosIdsPerRequest).max().orElse(0);
    if (losIds.size() > maxLosIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid LOS IDs per request is " + maxLosIdsPerRequest);
    }

    Map<String, LearningOpportunitySpecification> losIdToLosMap = new HashMap<>();
    Set<String> missingLosIds = new HashSet<>(losIds);
    Iterator<CoursesV0HostProvider> providerIterator = providers.iterator();
    while (providerIterator.hasNext() && !missingLosIds.isEmpty()) {
      CoursesV0HostProvider provider = providerIterator.next();
      Collection<LearningOpportunitySpecification> learningOpportunitySpecifications = provider.findByHeiIdAndLosIds(
          heiId, missingLosIds, loisBefore, loisAfter, losAtDate);
      for (LearningOpportunitySpecification learningOpportunitySpecification : learningOpportunitySpecifications) {
        losIdToLosMap.put(learningOpportunitySpecification.getLosId(),
            learningOpportunitySpecification);
        missingLosIds.remove(learningOpportunitySpecification.getLosId());
      }
    }

    CoursesResponseV0 response = new CoursesResponseV0();
    for (LearningOpportunitySpecification learningOpportunitySpecification : losIdToLosMap.values()) {
      response.getLearningOpportunitySpecification().add(learningOpportunitySpecification);
    }
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<CoursesResponseV0> coursesByCodes(
      Collection<CoursesV0HostProvider> providers, String heiId, List<String> losCodes,
      LocalDate loisBefore,
      LocalDate loisAfter, LocalDate losAtDate) {

    int maxLosCodesPerRequest = providers.stream().mapToInt(
        CoursesV0HostProvider::getMaxLosCodesPerRequest).max().orElse(0);
    if (losCodes.size() > maxLosCodesPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid LOS codes per request is " + maxLosCodesPerRequest);
    }

    Map<String, LearningOpportunitySpecification> losCodeToLosMap = new HashMap<>();
    Set<String> missingLosCodes = new HashSet<>(losCodes);
    Iterator<CoursesV0HostProvider> providerIterator = providers.iterator();
    while (providerIterator.hasNext() && !missingLosCodes.isEmpty()) {
      CoursesV0HostProvider provider = providerIterator.next();
      Collection<LearningOpportunitySpecification> learningOpportunitySpecifications = provider.findByHeiIdAndLosCodes(
          heiId, missingLosCodes, loisBefore, loisAfter, losAtDate);
      for (LearningOpportunitySpecification learningOpportunitySpecification : learningOpportunitySpecifications) {
        losCodeToLosMap.put(learningOpportunitySpecification.getLosCode(),
            learningOpportunitySpecification);
        missingLosCodes.remove(learningOpportunitySpecification.getLosCode());
      }
    }

    CoursesResponseV0 response = new CoursesResponseV0();
    for (LearningOpportunitySpecification learningOpportunitySpecification : losCodeToLosMap.values()) {
      response.getLearningOpportunitySpecification().add(learningOpportunitySpecification);
    }
    return ResponseEntity.ok(response);
  }
}
