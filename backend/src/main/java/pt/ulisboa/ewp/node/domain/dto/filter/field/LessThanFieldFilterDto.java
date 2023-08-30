package pt.ulisboa.ewp.node.domain.dto.filter.field;

import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class LessThanFieldFilterDto<T> extends FieldFilterDto<T> {

  private final Number value;

  public LessThanFieldFilterDto(String field, Number value) {
    super(field);
    Objects.requireNonNull(value);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.lt(selection.get(getField()), value);
  }
}
