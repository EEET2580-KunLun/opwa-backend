package eeet2580.kunlun.opwa.backend.statistics.service.impl;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import eeet2580.kunlun.opwa.backend.external.pawa.service.TicketService;
import eeet2580.kunlun.opwa.backend.statistics.dto.resp.TicketAnalyticsRes;
import eeet2580.kunlun.opwa.backend.statistics.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final TicketService ticketService;

    @Override
    public TicketAnalyticsRes getTicketAnalytics() {
        log.info("Fetching ticket analytics data");

        List<TicketRes> tickets = ticketService.getAllTickets()
                .collectList()
                .block();

        if (tickets == null || tickets.isEmpty()) {
            log.info("No tickets found for analytics");
            return new TicketAnalyticsRes(new ArrayList<>(), new ArrayList<>(), 0);
        }

        // Create maps to store counts by type and status
        Map<TicketRes.TicketType, Integer> typeCountMap = new HashMap<>();
        Map<TicketRes.TicketStatus, Integer> statusCountMap = new HashMap<>();

        // Process all tickets
        for (TicketRes ticket : tickets) {
            // Map external ticket type to analytics ticket type
            TicketRes.TicketType mappedType = null;
            try {
                mappedType = TicketRes.TicketType.valueOf(ticket.getType().name());
                typeCountMap.put(mappedType, typeCountMap.getOrDefault(mappedType, 0) + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket type: {}", ticket.getType());
            }

            // Map external ticket status to analytics ticket status
            TicketRes.TicketStatus mappedStatus = null;
            try {
                mappedStatus = TicketRes.TicketStatus.valueOf(ticket.getStatus().name());
                statusCountMap.put(mappedStatus, statusCountMap.getOrDefault(mappedStatus, 0) + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket status: {}", ticket.getStatus());
            }
        }

        // Convert maps to lists of count objects
        List<TicketAnalyticsRes.TicketTypeCount> typeCountsList = new ArrayList<>();
        for (Map.Entry<TicketRes.TicketType, Integer> entry : typeCountMap.entrySet()) {
            typeCountsList.add(new TicketAnalyticsRes.TicketTypeCount(entry.getKey(), entry.getValue()));
        }

        List<TicketAnalyticsRes.TicketStatusCount> statusCountsList = new ArrayList<>();
        for (Map.Entry<TicketRes.TicketStatus, Integer> entry : statusCountMap.entrySet()) {
            statusCountsList.add(new TicketAnalyticsRes.TicketStatusCount(entry.getKey(), entry.getValue()));
        }

        log.info("Analytics completed: found {} tickets", tickets.size());

        return TicketAnalyticsRes.builder()
                .ticketTypeCounts(typeCountsList)
                .ticketStatusCounts(statusCountsList)
                .totalTickets(tickets.size())
                .build();
    }
}