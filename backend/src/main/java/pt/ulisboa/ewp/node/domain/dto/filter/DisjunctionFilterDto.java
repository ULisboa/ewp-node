package pt.ulisboa.ewp.node.domain.dto.filter;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DisjunctionFilterDto extends FilterDto {

  private final List<? extends FilterDto> subFilters;

  public DisjunctionFilterDto(List<? extends FilterDto> subFilters) {
    this.subFilters = subFilters;
  }

  public List<? extends FilterDto> getSubFilters() {
    return subFilters;
  }

  @Override
  public Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<?> selection) {
    return criteriaBuilder.or(
        subFilters.stream()
            .map(sf -> sf.createPredicate(criteriaBuilder, selection))
            .toArray(Predicate[]::new));
  }
}
