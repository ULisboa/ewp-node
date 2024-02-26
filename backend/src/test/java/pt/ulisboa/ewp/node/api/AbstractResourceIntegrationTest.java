package pt.ulisboa.ewp.node.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.service.xml.XmlValidator;

public abstract class AbstractResourceIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private XmlValidator xmlValidator;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
  }

  protected void validateXml(String xml) {
    assertTrue(xmlValidator.validate(xml.getBytes(StandardCharsets.UTF_8)).isValid());
  }

  protected void validateXml(String xml, String xpath)
      throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
    assertTrue(xmlValidator.validateXpath(xml, xpath).isValid());
  }
}
