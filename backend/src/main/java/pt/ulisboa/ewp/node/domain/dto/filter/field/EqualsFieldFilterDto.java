package pt.ulisboa.ewp.node.domain.dto.filter.field;

import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
