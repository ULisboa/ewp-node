package pt.ulisboa.ewp.node.api.ewp.utils;

import static org.joox.JOOX.$;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.none.v1.CliauthAnonymousV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.v1.CliauthTlscertV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.v1.SrvauthTlscertV1;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;
import eu.erasmuswithoutpaper.registryclient.ApiSearchConditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.w3c.dom.Element;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdAndMajorVersionException;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
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

  private EwpApiUtils() {
  }

  public static <T, C> C getApiConfiguration(
      RegistryClient registryClient,
      String heiId,
      String apiLocalName,
      int wantedMajorVersion,
      Class<T> apiConfigurationElementClassType,
      Function<T, C> apiConfigurationTransformer)
      throws NoEwpApiForHeiIdAndMajorVersionException {
    Optional<T> apiElementOptional =
        getApiElement(
            registryClient,
            heiId,
            apiLocalName,
            wantedMajorVersion,
            apiConfigurationElementClassType);
    if (apiElementOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdAndMajorVersionException(heiId, apiLocalName, wantedMajorVersion);
    }
    return apiConfigurationTransformer.apply(apiElementOptional.get());
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

  public static List<Integer> getSupportedMajorVersions(
      RegistryClient registryClient, String heiId, String apiLocalName) {
    Collection<Element> rawApiElements = getRawApiElements(registryClient, heiId, apiLocalName);
    List<Integer> result = new ArrayList<>();
    for (Element rawApiElement : rawApiElements) {
      SemanticVersion semanticVersion = getSemanticVersionFromRawApiElement(rawApiElement);
      result.add(semanticVersion.getMajorVersion());
    }
    return result;
  }

  public static Optional<Element> getRawApiElement(
      RegistryClient registryClient, String heiId, String apiLocalName, int wantedMajorVersion) {
    Collection<Element> rawApiElements = getRawApiElements(registryClient, heiId, apiLocalName);
    if (rawApiElements.isEmpty()) {
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

  public static Collection<Element> getRawApiElements(
      RegistryClient registryClient, String heiId, String apiLocalName) {
    ApiSearchConditions apiSearchConditions =
        new ApiSearchConditions().setRequiredHei(heiId).setApiClassRequired(null, apiLocalName);
    Collection<Element> rawApiElements = registryClient.findApis(apiSearchConditions);
    if (rawApiElements == null) {
      return new ArrayDeque<>();
    }
    return rawApiElements;
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
   */
  public static EwpAuthenticationMethod getBestSupportedApiAuthenticationMethod(
      EwpApiConfiguration api) {
    for (EwpAuthenticationMethod authenticationMethod :
        EwpClientConstants.AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER) {
      if (api.supportsAuthenticationMethod(authenticationMethod)) {
        return authenticationMethod;
      }
    }

    throw new IllegalStateException(
        "Failed to find an admissible authentication method for API: " + api);
  }

  public static ErrorResponseV1 createErrorResponseWithDeveloperMessage(String developerMessage) {
    ErrorResponseV1 errorResponse = new ErrorResponseV1();
    MultilineStringV1 message = new MultilineStringV1();
    message.setValue(developerMessage);
    errorResponse.setDeveloperMessage(message);
    return errorResponse;
  }
}
