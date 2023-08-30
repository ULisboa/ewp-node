package pt.ulisboa.ewp.node.domain.dto.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class NotFilterDto<T> extends FilterDto<T> {

  private final FilterDto<T> subFilter;

  public NotFilterDto(FilterDto<T> subFilter) {
    this.subFilter = subFilter;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection) {
    return criteriaBuilder.not(subFilter.createPredicate(criteriaBuilder, selection));
  }
}
