package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import eu.erasmuswithoutpaper.api.architecture.v1.StringWithOptionalLangV1;
import eu.erasmuswithoutpaper.api.discovery.v5.HostV5;
import eu.erasmuswithoutpaper.api.discovery.v5.ManifestV5;
import eu.erasmuswithoutpaper.api.registry.v1.ApisImplementedV1;
import eu.erasmuswithoutpaper.api.registry.v1.OtherHeiIdV1;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

@RestController
@EwpApi
@RequestMapping(
    value = {EwpApiConstants.API_BASE_URI + "manifest", EwpApiConstants.REST_BASE_URI + "manifest"})
public class EwpApiDiscoveryManifestController {

  @Value("${baseContextPath}")
  private String baseContextPath;

  @Autowired
  private Logger log;

  @Autowired
  private KeyStoreService keyStoreService;

  @Autowired
  private HostRepository hostRepository;

  @Autowired
  Collection<EwpManifestEntryProvider> manifestEntryProviders;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Discovery manifest API.",
      tags = {"ewp"})
  public ResponseEntity<ManifestV5> manifest(HttpServletRequest request)
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
      KeyStoreException, IOException {
    ManifestV5 manifest = new ManifestV5();

    setHosts(request, manifest);

    return ResponseEntity.ok(manifest);
  }

  private void setHosts(HttpServletRequest request, ManifestV5 manifest)
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
      KeyStoreException, IOException {
    DecodedCertificateAndKey decodedCertificateAndKeyFromStorage =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();

    hostRepository
        .findAll()
        .forEach(
            hostConfiguration ->
                hostConfiguration
                    .getCoveredHeis()
                    .forEach(
                        coveredHei -> {
                          HostV5 host = new HostV5();
                          host.getAdminEmail().add(hostConfiguration.getAdminEmail());

                          MultilineStringV1 adminNotes = new MultilineStringV1();
                          adminNotes.setValue(hostConfiguration.getAdminNotes());
                          host.setAdminNotes(adminNotes);

                          host.setApisImplemented(
                              getApisImplemented(request, coveredHei.getSchacCode()));

                          HostV5.InstitutionsCovered institutionsCovered =
                              new HostV5.InstitutionsCovered();
                          institutionsCovered.getHei().add(createHei(coveredHei));
                          host.setInstitutionsCovered(institutionsCovered);

                          host.setClientCredentialsInUse(
                              getClientCredentialsInUse(decodedCertificateAndKeyFromStorage));
                          host.setServerCredentialsInUse(
                              getServerCredentialsInUse(decodedCertificateAndKeyFromStorage));
                          manifest.getHost().add(host);
                        }));
  }

  private ApisImplementedV1 getApisImplemented(HttpServletRequest originalRequest, String heiId) {
    ApisImplementedV1 apisImplemented = new ApisImplementedV1();
    for (EwpManifestEntryProvider manifestEntryProvider : manifestEntryProviders) {
      String baseUrl = getBaseUrl(originalRequest,
          manifestEntryProvider instanceof EwpApiDiscoveryManifestEntryProvider);
      apisImplemented.getAny().addAll(manifestEntryProvider.getManifestEntries(heiId, baseUrl));
    }
    return apisImplemented;
  }

  private HostV5.ClientCredentialsInUse getClientCredentialsInUse(
      DecodedCertificateAndKey decodedCertificateAndKey) {
    HostV5.ClientCredentialsInUse clientCredentialsInUse = null;

    String certificate = decodedCertificateAndKey.getFormattedCertificate();
    if (certificate != null) {
      clientCredentialsInUse = new HostV5.ClientCredentialsInUse();
      clientCredentialsInUse.getCertificate().add(certificate);

      String formattedRsaPublicKey = decodedCertificateAndKey.getFormattedRsaPublicKey();
      clientCredentialsInUse.getRsaPublicKey().add(formattedRsaPublicKey);
    }

    return clientCredentialsInUse;
  }

  private HostV5.ServerCredentialsInUse getServerCredentialsInUse(
      DecodedCertificateAndKey decodedCertificateAndKey) {
    HostV5.ServerCredentialsInUse serverCredentialsInUse = null;

    String rsaPublicKey = decodedCertificateAndKey.getFormattedRsaPublicKey();
    if (rsaPublicKey != null) {
      serverCredentialsInUse = new HostV5.ServerCredentialsInUse();
      serverCredentialsInUse.getRsaPublicKey().add(rsaPublicKey);
    }

    return serverCredentialsInUse;
  }

  private eu.erasmuswithoutpaper.api.registry.v1.HeiV1 createHei(Hei heiConfiguration) {
    eu.erasmuswithoutpaper.api.registry.v1.HeiV1 hei =
        new eu.erasmuswithoutpaper.api.registry.v1.HeiV1();
    hei.setId(heiConfiguration.getSchacCode());

    heiConfiguration
        .getOtherHeiIds()
        .forEach(
            otherHeiIdConfiguration -> {
              OtherHeiIdV1 otherHeiId = new OtherHeiIdV1();
              otherHeiId.setType(otherHeiIdConfiguration.getType());
              otherHeiId.setValue(otherHeiIdConfiguration.getValue());
              hei.getOtherId().add(otherHeiId);
            });

    heiConfiguration
        .getName()
        .forEach(
            (locale, name) -> {
              StringWithOptionalLangV1 stringWithOptionalLang = new StringWithOptionalLangV1();
              stringWithOptionalLang.setLang(locale.toLanguageTag());
              stringWithOptionalLang.setValue(name);
              hei.getName().add(stringWithOptionalLang);
            });

    return hei;
  }

  private String getBaseUrl(HttpServletRequest request, boolean supportRestBaseUri) {
    try {
      URL requestUrl = new URL(request.getRequestURL().toString());
      String completeBaseUri = baseContextPath + getBaseApiUri(request, supportRestBaseUri);
      return new URL(
          "https://"
              + requestUrl.getHost()
              + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort())
              + completeBaseUri)
          .toString();
    } catch (MalformedURLException e) {
      log.error("Failed to get base URL", e);
    }
    return null;
  }

  private String getBaseApiUri(HttpServletRequest request, boolean supportRestBaseUri) {
    String requestURI = request.getRequestURI();
    if (supportRestBaseUri && requestURI.startsWith(EwpApiConstants.REST_BASE_URI)) {
      // NOTE: Backwards compatibility in case EWP registry has already
      // the manifest registered with base URI EwpApiConstants.REST_BASE_URI
      return EwpApiConstants.REST_BASE_URI;
    } else {
      return EwpApiConstants.API_BASE_URI;
    }
  }
}
