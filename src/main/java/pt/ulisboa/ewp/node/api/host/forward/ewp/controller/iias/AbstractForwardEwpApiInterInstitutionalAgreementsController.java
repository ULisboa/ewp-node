package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public abstract class AbstractForwardEwpApiInterInstitutionalAgreementsController
    extends AbstractForwardEwpApiController {

  public static class InterInstitutionalAgreementsIndexRequestDto {

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

  public static class InterInstitutionalAgreementsGetRequestDto {

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
