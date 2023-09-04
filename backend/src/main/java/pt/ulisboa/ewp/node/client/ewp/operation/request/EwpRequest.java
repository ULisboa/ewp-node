package pt.ulisboa.ewp.node.client.ewp.operation.request;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContext;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.utils.http.ExtendedHttpHeaders;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.http.HttpUtils;

public class EwpRequest implements Serializable {

  private String id = UUID.randomUUID().toString();
  private HttpMethod method;
  private String urlWithoutQueryParams;
  private ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
  private HttpParams queryParams = new HttpParams();
  private EwpRequestBody body = new EwpRequestFormDataUrlEncodedBody(new HttpParams());
  private EwpAuthenticationMethod authenticationMethod = EwpAuthenticationMethod.HTTP_SIGNATURE;
  private Long parentCommunicationId;
  private final EwpApiInformation apiInformation;

  public EwpRequest(
      HttpMethod method, @NotNull String urlWithoutQueryParams, EwpApiInformation apiInformation) {
    this.method = method;
    this.urlWithoutQueryParams = urlWithoutQueryParams;
    this.apiInformation = apiInformation;

    CommunicationContext context = CommunicationContextHolder.getContext();
    if (context.getCurrentCommunicationLog() != null) {
      this.parentCommunicationId = context.getCurrentCommunicationLog().getId();
    }
  }

  public String getId() {
    return id;
  }

  public static EwpRequest create(
      EwpApiConfiguration api,
      HttpMethod method,
      @NotNull String urlWithoutQueryParams,
      HttpParams queryParams,
      EwpRequestBody body) {
    EwpRequest request =
        new EwpRequest(
            method, urlWithoutQueryParams, new EwpApiInformation(api.getHeiId(), api.getApiName(),
            api.getVersion()));
    request.authenticationMethod(api.getBestSupportedAuthenticationMethod());
    request.queryParams(queryParams);
    request.body(body);
    return request;
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
    if (queryParams != null) {
      this.queryParams = queryParams;
    }
    return this;
  }

  public EwpRequestBody getBody() {
    return body;
  }

  public EwpRequest body(EwpRequestBody body) {
    if (body != null) {
      this.body = body;
    }
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

  public Long getParentCommunicationId() {
    return parentCommunicationId;
  }

  public EwpApiInformation getApiInformation() {
    return apiInformation;
  }

  public static EwpRequest createGet(
      EwpApiConfiguration api, @NotNull String urlWithoutQueryParams, HttpParams queryParams) {
    return create(api, HttpMethod.GET, urlWithoutQueryParams, queryParams, null);
  }

  public static EwpRequest createPost(
      EwpApiConfiguration api, @NotNull String urlWithoutQueryParams, EwpRequestBody body) {
    return create(api, HttpMethod.POST, urlWithoutQueryParams, null, body);
  }

  public static class EwpApiInformation {

    private final String heiId;
    private final String apiName;
    private final String version;

    public EwpApiInformation(String heiId, String apiName, String version) {
      this.heiId = heiId;
      this.apiName = apiName;
      this.version = version;
    }

    public String getHeiId() {
      return heiId;
    }

    public String getApiName() {
      return apiName;
    }

    public String getVersion() {
      return version;
    }
  }
}
