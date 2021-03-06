package pt.ulisboa.ewp.node.api.ewp.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpApiAuthenticateMethodResponse implements Serializable {

  private final EwpAuthenticationMethod method;
  private final boolean isUsingMethod;
  private final boolean isRequiredMethodInfoFulfilled;
  private final String errorMessage;
  private final HttpStatus status;
  private final Collection<String> heiIdsCoveredByClient;

  private EwpApiAuthenticateMethodResponse(
      EwpAuthenticationMethod method,
      boolean isUsingMethod,
      boolean isRequiredMethodInfoFulfilled,
      String errorMessage,
      HttpStatus status,
      Collection<String> heiIdsCoveredByClient) {
    this.method = method;
    this.isUsingMethod = isUsingMethod;
    this.isRequiredMethodInfoFulfilled = isRequiredMethodInfoFulfilled;
    this.errorMessage = errorMessage;
    this.status = status;
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }

  public EwpAuthenticationMethod getMethod() {
    return method;
  }

  public boolean isUsingMethod() {
    return isUsingMethod;
  }

  public boolean isRequiredMethodInfoFulfilled() {
    return isRequiredMethodInfoFulfilled;
  }

  public boolean hasErrorMessage() {
    return errorMessage != null;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean isOk() {
    return isUsingMethod && status == HttpStatus.OK;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EwpApiAuthenticateMethodResponse that = (EwpApiAuthenticateMethodResponse) o;
    return isUsingMethod == that.isUsingMethod
        && isRequiredMethodInfoFulfilled == that.isRequiredMethodInfoFulfilled
        && method == that.method && Objects.equals(errorMessage, that.errorMessage)
        && status == that.status && Objects
        .equals(heiIdsCoveredByClient, that.heiIdsCoveredByClient);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, isUsingMethod, isRequiredMethodInfoFulfilled, errorMessage, status,
        heiIdsCoveredByClient);
  }

  @Override
  public String toString() {
    return "EwpApiAuthenticateMethodResponse{" +
        "method=" + method +
        ", isUsingMethod=" + isUsingMethod +
        ", isRequiredMethodInfoFulfilled=" + isRequiredMethodInfoFulfilled +
        ", errorMessage='" + errorMessage + '\'' +
        ", status=" + status +
        ", heiIdsCoveredByClient=" + heiIdsCoveredByClient +
        '}';
  }

  public static AuthenticateMethodResponseBuilder failureBuilder(
      EwpAuthenticationMethod method, String errorMessage) {
    return new AuthenticateMethodResponseBuilder(method).withErrorMessage(errorMessage);
  }

  public static AuthenticateMethodResponseBuilder successBuilder(
      EwpAuthenticationMethod method, Collection<String> heiIdsCoveredByClient) {
    AuthenticateMethodResponseBuilder builder = new AuthenticateMethodResponseBuilder(method);
    builder.withHeiIdsCoveredByClient(heiIdsCoveredByClient);
    return builder;
  }

  public static class AuthenticateMethodResponseBuilder {
    private final EwpAuthenticationMethod method;
    private boolean isUsingMethod = true;
    private boolean isRequiredMethodInfoFulfilled = true;
    private String errorMessage;
    private HttpStatus status = HttpStatus.OK;
    private Collection<String> heiIdsCoveredByClient = Collections.emptyList();

    private AuthenticateMethodResponseBuilder(EwpAuthenticationMethod method) {
      this.method = method;
    }

    public AuthenticateMethodResponseBuilder notUsingMethod() {
      this.isUsingMethod = false;
      this.isRequiredMethodInfoFulfilled = false;
      return this;
    }

    public AuthenticateMethodResponseBuilder withRequiredMethodInfoFulfilled(
        boolean methodIsValid) {
      this.isRequiredMethodInfoFulfilled = methodIsValid;
      return this;
    }

    public AuthenticateMethodResponseBuilder withErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public AuthenticateMethodResponseBuilder withResponseCode(HttpStatus status) {
      this.status = status;
      return this;
    }

    public AuthenticateMethodResponseBuilder withHeiIdsCoveredByClient(
        Collection<String> heiIdsCoveredByClient) {
      this.heiIdsCoveredByClient = heiIdsCoveredByClient;
      return this;
    }

    public EwpApiAuthenticateMethodResponse build() {
      return new EwpApiAuthenticateMethodResponse(
          method,
          isUsingMethod,
          isRequiredMethodInfoFulfilled,
          errorMessage,
          status,
          heiIdsCoveredByClient);
    }
  }
}
