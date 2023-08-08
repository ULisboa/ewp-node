package pt.ulisboa.ewp.node.api;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/healthcheck")
public class HealthCheckResource {

  @Autowired private Logger log;

  @GetMapping
  @Operation(
      summary = "Health check operation.",
      tags = {"healthcheck"})
  public ResponseEntity<Object> healthCheck() {
    log.info("[HEALTHCHECK] Responding OK to health check request");
    return ResponseEntity.ok().build();
  }
}
