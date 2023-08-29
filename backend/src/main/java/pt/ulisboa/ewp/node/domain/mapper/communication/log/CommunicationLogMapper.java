package pt.ulisboa.ewp.node.domain.mapper.communication.log;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.domain.dto.communication.log.CommunicationLogSummaryDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Mapper
public interface CommunicationLogMapper {

  CommunicationLogMapper INSTANCE = Mappers.getMapper(CommunicationLogMapper.class);

  CommunicationLogSummaryDto communicationLogToCommunicationLogSummaryDto(CommunicationLog communicationLog);
}
