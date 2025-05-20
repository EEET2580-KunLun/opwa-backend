package eeet2580.kunlun.opwa.backend.statistics.service.impl;

import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.PassengerRes;
import eeet2580.kunlun.opwa.backend.external.pawa.dto.resp.TicketRes;
import eeet2580.kunlun.opwa.backend.external.pawa.service.PawaService;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.statistics.dto.resp.TicketAnalyticsRes;
import eeet2580.kunlun.opwa.backend.statistics.dto.resp.UserAnalyticsRes;
import eeet2580.kunlun.opwa.backend.statistics.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final PawaService pawaService;
    private final StaffRepository staffRepository;

//    @Override
//    public TicketAnalyticsRes getTicketAnalytics() {
//        List<TicketRes> tickets = pawaService.getAllTickets()
//                .collectList()
//                .block();
//
//        if (tickets == null || tickets.isEmpty()) {
//            return new TicketAnalyticsRes(new ArrayList<>(), new ArrayList<>(), 0);
//        }
//
//        Map<TicketRes.TicketType, Integer> typeCountMap = new HashMap<>();
//        Map<TicketRes.TicketStatus, Integer> statusCountMap = new HashMap<>();
//
//        for (TicketRes ticket : tickets) {
//            TicketRes.TicketType mappedType = null;
//            try {
//                mappedType = TicketRes.TicketType.valueOf(ticket.getType().name());
//                typeCountMap.put(mappedType, typeCountMap.getOrDefault(mappedType, 0) + 1);
//            } catch (IllegalArgumentException e) {
//                log.warn("Unknown ticket type: {}", ticket.getType());
//            }
//
//            TicketRes.TicketStatus mappedStatus = null;
//            try {
//                mappedStatus = TicketRes.TicketStatus.valueOf(ticket.getStatus().name());
//                statusCountMap.put(mappedStatus, statusCountMap.getOrDefault(mappedStatus, 0) + 1);
//            } catch (IllegalArgumentException e) {
//                log.warn("Unknown ticket status: {}", ticket.getStatus());
//            }
//        }
//
//        List<TicketAnalyticsRes.TicketTypeCount> typeCountsList = new ArrayList<>();
//        for (Map.Entry<TicketRes.TicketType, Integer> entry : typeCountMap.entrySet()) {
//            typeCountsList.add(new TicketAnalyticsRes.TicketTypeCount(entry.getKey(), entry.getValue()));
//        }
//
//        List<TicketAnalyticsRes.TicketStatusCount> statusCountsList = new ArrayList<>();
//        for (Map.Entry<TicketRes.TicketStatus, Integer> entry : statusCountMap.entrySet()) {
//            statusCountsList.add(new TicketAnalyticsRes.TicketStatusCount(entry.getKey(), entry.getValue()));
//        }
//
//        return TicketAnalyticsRes.builder()
//                .ticketTypeCounts(typeCountsList)
//                .ticketStatusCounts(statusCountsList)
//                .totalTickets(tickets.size())
//                .build();
//    }

    @Override
    public TicketAnalyticsRes getTicketAnalytics() {
        List<TicketRes> tickets = pawaService.getAllTickets()
                .collectList()
                .block();

        if (tickets == null || tickets.isEmpty()) {
            return TicketAnalyticsRes.builder()
                    .ticketTypeCounts(new ArrayList<>())
                    .ticketStatusCounts(new ArrayList<>())
                    .totalTickets(0)
                    .totalRevenue(0L)
                    .guestTicketPercentage(0.0)
                    .monthlyRevenues(Collections.nCopies(12, 0L))
                    .build();
        }

        Map<TicketRes.TicketType, Integer> typeCountMap = new HashMap<>();
        Map<TicketRes.TicketStatus, Integer> statusCountMap = new HashMap<>();

        long totalRevenue = 0L;
        int guestCount = 0;
        long[] monthlyRevenue = new long[12];

        for (TicketRes ticket : tickets) {
            try {
                TicketRes.TicketType type = TicketRes.TicketType.valueOf(ticket.getType().name());
                typeCountMap.compute(type, (k, v) -> v == null ? 1 : v + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket type: {}", ticket.getType());
            }

            try {
                TicketRes.TicketStatus status = TicketRes.TicketStatus.valueOf(ticket.getStatus().name());
                statusCountMap.compute(status, (k, v) -> v == null ? 1 : v + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket status: {}", ticket.getStatus());
            }

            long price = ticket.getPrice();
            totalRevenue += price;

            // use the primitive getter
            if (ticket.getIsGuestTicket()) {
                guestCount++;
            }

            if (ticket.getPurchaseTime() != null) {
                int monthIndex = ticket.getPurchaseTime()
                        .atZone(ZoneId.systemDefault())
                        .getMonthValue() - 1;
                monthlyRevenue[monthIndex] += price;
            }
        }

        List<TicketAnalyticsRes.TicketTypeCount> typeCounts = typeCountMap.entrySet().stream()
                .map(e -> new TicketAnalyticsRes.TicketTypeCount(e.getKey(), e.getValue()))
                .toList();

        List<TicketAnalyticsRes.TicketStatusCount> statusCounts = statusCountMap.entrySet().stream()
                .map(e -> new TicketAnalyticsRes.TicketStatusCount(e.getKey(), e.getValue()))
                .toList();

        int totalTickets = tickets.size();
        double guestPercentage = totalTickets > 0
                ? guestCount * 100.0 / totalTickets
                : 0.0;

        log.info("Total tickets: {}, Guest tickets: {}, Total revenue: {}", totalTickets, guestPercentage, totalRevenue);

        List<Long> monthlyRevenues = Arrays.stream(monthlyRevenue)
                .boxed()
                .toList();

        return TicketAnalyticsRes.builder()
                .ticketTypeCounts(typeCounts)
                .ticketStatusCounts(statusCounts)
                .totalTickets(totalTickets)
                .totalRevenue(totalRevenue)
                .guestTicketPercentage(guestPercentage)
                .monthlyRevenues(monthlyRevenues)
                .build();
    }

    @Override
    public UserAnalyticsRes getUserAnalytics() {
        List<StaffEntity> staffUsers = staffRepository.findAll();

        Map<StaffEntity.Role, Integer> roleCountMap = new HashMap<>();

        for (StaffEntity.Role role : StaffEntity.Role.values()) {
            roleCountMap.put(role, 0);
        }

        for (StaffEntity staff : staffUsers) {
            if (staff.getRole() != StaffEntity.Role.PASSENGER) {
                roleCountMap.put(staff.getRole(), roleCountMap.getOrDefault(staff.getRole(), 0) + 1);
            }
        }

        List<PassengerRes> passengers = pawaService.getAllPassengers()
                .collectList()
                .block();

        int passengerCount = (passengers != null) ? passengers.size() : 0;
        roleCountMap.put(StaffEntity.Role.PASSENGER, passengerCount);

        List<UserAnalyticsRes.UserTypeCount> userTypeCounts = new ArrayList<>();
        int totalUsers = 0;

        for (Map.Entry<StaffEntity.Role, Integer> entry : roleCountMap.entrySet()) {
            userTypeCounts.add(new UserAnalyticsRes.UserTypeCount(entry.getKey(), entry.getValue()));
            totalUsers += entry.getValue();
        }

        return UserAnalyticsRes.builder()
                .userTypeCounts(userTypeCounts)
                .totalUsers(totalUsers)
                .build();
    }
}