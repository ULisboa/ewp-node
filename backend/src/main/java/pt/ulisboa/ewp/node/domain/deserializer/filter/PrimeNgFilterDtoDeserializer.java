package pt.ulisboa.ewp.node.domain.deserializer.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pt.ulisboa.ewp.node.domain.dto.filter.ConjunctionFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.DisjunctionFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.NotFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.CommunicationLogIsOfTypeFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp.HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.communication.log.http.ewp.HttpCommunicationToEwpNodeIsToHeiIdFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.EqualsFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.GreaterThanFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.GreaterThanOrEqualFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.InFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.LessThanFieldFilterDto;
import pt.ulisboa.ewp.node.domain.dto.filter.field.LessThanOrEqualFieldFilterDto;
import pt.ulisboa.ewp.node.domain.utils.FilterConstants;

public class PrimeNgFilterDtoDeserializer implements FilterDtoDeserializer.Deserializer {

  public static final String FORMAT = "primeng";

  @Override
  public FilterDto<?> deserialize(JsonNode filtersNode) {
    Iterator<String> fieldNamesIterator = filtersNode.fieldNames();
    List<FilterDto<?>> allFilters = new ArrayList<>();
    while (fieldNamesIterator.hasNext()) {
      String fieldName = fieldNamesIterator.next();
      JsonNode fieldFiltersNode = filtersNode.get(fieldName);
      int numberFieldFilters = fieldFiltersNode.size();
      List<FilterDto<?>> currentFieldFilters = new ArrayList<>();
      boolean conjunction = true;
      for (int i = 0; i < numberFieldFilters; i++) {
        JsonNode fieldFilterNode = fieldFiltersNode.get(i);
        if (i == 0) {
          conjunction = "and".equals(fieldFilterNode.get("operator").asText());
        }
        String matchMode = fieldFilterNode.get("matchMode").asText();
        Object value;
        Number numberValue;
        switch (matchMode) {
          case "equals":
            value = deserializeValue(fieldFilterNode.get("value"));
            if (value != null) {
              currentFieldFilters.add(new EqualsFieldFilterDto<>(fieldName, value));
            }
            break;

          case "notEquals":
            value = deserializeValue(fieldFilterNode.get("value"));
            if (value != null) {
              currentFieldFilters.add(
                  new NotFilterDto<>(new EqualsFieldFilterDto<>(fieldName, value)));
            }
            break;

          case "lt":
            numberValue = deserializeNumberValue(fieldFilterNode.get("value"));
            if (numberValue != null) {
              currentFieldFilters.add(new LessThanFieldFilterDto<>(fieldName, numberValue));
            }
            break;

          case "lte":
            numberValue = deserializeNumberValue(fieldFilterNode.get("value"));
            if (numberValue != null) {
              currentFieldFilters.add(new LessThanOrEqualFieldFilterDto<>(fieldName, numberValue));
            }
            break;

          case "gt":
            numberValue = deserializeNumberValue(fieldFilterNode.get("value"));
            if (numberValue != null) {
              currentFieldFilters.add(new GreaterThanFieldFilterDto<>(fieldName, numberValue));
            }
            break;

          case "gte":
            numberValue = deserializeNumberValue(fieldFilterNode.get("value"));
            if (numberValue != null) {
              currentFieldFilters.add(
                  new GreaterThanOrEqualFieldFilterDto<>(fieldName, numberValue));
            }
            break;

          case "in":
            JsonNode valueNode = fieldFilterNode.get("value");
            if (valueNode != null) {
              int valueNodeSize = valueNode.size();
              List<Object> possibleValues = new ArrayList<>();
              for (int j = 0; j < valueNodeSize; j++) {
                Object possibleValue = deserializeValue(valueNode.get(j));
                if (possibleValue != null) {
                  possibleValues.add(possibleValue);
                }
              }

              if (!possibleValues.isEmpty()) {
                currentFieldFilters.add(new InFieldFilterDto<>(fieldName, possibleValues));
              }
            }
            break;

          case FilterConstants.COMMUNICATION_IS_OF_TYPE:
            value = deserializeValue(fieldFilterNode.get("value"));
            if (value != null) {
              currentFieldFilters.add(new CommunicationLogIsOfTypeFilterDto(value.toString()));
            }
            break;

          case FilterConstants.COMMUNICATION_FROM_HEI_ID:
            value = deserializeValue(fieldFilterNode.get("value"));
            if (value != null) {
              currentFieldFilters.add(
                  new HttpCommunicationFromEwpNodeIsFromHeiIdFilterDto(value.toString()));
            }
            break;

          case FilterConstants.COMMUNICATION_TO_HEI_ID:
            value = deserializeValue(fieldFilterNode.get("value"));
            if (value != null) {
              currentFieldFilters.add(
                  new HttpCommunicationToEwpNodeIsToHeiIdFilterDto(value.toString()));
            }
            break;

          default:
            throw new IllegalArgumentException("Invalid match mode: " + matchMode);
        }
      }

      if (conjunction) {
        allFilters.add(new ConjunctionFilterDto(currentFieldFilters));
      } else {
        allFilters.add(new DisjunctionFilterDto(currentFieldFilters));
      }
    }
    return new ConjunctionFilterDto(allFilters);
  }

  private Number deserializeNumberValue(JsonNode jsonNode) {
    Object value = deserializeValue(jsonNode);
    if (value == null) {
      return null;
    }
    if (!(value instanceof Number)) {
      throw new IllegalArgumentException("Expected a number value: " + value);
    }
    return (Number) value;
  }

  private Object deserializeValue(JsonNode jsonNode) {
    if (jsonNode instanceof NumericNode) {
      return jsonNode.numberValue();
    } else if (jsonNode instanceof TextNode) {
      return jsonNode.textValue();
    } else if (jsonNode instanceof BooleanNode) {
      return jsonNode.booleanValue();
    } else if (jsonNode instanceof NullNode) {
      return null;
    } else {
      throw new IllegalArgumentException(
          "Unknown value node type: " + jsonNode.getClass().getName());
    }
  }
}
