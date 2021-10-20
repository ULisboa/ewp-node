package pt.ulisboa.ewp.node.client.ewp.operation.request.body;

import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

public class EwpRequestFormDataBody extends EwpRequestBody {

  private final HttpParams formData;

  public EwpRequestFormDataBody(HttpParams formData) {
    this.formData = formData;
  }

  public HttpParams getFormData() {
    return formData;
  }

  @Override
  public String serialize() {
    return HttpUtils.serializeFormData(formData.asMap());
  }
}
