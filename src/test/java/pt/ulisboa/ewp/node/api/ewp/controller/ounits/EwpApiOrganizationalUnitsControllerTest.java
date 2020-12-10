package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2.Ounit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.OrganizationalUnitsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerTest;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

public class EwpApiOrganizationalUnitsControllerTest extends AbstractEwpControllerTest {

  @Mock private HostPluginManager hostPluginManager;

  private EwpApiOrganizationalUnitsController controller;

  @Before
  public void before() {
    this.controller = new EwpApiOrganizationalUnitsController(hostPluginManager);
  }

  @Test
  public void testOunitGet_UnknownHeiId() throws Exception {
    String unknownHeiId = "test";
    assertThatThrownBy(
            () -> {
              controller.ounitsGet(unknownHeiId, Collections.emptyList(), Collections.emptyList());
            })
        .isInstanceOf(EwpBadRequestException.class)
        .hasMessageContaining("Unknown HEI ID: " + unknownHeiId);
  }

  @Test
  public void testOunitGet_ValidHeiIdAndValidOunitIds() throws Exception {
    String validHeiId = "test";
    List<String> validOunitIds = Arrays.asList("a1", "b2");
    OrganizationalUnitsHostProvider provider =
        new OrganizationalUnitsHostProvider() {
          @Override
          public Collection<Ounit> findByHeiIdAndOunitIds(
              String heiId, Collection<String> ounitIds) {
            Collection<Ounit> result = new ArrayList<>();
            if (heiId.equalsIgnoreCase(validHeiId) && ounitIds.size() == 2) {
              ArrayList<String> ounitIdsAsList = new ArrayList<>(ounitIds);

              Ounit ounit1 = new Ounit();
              ounit1.setOunitId(ounitIdsAsList.get(0));
              result.add(ounit1);

              Ounit ounit2 = new Ounit();
              ounit2.setOunitId(ounitIdsAsList.get(1));
              result.add(ounit2);
            }
            return result;
          }

          @Override
          public Collection<Ounit> findByHeiIdAndOunitCodes(
              String heiId, Collection<String> ounitCodes) {
            return Collections.emptyList();
          }

          @Override
          public int getMaxOunitIdsPerRequest() {
            return validOunitIds.size();
          }
        };
    Mockito.when(hostPluginManager.getProvider(validHeiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(provider));

    ResponseEntity<OunitsResponseV2> response =
        controller.ounitsGet(validHeiId, validOunitIds, Collections.emptyList());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getOunit()).hasSize(validOunitIds.size());
    assertThat(response.getBody().getOunit().get(0).getOunitId()).isEqualTo(validOunitIds.get(0));
    assertThat(response.getBody().getOunit().get(1).getOunitId()).isEqualTo(validOunitIds.get(1));
  }

  @Test
  public void testOunitGet_ValidHeiIdAndValidOunitCodes() throws Exception {
    String validHeiId = "test";
    List<String> validOunitCodes = Arrays.asList("a1", "b2");
    OrganizationalUnitsHostProvider provider =
        new OrganizationalUnitsHostProvider() {
          @Override
          public Collection<Ounit> findByHeiIdAndOunitIds(
              String heiId, Collection<String> ounitIds) {
            return Collections.emptyList();
          }

          @Override
          public Collection<Ounit> findByHeiIdAndOunitCodes(
              String heiId, Collection<String> ounitCodes) {
            Collection<Ounit> result = new ArrayList<>();
            if (heiId.equalsIgnoreCase(validHeiId) && ounitCodes.size() == 2) {
              ArrayList<String> ounitCodesAsList = new ArrayList<>(ounitCodes);

              Ounit ounit1 = new Ounit();
              ounit1.setOunitCode(ounitCodesAsList.get(0));
              result.add(ounit1);

              Ounit ounit2 = new Ounit();
              ounit2.setOunitCode(ounitCodesAsList.get(1));
              result.add(ounit2);
            }
            return result;
          }

          @Override
          public int getMaxOunitCodesPerRequest() {
            return validOunitCodes.size();
          }
        };
    Mockito.when(hostPluginManager.getProvider(validHeiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(provider));

    ResponseEntity<OunitsResponseV2> response =
        controller.ounitsGet(validHeiId, Collections.emptyList(), validOunitCodes);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getOunit()).hasSize(validOunitCodes.size());
    assertThat(response.getBody().getOunit().get(0).getOunitCode())
        .isEqualTo(validOunitCodes.get(0));
    assertThat(response.getBody().getOunit().get(1).getOunitCode())
        .isEqualTo(validOunitCodes.get(1));
  }
}
