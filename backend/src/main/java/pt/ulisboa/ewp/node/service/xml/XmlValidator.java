// NOTE: based on
// https://github.com/erasmus-without-paper/ewp-registry-service/blob/master/src/main/java/eu/erasmuswithoutpaper/registry/documentbuilder/EwpDocBuilder.java

package pt.ulisboa.ewp.node.service.xml;

import static org.joox.JOOX.$;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.xerces.util.XMLCatalogResolver;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationEntryDto;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationResultDto;

@Service
public class XmlValidator {

  private final Schema compoundSchema;

  XmlValidator() {
    this.compoundSchema = createCompoundSchema();
  }

  private Schema createCompoundSchema() {
    URL catalogUrl = getClass().getResource("/schemas/__index__.xml");
    if (catalogUrl == null) {
      throw new IllegalStateException("Failed to find schemas index file");
    }

    try (InputStream xmlCatalogInputStream = catalogUrl.openStream()) {
      XMLCatalogResolver baseResolver =
          new XMLCatalogResolver(new String[] {catalogUrl.toString()});

      /*
       * Wrap it in a custom LSResourceResolver.
       *
       * XMLCatalogResolver implements the LSResourceResolver interface, but we need it to behave
       * differently. We want to make sure that we have all necessary XSD files in our resources, so
       * that the compiler doesn't depend on the external XSDs dynamically fetched from the Internet.
       *
       * In order to assure that, we will use our custom resource resolver which will throw
       * RuntimeException whenever the compiler attempts to resolve resources which are NOT present in
       * our catalog (thus preventing it from trying to resolve them online).
       */
      LSResourceResolver customResolver =
          new LSResourceResolver() {
            @Override
            public LSInput resolveResource(
                String type,
                String namespaceUri,
                String publicId,
                String systemId,
                String baseUri) {

              // First, try to resolve the entity from our built-in schema catalog.
              LSInput result =
                  baseResolver.resolveResource(type, namespaceUri, publicId, systemId, baseUri);
              if (result != null) {
                return result;
              }

              /*
               * Should not happen. It is does, then it means that some of our schemas reference other
               * schemas which are not present in our schema catalog. The catalog needs to be updated.
               */
              throw new IllegalStateException(
                  "Missing schema in registry's resources:\nnamespaceUri: "
                      + namespaceUri
                      + "\ntype: "
                      + type
                      + "\npublicId: "
                      + publicId
                      + "\nsystemId: "
                      + systemId
                      + "\nbaseUri: "
                      + baseUri);
            }
          };

      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      schemaFactory.setResourceResolver(customResolver);

      List<StreamSource> xsdSources = new ArrayList<>();
      try {
        for (Element element : $(xmlCatalogInputStream).find("uri")) {
          String relativePath = $(element).attr("uri");
          InputStream xsdInputStream = getClass().getResourceAsStream("/schemas/" + relativePath);
          StreamSource xsdSource = new StreamSource(xsdInputStream);
          xsdSources.add(xsdSource);
        }
      } catch (IOException | SAXException e) {
        throw new RuntimeException(e);
      }

      try {
        return schemaFactory.newSchema(xsdSources.toArray(new StreamSource[0]));
      } catch (SAXException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read schemas index file");
    }
  }

  public ValidationResultDto validate(byte[] xml) {
    DocumentBuilder documentBuilder = newSecureDocumentBuilder();
    try {
      documentBuilder.parse(new ByteArrayInputStream(xml));
    } catch (SAXException e) {
      List<ValidationEntryDto> parseErrors = new ArrayList<>();
      parseErrors.add(new ValidationEntryDto(ValidationEntryDto.Severity.ERROR, e.getMessage()));
      return new ValidationResultDto(parseErrors);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Validators are not thread-safe (hence, separate instance).
    Validator validator = this.compoundSchema.newValidator();

    List<ValidationEntryDto> validationEntries = new ArrayList<>();
    validator.setErrorHandler(
        new ErrorHandler() {

          @Override
          public void error(SAXParseException exception) {
            validationEntries.add(new ValidationEntryDto(ValidationEntryDto.Severity.ERROR, exception));
          }

          @Override
          public void fatalError(SAXParseException exception) {
            validationEntries.add(new ValidationEntryDto(ValidationEntryDto.Severity.ERROR, exception));
          }

          @Override
          public void warning(SAXParseException exception) {
            validationEntries.add(new ValidationEntryDto(ValidationEntryDto.Severity.WARNING, exception));
          }
        });

    try {
      validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
    } catch (IOException | SAXException e) {
      throw new RuntimeException(e);
    }

    return new ValidationResultDto(validationEntries);
  }

  /**
   * Get a new, safely configured instance of {@link DocumentBuilder}.
   *
   * @return a {@link DocumentBuilder} instance.
   */
  public static DocumentBuilder newSecureDocumentBuilder() {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setIgnoringComments(true);

      /*
       * XXE prevention. See here:
       * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Java
       */
      dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
      dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      dbf.setXIncludeAware(false);
      dbf.setExpandEntityReferences(false);

      return dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}
