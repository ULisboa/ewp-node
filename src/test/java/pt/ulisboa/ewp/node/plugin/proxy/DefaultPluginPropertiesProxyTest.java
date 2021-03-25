package pt.ulisboa.ewp.node.plugin.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pt.ulisboa.ewp.node.AbstractTest;

public class DefaultPluginPropertiesProxyTest extends AbstractTest {

  @Test
  public void testGetValidPropertyAsString() {
    DefaultPluginPropertiesProxy propertiesProxy =
        new DefaultPluginPropertiesProxy("ewp-host-plugin-demo");
    assertThat(propertiesProxy.getPropertyAsString("example.key")).isEqualTo("test");
  }

  @Test
  public void testGetUnknownPropertyAsString() {
    DefaultPluginPropertiesProxy propertiesProxy =
        new DefaultPluginPropertiesProxy("ewp-host-plugin-demo");
    assertThat(propertiesProxy.getPropertyAsString("unknown")).isNull();
  }
}
