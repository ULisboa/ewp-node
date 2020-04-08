package pt.ulisboa.ewp.node.api.admin.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;
import pt.ulisboa.ewp.node.domain.entity.user.UserRole;
import pt.ulisboa.ewp.node.domain.repository.UserProfileRepository;

@Service
@Transactional
public class AdminApiUserRolesPopulator {

  @Autowired private UserProfileRepository userRepository;

  public UserRole getUserRole(String username) {
    Optional<UserProfile> optionalUserProfile = userRepository.findByUsername(username);
    if (optionalUserProfile.isPresent()) {
      return optionalUserProfile.get().getRole();
    }
    return UserRole.GUEST;
  }
}
