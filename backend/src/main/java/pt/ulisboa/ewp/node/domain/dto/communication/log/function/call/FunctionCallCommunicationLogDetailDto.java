package pt.ulisboa.ewp.node.domain.dto.communication.log.function.call;

import java.util.Collection;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogDetailDto;

public class FunctionCallCommunicationLogDetailDto extends CommunicationLogDetailDto {

  private String className;
  private String method;
  private Collection<FunctionCallArgumentLogDto> sortedArguments;
  private String resultType;
  private String result;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Collection<FunctionCallArgumentLogDto> getSortedArguments() {
    return sortedArguments;
  }

  public void setSortedArguments(Collection<FunctionCallArgumentLogDto> sortedArguments) {
    this.sortedArguments = sortedArguments;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
