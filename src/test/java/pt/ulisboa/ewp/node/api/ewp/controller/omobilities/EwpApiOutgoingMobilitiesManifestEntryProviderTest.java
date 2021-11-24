package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.OmobilitiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

class EwpApiOutgoingMobilitiesManifestEntryProviderTest {

  @Test
  void testGetManifestEntries_NoRegisteredProviders_NoManifestEntriesReturned() {
    // Arrange
    HostPluginManager hostPluginManager = Mockito.spy(new HostPluginManager(""));
    EwpApiOutgoingMobilitiesManifestEntryProvider manifestEntryProvider = new EwpApiOutgoingMobilitiesManifestEntryProvider(
        hostPluginManager);
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
  void testGetManifestEntries_OneRegisteredV1Provider_ManifestEntryReturned() {
    // Arrange
    HostPluginManager hostPluginManager = Mockito.spy(new HostPluginManager(""));
    EwpApiOutgoingMobilitiesManifestEntryProvider manifestEntryProvider = new EwpApiOutgoingMobilitiesManifestEntryProvider(
        hostPluginManager);
    String heiId = "abc";
    String baseUrl = "http://example.com";

    OutgoingMobilitiesV1HostProvider provider = new OutgoingMobilitiesV1HostProvider() {

      @Override
      public Collection<String> findOutgoingMobilityIds(Collection<String> requesterCoveredHeiIds,
          String sendingHeiId, Collection<String> receivingHeiIds,
          @Nullable String receivingAcademicYearId, @Nullable LocalDateTime modifiedSince) {
        return null;
      }

      @Override
      public Collection<StudentMobilityForStudiesV1> findBySendingHeiIdAndOutgoingMobilityIds(
          Collection<String> requesterCoveredHeiIds, String sendingHeiId,
          Collection<String> outgoingMobilityIds) {
        return null;
      }
    };

    doReturn(Collections.singletonList(provider)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, HostProvider.class);

    // Act
    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);

    // Assert
    assertThat(manifestEntries).hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(OmobilitiesV1.class);
    OmobilitiesV1 manifestEntry = (OmobilitiesV1) manifestEntries.iterator().next();
    assertThat(manifestEntry.getVersion()).isEqualTo(provider.getVersion());
    assertThat(manifestEntry.getIndexUrl()).isEqualTo(
        baseUrl + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH + "/index");
    assertThat(manifestEntry.getGetUrl()).isEqualTo(
        baseUrl + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH + "/get");
    assertThat(manifestEntry.getMaxOmobilityIds()).isEqualTo(
        provider.getMaxOutgoingMobilityIdsPerRequest());
    assertThat(manifestEntry.getSendsNotifications()).isNotNull();
  }

}