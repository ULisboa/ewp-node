package pt.ulisboa.ewp.node.utils;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetV1;
import eu.erasmuswithoutpaper.api.files.v1.FileV1;
import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrV1;
import eu.erasmuswithoutpaper.api.iias.approval.cnr.v2.IiaApprovalCnrV2;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalV1;
import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalV2;
import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrV2;
import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrV3;
import eu.erasmuswithoutpaper.api.iias.v7.IiasV7;
import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.cnr.v1.ImobilityTorCnrV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v1.ImobilityTorsV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v2.ImobilityTorsV2;
import eu.erasmuswithoutpaper.api.imobilities.v1.ImobilitiesV1;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsV2;
import eu.erasmuswithoutpaper.api.monitoring.v1.MonitoringV1;
import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrV1;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.OmobilityLasV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.OmobilitiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v2.OmobilitiesV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFactsheetApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFilesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityToRApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityToRCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementApprovalCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInterInstitutionalAgreementCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpMonitoringApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityLearningAgreementCnrApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityLearningAgreementsApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;

public final class EwpApiSpecification {

  private EwpApiSpecification() {}

  public static class Institutions {

    private Institutions() {}

    public static final EwpApiVersionSpecification<InstitutionsV2, EwpInstitutionApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.INSTITUTIONS,
                2,
                InstitutionsV2.class,
                EwpInstitutionApiConfiguration::create);
  }

  public static class OrganizationalUnits {

    private OrganizationalUnits() {}

    public static final EwpApiVersionSpecification<
            OrganizationalUnitsV2, EwpOrganizationalUnitApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.ORGANIZATIONAL_UNITS,
                2,
                OrganizationalUnitsV2.class,
                EwpOrganizationalUnitApiConfiguration::create);
  }

  public static class Courses {

    private Courses() {}

    public static final EwpApiVersionSpecification<CoursesV0, EwpCourseApiConfiguration> V0 =
        new EwpApiVersionSpecification<>(
            EwpApi.COURSES, 0, CoursesV0.class, EwpCourseApiConfiguration::create);
  }

  public static class SimpleCourseReplication {

    private SimpleCourseReplication() {}

    public static final EwpApiVersionSpecification<
            SimpleCourseReplicationV1, EwpSimpleCourseReplicationApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.SIMPLE_COURSE_REPLICATION,
                1,
                SimpleCourseReplicationV1.class,
                EwpSimpleCourseReplicationApiConfiguration::create);
  }

  public static class InterInstitutionalAgreements {

    private InterInstitutionalAgreements() {}

    public static final EwpApiVersionSpecification<
            IiasV7, EwpInterInstitutionalAgreementApiConfiguration>
        V7 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENTS,
                7,
                IiasV7.class,
                EwpInterInstitutionalAgreementApiConfiguration::create);
  }

  public static class InterInstitutionalAgreementCnr {

    private InterInstitutionalAgreementCnr() {}

    public static final EwpApiVersionSpecification<
            IiaCnrV2, EwpInterInstitutionalAgreementCnrApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENT_CNR,
                2,
                IiaCnrV2.class,
                EwpInterInstitutionalAgreementCnrApiConfiguration::create);

    public static final EwpApiVersionSpecification<
            IiaCnrV3, EwpInterInstitutionalAgreementCnrApiConfiguration>
        V3 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENT_CNR,
                3,
                IiaCnrV3.class,
                EwpInterInstitutionalAgreementCnrApiConfiguration::create);
  }

  public static class InterInstitutionalAgreementApprovals {

    private InterInstitutionalAgreementApprovals() {}

    public static final EwpApiVersionSpecification<
            IiasApprovalV1, EwpInterInstitutionalAgreementApprovalApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL,
                1,
                IiasApprovalV1.class,
                EwpInterInstitutionalAgreementApprovalApiConfiguration::create);

    public static final EwpApiVersionSpecification<
            IiasApprovalV2, EwpInterInstitutionalAgreementApprovalApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL,
                2,
                IiasApprovalV2.class,
                EwpInterInstitutionalAgreementApprovalApiConfiguration::create);
  }

  public static class InterInstitutionalAgreementApprovalCnr {

    private InterInstitutionalAgreementApprovalCnr() {}

    public static final EwpApiVersionSpecification<
            IiaApprovalCnrV1, EwpInterInstitutionalAgreementApprovalCnrApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_CNR,
                1,
                IiaApprovalCnrV1.class,
                EwpInterInstitutionalAgreementApprovalCnrApiConfiguration::create);

    public static final EwpApiVersionSpecification<
            IiaApprovalCnrV2, EwpInterInstitutionalAgreementApprovalCnrApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_CNR,
                2,
                IiaApprovalCnrV2.class,
                EwpInterInstitutionalAgreementApprovalCnrApiConfiguration::create);
  }

  public static class FactSheets {

    private FactSheets() {}

    public static final EwpApiVersionSpecification<FactsheetV1, EwpFactsheetApiConfiguration> V1 =
        new EwpApiVersionSpecification<>(
            EwpApi.FACTSHEETS, 1, FactsheetV1.class, EwpFactsheetApiConfiguration::create);
  }

  public static class Files {

    private Files() {}

    public static final EwpApiVersionSpecification<FileV1, EwpFilesApiConfiguration> V1 =
        new EwpApiVersionSpecification<>(
            EwpApi.FILES, 1, FileV1.class, EwpFilesApiConfiguration::create);
  }

  public static class OutgoingMobilities {

    private OutgoingMobilities() {}

    public static final EwpApiVersionSpecification<
            OmobilitiesV1, EwpOutgoingMobilitiesApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.OUTGOING_MOBILITIES,
                1,
                OmobilitiesV1.class,
                EwpOutgoingMobilitiesApiConfiguration::create);

    public static final EwpApiVersionSpecification<
            OmobilitiesV2, EwpOutgoingMobilitiesApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.OUTGOING_MOBILITIES,
                2,
                OmobilitiesV2.class,
                EwpOutgoingMobilitiesApiConfiguration::create);
  }

  public static class OutgoingMobilityCnr {

    private OutgoingMobilityCnr() {}

    public static final EwpApiVersionSpecification<
            OmobilityCnrV1, EwpOutgoingMobilityCnrApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.OUTGOING_MOBILITY_CNR,
                1,
                OmobilityCnrV1.class,
                EwpOutgoingMobilityCnrApiConfiguration::create);
  }

  public static class OutgoingMobilityLearningAgreements {

    private OutgoingMobilityLearningAgreements() {}

    public static final EwpApiVersionSpecification<
            OmobilityLasV1, EwpOutgoingMobilityLearningAgreementsApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.OUTGOING_MOBILITY_LEARNING_AGREEMENTS,
                1,
                OmobilityLasV1.class,
                EwpOutgoingMobilityLearningAgreementsApiConfiguration::create);
  }

  public static class OutgoingMobilityLearningAgreementCnr {

    private OutgoingMobilityLearningAgreementCnr() {}

    public static final EwpApiVersionSpecification<
            OmobilityLaCnrV1, EwpOutgoingMobilityLearningAgreementCnrApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR,
                1,
                OmobilityLaCnrV1.class,
                EwpOutgoingMobilityLearningAgreementCnrApiConfiguration::create);
  }

  public static class IncomingMobilities {

    private IncomingMobilities() {}

    public static final EwpApiVersionSpecification<
            ImobilitiesV1, EwpIncomingMobilitiesApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INCOMING_MOBILITIES,
                1,
                ImobilitiesV1.class,
                EwpIncomingMobilitiesApiConfiguration::create);
  }

  public static class IncomingMobilityCnr {

    private IncomingMobilityCnr() {}

    public static final EwpApiVersionSpecification<
            ImobilityCnrV1, EwpIncomingMobilityCnrApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INCOMING_MOBILITY_CNR,
                1,
                ImobilityCnrV1.class,
                EwpIncomingMobilityCnrApiConfiguration::create);
  }

  public static class IncomingMobilityToRs {

    private IncomingMobilityToRs() {}

    public static final EwpApiVersionSpecification<
            ImobilityTorsV1, EwpIncomingMobilityToRApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INCOMING_MOBILITY_TORS,
                1,
                ImobilityTorsV1.class,
                EwpIncomingMobilityToRApiConfiguration::create);

    public static final EwpApiVersionSpecification<
            ImobilityTorsV2, EwpIncomingMobilityToRApiConfiguration>
        V2 =
            new EwpApiVersionSpecification<>(
                EwpApi.INCOMING_MOBILITY_TORS,
                2,
                ImobilityTorsV2.class,
                EwpIncomingMobilityToRApiConfiguration::create);
  }

  public static class IncomingMobilityToRCnr {

    private IncomingMobilityToRCnr() {}

    public static final EwpApiVersionSpecification<
            ImobilityTorCnrV1, EwpIncomingMobilityToRCnrApiConfiguration>
        V1 =
            new EwpApiVersionSpecification<>(
                EwpApi.INCOMING_MOBILITY_TOR_CNR,
                1,
                ImobilityTorCnrV1.class,
                EwpIncomingMobilityToRCnrApiConfiguration::create);
  }

  public static class Monitoring {

    public static final EwpApiVersionSpecification<MonitoringV1, EwpMonitoringApiConfiguration> V1 =
        new EwpApiVersionSpecification<>(
            EwpApi.MONITORING, 1, MonitoringV1.class, EwpMonitoringApiConfiguration::create);

    private Monitoring() {}
  }

  @FunctionalInterface
  public interface SpecificationElementToConfigurationConverter<
      E extends ManifestApiEntryBaseV1, C extends EwpApiConfiguration> {

    C convert(String heiId, E manifestEntry);
  }

  public static class EwpApiVersionSpecification<
      E extends ManifestApiEntryBaseV1, C extends EwpApiConfiguration> {

    private final EwpApi api;
    private final int majorVersion;
    private final Class<E> specificationElementClassType;
    private final SpecificationElementToConfigurationConverter<E, C>
        specificationElementToConfigurationConverter;

    private EwpApiVersionSpecification(
        EwpApi api,
        int majorVersion,
        Class<E> specificationElementClassType,
        SpecificationElementToConfigurationConverter<E, C>
            specificationElementToConfigurationConverter) {
      this.api = api;
      this.majorVersion = majorVersion;
      this.specificationElementClassType = specificationElementClassType;
      this.specificationElementToConfigurationConverter =
          specificationElementToConfigurationConverter;
    }

    public C getConfigurationForHeiId(RegistryClient registryClient, String heiId) {
      return EwpApiUtils.getApiConfiguration(
          registryClient,
          heiId,
          api.getLocalName(),
          majorVersion,
          specificationElementClassType,
          specificationElementToConfigurationConverter);
    }
  }
}
