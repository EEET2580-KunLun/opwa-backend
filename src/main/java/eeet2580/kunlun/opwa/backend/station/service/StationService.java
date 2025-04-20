package eeet2580.kunlun.opwa.backend.station.service;

import eeet2580.kunlun.opwa.backend.station.model.StationEntity;

import java.util.List;
import java.util.Optional;

public interface StationService {
    List<StationEntity> getAllStations();

    Optional<StationEntity> getStationById(String id);

    StationEntity createStation(StationEntity station);

    StationEntity updateStation(String id, StationEntity updatedStation);

    void deleteStation(String id);
}