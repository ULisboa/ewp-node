package pt.ulisboa.ewp.node.exception.ewp;

import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;

public class EwpResponseBodyCannotBeCastToException extends Exception {

  private EwpResponse response;
  private Class<?> classType;

  public EwpResponseBodyCannotBeCastToException(EwpResponse response, Class classType) {
    this.response = response;
    this.classType = classType;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public Class<?> getClassType() {
    return classType;
  }

  @Override
  public String getMessage() {
    return "EWP response's body cannot be cast to " + classType.getCanonicalName();
  }
}
