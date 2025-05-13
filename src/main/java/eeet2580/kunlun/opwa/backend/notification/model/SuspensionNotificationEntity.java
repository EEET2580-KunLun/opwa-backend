package eeet2580.kunlun.opwa.backend.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity for tracking suspension notifications sent to PAWA.
 */
@Data
@Document(collection = "metro_suspension_notifications")
public class SuspensionNotificationEntity {

    public enum NotificationType {
        SUSPENSION,
        RESUMPTION
    }

    @Id
    private String id;

    private String lineId;
    private String lineName;
    private List<String> affectedStationIds;
    private String reason;
    private LocalDateTime expectedRestorationTime;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean acknowledged;
    private int retryCount;
}
