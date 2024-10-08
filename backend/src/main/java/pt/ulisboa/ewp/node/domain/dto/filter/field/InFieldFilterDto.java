package pt.ulisboa.ewp.node.domain.dto.filter.field;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;
import pt.ulisboa.ewp.node.domain.dto.filter.DisjunctionFilterDto;

public class InFieldFilterDto<T> extends FieldFilterDto<T> {

  private List<Object> values;

  public InFieldFilterDto() {}

  public InFieldFilterDto(String field, List<Object> values) {
    super(field);
    this.values = values;
  }

  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    DisjunctionFilterDto<T> disjunctionFilter =
        new DisjunctionFilterDto<>(
            values.stream()
                .map(v -> new EqualsFieldFilterDto<T>(getField(), v))
                .collect(Collectors.toList()));
    return disjunctionFilter.createPredicate(criteriaBuilder, selection);
  }
}
