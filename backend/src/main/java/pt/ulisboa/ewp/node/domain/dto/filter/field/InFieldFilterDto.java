package pt.ulisboa.ewp.node.domain.dto.filter.field;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.DisjunctionFilterDto;

public class InFieldFilterDto extends FieldFilterDto {

  private final List<Object> values;

  public InFieldFilterDto(String field, List<Object> values) {
    super(field);
    this.values = values;
  }

  public List<Object> getValues() {
    return values;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<?> selection) {
    DisjunctionFilterDto disjunctionFilter =
        new DisjunctionFilterDto(
            values.stream()
                .map(v -> new EqualsFieldFilterDto(getField(), v))
                .collect(Collectors.toList()));
    return disjunctionFilter.createPredicate(criteriaBuilder, selection);
  }
}
