package pt.ulisboa.ewp.node.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.EwpNodeApplication;
import pt.ulisboa.ewp.node.utils.XmlValidator;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EwpNodeApplication.class)
@ActiveProfiles(profiles = {"dev", "test"})
@ContextConfiguration(classes = EwpNodeApplication.class)
@Import(ValidationAutoConfiguration.class)
public abstract class AbstractResourceIntegrationTest extends AbstractTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private XmlValidator xmlValidator;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity()).build();
  }

  protected void validateXml(String xml, String schemaFilePath) {
    assertTrue(xmlValidator.validate(xml, getClass().getClassLoader().getResource(schemaFilePath)));
  }

  protected void validateXml(String xml, String xpath, String schemaFilePath)
      throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
    assertTrue(
        xmlValidator.validateXpath(
            xml, xpath, getClass().getClassLoader().getResource(schemaFilePath)));
  }
}
