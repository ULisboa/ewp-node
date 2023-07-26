package pt.ulisboa.ewp.node.plugin.manager.host;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.MockHostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;

public class AbstractHostPluginManagerTest {

  private MockHostPluginManager hostPluginManager;

  @BeforeEach
  public void beforeEach() throws IOException {
    // Create a new fresh host plugin manager
    this.hostPluginManager = new MockHostPluginManager();
  }

  @AfterEach
  public void afterEach() throws IOException {
    this.hostPluginManager = null;
  }

  @Test
  public void testHasHostProvider_ExistingHostProvidersOfHeiIdAndClassType_ReturnsTrue() {
    String heiId = UUID.randomUUID().toString();
    HostPlugin plugin = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    this.hostPluginManager.registerPlugin(plugin, List.of(new DummyHostProvider()));

    boolean result = this.hostPluginManager.hasHostProvider(heiId, DummyHostProvider.class);

    assertThat(result).isTrue();
  }

  @Test
  public void
      testHasHostProvider_ExistingHostProvidersOfWrongHeiIdAndCorrectClassType_ReturnsFalse() {
    String heiId = "test.com";
    HostPlugin plugin = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    this.hostPluginManager.registerPlugin(plugin, List.of(new DummyHostProvider()));

    boolean result = this.hostPluginManager.hasHostProvider("wrong", DummyHostProvider.class);

    assertThat(result).isFalse();
  }

  @Test
  public void testGetPrimaryProvider_ExistingPrimaryHostProvider_ReturnsCorrectHostProvider() {
    String heiId = "test.com";

    HostPlugin primaryPlugin =
        new MockHostPlugin.Builder().coveredHeiId(heiId).heiIdOnWhichIsPrimary(heiId).build();
    DummyHostProvider primaryHostProvider = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(primaryPlugin, List.of(primaryHostProvider));

    HostPlugin nonPrimaryPlugin = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    DummyHostProvider nonPrimaryHostProvider = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(nonPrimaryPlugin, List.of(nonPrimaryHostProvider));

    Optional<DummyHostProvider> result =
        this.hostPluginManager.getPrimaryProvider(heiId, DummyHostProvider.class);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(primaryHostProvider);
  }

  @Test
  public void testGetPrimaryProvider_NonExistingPrimaryHostProvider_ReturnsEmptyResult() {
    String heiId = "test.com";

    HostPlugin primaryPlugin =
        new MockHostPlugin.Builder().coveredHeiId(heiId).heiIdOnWhichIsPrimary(heiId).build();
    DummyHostProvider primaryHostProvider = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(primaryPlugin, List.of(primaryHostProvider));

    Optional<DummyHostProvider> result =
        this.hostPluginManager.getPrimaryProvider("wrong", DummyHostProvider.class);

    assertThat(result).isEmpty();
  }

  @Test
  public void
      testGetSingleProvider_ExistingHostProviderForHeiIdAndNullOunitId_ReturnsPrimaryHostProvider() {
    String heiId = "test.com";

    HostPlugin primaryPlugin =
        new MockHostPlugin.Builder().coveredHeiId(heiId).heiIdOnWhichIsPrimary(heiId).build();
    DummyHostProvider primaryHostProvider = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(primaryPlugin, List.of(primaryHostProvider));

    HostPlugin nonPrimaryPlugin = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    DummyHostProvider nonPrimaryHostProvider = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(nonPrimaryPlugin, List.of(nonPrimaryHostProvider));

    Optional<DummyHostProvider> result =
        this.hostPluginManager.getSingleProvider(heiId, null, DummyHostProvider.class);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(primaryHostProvider);
  }

  @Test
  public void
      testGetSingleProvider_ExistingHostProviderForHeiIdAndOunitId_ReturnsCorrectHostProvider() {
    String heiId = "test.com";
    String ounitId = "test";

    HostPlugin pluginNotCoveringOunitId =
        new MockHostPlugin.Builder()
            .coveredHeiId(heiId)
            .coveredOunitIdsByHeiId(heiId, List.of())
            .build();
    DummyHostProvider hostProviderOfPluginNotCoveringOunitId = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        pluginNotCoveringOunitId, List.of(hostProviderOfPluginNotCoveringOunitId));

