package pt.ulisboa.ewp.node.utils.serialization;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

public class SerializationUtils {

  private static final Logger LOG = LoggerFactory.getLogger(SerializationUtils.class);

  private SerializationUtils() {}

  public static TypeAndString convertToTypeAndString(Object object) {
    if (object == null) {
      return new TypeAndString("Object", "null");
      
    } else if (object instanceof String) {
      return new TypeAndString("String", "\"" + object + "\"");

    } else if (object instanceof Number) {
      return new TypeAndString("Number", "" + object);

    } else if (object instanceof LocalDate) {
      return new TypeAndString(
          "LocalDate", ((LocalDate) object).format(DateTimeFormatter.ISO_LOCAL_DATE));

    } else if (object instanceof LocalDateTime) {
      return new TypeAndString(
          "LocalDateTime", ((LocalDateTime) object).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    } else if (object instanceof Collection) {
      List<TypeAndString> childrenTypeAndString =
          ((Collection<?>) object)
              .stream()
                  .map(SerializationUtils::convertToTypeAndString)
                  .collect(Collectors.toList());
      String childrenType =
          childrenTypeAndString.isEmpty() ? "?" : childrenTypeAndString.get(0).getType();
      String stringBuilder =
          "["
              + childrenTypeAndString.stream()
                  .map(TypeAndString::getString)
                  .collect(Collectors.joining(", "))
              + "]";
      return new TypeAndString("Collection<" + childrenType + ">", stringBuilder);

    } else if (object instanceof Optional) {
      Optional<?> optional = (Optional<?>) object;
      if (optional.isPresent()) {
        Object optionalValue = optional.get();
        TypeAndString optionalValueTypeAndString = convertToTypeAndString(optionalValue);
        return new TypeAndString(
            "Optional<" + optionalValueTypeAndString.getType() + ">",
            "Optional.of(" + optionalValueTypeAndString.getString() + ")");

      } else {
        return new TypeAndString("Optional<?>", "Optional.empty()");
      }

    } else if (object instanceof JAXBElement) {
      String string = XmlUtils.marshallAndOptimize(object);
      return new TypeAndString("JAXBElement<?>", string);

    } else if (object.getClass().getAnnotation(XmlRootElement.class) != null) {
      String string = XmlUtils.marshallAndOptimize(object).trim();
      return new TypeAndString(object.getClass().getName(), string);

    } else if (object.getClass().getAnnotation(XmlType.class) != null) {
      @SuppressWarnings("unchecked")
      JAXBElement<Object> jaxbElement =
          new JAXBElement<>(new QName("xml"), (Class<Object>) object.getClass(), object);
      String string = XmlUtils.marshallAndOptimize(jaxbElement).trim();
      return new TypeAndString(object.getClass().getName(), string);

    } else if (object instanceof FileResponse) {
      FileResponse fileResponse = (FileResponse) object;
      return new TypeAndString(
          "FileResponse",
          "[media type '"
              + fileResponse.getMediaType()
              + "'] "
              + fileResponse.getData().length
              + " bytes");

    } else {
      LOG.warn("Unknown object type: " + object.getClass().getName());
      return new TypeAndString(object.getClass().getName(), "");
    }
  }
}
