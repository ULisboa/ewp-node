package pt.ulisboa.ewp.node.utils.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HttpUtilsTest {

  @Test
  public void testSerializeFormDataWithNoParams() {
    Map<String, List<String>> params = new HashMap<>();
    assertThat(HttpUtils.serializeFormDataUrlEncoded(params)).isEqualTo("");
  }

  @Test
  public void testSerializeFormDataWithOneParams() {
    Map<String, List<String>> params = new HashMap<>();
    params.put("a", Collections.singletonList("b"));
    assertThat(HttpUtils.serializeFormDataUrlEncoded(params)).isEqualTo("a=b");
  }

  @Test
  public void testSerializeFormDataWithTwoDifferentParams() {
    Map<String, List<String>> params = new HashMap<>();
    params.put("a", Collections.singletonList("b"));
    params.put("c", Collections.singletonList("d"));
    assertThat(HttpUtils.serializeFormDataUrlEncoded(params)).isEqualTo("a=b&c=d");
  }

  @Test
  public void testSerializeFormDataWithTwoParamsWithSameKey() {
    Map<String, List<String>> params = new HashMap<>();
    params.put("a", Arrays.asList("b", "c"));
    assertThat(HttpUtils.serializeFormDataUrlEncoded(params)).isEqualTo("a=b&a=c");
  }

}