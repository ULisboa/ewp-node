package pt.ulisboa.ewp.node.api.host.forward.ewp.mapper.cnr;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr.ForwardEwpApiCnrStatusResponseDTO;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;

@Mapper
public interface ForwardEwpApiChangeNotificationMapper {

  ForwardEwpApiChangeNotificationMapper INSTANCE =
      Mappers.getMapper(ForwardEwpApiChangeNotificationMapper.class);

  ForwardEwpApiCnrStatusResponseDTO mapEwpChangeNotificationDtoToForwardEwpApiCnrStatusResponseDTO(
      EwpChangeNotificationDto ewpChangeNotificationDto);
}
