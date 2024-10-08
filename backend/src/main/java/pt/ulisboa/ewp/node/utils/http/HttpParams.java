package pt.ulisboa.ewp.node.utils.http;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.web.util.UriUtils;

public class HttpParams implements Serializable {

  private final Map<String, List<String>> params = new HashMap<>();

  public List<String> getValue(@NotNull String key) {
    return params.getOrDefault(key, Collections.emptyList());
  }

  public HttpParams param(@NotNull String key, String value) {
    this.addParamValue(key, value, Function.identity());
    return this;
  }

  public HttpParams param(@NotNull String key, TemporalAccessor value) {
    this.addParamValue(key, value, v -> DateTimeFormatter.ISO_DATE_TIME.format(value));
    return this;
  }

  public HttpParams param(@NotNull String key, Iterable<String> values) {
    this.addParamValues(key, values, Function.identity());
    return this;
  }

  public HttpParams param(@NotNull String key, Object value) {
    this.addParamValue(key, value, Object::toString);
    return this;
  }

  public Map<String, List<String>> asMap() {
    return this.params;
  }

  @Override
  public String toString() {
    List<String> fragments = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : params.entrySet()) {
      for (String value : entry.getValue()) {
        fragments.add(
            UriUtils.encodeQuery(entry.getKey(), "UTF-8")
                + "="
                + UriUtils.encodeQuery(value, "UTF-8"));
      }
    }

    return String.join("&", fragments);
  }

  private <T> void addParamValues(@NotNull String key, Iterable<T> values,
      Function<T, String> valueToStringConverter) {
    if (values != null) {
      values.forEach(value -> this.addParamValue(key, value, valueToStringConverter));
    }
  }

  private <T> void addParamValue(@NotNull String key, T value,
      Function<T, String> valueToStringConverter) {
    if (value != null) {
      this.params.computeIfAbsent(key, k -> new ArrayList<>());
      this.params.get(key).add(valueToStringConverter.apply(value));
    }
  }
}
