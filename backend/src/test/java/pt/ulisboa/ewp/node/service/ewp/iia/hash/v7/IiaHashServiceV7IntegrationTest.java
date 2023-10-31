package pt.ulisboa.ewp.node.service.ewp.iia.hash.v7;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashComparisonResult;

class IiaHashServiceV7IntegrationTest extends AbstractIntegrationTest {

  private final IiaHashServiceV7 iiaHashService;

  IiaHashServiceV7IntegrationTest(@Autowired IiaHashServiceV7 iiaHashService) {
    this.iiaHashService = iiaHashService;
  }

  @Test
  public void testCalculateIiaHashes_IiaV7_ReturnCorrectHash()
      throws HashCalculationException, IOException {
    // Given
    byte[] iiasGetResponseXml =
        getClass()
            .getClassLoader()
            .getResourceAsStream("samples/iias/iias-get-response-v7.xml")
            .readAllBytes();

    // When
    List<HashCalculationResult> result =
        this.iiaHashService.calculateIiaHashes(iiasGetResponseXml, 7);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getHash())
        .isEqualTo("87b33170d7a6c6d894215641f39e7b7de36501265479e5ab3922f32d5b225033");
  }

  @Test
  public void testCalculateIiaHashes_IiaV6_ReturnCorrectHash()
      throws HashCalculationException, IOException {
    // Given
    byte[] iiasGetResponseXml =
        getClass()
            .getClassLoader()
            .getResourceAsStream("samples/iias/iias-get-response-v6.xml")
            .readAllBytes();

    // When
    List<HashCalculationResult> result =
        this.iiaHashService.calculateIiaHashes(iiasGetResponseXml, 6);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getHash())
        .isEqualTo("87b33170d7a6c6d894215641f39e7b7de36501265479e5ab3922f32d5b225033");
  }

  @Test
  public void testCheckIiaHashes_ValidIiaHash_ReturnCorrectResult()
      throws HashComparisonException, IOException {
    // Given
    InputStream iiasInputStream =
        getClass().getClassLoader().getResourceAsStream("samples/iias/iias-get-response-v7.xml");

    // When
    List<HashComparisonResult> result =
        this.iiaHashService.checkIiaHashes(iiasInputStream.readAllBytes());

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).isCorrect()).isTrue();
  }
}
