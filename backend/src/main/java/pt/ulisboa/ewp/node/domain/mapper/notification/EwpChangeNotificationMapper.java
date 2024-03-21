package pt.ulisboa.ewp.node.domain.mapper.notification;

import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;

@Mapper
public interface EwpChangeNotificationMapper {

  EwpChangeNotificationMapper INSTANCE = Mappers.getMapper(EwpChangeNotificationMapper.class);

  @Mapping(target = "extraVariables", expression = "java(mapExtraVariables(ewpChangeNotification))")
  EwpChangeNotificationDto mapEwpChangeNotificationToEwpChangeNotificationDto(
      EwpChangeNotification ewpChangeNotification);

  default Map<String, String> mapExtraVariables(EwpChangeNotification ewpChangeNotification) {
    return ewpChangeNotification.getExtraVariables();
  }
}
