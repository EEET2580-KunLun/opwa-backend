package eeet2580.kunlun.opwa.backend.line.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.req.LineSuspensionReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.service.LineService;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.ScheduleOverviewRes;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.TripScheduleRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/lines")
@RequiredArgsConstructor
public class LineController {
    private final LineService lineService;

    @GetMapping
    public ResponseEntity<BaseRes<List<LineRes>>> getAllLines() {
        List<LineRes> lines = lineService.getAllLines();
        BaseRes<List<LineRes>> response = new BaseRes<>(
                HttpStatus.OK.value(), "Line list retrieved successfully", lines);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<LineRes>> getLineById(@PathVariable String id) {
        return lineService.getLineById(id)
                .map(line -> {
                    BaseRes<LineRes> response = new BaseRes<>(
                            HttpStatus.OK.value(), "Line retrieved successfully", line);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<LineRes> response = new BaseRes<>(
                            HttpStatus.NOT_FOUND.value(), "Line not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<BaseRes<LineRes>> createLine(
            @Valid @RequestBody LineReq lineReq,
            Authentication authentication) {
        try {
            LineRes createdLine = lineService.createLine(lineReq, authentication);
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.CREATED.value(), "Line created successfully", createdLine);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<LineRes>> updateLine(
            @PathVariable String id,
            @Valid @RequestBody LineReq lineReq,
            Authentication authentication) {
        try {
            LineRes updatedLine = lineService.updateLine(id, lineReq, authentication);
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Line updated successfully", updatedLine);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteLine(@PathVariable String id) {
        if (lineService.getLineById(id).isEmpty()) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), "Line not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        lineService.deleteLine(id);
        BaseRes<Void> response = new BaseRes<>(
                HttpStatus.OK.value(), "Line deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/schedule/generate")
    public ResponseEntity<BaseRes<ScheduleOverviewRes>> generateLineSchedule(
            @PathVariable String id) {
        try {
            ScheduleOverviewRes scheduleOverview = lineService.generateLineSchedule(id);
            BaseRes<ScheduleOverviewRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Schedule generated successfully", scheduleOverview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<ScheduleOverviewRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}/schedule/overview")
    public ResponseEntity<BaseRes<ScheduleOverviewRes>> getScheduleOverview(
            @PathVariable String id) {
        try {
            ScheduleOverviewRes scheduleOverview = lineService.getScheduleOverview(id);
            BaseRes<ScheduleOverviewRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Schedule overview retrieved successfully", scheduleOverview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<ScheduleOverviewRes> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<BaseRes<List<TripScheduleRes>>> getLineTrips(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<TripScheduleRes> trips = lineService.getLineTrips(id, page, size);
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Trips retrieved successfully", trips);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<List<TripScheduleRes>> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<BaseRes<LineRes>> suspendLine(
            @PathVariable String id,
            @Valid @RequestBody LineSuspensionReq suspensionReq,
            Authentication authentication) {
        try {
            LineRes suspendedLine = lineService.suspendLine(id, suspensionReq, authentication);
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Line suspended successfully", suspendedLine);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<BaseRes<LineRes>> resumeLine(
            @PathVariable String id,
            Authentication authentication) {
        try {
            LineRes resumedLine = lineService.resumeLine(id, authentication);
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Line resumed successfully", resumedLine);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<LineRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

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
}