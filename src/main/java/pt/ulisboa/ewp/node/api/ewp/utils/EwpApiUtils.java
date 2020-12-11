package pt.ulisboa.ewp.node.api.ewp.utils;

import static org.joox.JOOX.$;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.none.v1.CliauthAnonymousV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.v1.CliauthTlscertV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.v1.SrvauthTlscertV1;
import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;
import eu.erasmuswithoutpaper.registryclient.ApiSearchConditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.w3c.dom.Element;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpCourseApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpInstitutionApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOrganizationalUnitApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpSimpleCourseReplicationApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationAnonymousConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationHttpSignatureConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationTlsCertificateConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationHttpSignatureConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationTlsCertificateConfiguration;
import pt.ulisboa.ewp.node.utils.SemanticVersion;

public class EwpApiUtils {

  private EwpApiUtils() {}

  public static Optional<EwpInstitutionApiConfiguration> getInstitutionApiConfiguration(
      RegistryClient registryClient, String heiId) {
    Optional<InstitutionsV2> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_INSTITUTIONS_LOCAL_NAME,
            2,
            InstitutionsV2.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    InstitutionsV2 apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpInstitutionApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxHeiIds()));
  }

  public static Optional<EwpOrganizationalUnitApiConfiguration>
      getOrganizationalUnitApiConfiguration(RegistryClient registryClient, String heiId) {
    Optional<OrganizationalUnitsV2> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_ORGANIZATIONAL_UNITS_NAME,
            2,
            OrganizationalUnitsV2.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    OrganizationalUnitsV2 apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpOrganizationalUnitApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxOunitIds(),
            apiElement.getMaxOunitCodes()));
  }

  public static Optional<EwpCourseApiConfiguration> getCourseApiConfiguration(
      RegistryClient registryClient, String heiId) {
    Optional<CoursesV0> apiElementOptional =
        getApiElement(
            registryClient, heiId, EwpClientConstants.API_COURSES_NAME, 0, CoursesV0.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    CoursesV0 apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpCourseApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.getMaxLosIds(),
            apiElement.getMaxLosCodes()));
  }

  public static Optional<EwpSimpleCourseReplicationApiConfiguration>
      getSimpleCourseReplicationApiConfiguration(RegistryClient registryClient, String heiId) {
    Optional<SimpleCourseReplicationV1> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            EwpClientConstants.API_SIMPLE_COURSE_REPLICATION_NAME,
            1,
            SimpleCourseReplicationV1.class);
    if (!apiElementOptional.isPresent()) {
      return Optional.empty();
    }
    SimpleCourseReplicationV1 apiElement = apiElementOptional.get();

    return Optional.of(
        new EwpSimpleCourseReplicationApiConfiguration(
            apiElement.getUrl(),
            getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
            getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
            apiElement.isSupportsModifiedSince()));
  }

  public static <T> Optional<T> getApiElement(
      RegistryClient registryClient,
      String heiId,
      String apiLocalName,
      int wantedMajorVersion,
      Class<T> elementClassType) {
    Optional<Element> rawApiElementOptional =
        getRawApiElement(registryClient, heiId, apiLocalName, wantedMajorVersion);
    if (rawApiElementOptional.isEmpty()) {
      return Optional.empty();
    }
    Element rawApiElement = rawApiElementOptional.get();
    return Optional.of($(rawApiElement).unmarshalOne(elementClassType));
  }

  public static Optional<Element> getRawApiElement(
      RegistryClient registryClient, String heiId, String apiLocalName, int wantedMajorVersion) {
    ApiSearchConditions apiSearchConditions =
        new ApiSearchConditions().setRequiredHei(heiId).setApiClassRequired(null, apiLocalName);
    Collection<Element> rawApiElements = registryClient.findApis(apiSearchConditions);
    if (rawApiElements == null || rawApiElements.isEmpty()) {
      return Optional.empty();
    }

    for (Element rawApiElement : rawApiElements) {
      SemanticVersion semanticVersion = getSemanticVersionFromRawApiElement(rawApiElement);
      if (semanticVersion.getMajorVersion() == wantedMajorVersion) {
        return Optional.of(rawApiElement);
      }
    }
    return Optional.empty();
  }

  public static SemanticVersion getSemanticVersionFromRawApiElement(Element rawApiElement) {
    String rawVersion = rawApiElement.getAttribute("version");
    return SemanticVersion.createFromSemanticVersion(rawVersion);
  }

  public static Collection<EwpClientAuthenticationConfiguration>
      getSupportedClientAuthenticationMethods(HttpSecurityOptions httpSecurityOptions) {
    Collection<EwpClientAuthenticationConfiguration> result = new HashSet<>();
    if (httpSecurityOptions != null) {
      List<Object> clientAuthMethods = httpSecurityOptions.getClientAuthMethods().getAny();
      for (Object object : clientAuthMethods) {
        if (object instanceof CliauthHttpsigV1) {
          result.add(new EwpClientAuthenticationHttpSignatureConfiguration());
        } else if (object instanceof CliauthAnonymousV1) {
          result.add(new EwpClientAuthenticationAnonymousConfiguration());
        } else if (object instanceof CliauthTlscertV1) {
          CliauthTlscertV1 clientAuthTlsCert = (CliauthTlscertV1) object;
          result.add(
              new EwpClientAuthenticationTlsCertificateConfiguration(
                  clientAuthTlsCert.isAllowsSelfSigned()));
        } else {
          throw new IllegalArgumentException(
              "Unknown client authentication method: " + object.getClass().getCanonicalName());
        }
      }
    } else {
      // Default authentication methods according to EWP documentation
      result.add(new EwpClientAuthenticationTlsCertificateConfiguration(true));
    }
    return result;
  }

  public static Collection<EwpServerAuthenticationConfiguration>
      getSupportedServerAuthenticationMethods(HttpSecurityOptions httpSecurityOptions) {
    Collection<EwpServerAuthenticationConfiguration> result = new HashSet<>();
    if (httpSecurityOptions != null) {
      List<Object> serverAuthMethods = httpSecurityOptions.getServerAuthMethods().getAny();
      for (Object object : serverAuthMethods) {
        if (object instanceof SrvauthHttpsigV1) {
          result.add(new EwpServerAuthenticationHttpSignatureConfiguration());
        } else if (object instanceof SrvauthTlscertV1) {
          result.add(new EwpServerAuthenticationTlsCertificateConfiguration());
        } else {
          throw new IllegalArgumentException(
              "Unknown server authentication method: " + object.getClass().getCanonicalName());
        }
      }
    } else {
      // Default authentication methods according to EWP documentation
      result.add(new EwpServerAuthenticationTlsCertificateConfiguration());
    }
    return result;
  }

  /**
   * Returns the "best" supported API authentication method using a predefined list of
   * authentication methods order.
   *
   * @return
   */
  public static EwpAuthenticationMethod getBestSupportedApiAuthenticationMethod(
      EwpApiConfiguration api) {
    for (EwpAuthenticationMethod authenticationMethod :
        EwpClientConstants.AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER) {
      if (EwpApiUtils.doesApiSupportAuthenticationMethod(
          api.getSupportedClientAuthenticationMethods(),
          api.getSupportedServerAuthenticationMethods(),
          authenticationMethod)) {
        return authenticationMethod;
      }
    }

    throw new IllegalStateException(
        "Failed to find an admissible authentication method for API: " + api);
  }

  public static boolean doesApiSupportAuthenticationMethod(
      Collection<EwpClientAuthenticationConfiguration> clientAuthenticationConfigurations,
      Collection<EwpServerAuthenticationConfiguration> serverAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return doesClientSupportAuthenticationMethod(
            clientAuthenticationConfigurations, authenticationMethod)
        && doesServerSupportAuthenticationMethod(
            serverAuthenticationConfigurations, authenticationMethod);
  }

  public static boolean doesServerSupportAuthenticationMethod(
      Collection<EwpServerAuthenticationConfiguration> serverAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return serverAuthenticationConfigurations.stream()
        .anyMatch(
            c -> {
              switch (authenticationMethod) {
                case HTTP_SIGNATURE:
                  return c.isHttpSignature();

                case TLS:
                  return c.isTlsCertificate();

                default:
                  return false;
              }
            });
  }

  public static boolean doesClientSupportAuthenticationMethod(
      Collection<EwpClientAuthenticationConfiguration> clientAuthenticationConfigurations,
      EwpAuthenticationMethod authenticationMethod) {
    return clientAuthenticationConfigurations.stream()
        .anyMatch(
            c -> {
              switch (authenticationMethod) {
                case HTTP_SIGNATURE:
                  return c.isHttpSignature();

                case TLS:
                  return c.isTlsCertificate();

                case ANONYMOUS:
                  return c.isAnonymous();

                default:
                  return false;
              }
            });
  }

  public static ErrorResponseV1 createErrorResponseWithDeveloperMessage(String developerMessage) {
    ErrorResponseV1 errorResponse = new ErrorResponseV1();
    MultilineStringV1 message = new MultilineStringV1();
    message.setValue(developerMessage);
    errorResponse.setDeveloperMessage(message);
    return errorResponse;
  }
}
