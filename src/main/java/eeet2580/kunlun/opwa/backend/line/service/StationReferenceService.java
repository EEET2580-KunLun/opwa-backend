package eeet2580.kunlun.opwa.backend.line.service;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import eeet2580.kunlun.opwa.backend.line.repository.LineRepository;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationReferenceService {
    private final LineRepository lineRepository;

    public void updateStationReferenceData(String stationId, StationEntity updatedStation) {
        for (LineEntity line : lineRepository.findByStationsStationId(stationId)) {
            line.getStations().stream()
                    .filter(s -> s.getStationId().equals(stationId))
                    .forEach(s -> {
                        s.setStationName(updatedStation.getName());
                        s.setLocation(updatedStation.getLocation());
                    });
            lineRepository.save(line);
        }
    }
}