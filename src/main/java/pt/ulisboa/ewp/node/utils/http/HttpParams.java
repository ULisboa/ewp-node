package pt.ulisboa.ewp.node.utils.http;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpParams implements Serializable {

  private final Map<String, List<String>> params = new HashMap<>();

  public HttpParams param(String key, String value) {
    this.addParamValue(key, value);
    return this;
  }

  public HttpParams param(String key, TemporalAccessor value) {
    if (value != null) {
      this.addParamValue(key, DateTimeFormatter.ISO_DATE_TIME.format(value));
    }
    return this;
  }

  public HttpParams param(String key, Object value) {
    if (value != null) {
      this.addParamValue(key, value.toString());
    }
    return this;
  }

  public HttpParams param(String key, Collection<String> values) {
    this.addParamValues(key, values);
    return this;
  }

  public Map<String, List<String>> asMap() {
    return this.params;
  }

  private void addParamValue(String key, String value) {
    this.params.computeIfAbsent(key, k -> new ArrayList<>());
    if (value != null) {
      this.params.get(key).add(value);
    }
  }

  private void addParamValues(String key, Collection<String> values) {
    this.params.computeIfAbsent(key, k -> new ArrayList<>());
    if (values != null) {
      this.params.get(key).addAll(values);
    }
  }
}
