package pt.ulisboa.ewp.node.client.ewp.operation.request;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

public class EwpRequest implements Serializable {

  private String id = UUID.randomUUID().toString();
  private HttpMethod method;
  private String urlWithoutQueryParams;
  private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
  private HttpParams queryParams = new HttpParams();
  private HttpParams bodyParams = new HttpParams();
  private EwpAuthenticationMethod authenticationMethod = EwpAuthenticationMethod.TLS;

  public EwpRequest(HttpMethod method, @NotNull String urlWithoutQueryParams) {
    this.method = method;
    this.urlWithoutQueryParams = urlWithoutQueryParams;
  }

  public String getId() {
    return id;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public EwpRequest method(HttpMethod method) {
    this.method = method;
    return this;
  }

  public String getUrlWithoutQueryParams() {
    return urlWithoutQueryParams;
  }

  public EwpRequest urlWithoutQueryParams(String urlWithoutQueryParams) {
    this.urlWithoutQueryParams = urlWithoutQueryParams;
    return this;
  }

  public ExtendedHttpHeaders getHeaders() {
    return headers;
  }

  public EwpRequest headers(ExtendedHttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  public EwpRequest header(String key, List<String> values) {
    this.headers.put(key, values);
    return this;
  }

  public HttpParams getQueryParams() {
    return queryParams;
  }

  public EwpRequest queryParams(HttpParams queryParams) {
    this.queryParams = queryParams;
    return this;
  }

  public HttpParams getBodyParams() {
    return bodyParams;
  }

  public EwpRequest bodyParams(HttpParams bodyParams) {
    this.bodyParams = bodyParams;
    return this;
  }

  public EwpAuthenticationMethod getAuthenticationMethod() {
    return authenticationMethod;
  }

  public EwpRequest authenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    this.authenticationMethod = authenticationMethod;
    return this;
  }

  public String getUrl() {
    StringBuilder url = new StringBuilder(urlWithoutQueryParams);
    String queryString = HttpUtils.serializeQueryString(queryParams.asMap());
    if (!Strings.isNullOrEmpty(queryString)) {
      url.append('?').append(queryString);
    }
    return url.toString();
  }
}
