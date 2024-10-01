package pt.ulisboa.ewp.node.domain.dto.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class DisjunctionFilterDto<T> extends FilterDto<T> {

  private List<? extends FilterDto<T>> subFilters;

  public DisjunctionFilterDto() {}

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

  public void setSubFilters(List<? extends FilterDto<T>> subFilters) {
    this.subFilters = subFilters;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.or(
        subFilters.stream()
            .map(sf -> sf.createPredicate(criteriaBuilder, selection))
            .toArray(Predicate[]::new));
  }
}
