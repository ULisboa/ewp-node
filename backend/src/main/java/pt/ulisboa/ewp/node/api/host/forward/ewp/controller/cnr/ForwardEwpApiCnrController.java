package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.cnr;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr.ForwardEwpApiCnrStatusResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.mapper.cnr.ForwardEwpApiChangeNotificationMapper;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpChangeNotificationService;

@RestController
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "cnr")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
@Validated
public class ForwardEwpApiCnrController {

  private final EwpChangeNotificationService changeNotificationService;

  public ForwardEwpApiCnrController(EwpChangeNotificationService changeNotificationService) {
    this.changeNotificationService = changeNotificationService;
  }

  @ForwardEwpApiEndpoint(api = "cnr", endpoint = "status")
  @GetMapping(value = "/status", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP CNR Forward API.",
      tags = {"CNR"})
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiCnrStatusResponseDTO>>
      getCnrStatus(@RequestParam("id") Long id) {
    Optional<EwpChangeNotificationDto> ewpChangeNotificationDtoOptional =
        this.changeNotificationService.findById(id);
    if (ewpChangeNotificationDtoOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    ForwardEwpApiChangeNotificationMapper mapper = ForwardEwpApiChangeNotificationMapper.INSTANCE;
    ForwardEwpApiCnrStatusResponseDTO forwardEwpApiCnrStatusResponseDTO =
        mapper.mapEwpChangeNotificationDtoToForwardEwpApiCnrStatusResponseDTO(
            ewpChangeNotificationDtoOptional.get());
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            forwardEwpApiCnrStatusResponseDTO));
  }
}
