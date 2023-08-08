package pt.ulisboa.ewp.node.utils.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpParamsTest {

  @Test
  void testParam_ValidStringParam_ParamIsAdded() {
    HttpParams params = new HttpParams();
    params.param("test", "abc");
    assertThat(params.getValue("test")).isEqualTo(Collections.singletonList("abc"));
  }

  @Test
  void testParam_NullStringParam_ParamIsNotAdded() {
    HttpParams params = new HttpParams();
    params.param("test", (String) null);
    assertThat(params.getValue("test")).isEqualTo(Collections.emptyList());
  }

  @Test
  void testParam_ValidTemporalAccessorParam_ParamIsAdded() {
    LocalDateTime dateTime = LocalDateTime.now();

    HttpParams params = new HttpParams();
    params.param("test", dateTime);
    assertThat(params.getValue("test"))
        .isEqualTo(Collections.singletonList(DateTimeFormatter.ISO_DATE_TIME.format(dateTime)));
  }

  @Test
  void testParam_NullTemporalAccessorParam_ParamIsNotAdded() {
    HttpParams params = new HttpParams();
    params.param("test", (TemporalAccessor) null);
    assertThat(params.getValue("test")).isEqualTo(Collections.emptyList());
  }

  @Test
  void testParam_ValidIntegerParam_ParamIsAdded() {
    HttpParams params = new HttpParams();
    params.param("test", 1);
    assertThat(params.getValue("test"))
        .isEqualTo(Collections.singletonList("1"));
  }

  @Test
  void testParam_NullIntegerParam_ParamIsNotAdded() {
    HttpParams params = new HttpParams();
    params.param("test", (Integer) null);
    assertThat(params.getValue("test")).isEqualTo(Collections.emptyList());
  }

  @Test
  void testParam_ValidStringCollectionParam_ParamIsAdded() {
    List<String> value = Arrays.asList("1", "2");

    HttpParams params = new HttpParams();
    params.param("test", value);
    assertThat(params.getValue("test"))
        .isEqualTo(value);
  }

  @Test
  void testParam_NullStringCollectionParam_ParamIsNotAdded() {
    HttpParams params = new HttpParams();
    params.param("test", (List<String>) null);
    assertThat(params.getValue("test")).isEqualTo(Collections.emptyList());
  }

  @Test
  void testAsMap_MultipleParamValues_ReturnsAllParamValues() {
    HttpParams params = new HttpParams();
    params.param("test1", "abc");
    params.param("test2", "def");
    params.param("test1", "ghi");
    Map<String, List<String>> result = params.asMap();
    assertThat(result).isNotNull();
    assertThat(result.get("test1")).containsExactly("abc", "ghi");
    assertThat(result.get("test2")).containsExactly("def");
  }

  @Test
  void testToString_NoParamValues_ReturnsEmptyString() {
    HttpParams params = new HttpParams();
    String result = params.toString();
    assertThat(result).isEmpty();
  }

  @Test
  void testToString_OneParamValue_ReturnsValidQueryString() {
    HttpParams params = new HttpParams();
    params.param("test1", "abc");
    String result = params.toString();
    assertThat(result).isEqualTo("test1=abc");
  }

  @Test
  void testToString_MultipleParamValues_ReturnsValidQueryString() {
    HttpParams params = new HttpParams();
    params.param("test1", "abc");
    params.param("test2", "def");
    params.param("test1", "ghi");
    String result = params.toString();
    assertThat(result.split("&")).containsExactlyInAnyOrder("test1=abc", "test2=def", "test1=ghi");
  }
}