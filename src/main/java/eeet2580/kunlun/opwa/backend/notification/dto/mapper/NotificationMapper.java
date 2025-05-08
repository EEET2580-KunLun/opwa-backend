package eeet2580.kunlun.opwa.backend.notification.dto.mapper;

import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.dto.resp.SuspensionNoticeResponse;

public class NotificationMapper {

    public static SuspensionNoticeResponse toDto(NotificationEntity entity) {
        SuspensionNoticeResponse dto = new SuspensionNoticeResponse();
        dto.setId(entity.getId());
        dto.setLineId(entity.getLineId());
        dto.setAffectedStations(entity.getAffectedStations());
        dto.setReason(entity.getReason().name());
        dto.setDescription(entity.getDescription());
        dto.setExpectedRestorationTime(entity.getExpectedRestorationTime());

        dto.setAcknowledged(entity.getAcknowledged());
        dto.setNotificationSent(entity.getNotificationSent());
        dto.setRetryCount(entity.getRetryCount());
        dto.setLastRetry(entity.getLastRetry());

        return dto;
    }
}
