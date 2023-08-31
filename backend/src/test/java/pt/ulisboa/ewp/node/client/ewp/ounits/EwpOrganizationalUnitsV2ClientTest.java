package pt.ulisboa.ewp.node.client.ewp.ounits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.math.BigInteger;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;

public class EwpOrganizationalUnitsV2ClientTest extends AbstractTest {

  @Test
  public void testGetApiSpecification() {
    String heiId = UUID.randomUUID().toString();

    EwpOrganizationalUnitsV2Client client = Mockito
        .spy(new EwpOrganizationalUnitsV2Client(null, null));

    EwpOrganizationalUnitApiConfiguration apiConfiguration =
        new EwpOrganizationalUnitApiConfiguration(
            "example.com",
            "http://example.com",
            Collections.emptyList(),
            Collections.emptyList(),
            BigInteger.ONE,
            BigInteger.TWO);
    doReturn(apiConfiguration)
        .when(client)
        .getApiConfigurationForHeiId(ArgumentMatchers.anyString());

    ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO specification =
        client.getApiSpecification(heiId);

    assertThat(specification).isNotNull();
    assertThat(specification.getMaxOunitIds())
        .isEqualTo(apiConfiguration.getMaxOunitIds().intValueExact());
    assertThat(specification.getMaxOunitCodes())
        .isEqualTo(apiConfiguration.getMaxOunitCodes().intValueExact());
  }
}
