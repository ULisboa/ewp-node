package pt.ulisboa.ewp.node.api.common.dto;

import java.util.Objects;

public class ApiOperationStatusDTO {

  private boolean success;

  public ApiOperationStatusDTO() {
    this(true);
  }

  public ApiOperationStatusDTO(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ApiOperationStatusDTO that = (ApiOperationStatusDTO) o;
    return success == that.success;
  }

  @Override
  public int hashCode() {
    return Objects.hash(success);
  }

  @Override
  public String toString() {
    return "ApiOperationStatusDTO(" + success + ")";
  }
}
