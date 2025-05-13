package eeet2580.kunlun.opwa.backend.trip;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.notification.service.SuspensionNotificationService;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.TripScheduleRes;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Public API controller for metro line data to be consumed by PAWA
 */
@RestController
@RequestMapping("/api/v1/metro")
@RequiredArgsConstructor
public class MetroApiController {
    private final LineService lineService;
    private final SuspensionNotificationService suspensionNotificationService;

    /**
     * Find trips between two stations (possibly on different lines)
     * This API is consumed by PAWA for search functionality
     */
    @GetMapping("/trips/search")
    public ResponseEntity<BaseRes<List<TripScheduleRes>>> findTripsBetweenStations(
            @RequestParam String departureStationId,
            @RequestParam String arrivalStationId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
        try {
            List<TripScheduleRes> trips = lineService.findTripsBetweenStations(
                    departureStationId, arrivalStationId, departureTime);
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Trips found successfully", trips);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Find the next three upcoming trips for a station
     * This API is consumed by PAWA for displaying upcoming departures
     */
    @GetMapping("/trips/upcoming")
    public ResponseEntity<BaseRes<List<TripScheduleRes>>> findNextThreeUpcomingTrips(
            @RequestParam String stationId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTime) {
        try {
            List<TripScheduleRes> trips = lineService.findNextThreeUpcomingTrips(stationId, fromTime);
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Upcoming trips found successfully", trips);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Acknowledge a suspension notification from PAWA
     * This API is used by PAWA to confirm receipt of suspension notifications
     */
    @PostMapping("/notifications/{notificationId}/acknowledge")
    public ResponseEntity<BaseRes<Void>> acknowledgeSuspensionNotification(
            @PathVariable String notificationId) {
        try {
            suspensionNotificationService.acknowledgeNotification(notificationId);

            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Notification acknowledged successfully", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
