package pt.ulisboa.ewp.node.domain.dto.filter.field;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;

public class LessThanFieldFilterDto<T> extends FieldFilterDto<T> {

  private Number value;

  public LessThanFieldFilterDto() {}

  public LessThanFieldFilterDto(String field, Number value) {
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
    return criteriaBuilder.lt(selection.get(getField()), value);
  }
}
