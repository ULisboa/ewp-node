package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import eu.erasmuswithoutpaper.api.architecture.MultilineString;
import eu.erasmuswithoutpaper.api.architecture.StringWithOptionalLang;
import eu.erasmuswithoutpaper.api.discovery.Host;
import eu.erasmuswithoutpaper.api.discovery.Manifest;
import eu.erasmuswithoutpaper.api.registry.ApisImplemented;
import eu.erasmuswithoutpaper.api.registry.OtherHeiId;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.EWP_API_BASE_URI + "manifest")
public class EwpApiDiscoveryManifestController {

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

    hostRepository
        .findAll()
        .forEach(
            hostConfiguration -> {
              Host host = new Host();
              host.getAdminEmail().add(hostConfiguration.getAdminEmail());

              MultilineString adminNotes = new MultilineString();
              adminNotes.setValue(hostConfiguration.getAdminNotes());
              host.setAdminNotes(adminNotes);

              host.setApisImplemented(getApisImplemented(request));
              host.setInstitutionsCovered(
                  getInstitutionsCovered(hostConfiguration.getCoveredHeis()));
              host.setClientCredentialsInUse(
                  getClientCredentialsInUse(decodedCertificateAndKeyFromStorage));
              host.setServerCredentialsInUse(
                  getServerCredentialsInUse(decodedCertificateAndKeyFromStorage));
              manifest.getHost().add(host);
            });
  }

  private Host.InstitutionsCovered getInstitutionsCovered(Collection<Hei> coveredHeis) {
    Host.InstitutionsCovered institutionsCovered = new Host.InstitutionsCovered();
    coveredHeis.forEach(coveredHei -> institutionsCovered.getHei().add(createHei(coveredHei)));
    return institutionsCovered;
  }

  private ApisImplemented getApisImplemented(HttpServletRequest request) {
    ApisImplemented apisImplemented = new ApisImplemented();
    manifestEntries.forEach(me -> apisImplemented.getAny().add(me.getManifestEntry(request)));
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
}
