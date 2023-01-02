package pt.ulisboa.ewp.node.api.ewp.utils;

import static org.joox.JOOX.$;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
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
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfigurationFactory;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfigurationFactory;
import pt.ulisboa.ewp.node.utils.EwpApi;
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
      RegistryClient registryClient, String heiId, EwpApi api) {
    Collection<Element> rawApiElements = getRawApiElements(registryClient, heiId,
        api.getLocalName());
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
        Optional<EwpClientAuthenticationConfiguration> configurationOptional = EwpClientAuthenticationConfigurationFactory.getInstance()
            .create(object);
        configurationOptional.ifPresent(result::add);
      }
    }
    return result;
  }

  public static Collection<EwpServerAuthenticationConfiguration>
      getSupportedServerAuthenticationMethods(HttpSecurityOptions httpSecurityOptions) {
    Collection<EwpServerAuthenticationConfiguration> result = new HashSet<>();
    if (httpSecurityOptions != null) {
      List<Object> serverAuthMethods = httpSecurityOptions.getServerAuthMethods().getAny();
      for (Object object : serverAuthMethods) {
        Optional<EwpServerAuthenticationConfiguration> configurationOptional = EwpServerAuthenticationConfigurationFactory.getInstance()
            .create(object);
        configurationOptional.ifPresent(result::add);
      }
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
