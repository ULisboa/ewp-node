package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.MockCoursesV0HostProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestEntriesProperties;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

public class EwpApiCoursesV0ManifestEntryProviderTest {

  @Test
  public void testManifestEntryRetrieval_NoHostProvider_NoManifestEntriesReturned() {
    String baseUrl = "https://www.example.org/";
    String heiId = UUID.randomUUID().toString();

    HostPluginManager hostPluginManager = new HostPluginManager("/" + UUID.randomUUID(),
        null);
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiCoursesManifestEntryProvider manifestEntryProvider = new EwpApiCoursesManifestEntryProvider(
        hostPluginManager, manifestProperties);

    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);
    assertThat(manifestEntries).isEmpty();
  }

  @Test
  public void testManifestEntryRetrieval_TwoHostProvidersOfSameHeiId_OneManifestEntryReturned() {
    String baseUrl = "https://www.example.org/";
    String heiId = UUID.randomUUID().toString();

    HostPluginManager hostPluginManager = Mockito.mock(HostPluginManager.class);
    ManifestProperties manifestProperties = ManifestProperties.create(
        ManifestEntriesProperties.create(false));
    EwpApiCoursesManifestEntryProvider manifestEntryProvider = new EwpApiCoursesManifestEntryProvider(
        hostPluginManager, manifestProperties);

    MockCoursesV0HostProvider provider1 = new MockCoursesV0HostProvider(5, 2);
    MockCoursesV0HostProvider provider2 = new MockCoursesV0HostProvider(3, 5);

    when(hostPluginManager.getAllProviders(heiId)).thenReturn(List.of(provider1, provider2));

    Collection<ManifestApiEntryBaseV1> manifestEntries = manifestEntryProvider.getManifestEntries(
        heiId, baseUrl);
    assertThat(manifestEntries).hasSize(1);
    assertThat(manifestEntries.iterator().next()).isInstanceOf(CoursesV0.class);

    CoursesV0 coursesManifestEntry = (CoursesV0) manifestEntries.iterator().next();
    assertThat(coursesManifestEntry.getUrl())
        .isEqualTo(baseUrl + EwpApiCoursesV0Controller.BASE_PATH);
    assertThat(coursesManifestEntry.getMaxLosIds()).isEqualTo(3);
    assertThat(coursesManifestEntry.getMaxLosCodes()).isEqualTo(2);
  }

}
