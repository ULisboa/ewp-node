package pt.ulisboa.ewp.node.service.ewp.iia.hash.v6;

import static org.assertj.core.api.Assertions.assertThat;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.CooperationConditions;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.Partner;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.MobilitySpecificationV6.RecommendedLanguageSkill;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.StudentStudiesMobilitySpecV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.StudentTraineeshipMobilitySpecV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.SubjectAreaV6;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashComparisonResult;
import pt.ulisboa.ewp.node.utils.EwpApiNamespaces;

class IiaHashServiceV6IntegrationTest extends AbstractIntegrationTest {

  private final IiaHashServiceV6 iiaHashService;

  IiaHashServiceV6IntegrationTest(@Autowired IiaHashServiceV6 iiaHashService) {
    this.iiaHashService = iiaHashService;
  }

  @Test
  public void testCalculateCooperationConditionsHashes_OneIia_ReturnCorrectHash()
      throws HashCalculationException {
    // Given
    Iia iia = createSampleIia1();

    // When
    List<HashCalculationResult> result =
        this.iiaHashService.calculateCooperationConditionsHashes(List.of(iia));

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getHash())
        .isEqualTo("4136142a584c4af354bdd508ea8ba5675dba3f336c2a2928e073ecadec280e14");
  }

  @Test
  public void testCalculateCooperationConditionsHashes_OneIiaTwice_ReturnSameCorrectHashTwice()
      throws HashCalculationException {
    // Given
    Iia iia = createSampleIia1();

    // When
    List<HashCalculationResult> result =
        this.iiaHashService.calculateCooperationConditionsHashes(List.of(iia, iia));

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getHash())
        .isEqualTo("4136142a584c4af354bdd508ea8ba5675dba3f336c2a2928e073ecadec280e14");
    assertThat(result.get(1).getHash())
        .isEqualTo("4136142a584c4af354bdd508ea8ba5675dba3f336c2a2928e073ecadec280e14");
  }

  @Test
  public void testCheckCooperationConditionsHash_ValidIiaCooperationConditionsHash_ReturnCorrectResult()
      throws HashComparisonException, IOException {
    // Given
    InputStream iiasInputStream = getClass().getClassLoader()
        .getResource("samples/iias/iias-get-response-example.1.xml").openStream();

    // When
    List<HashComparisonResult> result = this.iiaHashService.checkCooperationConditionsHash(
        new InputSource(iiasInputStream), EwpApiNamespaces.IIAS_V6_GET_RESPONSE.getNamespaceUrl());

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).isCorrect()).isTrue();
  }

  private Iia createSampleIia1() {
    Iia iia = new Iia();

    Partner partner1 = new Partner();
    partner1.setHeiId("uw.edu.pl");
    partner1.setIiaId("0f7a5682-faf7-49a7-9cc7-ec486c49a281");
    iia.getPartner().add(partner1);

    Partner partner2 = new Partner();
    partner2.setHeiId("hibo.no");
    partner2.setIiaId("1954991");
    iia.getPartner().add(partner2);

    CooperationConditions cooperationConditions = new CooperationConditions();

    StudentStudiesMobilitySpecV6 studentStudiesMobilitySpec = new StudentStudiesMobilitySpecV6();
    studentStudiesMobilitySpec.setSendingHeiId("uw.edu.pl");
    studentStudiesMobilitySpec.setSendingOunitId("140");
    studentStudiesMobilitySpec.setReceivingHeiId("hibo.no");
    cooperationConditions.getStudentStudiesMobilitySpec().add(studentStudiesMobilitySpec);

    StudentTraineeshipMobilitySpecV6 studentTraineeshipMobilitySpec = new StudentTraineeshipMobilitySpecV6();
    studentTraineeshipMobilitySpec.setSendingHeiId("uw.edu.pl");
    studentTraineeshipMobilitySpec.setSendingOunitId("140");
    studentTraineeshipMobilitySpec.setReceivingHeiId("hibo.no");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2014/2015");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2015/2016");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2016/2017");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2017/2018");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2018/2019");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2019/2020");
    studentTraineeshipMobilitySpec.getReceivingAcademicYearId().add("2020/2021");
    studentTraineeshipMobilitySpec.setMobilitiesPerYear(BigInteger.TEN);
    studentTraineeshipMobilitySpec.getRecommendedLanguageSkill()
        .add(new RecommendedLanguageSkill());
    studentTraineeshipMobilitySpec.getRecommendedLanguageSkill().get(0).setLanguage("no");
    studentTraineeshipMobilitySpec.getRecommendedLanguageSkill().get(0).setLanguage("B1");
    studentTraineeshipMobilitySpec.getSubjectArea().add(new SubjectAreaV6());
    studentTraineeshipMobilitySpec.getSubjectArea().get(0).setIscedFCode("0314");
    studentTraineeshipMobilitySpec.setTotalMonthsPerYear(BigDecimal.TEN);
    studentTraineeshipMobilitySpec.setBlended(false);
    cooperationConditions.getStudentTraineeshipMobilitySpec().add(studentTraineeshipMobilitySpec);

    iia.setCooperationConditions(cooperationConditions);

    return iia;
  }

}