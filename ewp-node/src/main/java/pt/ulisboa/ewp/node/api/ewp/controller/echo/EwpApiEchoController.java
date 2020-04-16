package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import eu.erasmuswithoutpaper.api.echo.Response;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "echo")
public class EwpApiEchoController {

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Echo API.",
      tags = {"ewp"})
  public ResponseEntity<Response> echoGet(
      EwpApiHostAuthenticationToken authentication,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_ECHO, defaultValue = "")
          List<String> echo) {
    return echo(authentication.getPrincipal().getHeiIdsCoveredByClient(), echo);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Echo API.",
      tags = {"ewp"})
  public ResponseEntity<Response> echoPost(
      EwpApiHostAuthenticationToken authentication,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_ECHO, required = false)
          List<String> echo) {
    if (echo == null) {
      echo = new ArrayList<>();
    }
    return echo(authentication.getPrincipal().getHeiIdsCoveredByClient(), echo);
  }

  private ResponseEntity<Response> echo(
      Collection<String> heiIdsCoveredByClient, List<String> echo) {
    Response response = new Response();
    response.getEcho().addAll(echo);
    response.getHeiId().addAll(heiIdsCoveredByClient);

    return ResponseEntity.ok(response);
  }
}
