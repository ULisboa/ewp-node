package pt.ulisboa.ewp.node.client.ewp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.ounits.EwpOrganizationalUnitsV2Client;

@SpringBootTest(
    classes = {EwpNodeApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EwpOrganizationalUnitsV2ClientTest extends AbstractTest {

  @Autowired
  private EwpOrganizationalUnitsV2Client client;

  @Test
  public void testGetOrganizationalUnits() throws AbstractEwpClientErrorException {
    String heiId = "demo.usos.edu.pl";
    Collection<String> organizationalUnitIds =
        Arrays.asList("5653486D96841E16E0530B501E0A4594", "5653486D96871E16E0530B501E0A4594");
    EwpSuccessOperationResult<OunitsResponseV2> response =
        client.findByOunitIds(heiId, organizationalUnitIds);
    assertThat(response.getResponse().getStatus(), equalTo(HttpStatus.OK));
    assertThat(response.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(response.getResponseBody(), notNullValue());
    assertThat(response.getResponseBody().getOunit().size(), equalTo(organizationalUnitIds.size()));
  }
}
