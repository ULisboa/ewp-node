package pt.ulisboa.ewp.node.domain.dto.filter.field;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;

public class GreaterThanFieldFilterDto<T> extends FieldFilterDto<T> {

  private Number value;

  public GreaterThanFieldFilterDto() {}

  public GreaterThanFieldFilterDto(String field, Number value) {
    super(field);
    Objects.requireNonNull(value);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Number value) {
    this.value = value;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.gt(selection.get(getField()), value);
  }
}
