package pt.ulisboa.ewp.node.client.ewp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import eu.erasmuswithoutpaper.api.ounits.OunitsResponse;

@SpringBootTest(
    classes = {EwpNodeApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EwpOrganizationalUnitsClientTest extends AbstractTest {

  @Autowired private EwpOrganizationalUnitsClient client;

  @Test
  public void testGetOrganizationalUnits()
      throws EwpClientUnknownErrorResponseException, EwpClientProcessorException,
          EwpClientResponseAuthenticationFailedException, EwpClientErrorResponseException {
    String heiId = "demo.usos.edu.pl";
    Collection<String> organizationalUnitIds =
        Arrays.asList("5653486D96841E16E0530B501E0A4594", "5653486D96871E16E0530B501E0A4594");
    EwpSuccessOperationResult<OunitsResponse> response =
        client.findByOunitIds(heiId, organizationalUnitIds);
    assertThat(response.getResponse().getStatusCode(), equalTo(200));
    assertThat(response.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(response.getResponseBody(), notNullValue());
    assertThat(response.getResponseBody().getOunit().size(), equalTo(organizationalUnitIds.size()));
  }
}
