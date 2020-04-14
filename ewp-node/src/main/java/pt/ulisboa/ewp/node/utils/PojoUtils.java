package pt.ulisboa.ewp.node.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import java.util.Iterator;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import org.springframework.validation.FieldError;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class PojoUtils {

  private PojoUtils() {}

  public static String getUserFriendlyPropertyName(Class<?> clazz, FieldError fieldError)
      throws NoSuchFieldException {
    String field = fieldError.getField();
    return getUserFriendlyFieldName(clazz, field);
  }

  public static String getUserFriendlyPropertyName(ConstraintViolation<?> constraintViolation) {
    Iterator<Path.Node> iterator = constraintViolation.getPropertyPath().iterator();

    String propertyName;
    for (propertyName = null; iterator.hasNext(); propertyName = iterator.next().getName()) {}

    return propertyName;
  }

  public static String getUserFriendlyFieldName(Class<?> clazz, String field) {
    Field declaredField = getDeclaredField(clazz, field);
    if (declaredField != null) {
      JsonProperty jsonPropertyAnnotation = declaredField.getAnnotation(JsonProperty.class);
      if (jsonPropertyAnnotation != null) {
        return jsonPropertyAnnotation.value();
      }

      ParamName paramNameAnnotation = declaredField.getAnnotation(ParamName.class);
      if (paramNameAnnotation != null) {
        return paramNameAnnotation.value();
      }
    }
    return field;
  }

  public static Field getDeclaredField(Class<?> clazz, String field) {
    try {
      Field declaredField = clazz.getDeclaredField(field);
      return declaredField;
    } catch (NoSuchFieldException e) {
      if (clazz.getSuperclass() != null) {
        return getDeclaredField(clazz.getSuperclass(), field);
      }
      return null;
    }
  }
}
