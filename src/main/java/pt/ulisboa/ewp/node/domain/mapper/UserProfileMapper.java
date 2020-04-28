package pt.ulisboa.ewp.node.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import pt.ulisboa.ewp.node.api.admin.dto.AdminApiUserProfileDTO;
import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;

@Mapper
public interface UserProfileMapper {

  UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

  AdminApiUserProfileDTO mapToDto(UserProfile object);
}
