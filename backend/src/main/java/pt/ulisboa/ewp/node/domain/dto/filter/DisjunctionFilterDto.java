package pt.ulisboa.ewp.node.domain.dto.filter;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DisjunctionFilterDto<T> extends FilterDto<T> {

  private final List<? extends FilterDto<T>> subFilters;

  @SafeVarargs
  public DisjunctionFilterDto(FilterDto<T>... subFilters) {
    this.subFilters = List.of(subFilters);
  }

  public DisjunctionFilterDto(List<? extends FilterDto<T>> subFilters) {
    this.subFilters = subFilters;
  }

  public List<? extends FilterDto<T>> getSubFilters() {
    return subFilters;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.or(
        subFilters.stream()
            .map(sf -> sf.createPredicate(criteriaBuilder, selection))
            .toArray(Predicate[]::new));
  }
}
