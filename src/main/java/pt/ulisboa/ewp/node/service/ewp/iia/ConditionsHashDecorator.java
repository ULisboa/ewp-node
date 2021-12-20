package pt.ulisboa.ewp.node.service.ewp.iia;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.CooperationConditions;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.converter.xml.EwpNamespacePrefixMapper;

@Service
public class ConditionsHashDecorator {

  private final ConditionsHashCalculator conditionsHashCalculator;

  public ConditionsHashDecorator(
      ConditionsHashCalculator conditionsHashCalculator) {
    this.conditionsHashCalculator = conditionsHashCalculator;
    org.apache.xml.security.Init.init();
  }

  public void decorateWithConditionsHashes(IiasGetResponseV6 iiasGetResponseV6) {
    for (Iia iia : iiasGetResponseV6.getIia()) {
      CooperationConditions cooperationConditions = iia.getCooperationConditions();
      JAXBElement<CooperationConditions> cooperationConditionsJAXBElement = new JAXBElement<>(
          new QName(
              "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v6/endpoints/get-response.xsd",
              "cooperation-conditions", ""), CooperationConditions.class,
          cooperationConditions);
      String cooperationConditionsXml = XmlUtils.marshall(createJaxb2Marshaller(iia.getClass()),
          cooperationConditionsJAXBElement);
      String hash = this.conditionsHashCalculator.calculateHashFor(cooperationConditionsXml);
      iia.setConditionsHash(hash);
    }
  }

  private Jaxb2Marshaller createJaxb2Marshaller(Class<?> clazz) {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setClassesToBeBound(clazz);
    marshaller.setSupportJaxbElementClass(true);

    Map<String, Object> jaxbProperties = new HashMap<>();
    jaxbProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbProperties.put("com.sun.xml.bind.namespacePrefixMapper", new EwpNamespacePrefixMapper());

    marshaller.setMarshallerProperties(jaxbProperties);

    return marshaller;
  }

}
