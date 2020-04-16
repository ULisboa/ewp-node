package pt.ulisboa.ewp.node.client.ewp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;

@SpringBootTest(
    classes = {EwpNodeApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EwpInstitutionsClientTest extends AbstractTest {

  @Autowired private EwpInstitutionsClient client;

  @Test
  public void testGetInstitutions() throws AbstractEwpClientErrorException {
    String heiId = "demo.usos.edu.pl";
    EwpSuccessOperationResult<InstitutionsResponse> response = client.find(heiId);
    assertThat(response.getResponse().getStatus(), equalTo(200));
    assertThat(response.getResponseAuthenticationResult().isValid(), equalTo(true));
    assertThat(response.getResponseBody(), notNullValue());
    assertThat(response.getResponseBody().getHei().size(), equalTo(1));
    assertThat(response.getResponseBody().getHei().get(0).getHeiId(), equalTo(heiId));
  }
}
