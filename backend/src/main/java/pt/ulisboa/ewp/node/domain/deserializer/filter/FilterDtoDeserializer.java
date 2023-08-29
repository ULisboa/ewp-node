package pt.ulisboa.ewp.node.domain.deserializer.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;

public class FilterDtoDeserializer extends StdDeserializer<FilterDto> {

  private final Map<String, Deserializer> formatToDeserializerMap = new HashMap<>();

  protected FilterDtoDeserializer() {
    this(null);
  }

  protected FilterDtoDeserializer(Class<?> vc) {
    super(vc);

    this.formatToDeserializerMap.put(
        PrimeNgFilterDtoDeserializer.FORMAT.toLowerCase(), new PrimeNgFilterDtoDeserializer());
  }

  @Override
  public FilterDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    String format = node.get("format").asText().toLowerCase();
    if (!this.formatToDeserializerMap.containsKey(format)) {
      throw new IllegalArgumentException("Invalid format: " + format);
    }
    JsonNode filtersNode = node.get("filters");
    return this.formatToDeserializerMap.get(format).deserialize(filtersNode);
  }

  public interface Deserializer {

    FilterDto deserialize(JsonNode filtersNode);
  }
}
