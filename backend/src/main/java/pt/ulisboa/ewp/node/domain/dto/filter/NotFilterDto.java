package pt.ulisboa.ewp.node.domain.dto.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class NotFilterDto extends FilterDto {

  private final FilterDto subFilter;

  public NotFilterDto(FilterDto subFilter) {
    this.subFilter = subFilter;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<?> selection) {
    return criteriaBuilder.not(subFilter.createPredicate(criteriaBuilder, selection));
  }
}
