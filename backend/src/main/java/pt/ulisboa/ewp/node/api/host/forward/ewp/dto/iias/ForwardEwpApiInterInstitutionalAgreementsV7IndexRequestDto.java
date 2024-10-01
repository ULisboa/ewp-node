package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiInterInstitutionalAgreementsV7IndexRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @NotNull
  @Size(min = 1)
  private String heiId;

  @ParamName(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID)
  private List<String> receivingAcademicYearIds = new ArrayList<>();

  @ParamName(value = EwpApiParamConstants.MODIFIED_SINCE)
  private ZonedDateTime modifiedSince;

  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
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
