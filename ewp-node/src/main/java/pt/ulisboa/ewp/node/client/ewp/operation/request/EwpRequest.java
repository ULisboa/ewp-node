package pt.ulisboa.ewp.node.client.ewp.operation.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpMethod;
import pt.ulisboa.ewp.node.utils.http.HttpHeadersMap;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

import com.google.common.base.Strings;

public class EwpRequest {

  private String id = UUID.randomUUID().toString();
  private HttpMethod method;
  private String urlWithoutQueryParams;
  private HttpHeadersMap headers = new HttpHeadersMap();
  private Map<String, List<String>> queryParams = new HashMap<>();
  private Map<String, List<String>> bodyParams = new HashMap<>();
  private EwpAuthenticationMethod authenticationMethod = EwpAuthenticationMethod.TLS;

  public EwpRequest(HttpMethod method, String urlWithoutQueryParams) {
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

  public HttpHeadersMap getHeaders() {
    return headers;
  }

  public EwpRequest headers(HttpHeadersMap headers) {
    this.headers = headers;
    return this;
  }

  public EwpRequest header(String name, String value) {
    this.headers.put(name, value);
    return this;
  }

  public Map<String, List<String>> getQueryParams() {
    return queryParams;
  }

  public EwpRequest queryParams(Map<String, List<String>> queryParams) {
    this.queryParams = queryParams;
    return this;
  }

  public Map<String, List<String>> getBodyParams() {
    return bodyParams;
  }

  public EwpRequest bodyParams(Map<String, List<String>> bodyParams) {
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
    String queryString = HttpUtils.serializeQueryString(queryParams);
    if (!Strings.isNullOrEmpty(queryString)) {
      url.append('?').append(queryString);
    }
    return url.toString();
  }
}
