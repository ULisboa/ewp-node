package pt.ulisboa.ewp.node.domain.mapper.communication.log;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogSummaryDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.function.call.FunctionCallCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.host.plugin.HostPluginFunctionCallCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;

@Mapper
public interface CommunicationLogMapper {

  CommunicationLogMapper INSTANCE = Mappers.getMapper(CommunicationLogMapper.class);

  @Named(value = "convertToSummaryDto")
  CommunicationLogSummaryDto communicationLogToCommunicationLogSummaryDto(
      CommunicationLog communicationLog);

  @SubclassMappings({
    @SubclassMapping(
        source = HostPluginFunctionCallCommunicationLog.class,
        target = HostPluginFunctionCallCommunicationLogDetailDto.class),
    @SubclassMapping(
        source = FunctionCallCommunicationLog.class,
        target = FunctionCallCommunicationLogDetailDto.class),
    @SubclassMapping(
        source = HttpCommunicationLog.class,
        target = HttpCommunicationLogDetailDto.class),
  })
  @Mapping(
      source = "sortedChildrenCommunications",
      target = "sortedChildrenCommunications",
      qualifiedByName = "convertToSummaryDto")
  CommunicationLogDetailDto communicationLogToCommunicationLogDetailDto(
      CommunicationLog communicationLog);
}
