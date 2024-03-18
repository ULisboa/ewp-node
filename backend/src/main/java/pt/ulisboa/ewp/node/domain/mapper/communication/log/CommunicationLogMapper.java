package pt.ulisboa.ewp.node.domain.mapper.communication.log;

import java.nio.charset.StandardCharsets;
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
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp.EwpHttpCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp.HttpCommunicationFromEwpNodeLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp.HttpCommunicationToEwpNodeLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.host.HostHttpCommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.EwpHttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HostHttpCommunicationLog;

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
        source = HttpCommunicationFromEwpNodeLog.class,
        target = HttpCommunicationFromEwpNodeLogDetailDto.class),
      @SubclassMapping(
          source = HttpCommunicationToEwpNodeLog.class,
          target = HttpCommunicationToEwpNodeLogDetailDto.class),
    @SubclassMapping(
        source = EwpHttpCommunicationLog.class,
        target = EwpHttpCommunicationLogDetailDto.class),
    @SubclassMapping(
        source = HostHttpCommunicationLog.class,
        target = HostHttpCommunicationLogDetailDto.class),
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

  @Mapping(source = "host.code", target = "hostCode")
  HostHttpCommunicationLogDetailDto communicationLogToCommunicationLogSummaryDto(
      HostHttpCommunicationLog communicationLog);

  default String mapByteArrayToString(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
