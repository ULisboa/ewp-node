package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiAuthenticationTestResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiAuthenticationToken;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "authentication")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiAuthenticationController extends AbstractForwardEwpApiController {

  @GetMapping(value = "/test", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Authentication test operation Forward API",
      description = "Used for testing authentication of EWP Forward APIs.",
      tags = {"Authentication"})
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiAuthenticationTestResponseDTO>>
      testAuthentication(ForwardEwpApiAuthenticationToken authentication) {
    ForwardEwpApiAuthenticationTestResponseDTO response =
        new ForwardEwpApiAuthenticationTestResponseDTO();
    response.setHostCode(authentication.getPrincipal().getHost().getCode());
    return ForwardEwpApiResponseUtils.toOkResponseEntity(response);
  }
}
