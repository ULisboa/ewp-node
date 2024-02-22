package pt.ulisboa.ewp.node.domain.dto.validation;

import java.util.List;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationEntryDto.Severity;

public class ValidationResultDto {

  private List<ValidationEntryDto> validationEntries;

  public ValidationResultDto() {}

  public ValidationResultDto(List<ValidationEntryDto> validationEntries) {
    this.validationEntries = validationEntries;
  }

  public boolean isValid() {
    return this.validationEntries.stream().noneMatch(e -> e.getSeverity().equals(Severity.ERROR));
  }

  public List<ValidationEntryDto> getValidationEntries() {
    return validationEntries;
  }

  public void setValidationEntries(List<ValidationEntryDto> validationEntries) {
    this.validationEntries = validationEntries;
  }

}
