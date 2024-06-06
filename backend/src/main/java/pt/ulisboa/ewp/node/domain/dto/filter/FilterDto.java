package pt.ulisboa.ewp.node.domain.dto.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.CommunicationLogEndProcessingBeforeOrEqualDateTimeFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.CommunicationLogIsRootFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.CommunicationLogStartProcessingAfterOrEqualDateTimeFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.CommunicationLogTypeIsOneOfSetFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.HttpCommunicationFormParameterStartsWithValueFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.HttpCommunicationResponseWithStatusCodeFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.HttpCommunicationToApiEndpointFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp.HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp.HttpCommunicationToEwpNodeIsToHeiIdFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.EqualsFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.GreaterThanFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.GreaterThanOrEqualFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.InFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.LessThanFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.LessThanOrEqualFieldFilterDto;

@JsonTypeInfo(use = Id.NAME, property = "type", include = As.PROPERTY)
@JsonSubTypes(
    value = {
      @JsonSubTypes.Type(value = ConjunctionFilterDto.class, name = "CONJUNCTION"),
      @JsonSubTypes.Type(value = DisjunctionFilterDto.class, name = "DISJUNCTION"),
      @JsonSubTypes.Type(value = NotFilterDto.class, name = "NOT"),
      @JsonSubTypes.Type(value = EqualsFieldFilterDto.class, name = "EQUALS"),
      @JsonSubTypes.Type(value = GreaterThanFieldFilterDto.class, name = "GREATER-THAN"),
      @JsonSubTypes.Type(
          value = GreaterThanOrEqualFieldFilterDto.class,
          name = "GREATER-THAN-OR-EQUAL"),
      @JsonSubTypes.Type(value = InFieldFilterDto.class, name = "IN"),
      @JsonSubTypes.Type(value = LessThanFieldFilterDto.class, name = "LESS-THAN"),
      @JsonSubTypes.Type(value = LessThanOrEqualFieldFilterDto.class, name = "LESS-THAN-OR-EQUAL"),
      @JsonSubTypes.Type(
          value = CommunicationLogIsRootFilterDto.class,
          name = "COMMUNICATION-LOG-IS-ROOT"),
      @JsonSubTypes.Type(
          value = CommunicationLogTypeIsOneOfSetFilterDto.class,
          name = "COMMUNICATION-LOG-TYPE-IS-ONE-OF-SET"),
      @JsonSubTypes.Type(
          value = HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto.class,
          name = "HTTP-COMMUNICATION-FROM-EWP-NODE-IS-FROM-HEI-ID"),
      @JsonSubTypes.Type(
          value = HttpCommunicationToEwpNodeIsToHeiIdFilterDto.class,
          name = "HTTP-COMMUNICATION-TO-EWP-NODE-IS-TO-HEI-ID"),
      @JsonSubTypes.Type(
          value = HttpCommunicationToApiEndpointFilterDto.class,
          name = "HTTP-COMMUNICATION-TO-API-ENDPOINT"),
      @JsonSubTypes.Type(
          value = HttpCommunicationFormParameterStartsWithValueFilterDto.class,
          name = "HTTP-COMMUNICATION-FORM-PARAMETER-STARTS-WITH-VALUE"),
      @JsonSubTypes.Type(
          value = HttpCommunicationResponseWithStatusCodeFilterDto.class,
          name = "HTTP-COMMUNICATION-RESPONSE-WITH-STATUS-CODE"),
      @JsonSubTypes.Type(
          value = CommunicationLogStartProcessingAfterOrEqualDateTimeFilterDto.class,
          name = "COMMUNICATION-LOG-START-PROCESSING-AFTER-OR-EQUAL-DATE-TIME"),
      @JsonSubTypes.Type(
          value = CommunicationLogEndProcessingBeforeOrEqualDateTimeFilterDto.class,
          name = "COMMUNICATION-LOG-END-PROCESSING-BEFORE-OR-EQUAL-DATE-TIME")
    })
public abstract class FilterDto<T> {

    public abstract Predicate createPredicate(CriteriaBuilder criteriaBuilder, Root<T> selection);

}
