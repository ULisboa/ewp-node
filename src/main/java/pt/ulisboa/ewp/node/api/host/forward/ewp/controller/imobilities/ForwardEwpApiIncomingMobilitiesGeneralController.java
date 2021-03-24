package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.imobilities;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiGeneralController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "imobilities")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiIncomingMobilitiesGeneralController
    extends AbstractForwardEwpApiGeneralController {

  public ForwardEwpApiIncomingMobilitiesGeneralController(RegistryClient registryClient) {
    super(registryClient);
  }

  @Override
  public String getApiLocalName() {
    return EwpApiConstants.API_INCOMING_MOBILITIES_NAME;
  }
}
