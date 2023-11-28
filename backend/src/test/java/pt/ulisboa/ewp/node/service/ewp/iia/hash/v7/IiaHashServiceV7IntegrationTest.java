package pt.ulisboa.ewp.node.service.ewp.iia.hash.v7;

import static org.assertj.core.api.Assertions.assertThat;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.CooperationConditions;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.Partner;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.MobilitySpecificationV7.MobilitiesPerYear;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.RecommendedLanguageSkillV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.StaffTeacherMobilitySpecV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.StudentStudiesMobilitySpecV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.SubjectAreaV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.SubjectAreaV7.IscedFCode;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
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
  public void testCalculateIiaHashes_IiaV6XmlWithValidIiaHash_ReturnCorrectHash()
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
  public void testCheckIiaHashes_IiaV7XmlWithValidIiaHash_ReturnCorrectResult()
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

  @Test
  public void testCheckIiaHashes_IiaV7_ReturnCorrectIiaHash() throws HashCalculationException {
    // Given
    Iia iia = new Iia();

    Partner partner1 = new Partner();
    partner1.setHeiId("uw.edu.pl");
    partner1.setIiaId("0f7a5682-faf7-49a7-9cc7-ec486c49a281");
    partner1.setIiaCode("983/E+/III14&amp;15");
    iia.getPartner().add(partner1);

    Partner partner2 = new Partner();
    partner2.setHeiId("hibo.no");
    partner2.setIiaId("1954991");
    partner2.setIiaCode("2014/E+/PL/4104B");
    iia.getPartner().add(partner2);

    iia.setInEffect(true);

    StudentStudiesMobilitySpecV7 studentStudiesMobilitySpec = new StudentStudiesMobilitySpecV7();
    studentStudiesMobilitySpec.setSendingHeiId("uw.edu.pl");
    studentStudiesMobilitySpec.setSendingOunitId("140");
    studentStudiesMobilitySpec.setReceivingHeiId("hibo.no");
    studentStudiesMobilitySpec.setReceivingFirstAcademicYearId("2014/2015");
    studentStudiesMobilitySpec.setReceivingLastAcademicYearId("2020/2021");
    MobilitiesPerYear mobilitiesPerYear = new MobilitiesPerYear();
    mobilitiesPerYear.setNotYetDefined(true);
    mobilitiesPerYear.setValue(BigInteger.TWO);
    studentStudiesMobilitySpec.setMobilitiesPerYear(mobilitiesPerYear);
    RecommendedLanguageSkillV7 recommendedLanguageSkill = new RecommendedLanguageSkillV7();
    recommendedLanguageSkill.setNotYetDefined(true);
    recommendedLanguageSkill.setLanguage("en");
    recommendedLanguageSkill.setCefrLevel("B1");
    studentStudiesMobilitySpec.getRecommendedLanguageSkill().add(recommendedLanguageSkill);
    SubjectAreaV7 subjectArea = new SubjectAreaV7();
    IscedFCode iscedFCode = new IscedFCode();
    iscedFCode.setV6Value("031");
    iscedFCode.setValue("0314");
    subjectArea.setIscedFCode(iscedFCode);
    subjectArea.setIscedClarification("Social and behavioural sciences");
    studentStudiesMobilitySpec.getSubjectArea().add(subjectArea);
    studentStudiesMobilitySpec.setTotalMonthsPerYear(BigDecimal.valueOf(5L));
    studentStudiesMobilitySpec.setBlended(false);
    studentStudiesMobilitySpec.getEqfLevel().add((byte) 7);
    studentStudiesMobilitySpec.getEqfLevel().add((byte) 8);

    StaffTeacherMobilitySpecV7 staffTeacherMobilitySpec = new StaffTeacherMobilitySpecV7();
    staffTeacherMobilitySpec.setSendingHeiId("uw.edu.pl");
    staffTeacherMobilitySpec.setSendingOunitId("140");
    staffTeacherMobilitySpec.setReceivingHeiId("hibo.no");
    staffTeacherMobilitySpec.setReceivingFirstAcademicYearId("2016/2017");
    staffTeacherMobilitySpec.setReceivingLastAcademicYearId("2017/2018");
    MobilitiesPerYear mobilitiesPerYear2 = new MobilitiesPerYear();
    mobilitiesPerYear2.setValue(BigInteger.TWO);
    staffTeacherMobilitySpec.setMobilitiesPerYear(mobilitiesPerYear2);
    RecommendedLanguageSkillV7 recommendedLanguageSkill2 = new RecommendedLanguageSkillV7();
    recommendedLanguageSkill2.setLanguage("en");
    recommendedLanguageSkill2.setCefrLevel("C1");
    staffTeacherMobilitySpec.getRecommendedLanguageSkill().add(recommendedLanguageSkill2);
    SubjectAreaV7 subjectArea2 = new SubjectAreaV7();
    IscedFCode iscedFCode2 = new IscedFCode();
    iscedFCode2.setValue("0314");
    subjectArea2.setIscedFCode(iscedFCode2);
    staffTeacherMobilitySpec.getSubjectArea().add(subjectArea2);
    staffTeacherMobilitySpec.setTotalDaysPerYear(BigDecimal.valueOf(8L));

    CooperationConditions cooperationConditions = new CooperationConditions();
    cooperationConditions.getStudentStudiesMobilitySpec().add(studentStudiesMobilitySpec);
    cooperationConditions.getStaffTeacherMobilitySpec().add(staffTeacherMobilitySpec);
    iia.setCooperationConditions(cooperationConditions);

    // When
    List<HashCalculationResult> result = this.iiaHashService.calculateIiaHashes(List.of(iia));

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getHash())
        .isEqualTo("87b33170d7a6c6d894215641f39e7b7de36501265479e5ab3922f32d5b225033");
  }
}
