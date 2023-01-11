package pt.ulisboa.ewp.node.utils.http.converter.xml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;

public class Jaxb2HttpMessageConverter extends Jaxb2RootElementHttpMessageConverter {

  private String[] packagesToScan;

  private boolean supportJaxbElementClass;

  private NamespacePrefixMapper namespacePrefixMapper;

  public void setPackagesToScan(String... packagesToScan) {
    this.packagesToScan = packagesToScan;
  }

  public void setSupportJaxbElementClass(boolean supportJaxbElementClass) {
    this.supportJaxbElementClass = supportJaxbElementClass;
  }

  public void setNamespacePrefixMapper(
      NamespacePrefixMapper namespacePrefixMapper) {
    this.namespacePrefixMapper = namespacePrefixMapper;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return (AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null) || (
        this.supportJaxbElementClass && JAXBElement.class.isAssignableFrom(clazz));
  }

  @Override
  public void writeToResult(Object object, HttpHeaders headers, Result result)
      throws TransformerException {
    try {
      // NOTE: try marshall object without other classes scanned by marshaller
      // If successful, it ensures that the main namespace uses an empty namespace prefix.
      Class<?> clazz = ClassUtils.getUserClass(object);
      Jaxb2Marshaller marshaller = createJaxb2Marshaller(clazz);
      marshal(marshaller, object, result);

    } catch (XmlMappingException e) {
      // NOTE: If the marshalling failed, then marshall using all known packages as context.
      Jaxb2Marshaller marshaller = createJaxb2Marshaller(null);
      marshal(marshaller, object, result);
    }
  }

  private void marshal(Jaxb2Marshaller marshaller, Object object, Result result)
      throws TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    StreamSource xslt = new StreamSource(
        getClass().getClassLoader().getResourceAsStream("xsl/remove-unused-namespaces.xsl"));
    Transformer transformer = tf.newTransformer(xslt);

    // NOTE: Marshall object to XML
    ByteArrayOutputStream intermediaryOutputStream = new ByteArrayOutputStream();
    StreamResult intermediaryResult = new StreamResult(intermediaryOutputStream);
    marshaller.marshal(object, intermediaryResult);

    // NOTE: Use XSLT to remove unused namespaces
    ByteArrayInputStream finalInputStream = new ByteArrayInputStream(
        intermediaryOutputStream.toByteArray());
    transformer.transform(new StreamSource(finalInputStream), result);
  }

  protected final Jaxb2Marshaller createJaxb2Marshaller(Class<?> clazz) {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    if (clazz != null) {
      marshaller.setClassesToBeBound(clazz);
    } else if (this.packagesToScan != null) {
      marshaller.setPackagesToScan(packagesToScan);
    }

    marshaller.setSupportJaxbElementClass(this.supportJaxbElementClass);

    Map<String, Object> jaxbProperties = new HashMap<>();
    jaxbProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    if (this.namespacePrefixMapper != null) {
      jaxbProperties.put("com.sun.xml.bind.namespacePrefixMapper", this.namespacePrefixMapper);
    }

    marshaller.setMarshallerProperties(jaxbProperties);

    return marshaller;
  }
}