    HostPlugin pluginCoveringOunitId =
        new MockHostPlugin.Builder()
            .coveredHeiId(heiId)
            .coveredOunitIdsByHeiId(heiId, List.of(ounitId))
            .build();
    DummyHostProvider hostProviderOfPluginCoveringOunitId = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        pluginCoveringOunitId, List.of(hostProviderOfPluginCoveringOunitId));

    Optional<DummyHostProvider> result =
        this.hostPluginManager.getSingleProvider(heiId, ounitId, DummyHostProvider.class);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(hostProviderOfPluginCoveringOunitId);
  }

  @Test
  public void
      testGetAllProvidersOfType_TwoHostPluginsWithDistinctHeiId_ReturnsHostProvidersOfCorrectHeiId() {
    String heiId = "test.com";

    HostPlugin pluginWithCorrectHeiId = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    DummyHostProvider hostProviderOfPluginWithCorrectHeiId1 = new DummyHostProvider();
    DummyHostProvider hostProviderOfPluginWithCorrectHeiId2 = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        pluginWithCorrectHeiId,
        List.of(hostProviderOfPluginWithCorrectHeiId1, hostProviderOfPluginWithCorrectHeiId2));

    HostPlugin pluginWithIncorrectHeiId =
        new MockHostPlugin.Builder().coveredHeiId("wrong").build();
    DummyHostProvider hostProviderOfPluginWithIncorrectHeiId = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        pluginWithIncorrectHeiId, List.of(hostProviderOfPluginWithIncorrectHeiId));

    Collection<DummyHostProvider> result =
        this.hostPluginManager.getAllProvidersOfType(heiId, DummyHostProvider.class);

    assertThat(result).hasSize(2);
    assertThat(result)
        .contains(hostProviderOfPluginWithCorrectHeiId1, hostProviderOfPluginWithCorrectHeiId2);
  }

  @Test
  public void
      testGetAllProvidersOfType_TwoHostPluginsWithDistinctHeiId_ReturnsAllHostProvidersOfCorrectType() {
    HostPlugin plugin1 = new MockHostPlugin.Builder().coveredHeiId("hei1").build();
    DummyHostProvider hostProvider1OfPlugin1 = new DummyHostProvider();
    DummyHostProvider hostProvider2OfPlugin1 = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        plugin1, List.of(hostProvider1OfPlugin1, hostProvider2OfPlugin1));

    HostPlugin plugin2 = new MockHostPlugin.Builder().coveredHeiId("hei2").build();
    DummyHostProvider hostProvider1OfPlugin2 = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(plugin2, List.of(hostProvider1OfPlugin2));

    Collection<DummyHostProvider> result =
        this.hostPluginManager.getAllProvidersOfType(DummyHostProvider.class);

    assertThat(result).hasSize(3);
    assertThat(result)
        .contains(hostProvider1OfPlugin1, hostProvider2OfPlugin1, hostProvider1OfPlugin2);
  }

  @Test
  public void
      testGetAllProvidersOfTypePerHeiId_TwoHostPluginsWithDistinctHeiId_ReturnsAllHostProvidersOfCorrectTypeSplittedByHeiId() {
    String coveredHeiIdByPlugin1 = "hei1";
    HostPlugin plugin1 = new MockHostPlugin.Builder().coveredHeiId(coveredHeiIdByPlugin1).build();
    DummyHostProvider hostProvider1OfPlugin1 = new DummyHostProvider();
    DummyHostProvider hostProvider2OfPlugin1 = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(
        plugin1, List.of(hostProvider1OfPlugin1, hostProvider2OfPlugin1));

    String coveredHeiIdByPlugin2 = "hei2";
    HostPlugin plugin2 = new MockHostPlugin.Builder().coveredHeiId(coveredHeiIdByPlugin2).build();
    DummyHostProvider hostProvider1OfPlugin2 = new DummyHostProvider();
    this.hostPluginManager.registerPlugin(plugin2, List.of(hostProvider1OfPlugin2));

    Map<String, Collection<DummyHostProvider>> result =
        this.hostPluginManager.getAllProvidersOfTypePerHeiId(DummyHostProvider.class);

    assertThat(result).hasSize(2);
    assertThat(result).containsKey(coveredHeiIdByPlugin1);
    assertThat(result.get(coveredHeiIdByPlugin1))
        .containsExactlyInAnyOrder(hostProvider1OfPlugin1, hostProvider2OfPlugin1);
    assertThat(result).containsKey(coveredHeiIdByPlugin2);
    assertThat(result.get(coveredHeiIdByPlugin2)).containsExactlyInAnyOrder(hostProvider1OfPlugin2);
  }

  @Test
  public void
      testGetAllProviders_TwoHostPluginsWithDistinctHeiId_ReturnsAllHostProvidersOfWantedHeiId() {
    String coveredHeiIdByPlugin1 = "hei1";
    HostPlugin plugin1 = new MockHostPlugin.Builder().coveredHeiId(coveredHeiIdByPlugin1).build();
    DummyHostProvider hostProvider1OfPlugin1 = new DummyHostProvider();
    DummyHostProvider hostProvider2OfPlugin1 = new DummyHostProvider();
    DummyHostProvider2 hostProvider3OfPlugin1 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin1, List.of(hostProvider1OfPlugin1, hostProvider2OfPlugin1, hostProvider3OfPlugin1));

    String coveredHeiIdByPlugin2 = "hei2";
    HostPlugin plugin2 = new MockHostPlugin.Builder().coveredHeiId(coveredHeiIdByPlugin2).build();
    DummyHostProvider hostProvider1OfPlugin2 = new DummyHostProvider();
    DummyHostProvider2 hostProvider2OfPlugin2 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin2, List.of(hostProvider1OfPlugin2, hostProvider2OfPlugin2));

    Collection<HostProvider> result = this.hostPluginManager.getAllProviders(coveredHeiIdByPlugin1);

    assertThat(result)
        .containsExactlyInAnyOrder(
            hostProvider1OfPlugin1, hostProvider2OfPlugin1, hostProvider3OfPlugin1);
  }

  @Test
  public void
      testGetOunitIdsCoveredPerProviderOfHeiId_TwoHostPluginsWithCoveringDistinctOunitIdsOfSameHeiId_ReturnsCorrectOunitIdsPerProvider() {
    String coveredHeiIdByPlugin1 = "hei1";
    List<String> coveredOunitIdsOfPlugin1 = List.of("hei1-ounit1", "hei1-ounit2", "hei1-ounit3");
    HostPlugin plugin1 =
        new MockHostPlugin.Builder()
            .coveredHeiId(coveredHeiIdByPlugin1)
            .coveredOunitIdsByHeiId(coveredHeiIdByPlugin1, coveredOunitIdsOfPlugin1)
            .build();
    DummyHostProvider hostProvider1OfPlugin1 = new DummyHostProvider();
    DummyHostProvider2 hostProvider2OfPlugin1 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin1, List.of(hostProvider1OfPlugin1, hostProvider2OfPlugin1));

    String coveredHeiIdByPlugin2 = "hei2";
    List<String> coveredOunitIdsOfPlugin2 = List.of("hei2-ounit1", "hei2-ounit2");
    HostPlugin plugin2 =
        new MockHostPlugin.Builder()
            .coveredHeiId(coveredHeiIdByPlugin2)
            .coveredOunitIdsByHeiId(coveredHeiIdByPlugin2, coveredOunitIdsOfPlugin2)
            .build();
    DummyHostProvider hostProvider1OfPlugin2 = new DummyHostProvider();
    DummyHostProvider2 hostProvider2OfPlugin2 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin2, List.of(hostProvider1OfPlugin2, hostProvider2OfPlugin2));

    List<String> wantedOunitIds = List.of("hei1-ounit1", "hei1-ounit2");
    Map<DummyHostProvider, Collection<String>> result =
        this.hostPluginManager.getOunitIdsCoveredPerProviderOfHeiId(
            coveredHeiIdByPlugin1, List.of("hei1-ounit1", "hei1-ounit2"), DummyHostProvider.class);

    assertThat(result).containsOnlyKeys(hostProvider1OfPlugin1);
    assertThat(result.get(hostProvider1OfPlugin1))
        .containsExactlyInAnyOrder(wantedOunitIds.toArray(new String[0]));
  }

  @Test
  public void
      testGetOunitCodesCoveredPerProviderOfHeiId_TwoHostPluginsWithCoveringDistinctOunitCodesOfSameHeiId_ReturnsCorrectOunitCodesPerProvider() {
    String coveredHeiIdByPlugin1 = "hei1";
    List<String> coveredOunitCodesOfPlugin1 = List.of("hei1-ounit1", "hei1-ounit2", "hei1-ounit3");
    HostPlugin plugin1 =
        new MockHostPlugin.Builder()
            .coveredHeiId(coveredHeiIdByPlugin1)
            .coveredOunitCodesByHeiId(coveredHeiIdByPlugin1, coveredOunitCodesOfPlugin1)
            .build();
    DummyHostProvider hostProvider1OfPlugin1 = new DummyHostProvider();
    DummyHostProvider2 hostProvider2OfPlugin1 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin1, List.of(hostProvider1OfPlugin1, hostProvider2OfPlugin1));

    String coveredHeiIdByPlugin2 = "hei2";
    List<String> coveredOunitCodesOfPlugin2 = List.of("hei2-ounit1", "hei2-ounit2");
    HostPlugin plugin2 =
        new MockHostPlugin.Builder()
            .coveredHeiId(coveredHeiIdByPlugin2)
            .coveredOunitCodesByHeiId(coveredHeiIdByPlugin2, coveredOunitCodesOfPlugin2)
            .build();
    DummyHostProvider hostProvider1OfPlugin2 = new DummyHostProvider();
    DummyHostProvider2 hostProvider2OfPlugin2 = new DummyHostProvider2();
    this.hostPluginManager.registerPlugin(
        plugin2, List.of(hostProvider1OfPlugin2, hostProvider2OfPlugin2));

    List<String> wantedOunitIds = List.of("hei1-ounit1", "hei1-ounit2");
    Map<DummyHostProvider, Collection<String>> result =
        this.hostPluginManager.getOunitCodesCoveredPerProviderOfHeiId(
            coveredHeiIdByPlugin1, List.of("hei1-ounit1", "hei1-ounit2"), DummyHostProvider.class);

    assertThat(result).containsOnlyKeys(hostProvider1OfPlugin1);
    assertThat(result.get(hostProvider1OfPlugin1))
        .containsExactlyInAnyOrder(wantedOunitIds.toArray(new String[0]));
  }

  private static class DummyHostProvider extends HostProvider {}

  private static class DummyHostProvider2 extends HostProvider {}
}
