package pt.ulisboa.ewp.node.domain.dto.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.HttpCommunicationFormParameterStartsWithValueFilterDto;

@JsonTypeInfo(use = Id.NAME, property = "type", include = As.PROPERTY)
@JsonSubTypes(
    value = {
      @JsonSubTypes.Type(value = ConjunctionFilterDto.class, name = "CONJUNCTION"),
      @JsonSubTypes.Type(value = DisjunctionFilterDto.class, name = "DISJUNCTION"),
      @JsonSubTypes.Type(
          value = HttpCommunicationFormParameterStartsWithValueFilterDto.class,
          name = "HTTP-COMMUNICATION-FORM-PARAMETER-STARTS-WITH-VALUE")
    })
public abstract class FilterDto<T> {

    public abstract Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection);

}
