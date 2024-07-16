package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.v2.OmobilitiesV2;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.MockOutgoingMobilitiesV2HostProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestEntriesProperties;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.plugin.manager.host.MockHostPluginManager;

class EwpApiOutgoingMobilitiesManifestEntryProviderTest {

  @Test
  void testGetManifestEntries_NoRegisteredProviders_NoManifestEntriesReturned() {
    // Arrange
    HostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiOutgoingMobilitiesManifestEntryProvider manifestEntryProvider = new EwpApiOutgoingMobilitiesManifestEntryProvider(
        hostPluginManager, manifestProperties);
    String heiId = "abc";
    String baseUrl = "http://example.com";

    doReturn(Collections.emptyList()).when(hostPluginManager)
        .getAllProvidersOfType(heiId, HostProvider.class);

    // Act
    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);

    // Assert
    assertThat(manifestEntries).isEmpty();
  }

  @Test
  void testGetManifestEntries_OneRegisteredV2Provider_ManifestEntryReturned() {
    // Arrange
    HostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiOutgoingMobilitiesManifestEntryProvider manifestEntryProvider = new EwpApiOutgoingMobilitiesManifestEntryProvider(
        hostPluginManager, manifestProperties);
    String heiId = "abc";
    String baseUrl = "http://example.com";

    MockOutgoingMobilitiesV2HostProvider provider = new MockOutgoingMobilitiesV2HostProvider(5);

    doReturn(Collections.singletonList(provider)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, HostProvider.class);

    // Act
    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);

    // Assert
    assertThat(manifestEntries).hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(OmobilitiesV2.class);
    OmobilitiesV2 manifestEntry = (OmobilitiesV2) manifestEntries.iterator().next();
    assertThat(manifestEntry.getVersion()).isEqualTo(provider.getVersion());
    assertThat(manifestEntry.getIndexUrl())
        .isEqualTo(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/index");
    assertThat(manifestEntry.getGetUrl())
        .isEqualTo(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/get");
    assertThat(manifestEntry.getMaxOmobilityIds()).isEqualTo(
        provider.getMaxOutgoingMobilityIdsPerRequest());
    assertThat(manifestEntry.getSendsNotifications()).isNotNull();
  }

  @Test
  void testGetManifestEntries_TwoRegisteredV2Provider_ManifestEntryReturned() {
    // Arrange
    HostPluginManager hostPluginManager = Mockito.spy(new MockHostPluginManager());
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiOutgoingMobilitiesManifestEntryProvider manifestEntryProvider = new EwpApiOutgoingMobilitiesManifestEntryProvider(
        hostPluginManager, manifestProperties);
    String heiId = "abc";
    String baseUrl = "http://example.com";

    MockOutgoingMobilitiesV2HostProvider provider1 = new MockOutgoingMobilitiesV2HostProvider(5);

    MockOutgoingMobilitiesV2HostProvider provider2 = new MockOutgoingMobilitiesV2HostProvider(10);

    doReturn(Arrays.asList(provider1, provider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, HostProvider.class);

    // Act
    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);

    // Assert
    assertThat(manifestEntries).hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(OmobilitiesV2.class);
    OmobilitiesV2 manifestEntry = (OmobilitiesV2) manifestEntries.iterator().next();
    assertThat(manifestEntry.getVersion()).isEqualTo(provider1.getVersion());
    assertThat(manifestEntry.getIndexUrl())
        .isEqualTo(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/index");
    assertThat(manifestEntry.getGetUrl())
        .isEqualTo(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/get");
    assertThat(manifestEntry.getMaxOmobilityIds()).isEqualTo(
        provider1.getMaxOutgoingMobilityIdsPerRequest());
    assertThat(manifestEntry.getSendsNotifications()).isNotNull();
  }

}