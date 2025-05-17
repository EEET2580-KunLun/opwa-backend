package eeet2580.kunlun.opwa.backend.statistics.dto.resp;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAnalyticsRes {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketTypeCount {
        private TicketRes.TicketType ticketType;
        private Integer count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketStatusCount {
        private TicketRes.TicketStatus ticketStatus;
        private Integer count;
    }

    private List<TicketTypeCount> ticketTypeCounts;
    private List<TicketStatusCount> ticketStatusCounts;
    private Integer totalTickets;
}