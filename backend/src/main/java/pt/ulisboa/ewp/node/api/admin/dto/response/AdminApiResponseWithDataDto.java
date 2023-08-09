package pt.ulisboa.ewp.node.api.admin.dto.response;


import java.util.List;

public class AdminApiResponseWithDataDto<T> extends AdminApiResponseDto {

  private T data;

  public AdminApiResponseWithDataDto() {
  }

  public AdminApiResponseWithDataDto(List<Message> messages, T data) {
    super(messages);
    this.data = data;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
