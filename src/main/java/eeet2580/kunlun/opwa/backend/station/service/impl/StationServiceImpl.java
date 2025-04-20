package eeet2580.kunlun.opwa.backend.station.service.impl;

import eeet2580.kunlun.opwa.backend.line.service.StationReferenceService;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import eeet2580.kunlun.opwa.backend.station.repository.StationRepository;
import eeet2580.kunlun.opwa.backend.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final StationReferenceService stationReferenceService;

    @Override
    public List<StationEntity> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public Optional<StationEntity> getStationById(String id) {
        return stationRepository.findById(id);
    }

    @Override
    public StationEntity createStation(StationEntity station) {
        return stationRepository.save(station);
    }

    @Override
    public StationEntity updateStation(String id, StationEntity updatedStation) {
        if (stationRepository.existsById(id)) {
            updatedStation.setId(id);
            StationEntity saved = stationRepository.save(updatedStation);

            stationReferenceService.updateStationReferenceData(id, saved);

            return saved;
        }
        throw new RuntimeException("Station not found with id: " + id);
    }

    @Override
    public void deleteStation(String id) {
        stationRepository.deleteById(id);
    }
}