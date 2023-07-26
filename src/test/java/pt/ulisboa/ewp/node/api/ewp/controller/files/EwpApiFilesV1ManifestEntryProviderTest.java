package pt.ulisboa.ewp.node.api.ewp.controller.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.files.v1.FileV1;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.MockFilesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.manifest.ManifestEntriesProperties;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.plugin.manager.host.MockHostPluginManager;

public class EwpApiFilesV1ManifestEntryProviderTest {

  @Test
  public void testManifestEntryRetrieval_NoHostProvider_NoManifestEntriesReturned()
      throws Exception {
    String baseUrl = "https://www.example.org/";
    String heiId = UUID.randomUUID().toString();

    HostPluginManager hostPluginManager = new MockHostPluginManager();
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiFilesManifestEntryProvider manifestEntryProvider = new EwpApiFilesManifestEntryProvider(
        hostPluginManager, manifestProperties);

    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);
    assertThat(manifestEntries).isEmpty();
  }

  @Test
  public void testManifestEntryRetrieval_TwoHostProvidersOfSameHeiId_OneManifestEntryReturned()
      throws Exception {
    String baseUrl = "https://www.example.org/";
    String heiId = UUID.randomUUID().toString();

    HostPluginManager hostPluginManager = Mockito.mock(HostPluginManager.class);
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiFilesManifestEntryProvider manifestEntryProvider = new EwpApiFilesManifestEntryProvider(
        hostPluginManager, manifestProperties);

    MockFilesV1HostProvider provider1 = new MockFilesV1HostProvider();
    MockFilesV1HostProvider provider2 = new MockFilesV1HostProvider();

    when(hostPluginManager.getAllProviders(heiId)).thenReturn(List.of(provider1, provider2));

    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);
    assertThat(manifestEntries).hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(FileV1.class);
    assertThat(((FileV1) manifestEntries.iterator().next()).getUrl())
        .isEqualTo(
            baseUrl + EwpApiFilesV1Controller.BASE_PATH + "?" + EwpApiParamConstants.HEI_ID + "=" +
                heiId);
  }

}
