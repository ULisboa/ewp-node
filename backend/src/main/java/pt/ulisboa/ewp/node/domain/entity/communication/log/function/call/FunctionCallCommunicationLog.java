package pt.ulisboa.ewp.node.domain.entity.communication.log.function.call;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@DiscriminatorValue(FunctionCallCommunicationLog.TYPE)
public class FunctionCallCommunicationLog extends CommunicationLog {

  public static final String TYPE = "FUNCTION_CALL";

  private String className;
  private String method;
  private Collection<FunctionCallArgumentLog> arguments = new HashSet<>();
  private String resultType;
  private String result;

  public FunctionCallCommunicationLog() {}

  public FunctionCallCommunicationLog(
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication,
      String className,
      String method,
      List<Object> arguments) {
    super(startProcessingDateTime, endProcessingDateTime, observations, parentCommunication);
    this.className = className;
    this.method = method;
    for (Object argument : arguments) {
      this.addArgument(argument);
    }
  }

  @Override
  public Status getStatus() {
    switch (super.getStatus()) {
      case SUCCESS:
        if (getResultType() == null || getResultType().isEmpty()) {
          return Status.FAILURE;
        }
        return Status.SUCCESS;

      case INCOMPLETE:
        return Status.INCOMPLETE;

      case FAILURE:
        return Status.FAILURE;

      default:
        throw new IllegalStateException("Unknown status: " + super.getStatus());
    }
  }

  @Column(name = "className")
  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  @Column(name = "method")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  @Transient
  public List<FunctionCallArgumentLog> getSortedArguments() {
    return arguments.stream()
        .sorted(Comparator.comparingInt(FunctionCallArgumentLog::getOrder))
        .collect(Collectors.toList());
  }

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "functionCallCommunicationLog",
      cascade = CascadeType.ALL)
  public Collection<FunctionCallArgumentLog> getArguments() {
    return arguments;
  }

  public void setArguments(Collection<FunctionCallArgumentLog> arguments) {
    this.arguments = arguments;
  }

  public void addArgument(Object value) {
    this.arguments.add(FunctionCallArgumentLog.create(this, value));
  }

  public void editResult(String resultType, String result) {
    setResultType(resultType);
    setResult(result);
  }

  @Column(name = "result_type", columnDefinition = "TEXT")
  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  @Column(name = "result", columnDefinition = "TEXT")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  @Transient
  public String getTarget() {
    return getClassName() + "." + getMethod();
  }
}
