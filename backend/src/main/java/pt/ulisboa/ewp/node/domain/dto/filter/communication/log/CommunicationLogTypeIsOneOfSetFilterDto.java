package pt.ulisboa.ewp.node.domain.dto.filter.communication.log;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationToHostLog;

public class CommunicationLogTypeIsOneOfSetFilterDto
    extends FilterDto<CommunicationLog> {

  private static final Map<String, Class<?>> TYPE_TO_CLASS_MAP = new HashMap<>();

  static {
    TYPE_TO_CLASS_MAP.put(FunctionCallCommunicationLog.TYPE, FunctionCallCommunicationLog.class);
    TYPE_TO_CLASS_MAP.put(HostPluginFunctionCallCommunicationLog.TYPE, HostPluginFunctionCallCommunicationLog.class);
    TYPE_TO_CLASS_MAP.put(HttpCommunicationFromEwpNodeLog.TYPE, HttpCommunicationFromEwpNodeLog.class);
    TYPE_TO_CLASS_MAP.put(HttpCommunicationToEwpNodeLog.TYPE, HttpCommunicationToEwpNodeLog.class);
    TYPE_TO_CLASS_MAP.put(HttpCommunicationFromHostLog.TYPE, HttpCommunicationFromHostLog.class);
    TYPE_TO_CLASS_MAP.put(HttpCommunicationToHostLog.TYPE, HttpCommunicationToHostLog.class);
  }

  private Collection<String> values;

  public CommunicationLogTypeIsOneOfSetFilterDto() {}

  public CommunicationLogTypeIsOneOfSetFilterDto(Collection<String> values) {
    this.values = values;
  }

  public Collection<String> getValues() {
    return values;
  }

  public void setValues(Collection<String> values) {
    this.values = values;
  }

  @Override
  public Predicate createPredicate(
      CriteriaBuilder criteriaBuilder, Root<CommunicationLog> selection) {
    return criteriaBuilder.or(
        values.stream()
            .map(v -> {
              Class<?> javaType = TYPE_TO_CLASS_MAP.get(v);
              if (!TYPE_TO_CLASS_MAP.containsKey(v)) {
                throw new IllegalArgumentException("Invalid type: " + values);
              }
              return criteriaBuilder.equal(selection.type(), criteriaBuilder.literal(javaType));
            })
            .toArray(Predicate[]::new));
  }
}
