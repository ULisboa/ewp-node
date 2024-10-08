package pt.ulisboa.ewp.node.utils.bean;

import jakarta.servlet.ServletRequest;
import java.util.Map;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

/**
 * ServletRequestDataBinder which supports fields renaming using {@link ParamName}
 */
public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {

  private final Map<String, String> paramMappings;

  public ParamNameDataBinder(Object target, String objectName, Map<String, String> paramMappings) {
    super(target, objectName);
    this.paramMappings = paramMappings;
  }

  @Override
  protected void addBindValues(
      MutablePropertyValues mutablePropertyValues, ServletRequest request) {
    super.addBindValues(mutablePropertyValues, request);
    for (Map.Entry<String, String> entry : paramMappings.entrySet()) {
      String paramName = entry.getKey();
      String fieldName = entry.getValue();
      if (mutablePropertyValues.contains(paramName)) {
        var propertyValue = mutablePropertyValues.getPropertyValue(paramName);
        if (propertyValue != null) {
          mutablePropertyValues.add(fieldName, propertyValue.getValue());
        }
      }
    }
  }
}
