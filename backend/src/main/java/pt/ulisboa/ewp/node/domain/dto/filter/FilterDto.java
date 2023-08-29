package pt.ulisboa.ewp.node.domain.dto.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FilterDto {

    public abstract Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<?> selection);

}
