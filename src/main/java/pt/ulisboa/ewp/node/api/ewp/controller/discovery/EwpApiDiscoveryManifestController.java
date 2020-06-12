package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import eu.erasmuswithoutpaper.api.architecture.MultilineString;
import eu.erasmuswithoutpaper.api.architecture.StringWithOptionalLang;
import eu.erasmuswithoutpaper.api.discovery.Host;
import eu.erasmuswithoutpaper.api.discovery.Manifest;
import eu.erasmuswithoutpaper.api.registry.ApisImplemented;
import eu.erasmuswithoutpaper.api.registry.OtherHeiId;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Optional;
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
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "manifest")
public class EwpApiDiscoveryManifestController {

  @Value("${baseContextPath}")
  private String baseContextPath;

  @Autowired private Logger log;

  @Autowired private KeyStoreService keyStoreService;

  @Autowired private HostRepository hostRepository;

  @Autowired Collection<EwpApiManifestEntryStrategy> manifestEntries;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Discovery manifest API.",
      tags = {"ewp"})
  public ResponseEntity<Manifest> manifest(HttpServletRequest request)
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
          KeyStoreException, IOException {
    Manifest manifest = new Manifest();

    setHosts(request, manifest);

    return ResponseEntity.ok(manifest);
  }

  private void setHosts(HttpServletRequest request, Manifest manifest)
      throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
          KeyStoreException, IOException {
    DecodedCertificateAndKey decodedCertificateAndKeyFromStorage =
        keyStoreService.getDecodedCertificateAndKeyFromStorage();

    String baseUrl = getBaseUrl(request);

    hostRepository
        .findAll()
        .forEach(
            hostConfiguration ->
                hostConfiguration
                    .getCoveredHeis()
                    .forEach(
                        coveredHei -> {
                          Host host = new Host();
                          host.getAdminEmail().add(hostConfiguration.getAdminEmail());

                          MultilineString adminNotes = new MultilineString();
                          adminNotes.setValue(hostConfiguration.getAdminNotes());
                          host.setAdminNotes(adminNotes);

                          host.setApisImplemented(
                              getApisImplemented(coveredHei.getSchacCode(), baseUrl));

                          Host.InstitutionsCovered institutionsCovered =
                              new Host.InstitutionsCovered();
                          institutionsCovered.getHei().add(createHei(coveredHei));
                          host.setInstitutionsCovered(institutionsCovered);

                          host.setClientCredentialsInUse(
                              getClientCredentialsInUse(decodedCertificateAndKeyFromStorage));
                          host.setServerCredentialsInUse(
                              getServerCredentialsInUse(decodedCertificateAndKeyFromStorage));
                          manifest.getHost().add(host);
                        }));
  }

  private ApisImplemented getApisImplemented(String heiId, String baseUrl) {
    ApisImplemented apisImplemented = new ApisImplemented();
    manifestEntries.stream()
        .map(me -> me.getManifestEntry(heiId, baseUrl))
        .filter(Optional::isPresent)
        .forEach(me -> apisImplemented.getAny().add(me.get()));
    return apisImplemented;
  }

  private Host.ClientCredentialsInUse getClientCredentialsInUse(
      DecodedCertificateAndKey decodedCertificateAndKey) {
    Host.ClientCredentialsInUse clientCredentialsInUse = null;

    String certificate = decodedCertificateAndKey.getFormattedCertificate();
    if (certificate != null) {
      clientCredentialsInUse = new Host.ClientCredentialsInUse();
      clientCredentialsInUse.getCertificate().add(certificate);

      String formattedRsaPublicKey = decodedCertificateAndKey.getFormattedRsaPublicKey();
      clientCredentialsInUse.getRsaPublicKey().add(formattedRsaPublicKey);
    }

    return clientCredentialsInUse;
  }

  private Host.ServerCredentialsInUse getServerCredentialsInUse(
      DecodedCertificateAndKey decodedCertificateAndKey) {
    Host.ServerCredentialsInUse serverCredentialsInUse = null;

    String rsaPublicKey = decodedCertificateAndKey.getFormattedRsaPublicKey();
    if (rsaPublicKey != null) {
      serverCredentialsInUse = new Host.ServerCredentialsInUse();
      serverCredentialsInUse.getRsaPublicKey().add(rsaPublicKey);
    }

    return serverCredentialsInUse;
  }

  private eu.erasmuswithoutpaper.api.registry.Hei createHei(Hei heiConfiguration) {
    eu.erasmuswithoutpaper.api.registry.Hei hei = new eu.erasmuswithoutpaper.api.registry.Hei();
    hei.setId(heiConfiguration.getSchacCode());

    heiConfiguration
        .getOtherHeiIds()
        .forEach(
            otherHeiIdConfiguration -> {
              OtherHeiId otherHeiId = new OtherHeiId();
              otherHeiId.setType(otherHeiIdConfiguration.getType());
              otherHeiId.setValue(otherHeiIdConfiguration.getValue());
              hei.getOtherId().add(otherHeiId);
            });

    heiConfiguration
        .getName()
        .forEach(
            (locale, name) -> {
              StringWithOptionalLang stringWithOptionalLang = new StringWithOptionalLang();
              stringWithOptionalLang.setLang(locale.toLanguageTag());
              stringWithOptionalLang.setValue(name);
              hei.getName().add(stringWithOptionalLang);
            });

    return hei;
  }

  private String getBaseUrl(HttpServletRequest request) {
    try {
      URL requestUrl = new URL(request.getRequestURL().toString());
      return new URL(
              "https://"
                  + requestUrl.getHost()
                  + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort())
                  + baseContextPath)
          .toString();
    } catch (MalformedURLException e) {
      log.error("Failed to get base URL", e);
    }
    return null;
  }
}
