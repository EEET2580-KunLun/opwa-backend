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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final PawaService pawaService;
    private final StaffRepository staffRepository;

    @Override
    public TicketAnalyticsRes getTicketAnalytics() {
        List<TicketRes> tickets = pawaService.getAllTickets()
                .collectList()
                .block();

        if (tickets == null || tickets.isEmpty()) {
            return new TicketAnalyticsRes(new ArrayList<>(), new ArrayList<>(), 0);
        }

        Map<TicketRes.TicketType, Integer> typeCountMap = new HashMap<>();
        Map<TicketRes.TicketStatus, Integer> statusCountMap = new HashMap<>();

        for (TicketRes ticket : tickets) {
            TicketRes.TicketType mappedType = null;
            try {
                mappedType = TicketRes.TicketType.valueOf(ticket.getType().name());
                typeCountMap.put(mappedType, typeCountMap.getOrDefault(mappedType, 0) + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket type: {}", ticket.getType());
            }

            TicketRes.TicketStatus mappedStatus = null;
            try {
                mappedStatus = TicketRes.TicketStatus.valueOf(ticket.getStatus().name());
                statusCountMap.put(mappedStatus, statusCountMap.getOrDefault(mappedStatus, 0) + 1);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown ticket status: {}", ticket.getStatus());
            }
        }

        List<TicketAnalyticsRes.TicketTypeCount> typeCountsList = new ArrayList<>();
        for (Map.Entry<TicketRes.TicketType, Integer> entry : typeCountMap.entrySet()) {
            typeCountsList.add(new TicketAnalyticsRes.TicketTypeCount(entry.getKey(), entry.getValue()));
        }

        List<TicketAnalyticsRes.TicketStatusCount> statusCountsList = new ArrayList<>();
        for (Map.Entry<TicketRes.TicketStatus, Integer> entry : statusCountMap.entrySet()) {
            statusCountsList.add(new TicketAnalyticsRes.TicketStatusCount(entry.getKey(), entry.getValue()));
        }

        return TicketAnalyticsRes.builder()
                .ticketTypeCounts(typeCountsList)
                .ticketStatusCounts(statusCountsList)
                .totalTickets(tickets.size())
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