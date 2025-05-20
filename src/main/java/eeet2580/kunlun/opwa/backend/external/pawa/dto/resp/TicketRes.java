package eeet2580.kunlun.opwa.backend.external.pawa.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRes {
    private String id;
    private TicketType type;
    private Integer stationCount;
    private Instant purchaseTime;
    private Instant activationTime;
    private Instant expiryTime;
    private TicketStatus status;
    private Integer price;
    private String userId;
    private Integer validDay;
    private Boolean isGuestTicket;
    private Long validHours;

    public enum TicketType {
        ONE_WAY,
        DAILY,
        THREE_DAY,
        MONTHLY_STUDENT,
        MONTHLY_ADULT,
        FREE
    }

    public enum TicketStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}