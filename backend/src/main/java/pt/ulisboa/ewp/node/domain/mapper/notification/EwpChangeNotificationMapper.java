package pt.ulisboa.ewp.node.domain.mapper.notification;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto.ExtraVariableEntryDto;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.ExtraVariableEntry;
import pt.ulisboa.ewp.node.domain.mapper.communication.log.CommunicationLogMapper;

@Mapper(uses = CommunicationLogMapper.class)
public interface EwpChangeNotificationMapper {

  EwpChangeNotificationMapper INSTANCE = Mappers.getMapper(EwpChangeNotificationMapper.class);

  @Mapping(target = "extraVariables", expression = "java(mapExtraVariables(ewpChangeNotification))")
  @Mapping(
      source = "sortedCommunications",
      target = "sortedCommunicationLogs",
      qualifiedByName = "convertToSummaryDto")
  @Mapping(source = "mergedIntoChangeNotification", target = "mergedInto")
  @Mapping(source = "scheduledDateTime", target = "nextAttemptDateTime")
  EwpChangeNotificationDto mapEwpChangeNotificationToEwpChangeNotificationDto(
      EwpChangeNotification ewpChangeNotification);

  ExtraVariableEntryDto mapExtraVariableEntryToExtraVariableDto(
      ExtraVariableEntry extraVariableEntry);

  default List<ExtraVariableEntryDto> mapExtraVariables(
      EwpChangeNotification ewpChangeNotification) {
    return ewpChangeNotification.getExtraVariables().stream()
        .map(this::mapExtraVariableEntryToExtraVariableDto)
        .collect(Collectors.toList());
  }
}
