package eeet2580.kunlun.opwa.backend.station.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.common.dto.resp.PagedResponse;
import eeet2580.kunlun.opwa.backend.station.dto.mapper.StationMapper;
import eeet2580.kunlun.opwa.backend.station.dto.req.StationReq;
import eeet2580.kunlun.opwa.backend.station.dto.resp.StationRes;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import eeet2580.kunlun.opwa.backend.station.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stations")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;
    private final StationMapper stationMapper;

    @GetMapping
    public ResponseEntity<BaseRes<PagedResponse<StationRes>>> getAllStations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        PagedResponse<StationRes> stationsPage = stationService.getAllStations(page, size, sortBy, direction);
        BaseRes<PagedResponse<StationRes>> response = new BaseRes<>(
                HttpStatus.OK.value(), "Station list retrieved successfully", stationsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<StationRes>> getStationById(@PathVariable String id) {
        return stationService.getStationById(id)
                .map(station -> {
                    BaseRes<StationRes> response = new BaseRes<>(
                            HttpStatus.OK.value(), "Station retrieved successfully", station);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<StationRes> response = new BaseRes<>(
                            HttpStatus.NOT_FOUND.value(), "Station not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<BaseRes<StationRes>> createStation(@Valid @RequestBody StationReq stationReq) {
        try {
            StationEntity station = stationMapper.toEntity(stationReq);
            StationRes createdStation = stationService.createStation(station);
            BaseRes<StationRes> response = new BaseRes<>(
                    HttpStatus.CREATED.value(), "Station created successfully", createdStation);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            BaseRes<StationRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<StationRes>> updateStation(
            @PathVariable String id,
            @Valid @RequestBody StationReq stationReq) {
        try {
            StationEntity station = stationMapper.toEntity(stationReq);
            StationRes updatedStation = stationService.updateStation(id, station);
            BaseRes<StationRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Station updated successfully", updatedStation);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseRes<StationRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            BaseRes<StationRes> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteStation(@PathVariable String id) {
        if (stationService.getStationById(id).isEmpty()) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), "Station not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        stationService.deleteStation(id);
        BaseRes<Void> response = new BaseRes<>(
                HttpStatus.OK.value(), "Station deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}