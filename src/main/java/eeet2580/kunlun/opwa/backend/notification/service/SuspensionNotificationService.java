package eeet2580.kunlun.opwa.backend.notification.service;

import eeet2580.kunlun.opwa.backend.notification.model.SuspensionNotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.repository.SuspensionNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for sending real-time notification to PAWA
 * when a metro line is suspended or resumed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuspensionNotificationService {

    private final SuspensionNotificationRepository suspensionNotificationRepository;
    private final RestTemplate restTemplate;

    //TODO: wait for the url from PAWA
//    @Value("${pawa.notification.url}")
    private String pawaNotificationUrl;

    /**
     * Notify PAWA about a line suspension
     * Store a suspension notification (to be sent to PAWA when integration is ready)
     */
    public void notifySuspension(String lineId, String lineName, List<String> affectedStationIds,
                                 String reason, LocalDateTime expectedRestorationTime) {
        log.info("Storing suspension notification for line: {} ({})", lineName, lineId);

        SuspensionNotificationEntity notification = new SuspensionNotificationEntity();
        notification.setLineId(lineId);
        notification.setLineName(lineName);
        notification.setAffectedStationIds(affectedStationIds);
        notification.setReason(reason);
        notification.setExpectedRestorationTime(expectedRestorationTime);
        notification.setType(SuspensionNotificationEntity.NotificationType.SUSPENSION);
        notification.setTimestamp(LocalDateTime.now());

        // In the absence of PAWA, we'll mark notifications as acknowledged immediately
        notification.setAcknowledged(false);
        notification.setRetryCount(0);

        // Save notification first
        suspensionNotificationRepository.save(notification);

        log.info("Suspension notification stored successfully for line: {}", lineName);

        //TODO: Try to send notification
//        trySendNotification(notification);
    }

    /**
     * Notify PAWA about a line resumption
     */
    public void notifyResumption(String lineId, String lineName) {
        log.info("Storing resumption notification for line: {} ({})", lineName, lineId);

        SuspensionNotificationEntity notification = new SuspensionNotificationEntity();
        notification.setLineId(lineId);
        notification.setLineName(lineName);
        notification.setType(SuspensionNotificationEntity.NotificationType.RESUMPTION);
        notification.setTimestamp(LocalDateTime.now());

        // In the absence of PAWA, we'll mark notifications as acknowledged immediately
        notification.setAcknowledged(true);
        notification.setRetryCount(0);

        // Save notification first
        suspensionNotificationRepository.save(notification);

        log.info("Resumption notification stored successfully for line: {}", lineName);

        //TODO: Try to send notification
//        trySendNotification(notification);
    }

    /**
     * Try to send a notification to PAWA
     */
    private void trySendNotification(SuspensionNotificationEntity notification) {
        try {
            // Create notification payload
            SuspensionNotificationPayload payload = new SuspensionNotificationPayload();
            payload.setNotificationId(notification.getId());
            payload.setLineId(notification.getLineId());
            payload.setLineName(notification.getLineName());
            payload.setAffectedStationIds(notification.getAffectedStationIds());
            payload.setType(notification.getType().toString());
            payload.setReason(notification.getReason());
            payload.setExpectedRestorationTime(notification.getExpectedRestorationTime());
            payload.setTimestamp(notification.getTimestamp());

            // Send to PAWA
            restTemplate.postForEntity(pawaNotificationUrl, payload, String.class);

            // Mark as sent successfully
            notification.setAcknowledged(true);
            suspensionNotificationRepository.save(notification);

            log.info("Successfully sent {} notification for line {} to PAWA",
                    notification.getType(), notification.getLineName());

        } catch (Exception e) {
            log.error("Failed to send notification to PAWA", e);

            // Update retry count
            notification.setRetryCount(notification.getRetryCount() + 1);
            suspensionNotificationRepository.save(notification);
        }
    }

    /**
     * Scheduled task to retry sending notifications that were not acknowledged
     * Runs every 3 seconds
     * TODO: Uncomment the @Scheduled annotation when PAWA integration is ready
     */
//    @Scheduled(fixedDelay = 3000)
    public void retryFailedNotifications() {
        //TODO: Enable retry mechanism when PAWA integration is ready
//        List<SuspensionNotificationEntity> failedNotifications =
//                suspensionNotificationRepository.findByAcknowledgedFalse();
//
//        for (SuspensionNotificationEntity notification : failedNotifications) {
//            log.info("Retrying notification for line {}, attempt {}",
//                    notification.getLineName(), notification.getRetryCount() + 1);
//
//            trySendNotification(notification);
//        }

        log.debug("Notification retry mechanism is disabled until PAWA integration is ready");
    }

    /**
     * Data class for the notification payload sent to PAWA
     */
    private static class SuspensionNotificationPayload {
        private String notificationId;
        private String lineId;
        private String lineName;
        private List<String> affectedStationIds;
        private String type;
        private String reason;
        private LocalDateTime expectedRestorationTime;
        private LocalDateTime timestamp;

        // Getters and setters
        public String getNotificationId() { return notificationId; }
        public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
        public String getLineId() { return lineId; }
        public void setLineId(String lineId) { this.lineId = lineId; }
        public String getLineName() { return lineName; }
        public void setLineName(String lineName) { this.lineName = lineName; }
        public List<String> getAffectedStationIds() { return affectedStationIds; }
        public void setAffectedStationIds(List<String> affectedStationIds) { this.affectedStationIds = affectedStationIds; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public LocalDateTime getExpectedRestorationTime() { return expectedRestorationTime; }
        public void setExpectedRestorationTime(LocalDateTime expectedRestorationTime) { this.expectedRestorationTime = expectedRestorationTime; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public void acknowledgeNotification(String notificationId) {
        SuspensionNotificationEntity notification = suspensionNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.setAcknowledged(true);
        suspensionNotificationRepository.save(notification);

        log.info("Notification {} for line {} acknowledged by PAWA",
                notificationId, notification.getLineName());
    }
}
