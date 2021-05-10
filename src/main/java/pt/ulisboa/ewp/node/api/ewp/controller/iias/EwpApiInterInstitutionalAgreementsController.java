package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasIndexResponseV4;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.InterInstitutionalAgreementsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "iias")
public class EwpApiInterInstitutionalAgreementsController {

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsController(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(value = "/index", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Index API.",
      tags = {"ewp"})
  public ResponseEntity<IiasIndexResponseV4> iiaIdsGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.PARTNER_HEI_ID, defaultValue = "") String partnerHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, required = false) Collection<String> receivingAcademicYearIds,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince) {

    Collection<String> iiaIds = getHostProvider(heiId)
        .findAllIiaIdsByHeiId(heiId, partnerHeiId, receivingAcademicYearIds, modifiedSince);
    IiasIndexResponseV4 response = new IiasIndexResponseV4();
    response.getIiaId().addAll(iiaIds);
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/index", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Index API.",
      tags = {"ewp"})
  public ResponseEntity<IiasIndexResponseV4> iiaIdsPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.PARTNER_HEI_ID, defaultValue = "") String partnerHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, required = false) Collection<String> receivingAcademicYearIds,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince) {

    Collection<String> iiaIds = getHostProvider(heiId)
        .findAllIiaIdsByHeiId(heiId, partnerHeiId, receivingAcademicYearIds, modifiedSince);
    IiasIndexResponseV4 response = new IiasIndexResponseV4();
    response.getIiaId().addAll(iiaIds);
    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/get", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Get API.",
      tags = {"ewp"})
  public ResponseEntity<IiasGetResponseV4> iiasGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID, required = false)
          List<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.IIA_CODE, required = false)
          List<String> iiaCodes,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {
    return iias(heiId, iiaIds, iiaCodes, sendPdf);
  }

  @PostMapping(value = "/get", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Get API.",
      tags = {"ewp"})
  public ResponseEntity<IiasGetResponseV4> iiasPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID, required = false)
          List<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.IIA_CODE, required = false)
          List<String> iiaCodes,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {
    return iias(heiId, iiaIds, iiaCodes, sendPdf);
  }

  private ResponseEntity<IiasGetResponseV4> iias(String heiId, List<String> iiaIds,
      List<String> iiaCodes, Boolean sendPdf) {
    iiaIds = iiaIds != null ? iiaIds : Collections.emptyList();
    iiaCodes = iiaCodes != null ? iiaCodes : Collections.emptyList();

    Optional<InterInstitutionalAgreementsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, InterInstitutionalAgreementsHostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    InterInstitutionalAgreementsHostProvider provider = providerOptional.get();

    if (!iiaIds.isEmpty() && !iiaCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only IIA IDs or codes are accepted, not both simultaneously");
    }

    if (iiaIds.isEmpty() && iiaCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some IIA ID or code must be provided");
    }

    if (!iiaIds.isEmpty()) {
      return iiasByIds(provider, heiId, iiaIds, sendPdf);
    } else {
      return iiasByCodes(provider, heiId, iiaCodes, sendPdf);
    }
  }

  private ResponseEntity<IiasGetResponseV4> iiasByIds(
      InterInstitutionalAgreementsHostProvider provider, String heiId, List<String> iiaIds,
      Boolean sendPdf) {
    if (iiaIds.size() > provider.getMaxIiaIdsPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid IIA IDs per request is "
              + provider.getMaxIiaIdsPerRequest());
    }

    IiasGetResponseV4 response = new IiasGetResponseV4();
    response.getIia()
        .addAll(provider.findByHeiIdAndIiaIds(heiId, iiaIds, sendPdf));
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<IiasGetResponseV4> iiasByCodes(
      InterInstitutionalAgreementsHostProvider provider, String heiId, List<String> iiaCodes,
      Boolean sendPdf) {
    if (iiaCodes.size() > provider.getMaxIiaCodesPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid IIA IDs per request is "
              + provider.getMaxIiaIdsPerRequest());
    }

    IiasGetResponseV4 response = new IiasGetResponseV4();
    response.getIia()
        .addAll(provider.findByHeiIdAndIiaIds(heiId, iiaCodes, sendPdf));
    return ResponseEntity.ok(response);
  }

  private InterInstitutionalAgreementsHostProvider getHostProvider(String heiId) {
    Optional<InterInstitutionalAgreementsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, InterInstitutionalAgreementsHostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    return providerOptional.get();
  }
}
