package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.institutions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2.Hei;
import java.io.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pt.ulisboa.ewp.node.api.AbstractResourceIntegrationTest;
import pt.ulisboa.ewp.node.api.common.security.SecurityCommonConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse.Message.MessageSeverity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.client.ewp.institutions.EwpInstitutionsV2Client;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult.Builder;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

@AutoConfigureMockMvc
public class ForwardEwpApiInstitutionsV2ControllerIntegrationTest extends
    AbstractResourceIntegrationTest {

  private static final String TOKEN_SECRET = "sample-host-forward-ewp-api-secret";
  private static final String HOST_CODE = "sample-host";

  private static final String API_FIND_URI =
      ForwardEwpApiConstants.API_BASE_URI + "institutions/v2";

  @Autowired
  private MockMvc mockMvc;

  @SpyBean
  private EwpInstitutionsV2Client client;

  private String jwtToken;

  @BeforeEach
  public void beforeTest() {
    jwtToken = JWT.create().withIssuer(HOST_CODE).sign(Algorithm.HMAC256(TOKEN_SECRET.getBytes()));
  }

  @Test
  public void testInstitutionsGet_NotAuthenticated() throws Exception {
    mockMvc
        .perform(get(API_FIND_URI))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized())
        .andExpect(xpath("/forward-ewp-api-response/messages/message[1]/severity").string("ERROR"))
        .andExpect(
            xpath("/forward-ewp-api-response/messages/message[1]/summary")
                .string("Token was not found on request"));
  }

  @Test
  public void testInstitutionsGet_Authenticated_NoParameters() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isBadRequest())
            .andExpect(
                header().string(HttpConstants.HEADER_X_HAS_DATA_OBJECT, String.valueOf(false)))
            .andReturn();

    ForwardEwpApiResponse responseBody =
        XmlUtils.unmarshall(result.getResponse().getContentAsString(), ForwardEwpApiResponse.class);
    assertThat(responseBody.getMessages()).hasSizeGreaterThan(0);
    assertThat(responseBody.getMessages().get(0).getSeverity()).isEqualTo(MessageSeverity.ERROR);
    assertThat(responseBody.getMessages().get(0).getContext()).isNotEmpty();
  }

  @Test
  public void testInstitutionsGet_Authenticated_NoHeiApiForHeiId() throws Exception {
    String heiId = "demo";

    NoEwpApiForHeiIdException exception =
        new NoEwpApiForHeiIdException(heiId, EwpInstitutionApiConfiguration.API_NAME);
    doThrow(exception).when(client).find(heiId);

    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI + "?hei_id=" + heiId)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isBadRequest())
            .andExpect(
                header().string(HttpConstants.HEADER_X_HAS_DATA_OBJECT, String.valueOf(false)))
            .andReturn();

    ForwardEwpApiResponse responseBody =
        XmlUtils.unmarshall(result.getResponse().getContentAsString(), ForwardEwpApiResponse.class);
    assertThat(responseBody.getMessages()).hasSize(1);
    assertThat(responseBody.getMessages().get(0).getSeverity()).isEqualTo(MessageSeverity.ERROR);
    assertThat(responseBody.getMessages().get(0).getSummary()).isEqualTo(exception.getMessage());
  }

  @Test
  public void testInstitutionsGet_Authenticated_InternalException() throws Exception {
    String heiId = "demo";

    RuntimeException exception = new RuntimeException("Test");
    doThrow(exception).when(client).find(heiId);

    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI + "?hei_id=" + heiId)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isInternalServerError())
            .andReturn();

    ForwardEwpApiResponse responseBody =
        XmlUtils.unmarshall(result.getResponse().getContentAsString(), ForwardEwpApiResponse.class);
    assertThat(responseBody.getMessages()).hasSize(1);
    assertThat(responseBody.getMessages().get(0).getSeverity()).isEqualTo(MessageSeverity.ERROR);
    assertThat(responseBody.getMessages().get(0).getSummary()).isEqualTo(exception.getMessage());
  }

  @Test
  public void testInstitutionsGet_Authenticated_InvalidServerResponse() throws Exception {
    String heiId = "demo";

    EwpClientInvalidResponseException exception =
        new EwpClientInvalidResponseException(null, null, null, new IllegalStateException("Test"));
    doThrow(exception).when(client).find(heiId);

    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI + "?hei_id=" + heiId)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isBadGateway())
            .andReturn();

    ForwardEwpApiResponse responseBody =
        XmlUtils.unmarshall(result.getResponse().getContentAsString(), ForwardEwpApiResponse.class);
    assertThat(responseBody.getMessages()).hasSize(1);
    assertThat(responseBody.getMessages().get(0).getSeverity()).isEqualTo(MessageSeverity.ERROR);
    assertThat(responseBody.getMessages().get(0).getSummary()).isEqualTo(exception.getMessage());
  }

  @Test
  public void testInstitutionsGet_Authenticated_ErrorResponse() throws Exception {
    String heiId = "demo";

    ErrorResponseV1 errorResponse = EwpApiUtils.createErrorResponseWithDeveloperMessage("Test");

    EwpResponse response = new EwpResponse.Builder(HttpStatus.BAD_REQUEST).build();

    EwpClientErrorResponseException exception =
        new EwpClientErrorResponseException(null, response, null, errorResponse);
    doThrow(exception).when(client).find(heiId);

    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI + "?hei_id=" + heiId)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isBadRequest())
            .andExpect(
                header().string(HttpConstants.HEADER_X_HAS_DATA_OBJECT, String.valueOf(true)))
            .andReturn();

    ForwardEwpApiResponseWithData<ErrorResponseV1> responseBody =
        XmlUtils.unmarshall(
            result.getResponse().getContentAsString(), ForwardEwpApiResponseWithData.class);
    assertThat(responseBody.getMessages()).hasSize(0);
    assertThat(responseBody.getData().getObject().getDeveloperMessage().getValue())
        .isEqualTo(errorResponse.getDeveloperMessage().getValue());
  }

  @Test
  public void testInstitutionsGet_Authenticated_Success() throws Exception {
    String heiId = "demo";
    InstitutionsResponseV2 expectedResponse = new InstitutionsResponseV2();
    Hei hei = new Hei();
    hei.setHeiId(heiId);
    expectedResponse.getHei().add(hei);

    EwpResponse response = new EwpResponse.Builder(HttpStatus.OK).build();

    EwpSuccessOperationResult<Serializable> successOperationResult =
        new Builder<>().response(response).responseBody(expectedResponse).build();
    doReturn(successOperationResult).when(client).find(heiId);

    MvcResult result =
        mockMvc
            .perform(
                get(API_FIND_URI + "?hei_id=" + heiId)
                    .header(
                        SecurityCommonConstants.HEADER_NAME,
                        SecurityCommonConstants.BEATER_TOKEN_PREFIX + jwtToken))
            .andExpect(status().isOk())
            .andReturn();

    ForwardEwpApiResponseWithData<InstitutionsResponseV2> responseBody =
        XmlUtils.unmarshall(
            result.getResponse().getContentAsString(), ForwardEwpApiResponseWithData.class);
    assertThat(responseBody.getMessages()).hasSize(0);
    assertThat(responseBody.getData().getObject().getHei()).hasSize(1);
    assertThat(responseBody.getData().getObject().getHei().get(0).getHeiId()).isEqualTo(heiId);
  }
}
