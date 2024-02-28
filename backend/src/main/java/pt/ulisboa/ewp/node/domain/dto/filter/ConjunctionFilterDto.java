package pt.ulisboa.ewp.node.domain.dto.filter;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ConjunctionFilterDto<T> extends FilterDto<T> {

  private List<? extends FilterDto<T>> subFilters;

  public ConjunctionFilterDto() {}

  @SafeVarargs
  public ConjunctionFilterDto(FilterDto<T>... subFilters) {
    this.subFilters = List.of(subFilters);
  }

  public ConjunctionFilterDto(List<? extends FilterDto<T>> subFilters) {
    this.subFilters = subFilters;
  }

  public List<? extends FilterDto<T>> getSubFilters() {
    return subFilters;
  }

  public void setSubFilters(List<? extends FilterDto<T>> subFilters) {
    this.subFilters = subFilters;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.and(
        subFilters.stream()
            .map(sf -> sf.createPredicate(criteriaBuilder, selection))
            .toArray(Predicate[]::new));
  }
}
