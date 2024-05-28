package pt.ulisboa.ewp.node.service.ewp.notification;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.mapper.notification.EwpChangeNotificationMapper;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;

@Service
@Transactional
public class EwpChangeNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(EwpChangeNotificationService.class);

  private final EwpChangeNotificationRepository repository;

  public EwpChangeNotificationService(EwpChangeNotificationRepository repository) {
    this.repository = repository;
  }

  public Optional<EwpChangeNotificationDto> findById(long id) {
    EwpChangeNotificationMapper mapper = EwpChangeNotificationMapper.INSTANCE;
    Optional<EwpChangeNotification> ewpChangeNotificationOptional = this.repository.findById(id);
    if (ewpChangeNotificationOptional.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        mapper.mapEwpChangeNotificationToEwpChangeNotificationDto(
            ewpChangeNotificationOptional.get()));
  }

  public Collection<EwpChangeNotificationDto> findByFilter(
      FilterDto<EwpChangeNotification> filter, int offset, int limit) {
    EwpChangeNotificationMapper mapper = EwpChangeNotificationMapper.INSTANCE;
    return this.repository.findByFilter(filter, offset, limit).stream()
        .map(mapper::mapEwpChangeNotificationToEwpChangeNotificationDto)
        .collect(Collectors.toList());
  }

  public long countByFilter(FilterDto<EwpChangeNotification> filter) {
    return this.repository.countByFilter(filter);
  }
}
