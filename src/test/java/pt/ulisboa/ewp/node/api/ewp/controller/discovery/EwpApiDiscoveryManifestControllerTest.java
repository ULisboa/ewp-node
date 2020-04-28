package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;

public class EwpApiDiscoveryManifestControllerTest extends AbstractEwpControllerTest {

  @Autowired private BootstrapProperties bootstrapProperties;

  @Test
  public void testManifest() throws Exception {
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                get(EwpApiConstants.API_BASE_URI + "manifest").accept(MediaType.APPLICATION_XML))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())

            // Assert 1 host
            .andExpect(
                xpath("count(/*[local-name()='manifest']/*[local-name()='host'])").number(1D))

            // Assert admin email
            .andExpect(
                xpath("/*[local-name()='manifest']/*[local-name()='host']/admin-email/text()")
                    .string(bootstrapProperties.getHosts().get(0).getAdminEmail()))

            // Assert 1 institution covered
            .andExpect(
                xpath(
                        "count(/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='institutions-covered']/*[local-name()='hei'])")
                    .number(1D))
            .andExpect(
                xpath(
                        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='institutions-covered']/*[local-name()='hei']/@id")
                    .string(
                        bootstrapProperties
                            .getHosts()
                            .get(0)
                            .getCoveredHeis()
                            .get(0)
                            .getSchacCode()))
            .andExpect(
                xpath(
                        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='institutions-covered']/*[local-name()='hei']/*[local-name()='name']/text()")
                    .string(
                        bootstrapProperties
                            .getHosts()
                            .get(0)
                            .getCoveredHeis()
                            .get(0)
                            .getNames()
                            .get(0)
                            .getValue()))

            // Assert Discovery API
            .andExpect(
                xpath(
                        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='apis-implemented']/*[local-name()='discovery']/@version")
                    .string(EwpApiConstants.DISCOVERY_VERSION))
            .andExpect(
                xpath(
                        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='apis-implemented']/*[local-name()='discovery']/url/text()")
                    .string("https://localhost" + EwpApiConstants.API_BASE_URI + "manifest"))
            .andReturn();

    String xml = mvcResult.getResponse().getContentAsString();

    validateXml(xml, "xsd/ewp/discovery/manifest.xsd");

    // Validate APIs implemented nodes
    validateXml(
        xml,
        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='apis-implemented']/*[local-name()='discovery']",
        "xsd/ewp/discovery/manifest-entry.xsd");
    validateXml(
        xml,
        "/*[local-name()='manifest']/*[local-name()='host']/*[local-name()='apis-implemented']/*[local-name()='echo']",
        "xsd/ewp/echo/manifest-entry.xsd");
  }
}
