package eeet2580.kunlun.opwa.backend.line.service;

import eeet2580.kunlun.opwa.backend.common.dto.resp.PagedResponse;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineSuspensionReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.trip.dto.req.ScheduleReq;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.ScheduleOverviewRes;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.TripScheduleRes;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LineService {
    PagedResponse<LineRes> getAllLines(int page, int size, String sortBy, String direction, String status);

    Optional<LineRes> getLineById(String id);

    LineRes createLine(LineEntity line);

    LineRes createLine(LineReq lineReq, Authentication authentication);

    LineRes updateLine(String id, LineEntity updatedLine);

    LineRes updateLine(String id, LineReq lineReq, Authentication authentication);

    void deleteLine(String id);

    /**
     * Generate trip schedules for a metro line based on first departure time and frequency
     */
    ScheduleOverviewRes generateLineSchedule(String lineId, ScheduleReq scheduleReq);

    /**
     * Get schedule overview for a metro line
     */
    ScheduleOverviewRes getScheduleOverview(String lineId);

    PagedResponse<TripScheduleRes> getLineTrips(String lineId, int page, int size, String sortBy, String direction);

    /**
     * Suspend a metro line or specific segments
     */
    LineRes suspendLine(String lineId, LineSuspensionReq suspensionReq, Authentication authentication);

    /**
     * Resume a suspended metro line
     */
    LineRes resumeLine(String lineId, Authentication authentication);

    /**
     * Find trips between two stations (possibly on different lines)
     */
    List<TripScheduleRes> findTripsBetweenStations(String departureStationId, String arrivalStationId, LocalDateTime departureTime);

    /**
     * Find the next three upcoming trips for a station
     */
    List<TripScheduleRes> findNextThreeUpcomingTrips(String stationId, LocalDateTime fromTime);

    /**
     * Check whether a line with the given name already exists.
     */
    boolean existsByName(String name);
}