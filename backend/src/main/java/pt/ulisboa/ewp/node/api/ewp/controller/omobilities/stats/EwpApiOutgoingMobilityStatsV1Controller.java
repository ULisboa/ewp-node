package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.stats;

import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsResponseV1.AcademicYearStats;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.stats.OutgoingMobilityStatsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilityStatsV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilityStatsV1Controller {

  public static final String BASE_PATH = "omobilities/stats/v1";

  private final HostPluginManager hostPluginManager;

  private final String statsPortalHeiId;

  public EwpApiOutgoingMobilityStatsV1Controller(
      HostPluginManager hostPluginManager,
      @Value("${stats.portal.heiId}") String statsPortalHeiId) {
    this.hostPluginManager = hostPluginManager;
    this.statsPortalHeiId = statsPortalHeiId;
  }

  @EwpApiEndpoint(api = "omobility-stats", apiMajorVersion = 1)
  @RequestMapping(
      path = "/{sendingHeiId}",
      method = {RequestMethod.GET},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility Stats API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityStatsResponseV1> getStats(
      @PathVariable String sendingHeiId, EwpApiHostAuthenticationToken authenticationToken) {

    if (!authenticationToken.getPrincipal().getHeiIdsCoveredByClient().contains(statsPortalHeiId)) {
      throw new EwpBadRequestException(
          "Unauthorized HEI IDs: " + authenticationToken.getPrincipal().getHeiIdsCoveredByClient());
    }

    Collection<OutgoingMobilityStatsV1HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            sendingHeiId, OutgoingMobilityStatsV1HostProvider.class);

    Map<String, AcademicYearStats> receivingAcademicYearToStatsMap = new HashMap<>();
    for (OutgoingMobilityStatsV1HostProvider provider : providers) {
      OmobilityStatsResponseV1 stats = provider.getStats(sendingHeiId);
      if (stats != null) {
        for (AcademicYearStats currentProviderLaStats : stats.getAcademicYearStats()) {
          String receivingAcademicYearId = currentProviderLaStats.getReceivingAcademicYearId();
          receivingAcademicYearToStatsMap.put(
              receivingAcademicYearId,
              mergeAcademicYearStats(
                  receivingAcademicYearToStatsMap.getOrDefault(
                      receivingAcademicYearId,
                      createEmptyAcademicYearStats(receivingAcademicYearId)),
                  currentProviderLaStats));
        }
      }
    }

    OmobilityStatsResponseV1 response = new OmobilityStatsResponseV1();
    for (AcademicYearStats academicYearStats : receivingAcademicYearToStatsMap.values()) {
      response.getAcademicYearStats().add(academicYearStats);
    }

    return ResponseEntity.ok(response);
  }

  private AcademicYearStats createEmptyAcademicYearStats(String receivingAcademicYear) {
    AcademicYearStats newStats = new AcademicYearStats();
    newStats.setReceivingAcademicYearId(receivingAcademicYear);
    newStats.setOmobilityApproved(BigInteger.ZERO);
    newStats.setOmobilityPending(BigInteger.ZERO);
    return newStats;
  }

  private static AcademicYearStats mergeAcademicYearStats(
      AcademicYearStats firstStats, AcademicYearStats secondStats) {

    AcademicYearStats result = new AcademicYearStats();
    result.setReceivingAcademicYearId(firstStats.getReceivingAcademicYearId());
    if (!firstStats.getReceivingAcademicYearId().equals(secondStats.getReceivingAcademicYearId())) {
      throw new IllegalArgumentException(
          "Statistics to merge must be of same receiving academic year");
    }

    result.setOmobilityApproved(BigInteger.ZERO);
    if (firstStats.getOmobilityApproved() != null) {
      result.setOmobilityApproved(
          result.getOmobilityApproved().add(firstStats.getOmobilityApproved()));
    }
    if (secondStats.getOmobilityApproved() != null) {
      result.setOmobilityApproved(
          result.getOmobilityApproved().add(secondStats.getOmobilityApproved()));
    }

    result.setOmobilityPending(BigInteger.ZERO);
    if (firstStats.getOmobilityPending() != null) {
      result.setOmobilityPending(
          result.getOmobilityPending().add(firstStats.getOmobilityPending()));
    }
    if (secondStats.getOmobilityPending() != null) {
      result.setOmobilityPending(
          result.getOmobilityPending().add(secondStats.getOmobilityPending()));
    }

    return result;
  }
}
