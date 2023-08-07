package pt.ulisboa.ewp.node.domain.entity.communication.log.function.call;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pt.ulisboa.ewp.node.utils.serialization.SerializationUtils;
import pt.ulisboa.ewp.node.utils.serialization.TypeAndString;

@Entity
@Table(name = "FUNCTION_CALL_ARGUMENT")
public class FunctionCallArgumentLog {

  private long id;
  private FunctionCallCommunicationLog functionCallCommunicationLog;
  private int order;
  private String type;
  private String value;

  protected FunctionCallArgumentLog() {}

  protected FunctionCallArgumentLog(
      FunctionCallCommunicationLog functionCallCommunicationLog,
      int order,
      String type,
      String value) {
    this.functionCallCommunicationLog = functionCallCommunicationLog;
    this.order = order;
    this.type = type;
    this.value = value;
  }

  public static FunctionCallArgumentLog create(
      FunctionCallCommunicationLog functionCallCommunicationLog, Object value) {
    Objects.requireNonNull(functionCallCommunicationLog);
    int order = functionCallCommunicationLog.getArguments().size() + 1;
    TypeAndString valueAsTypeAndString = SerializationUtils.convertToTypeAndString(value);
    return new FunctionCallArgumentLog(
        functionCallCommunicationLog,
        order,
        valueAsTypeAndString.getType(),
        valueAsTypeAndString.getString());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "function_call_communication_log_id")
  public FunctionCallCommunicationLog getFunctionCallCommunicationLog() {
    return functionCallCommunicationLog;
  }

  public void setFunctionCallCommunicationLog(
      FunctionCallCommunicationLog functionCallCommunicationLog) {
    this.functionCallCommunicationLog = functionCallCommunicationLog;
  }

  @Column(name = "order_number", nullable = false)
  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Column(name = "type", nullable = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "arg_value", nullable = false, columnDefinition = "TEXT")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
