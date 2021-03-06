package pt.ulisboa.ewp.node.api.ewp.controller.courses.replication;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.replication.SimpleCourseReplicationV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiSimpleCourseReplicationV1Controller.BASE_PATH)
public class EwpApiSimpleCourseReplicationV1Controller {

  public static final String BASE_PATH = "courses/replication/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiSimpleCourseReplicationV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Simple Course Replication API.",
      tags = {"ewp"})
  public ResponseEntity<CourseReplicationResponseV1> simpleCourseReplicationGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId) {
    return simpleCourseReplication(heiId);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Simple Course Replication API.",
      tags = {"ewp"})
  public ResponseEntity<CourseReplicationResponseV1> simpleCourseReplicationPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId) {
    return simpleCourseReplication(heiId);
  }

  private ResponseEntity<CourseReplicationResponseV1> simpleCourseReplication(String heiId) {
    Optional<SimpleCourseReplicationV1HostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, SimpleCourseReplicationV1HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    SimpleCourseReplicationV1HostProvider provider = providerOptional.get();

    CourseReplicationResponseV1 response = new CourseReplicationResponseV1();
    response.getLosId().addAll(provider.findAllByHeiId(heiId));
    return ResponseEntity.ok(response);
  }
}
