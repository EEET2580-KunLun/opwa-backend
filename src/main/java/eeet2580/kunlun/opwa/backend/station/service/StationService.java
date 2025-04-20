package eeet2580.kunlun.opwa.backend.station.service;

import eeet2580.kunlun.opwa.backend.station.dto.resp.StationRes;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;

import java.util.List;
import java.util.Optional;

public interface StationService {
    List<StationRes> getAllStations();

    Optional<StationRes> getStationById(String id);

    StationRes createStation(StationEntity station);

    StationRes updateStation(String id, StationEntity updatedStation);

    void deleteStation(String id);
}