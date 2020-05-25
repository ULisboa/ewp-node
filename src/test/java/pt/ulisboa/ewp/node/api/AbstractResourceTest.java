package pt.ulisboa.ewp.node.api;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import javax.servlet.Filter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.AbstractTest;
import pt.ulisboa.ewp.node.api.common.filter.CustomUrlRewriteFilter;
import pt.ulisboa.ewp.node.utils.XmlValidator;

public abstract class AbstractResourceTest extends AbstractTest {

  @Autowired private WebApplicationContext wac;

  @Autowired private XmlValidator xmlValidator;

  protected MockMvc mockMvc;

  @Before
  public void setup() {
    Filter urlRewriteFilter = wac.getBean(CustomUrlRewriteFilter.class);
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(urlRewriteFilter).build();
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
