package pt.ulisboa.ewp.node.domain.mapper.communication.log;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogDetailDto;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogSummaryDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Mapper
public interface CommunicationLogMapper {

  CommunicationLogMapper INSTANCE = Mappers.getMapper(CommunicationLogMapper.class);

  @Named(value = "convertToSummaryDto")
  CommunicationLogSummaryDto communicationLogToCommunicationLogSummaryDto(CommunicationLog communicationLog);

  @Mapping(source = "sortedChildrenCommunications", target = "sortedChildrenCommunications", qualifiedByName = "convertToSummaryDto")
  CommunicationLogDetailDto communicationLogToCommunicationLogDetailDto(CommunicationLog communicationLog);
}
