package pt.ulisboa.ewp.node.domain.dto.filter.field;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;

public class EqualsFieldFilterDto<T> extends FieldFilterDto<T> {

  private Object value;

  public EqualsFieldFilterDto() {}

  public EqualsFieldFilterDto(String field, Object value) {
    super(field);
    Objects.requireNonNull(value);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    Path<?> fieldPath = selection.get(getField());
    Class<?> fieldJavaType = fieldPath.getJavaType();
    Object resolvedValue = value;
    if (Enum.class.isAssignableFrom(fieldJavaType)) {
      resolvedValue = Enum.valueOf((Class<Enum>) fieldJavaType, String.valueOf(resolvedValue));
    }
    return criteriaBuilder.equal(selection.get(getField()), resolvedValue);
  }
}
