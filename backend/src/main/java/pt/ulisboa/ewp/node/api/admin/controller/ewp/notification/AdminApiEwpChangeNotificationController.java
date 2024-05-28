package pt.ulisboa.ewp.node.api.admin.controller.ewp.notification;

import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.admin.dto.response.AdminApiResponseWithDataDto;
import pt.ulisboa.ewp.node.api.admin.security.AdminApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.dto.notification.EwpChangeNotificationDto;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpChangeNotificationService;

@AdminApi
@RestController
@RequestMapping(AdminApiConstants.API_BASE_URI + "ewp/notifications")
@Secured({AdminApiSecurityCommonConstants.ROLE_ADMIN_WITH_PREFIX})
@Validated
public class AdminApiEwpChangeNotificationController {

  private final EwpChangeNotificationService ewpChangeNotificationService;

  public AdminApiEwpChangeNotificationController(
      EwpChangeNotificationService ewpChangeNotificationService) {
    this.ewpChangeNotificationService = ewpChangeNotificationService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Retrieves EWP change notifications.",
      tags = {"Admin"})
  public ResponseEntity<AdminApiResponseWithDataDto<GetEwpChangeNotificationsResponseDto>>
      getCommunicationLogs(@Valid @RequestBody GetEwpChangeNotificationsRequestDto requestDto) {

    FilterDto<EwpChangeNotification> filter = requestDto.getFilter();

    Collection<EwpChangeNotificationDto> communicationLogDtos =
        this.ewpChangeNotificationService.findByFilter(
            filter, requestDto.getOffset(), requestDto.getLimit());
    long totalResults = this.ewpChangeNotificationService.countByFilter(filter);
    GetEwpChangeNotificationsResponseDto responseDto =
        new GetEwpChangeNotificationsResponseDto(communicationLogDtos, totalResults);
    return AdminApiResponseUtils.toOkResponseEntity(responseDto);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Retrieves an EWP change notification.",
      tags = {"Admin"})
  public ResponseEntity<AdminApiResponseWithDataDto<EwpChangeNotificationDto>> getCommunicationLogs(
      @Min(1) @PathVariable(name = "id") long id) {
    Optional<EwpChangeNotificationDto> ewpChangeNotificationDtoOptional =
        this.ewpChangeNotificationService.findById(id);
    if (ewpChangeNotificationDtoOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return AdminApiResponseUtils.toOkResponseEntity(ewpChangeNotificationDtoOptional.get());
  }

  private static class GetEwpChangeNotificationsRequestDto implements Serializable {

    @Valid private FilterDto<EwpChangeNotification> filter;

    @Min(0)
    private int offset;

    @Min(1)
    @Max(50)
    private int limit;

    public FilterDto<EwpChangeNotification> getFilter() {
      return filter;
    }

    public void setFilter(FilterDto<EwpChangeNotification> filter) {
      this.filter = filter;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    public int getLimit() {
      return limit;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }
  }

  private static class GetEwpChangeNotificationsResponseDto implements Serializable {

    private final Collection<EwpChangeNotificationDto> ewpChangeNotifications;
    private final long totalResults;

    private GetEwpChangeNotificationsResponseDto(
        Collection<EwpChangeNotificationDto> ewpChangeNotifications, long totalResults) {
      this.ewpChangeNotifications = ewpChangeNotifications;
      this.totalResults = totalResults;
    }

    public Collection<EwpChangeNotificationDto> getEwpChangeNotifications() {
      return ewpChangeNotifications;
    }

    public long getTotalResults() {
      return totalResults;
    }
  }
}
