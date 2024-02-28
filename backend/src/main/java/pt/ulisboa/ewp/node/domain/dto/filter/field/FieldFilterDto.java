package pt.ulisboa.ewp.node.domain.dto.filter.field;

import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;

public abstract class FieldFilterDto<T> extends FilterDto<T> {

  private String field;

  public FieldFilterDto() {}

  public FieldFilterDto(String field) {
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }
}
