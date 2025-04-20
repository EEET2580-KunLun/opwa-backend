package eeet2580.kunlun.opwa.backend.station.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;
    private final StationMapper stationMapper;

    @GetMapping
    public ResponseEntity<BaseRes<List<StationRes>>> getAllStations() {
        List<StationEntity> stations = stationService.getAllStations();
        List<StationRes> stationDtoList = stationMapper.toDtoList(stations);
        BaseRes<List<StationRes>> response = new BaseRes<>(HttpStatus.OK.value(), "Station list retrieved successfully", stationDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<StationRes>> getStationById(@PathVariable String id) {
        return stationService.getStationById(id)
                .map(station -> {
                    StationRes stationDto = stationMapper.toDto(station);
                    BaseRes<StationRes> response = new BaseRes<>(HttpStatus.OK.value(), "Station retrieved successfully", stationDto);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<StationRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Station not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<BaseRes<StationRes>> createStation(@Valid @RequestBody StationReq stationReq) {
        StationEntity station = stationMapper.toEntity(stationReq);
        StationEntity createdStation = stationService.createStation(station);
        StationRes stationDto = stationMapper.toDto(createdStation);
        BaseRes<StationRes> response = new BaseRes<>(HttpStatus.CREATED.value(), "Station created successfully", stationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<StationRes>> updateStation(@PathVariable String id, @Valid @RequestBody StationReq stationReq) {
        try {
            StationEntity station = stationMapper.toEntity(stationReq);
            StationEntity updatedStation = stationService.updateStation(id, station);
            StationRes stationDto = stationMapper.toDto(updatedStation);
            BaseRes<StationRes> response = new BaseRes<>(HttpStatus.OK.value(), "Station updated successfully", stationDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<StationRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteStation(@PathVariable String id) {
        if (stationService.getStationById(id).isEmpty()) {
            BaseRes<Void> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Station not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        stationService.deleteStation(id);
        BaseRes<Void> response = new BaseRes<>(HttpStatus.OK.value(), "Station deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}