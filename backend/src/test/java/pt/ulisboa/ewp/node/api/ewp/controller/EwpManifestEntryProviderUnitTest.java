package pt.ulisboa.ewp.node.api.ewp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.MockHostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestEntriesProperties;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.MockHostPluginManager;

class EwpManifestEntryProviderUnitTest {

  @Test
  public void testGetManifestEntriesSupportedByHost_OneNonPrimaryProviderAndExclusionSettingEnabled_NoManifestEntryReturned() {
    // Given
    MockHostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(true));
    EwpManifestEntryProvider manifestEntryProvider = new EwpManifestEntryProvider(
        hostPluginManager, manifestProperties) {
    };

    String heiId = UUID.randomUUID().toString();
    String baseUrl = "https://example.com";

    MockHostPlugin hostPlugin = new MockHostPlugin.Builder().heiIdOnWhichIsPrimary(heiId).build();
    HostProvider hostProvider = new HostProvider() {
    };
    hostPluginManager.registerPlugin(hostPlugin, List.of(hostProvider));

    manifestEntryProvider.registerHostProviderToManifestEntryConverter(HostProvider.class,
        (ignored1, ignored2, ignored3) -> {
          ManifestApiEntryBaseV1 result = new ManifestApiEntryBaseV1();
          MultilineStringV1 multilineStringV1 = new MultilineStringV1();
          multilineStringV1.setValue("TEST");
          result.setAdminNotes(multilineStringV1);
          return result;
        });

    // When
    Collection<ManifestApiEntryBaseV1> manifestEntriesSupportedByHost = manifestEntryProvider.getManifestEntriesSupportedByHost(
        heiId, baseUrl);

    // Then
    assertThat(manifestEntriesSupportedByHost).isEmpty();
  }

  @Test
  public void testGetManifestEntriesSupportedByHost_OneNonPrimaryProviderAndExclusionSettingNotEnabled_ManifestEntryReturned() {
    // Given
    MockHostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpManifestEntryProvider manifestEntryProvider = new EwpManifestEntryProvider(
        hostPluginManager, manifestProperties) {
    };

    String heiId = UUID.randomUUID().toString();
    String baseUrl = "https://example.com";

    HostPlugin hostPlugin = new MockHostPlugin.Builder().coveredHeiId(heiId).build();
    HostProvider hostProvider = new HostProvider() {
    };
    hostPluginManager.registerPlugin(hostPlugin, List.of(hostProvider));

    manifestEntryProvider.registerHostProviderToManifestEntryConverter(HostProvider.class,
        (ignored1, ignored2, ignored3) -> {
          ManifestApiEntryBaseV1 result = new ManifestApiEntryBaseV1();
          MultilineStringV1 multilineStringV1 = new MultilineStringV1();
          multilineStringV1.setValue("TEST");
          result.setAdminNotes(multilineStringV1);
          return result;
        });

    // When
    Collection<ManifestApiEntryBaseV1> manifestEntriesSupportedByHost = manifestEntryProvider.getManifestEntriesSupportedByHost(
        heiId, baseUrl);

    // Then
    assertThat(manifestEntriesSupportedByHost).isNotEmpty();
    assertThat(
        manifestEntriesSupportedByHost.iterator().next().getAdminNotes().getValue()).isEqualTo(
        "TEST");
  }

  @Test
  public void testGetManifestEntriesSupportedByHost_OnePrimaryProviderAndExclusionSettingNotEnabled_ManifestEntryReturned() {
    // Given
    MockHostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpManifestEntryProvider manifestEntryProvider = new EwpManifestEntryProvider(
        hostPluginManager, manifestProperties) {
    };

    String heiId = UUID.randomUUID().toString();
    String baseUrl = "https://example.com";

    MockHostPlugin hostPlugin = new MockHostPlugin.Builder().coveredHeiId(heiId).heiIdOnWhichIsPrimary(heiId).build();
    HostProvider hostProvider = new HostProvider() {
    };
    hostPluginManager.registerPlugin(hostPlugin, List.of(hostProvider));

    manifestEntryProvider.registerHostProviderToManifestEntryConverter(HostProvider.class,
        (ignored1, ignored2, ignored3) -> {
          ManifestApiEntryBaseV1 result = new ManifestApiEntryBaseV1();
          MultilineStringV1 multilineStringV1 = new MultilineStringV1();
          multilineStringV1.setValue("TEST");
          result.setAdminNotes(multilineStringV1);
          return result;
        });

    // When
    Collection<ManifestApiEntryBaseV1> manifestEntriesSupportedByHost = manifestEntryProvider.getManifestEntriesSupportedByHost(
        heiId, baseUrl);

    // Then
    assertThat(manifestEntriesSupportedByHost).isNotEmpty();
    assertThat(
        manifestEntriesSupportedByHost.iterator().next().getAdminNotes().getValue()).isEqualTo(
        "TEST");
  }

  @Test
  public void testGetManifestEntriesSupportedByHost_OnePrimaryProviderAndExclusionSettingEnabled_ManifestEntryReturned() {
    // Given
    MockHostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(true));
    EwpManifestEntryProvider manifestEntryProvider = new EwpManifestEntryProvider(
        hostPluginManager, manifestProperties) {
    };

    String heiId = UUID.randomUUID().toString();
    String baseUrl = "https://example.com";

    HostPlugin hostPlugin = new MockHostPlugin.Builder().coveredHeiId(heiId).heiIdOnWhichIsPrimary(heiId).build();
    HostProvider hostProvider = new HostProvider() {
    };
    hostPluginManager.registerPlugin(hostPlugin, List.of(hostProvider));

    manifestEntryProvider.registerHostProviderToManifestEntryConverter(HostProvider.class,
        (ignored1, ignored2, ignored3) -> {
          ManifestApiEntryBaseV1 result = new ManifestApiEntryBaseV1();
          MultilineStringV1 multilineStringV1 = new MultilineStringV1();
          multilineStringV1.setValue("TEST");
          result.setAdminNotes(multilineStringV1);
          return result;
        });

    // When
    Collection<ManifestApiEntryBaseV1> manifestEntriesSupportedByHost = manifestEntryProvider.getManifestEntriesSupportedByHost(
        heiId, baseUrl);

    // Then
    assertThat(manifestEntriesSupportedByHost).isNotEmpty();
    assertThat(
        manifestEntriesSupportedByHost.iterator().next().getAdminNotes().getValue()).isEqualTo(
        "TEST");
  }

}