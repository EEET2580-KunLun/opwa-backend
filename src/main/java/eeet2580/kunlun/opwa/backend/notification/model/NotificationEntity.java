package eeet2580.kunlun.opwa.backend.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "notification")
public class NotificationEntity {

    public enum Reason {
        OUTAGE,
        MAINTENANCE,
        INCIDENT,
        OTHER
    }

    @Id
    private String id;

    private String lineId;
    private List<String> affectedStations;
    private Reason reason;

    private String description;
    private LocalDateTime expectedRestorationTime;

    private Boolean notificationSent;
    private Integer retryCount;
    private LocalDateTime lastRetry;
    private Boolean acknowledged;
}
