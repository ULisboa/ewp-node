package pt.ulisboa.ewp.node.service.ewp.iia;

import static org.assertj.core.api.Assertions.assertThat;

import eu.erasmuswithoutpaper.api.architecture.v1.StringWithOptionalLangV1;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.CooperationConditions;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.Partner;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.MobilitySpecificationV6.RecommendedLanguageSkill;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.StudentStudiesMobilitySpecV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.SubjectAreaV6;
import eu.erasmuswithoutpaper.api.types.contact.v1.ContactV1;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class ConditionsHashDecoratorTest {

  @Test
  void testDecoration_SameIiaTwice_BothIiaDecoratedWithSameHash() {
    // Arrange
    ConditionsHashDecorator conditionsHashDecorator = new ConditionsHashDecorator(
        new ConditionsHashCalculator());

    IiasGetResponseV6 iiasGetResponseV6 = new IiasGetResponseV6();

    Partner partner1 = new Partner();
    partner1.setHeiId("uw.edu.pl");
    partner1.setIiaId("0f7a5682-faf7-49a7-9cc7-ec486c49a281");
    partner1.setIiaCode("983/E+/III14&amp;15");

    ContactV1 signingContact1 = new ContactV1();
    StringWithOptionalLangV1 stringWithOptionalLang1 = new StringWithOptionalLangV1();
    stringWithOptionalLang1.setValue("Sylwia Nowak");
    signingContact1.getContactName().add(stringWithOptionalLang1);
    StringWithOptionalLangV1 stringWithOptionalLang2 = new StringWithOptionalLangV1();
    stringWithOptionalLang2.setValue("Sylwia");
    signingContact1.getPersonGivenNames().add(stringWithOptionalLang2);
    signingContact1.getEmail().add("email@example.com");
    partner1.setSigningContact(signingContact1);

    ContactV1 contact1 = new ContactV1();
    StringWithOptionalLangV1 stringWithOptionalLang3 = new StringWithOptionalLangV1();
    stringWithOptionalLang3.setValue("Jadwiga Kowalska");
    contact1.getContactName().add(stringWithOptionalLang3);
    StringWithOptionalLangV1 stringWithOptionalLang4 = new StringWithOptionalLangV1();
    stringWithOptionalLang4.setValue("Jadwiga");
    contact1.getPersonGivenNames().add(stringWithOptionalLang4);
    contact1.getEmail().add("email@example.com");
    partner1.getContact().add(contact1);

    CooperationConditions cooperationConditions = new CooperationConditions();
    StudentStudiesMobilitySpecV6 studentStudiesMobilitySpec = new StudentStudiesMobilitySpecV6();
    studentStudiesMobilitySpec.setSendingHeiId("uw.edu.pl");
    studentStudiesMobilitySpec.setSendingOunitId("140");
    studentStudiesMobilitySpec.setReceivingHeiId("hibo.no");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2014/2015");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2015/2016");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2016/2017");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2017/2018");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2018/2019");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2019/2020");
    studentStudiesMobilitySpec.getReceivingAcademicYearId().add("2020/2021");
    studentStudiesMobilitySpec.setMobilitiesPerYear(BigInteger.TWO);

    RecommendedLanguageSkill recommendedLanguageSkill1 = new RecommendedLanguageSkill();
    recommendedLanguageSkill1.setLanguage("en");
    recommendedLanguageSkill1.setCefrLevel("B1");
    studentStudiesMobilitySpec.getRecommendedLanguageSkill().add(recommendedLanguageSkill1);

    RecommendedLanguageSkill recommendedLanguageSkill2 = new RecommendedLanguageSkill();
    recommendedLanguageSkill2.setLanguage("no");
    recommendedLanguageSkill2.setCefrLevel("B1");
    studentStudiesMobilitySpec.getRecommendedLanguageSkill().add(recommendedLanguageSkill2);

    SubjectAreaV6 subjectArea = new SubjectAreaV6();
    subjectArea.setIscedFCode("0314");
    studentStudiesMobilitySpec.getSubjectArea().add(subjectArea);

    studentStudiesMobilitySpec.setTotalMonthsPerYear(BigDecimal.valueOf(5L));
    studentStudiesMobilitySpec.setBlended(false);
    studentStudiesMobilitySpec.getEqfLevel().add((byte) 7);
    studentStudiesMobilitySpec.getEqfLevel().add((byte) 8);

    cooperationConditions.getStudentStudiesMobilitySpec().add(studentStudiesMobilitySpec);

    Iia iia = new Iia();
    iia.setCooperationConditions(cooperationConditions);

    iiasGetResponseV6.getIia().add(iia);
    iiasGetResponseV6.getIia().add(iia);

    // Act
    conditionsHashDecorator.decorateWithConditionsHashes(iiasGetResponseV6);

    // Assert
    assertThat(iiasGetResponseV6.getIia().get(0).getConditionsHash()).isNotBlank();
    assertThat(iiasGetResponseV6.getIia().get(1).getConditionsHash()).isNotBlank();
    assertThat(iiasGetResponseV6.getIia().get(0).getConditionsHash()).isEqualTo(
        iiasGetResponseV6.getIia().get(1).getConditionsHash());
  }

}