package eeet2580.kunlun.opwa.backend.notification.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SuspensionNoticeResponse {
    private String id;
    private String lineId;
    private List<String> affectedStations;
    private String reason;
    private String description;
    private LocalDateTime expectedRestorationTime;
    private Boolean acknowledged;
    private Boolean notificationSent;
    private Integer retryCount;
    private LocalDateTime lastRetry;
}
