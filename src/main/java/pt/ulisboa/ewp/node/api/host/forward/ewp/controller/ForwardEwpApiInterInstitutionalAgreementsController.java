package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasIndexResponseV4;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpInterInstitutionalAgreementsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsController
    extends AbstractForwardEwpApiController {

  @Autowired private EwpInterInstitutionalAgreementsClient client;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE, value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV4>> findAllByHeiIdGet(
      @Valid @ParameterObject @RequestParam InterinstitutionalAgreementsIndexRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return findAllInterInstitutionalAgreementsByHeiId(
        requestDto.getHeiId(),
        requestDto.getPartnerHeiId(),
        requestDto.getReceivingAcademicYearIds(),
        requestDto.getModifiedSince());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV4>> findAllByHeiIdPost(
      @Valid InterinstitutionalAgreementsIndexRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return findAllInterInstitutionalAgreementsByHeiId(
        requestDto.getHeiId(),
        requestDto.getPartnerHeiId(),
        requestDto.getReceivingAcademicYearIds(),
        requestDto.getModifiedSince());
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE, value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasGetResponseV4>> findByHeiIdAndIiaIdsGet(
      @Valid @ParameterObject @RequestParam InterinstitutionalAgreementsGetRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return findInterInstitutionalAgreementsByHeiIdAndIiaIds(
        requestDto.getHeiId(),
        requestDto.getIiaIds(),
        requestDto.getIiaCodes(),
        requestDto.getSendPdf());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasGetResponseV4>> findByHeiIdAndIiaIdsPost(
      @Valid InterinstitutionalAgreementsGetRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return findInterInstitutionalAgreementsByHeiIdAndIiaIds(
        requestDto.getHeiId(),
        requestDto.getIiaIds(),
        requestDto.getIiaCodes(),
        requestDto.getSendPdf());
  }

  private ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV4>>
      findAllInterInstitutionalAgreementsByHeiId(
          String heiId,
          String partnerHeiId,
          List<String> receivingAcademicYearIds,
          ZonedDateTime modifiedSince)
          throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<IiasIndexResponseV4> response =
        client.findAllByHeiIds(heiId, partnerHeiId, receivingAcademicYearIds, modifiedSince);
    return createResponseEntityFromOperationResult(response);
  }

  private ResponseEntity<ForwardEwpApiResponseWithData<IiasGetResponseV4>>
      findInterInstitutionalAgreementsByHeiIdAndIiaIds(
          String heiId, List<String> iiaIds, List<String> iiaCodes, Boolean sendPdf)
          throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<IiasGetResponseV4> response;
    if (!iiaIds.isEmpty()) {
      response = client.findByHeiIdAndIiaIds(heiId, iiaIds, sendPdf);
    } else {
      response = client.findByHeiIdAndIiaCodes(heiId, iiaCodes, sendPdf);
    }
    return createResponseEntityFromOperationResult(response);
  }

  private static class InterinstitutionalAgreementsIndexRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @NotNull
    @Size(min = 1)
    private String heiId;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_PARTNER_HEI_ID)
    private String partnerHeiId;

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_RECEIVING_ACADEMIC_YEAR_ID)
    private List<String> receivingAcademicYearIds = new ArrayList<>();

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_MODIFIED_SINCE)
    private ZonedDateTime modifiedSince;

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }

    public String getPartnerHeiId() {
      return partnerHeiId;
    }

    public void setPartnerHeiId(String partnerHeiId) {
      this.partnerHeiId = partnerHeiId;
    }

    public List<String> getReceivingAcademicYearIds() {
      return receivingAcademicYearIds;
    }

    public void setReceivingAcademicYearIds(List<String> receivingAcademicYearIds) {
      this.receivingAcademicYearIds = receivingAcademicYearIds;
    }

    public ZonedDateTime getModifiedSince() {
      return modifiedSince;
    }

    public void setModifiedSince(ZonedDateTime modifiedSince) {
      this.modifiedSince = modifiedSince;
    }
  }

  private static class InterinstitutionalAgreementsGetRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @NotNull
    @Size(min = 1)
    private String heiId;

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_IIA_ID)
    private List<String> iiaIds = new ArrayList<>();

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_IIA_CODE)
    private List<String> iiaCodes = new ArrayList<>();

    private Boolean sendPdf;

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }

    public List<String> getIiaIds() {
      return iiaIds;
    }

    public void setIiaIds(List<String> iiaIds) {
      this.iiaIds = iiaIds;
    }

    public List<String> getIiaCodes() {
      return iiaCodes;
    }

    public void setIiaCodes(List<String> iiaCodes) {
      this.iiaCodes = iiaCodes;
    }

    public Boolean getSendPdf() {
      return sendPdf;
    }

    public void setSendPdf(Boolean sendPdf) {
      this.sendPdf = sendPdf;
    }
  }
}
