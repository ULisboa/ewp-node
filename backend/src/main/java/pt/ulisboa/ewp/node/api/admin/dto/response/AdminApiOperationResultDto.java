package pt.ulisboa.ewp.node.api.admin.dto.response;

public class AdminApiOperationResultDto {

  private boolean success;

  public AdminApiOperationResultDto(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}
