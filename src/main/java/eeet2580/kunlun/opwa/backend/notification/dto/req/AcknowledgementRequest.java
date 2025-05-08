package eeet2580.kunlun.opwa.backend.notification.dto.req;

import lombok.Data;

@Data
public class AcknowledgementRequest {
    private String notificationId;
    private boolean acknowledged;

    public AcknowledgementRequest(String notificationId, boolean acknowledged) {
        this.notificationId = notificationId;
        this.acknowledged = acknowledged;
    }
}
