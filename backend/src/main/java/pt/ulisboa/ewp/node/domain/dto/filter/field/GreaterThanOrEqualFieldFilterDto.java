package pt.ulisboa.ewp.node.domain.dto.filter.field;

import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class GreaterThanOrEqualFieldFilterDto<T> extends FieldFilterDto<T> {

  private Number value;

  public GreaterThanOrEqualFieldFilterDto() {}

  public GreaterThanOrEqualFieldFilterDto(String field, Number value) {
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
    return criteriaBuilder.ge(selection.get(getField()), value);
  }
}
