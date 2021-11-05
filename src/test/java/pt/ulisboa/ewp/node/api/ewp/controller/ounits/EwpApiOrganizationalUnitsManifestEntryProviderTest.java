package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2.Ounit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

class EwpApiOrganizationalUnitsManifestEntryProviderTest {

  @Test
  public void testGetManifestEntries_TwoApplicableHostProvidersWithSameVersion_OnlyOneManifestEntry() {
    HostPluginManager hostPluginManager = Mockito.spy(new HostPluginManager(""));
    EwpApiOrganizationalUnitsManifestEntryProvider manifestEntryProvider = new EwpApiOrganizationalUnitsManifestEntryProvider(
        hostPluginManager);

    OrganizationalUnitsV2HostProvider provider1 = new OrganizationalUnitsV2HostProvider() {
      @Override
      public Collection<Ounit> findByHeiIdAndOunitIds(String heiId, Collection<String> ounitIds) {
        return null;
      }

      @Override
      public Collection<Ounit> findByHeiIdAndOunitCodes(String heiId,
          Collection<String> ounitCodes) {
        return null;
      }

      @Override
      public int getMaxOunitIdsPerRequest() {
        return 5;
      }

      @Override
      public int getMaxOunitCodesPerRequest() {
        return 5;
      }
    };

    OrganizationalUnitsV2HostProvider provider2 = new OrganizationalUnitsV2HostProvider() {
      @Override
      public Collection<Ounit> findByHeiIdAndOunitIds(String heiId, Collection<String> ounitIds) {
        return null;
      }

      @Override
      public Collection<Ounit> findByHeiIdAndOunitCodes(String heiId,
          Collection<String> ounitCodes) {
        return null;
      }

      @Override
      public int getMaxOunitIdsPerRequest() {
        return 10;
      }

      @Override
      public int getMaxOunitCodesPerRequest() {
        return 10;
      }
    };

    String heiId = UUID.randomUUID().toString();
    doReturn(Map.of(provider1.getClass(), Arrays.asList(provider1, provider2))).when(
        hostPluginManager).getAllProvidersPerClassType(heiId);

    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, "http://example.com");
    assertThat(manifestEntries).asList().hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(OrganizationalUnitsV2.class);
    assertThat(((OrganizationalUnitsV2) manifestEntries.iterator().next()).getMaxOunitIds()
        .intValueExact())
        .isEqualTo(5);
    assertThat(((OrganizationalUnitsV2) manifestEntries.iterator().next()).getMaxOunitCodes()
        .intValueExact())
        .isEqualTo(5);
  }

}