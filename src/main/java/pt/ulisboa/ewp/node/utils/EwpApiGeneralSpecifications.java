package pt.ulisboa.ewp.node.utils;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetV1;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalV1;
import eu.erasmuswithoutpaper.api.iias.v3.IiasV3;
import eu.erasmuswithoutpaper.api.iias.v4.IiasV4;
import eu.erasmuswithoutpaper.api.imobilities.v1.ImobilitiesV1;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsV2;
import eu.erasmuswithoutpaper.api.omobilities.v1.OmobilitiesV1;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import java.util.function.Function;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFactsheetApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterinstitutionalAgreementApprovalApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;

public class EwpApiGeneralSpecifications {

  public static final EwpApiGeneralSpecification<InstitutionsV2, EwpInstitutionApiConfiguration>
      INSTITUTIONS_V2 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INSTITUTIONS_LOCAL_NAME,
          2,
          InstitutionsV2.class,
          EwpInstitutionApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      OrganizationalUnitsV2, EwpOrganizationalUnitApiConfiguration>
      ORGANIZATIONAL_UNITS_V2 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_ORGANIZATIONAL_UNITS_NAME,
          2,
          OrganizationalUnitsV2.class,
          EwpOrganizationalUnitApiConfiguration::create);

  public static final EwpApiGeneralSpecification<CoursesV0, EwpCourseApiConfiguration> COURSES_V0 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_COURSES_NAME, 0, CoursesV0.class, EwpCourseApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      SimpleCourseReplicationV1, EwpSimpleCourseReplicationApiConfiguration>
      SIMPLE_COURSE_REPLICATION_V1 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_SIMPLE_COURSE_REPLICATION_NAME,
          1,
          SimpleCourseReplicationV1.class,
          EwpSimpleCourseReplicationApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      IiasV3, EwpInterinstitutionalAgreementApiConfiguration>
      INTERINSTITUTIONAL_AGREEMENT_V3 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME,
          3,
          IiasV3.class,
          EwpInterinstitutionalAgreementApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      IiasV4, EwpInterinstitutionalAgreementApiConfiguration>
      INTERINSTITUTIONAL_AGREEMENT_V4 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME,
          4,
          IiasV4.class,
          EwpInterinstitutionalAgreementApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      IiasApprovalV1, EwpInterinstitutionalAgreementApprovalApiConfiguration>
      INTERINSTITUTIONAL_AGREEMENT_APPROVAL_V1 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_NAME,
          1,
          IiasApprovalV1.class,
          EwpInterinstitutionalAgreementApprovalApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      FactsheetV1, EwpFactsheetApiConfiguration>
      FACTSHEET_V1 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_FACTSHEET_NAME,
          1,
          FactsheetV1.class,
          EwpFactsheetApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      ImobilitiesV1, EwpIncomingMobilitiesApiConfiguration>
      INCOMING_MOBILITIES_V1 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INCOMING_MOBILITIES_NAME,
          1,
          ImobilitiesV1.class,
          EwpIncomingMobilitiesApiConfiguration::create);

  public static final EwpApiGeneralSpecification<
      OmobilitiesV1, EwpOutgoingMobilitiesApiConfiguration>
      OUTGOING_MOBILITIES_V1 =
      new EwpApiGeneralSpecification<>(
          EwpApiConstants.API_INCOMING_MOBILITIES_NAME,
          1,
          OmobilitiesV1.class,
          EwpOutgoingMobilitiesApiConfiguration::create);

  private EwpApiGeneralSpecifications() {
  }

  public static class EwpApiGeneralSpecification<
      E extends ManifestApiEntryBaseV1, C extends EwpApiConfiguration> {

    private final String localName;
    private final int majorVersion;
    private final Class<E> specificationElementClassType;
    private final Function<E, C> specificationElementToConfigurationTransformer;

    private EwpApiGeneralSpecification(
        String localName,
        int majorVersion,
        Class<E> specificationElementClassType,
        Function<E, C> specificationElementToConfigurationTransformer) {
      this.localName = localName;
      this.majorVersion = majorVersion;
      this.specificationElementClassType = specificationElementClassType;
      this.specificationElementToConfigurationTransformer =
          specificationElementToConfigurationTransformer;
    }

    public C getConfigurationForHeiId(RegistryClient registryClient, String heiId) {
      return EwpApiUtils.getApiConfiguration(
          registryClient,
          heiId,
          localName,
          majorVersion,
          specificationElementClassType,
          specificationElementToConfigurationTransformer);
    }
  }
}
