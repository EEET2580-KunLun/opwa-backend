package eeet2580.kunlun.opwa.backend.station.service.impl;

import eeet2580.kunlun.opwa.backend.common.dto.resp.PagedResponse;
import eeet2580.kunlun.opwa.backend.line.service.StationReferenceService;
import eeet2580.kunlun.opwa.backend.station.dto.mapper.StationMapper;
import eeet2580.kunlun.opwa.backend.station.dto.resp.StationRes;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import eeet2580.kunlun.opwa.backend.station.repository.StationRepository;
import eeet2580.kunlun.opwa.backend.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    private final StationReferenceService stationReferenceService;
    private final StationMapper stationMapper;

    @Override
    public PagedResponse<StationRes> getAllStations(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("ASC") ?
                Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StationEntity> stationsPage = stationRepository.findAll(pageable);

        List<StationRes> content = stationMapper.toDtoList(stationsPage.getContent());

        return new PagedResponse<>(
                content,
                stationsPage.getNumber(),
                stationsPage.getSize(),
                stationsPage.getTotalElements(),
                stationsPage.getTotalPages(),
                stationsPage.isLast()
        );
    }

    @Override
    public Optional<StationRes> getStationById(String id) {
        return stationRepository.findById(id).map(stationMapper::toDto);
    }

    @Override
    public StationRes createStation(StationEntity station) {
        if (stationRepository.existsByName(station.getName())) {
            throw new IllegalArgumentException("Station name '" + station.getName() + "' already exists");
        }
        StationEntity savedEntity = stationRepository.save(station);
        return stationMapper.toDto(savedEntity);
    }

    @Override
    public StationRes updateStation(String id, StationEntity updatedStation) {
        if (stationRepository.existsById(id)) {
            Optional<StationEntity> existingWithSameName = stationRepository.findByName(updatedStation.getName());
            if (existingWithSameName.isPresent() && !existingWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Station name '" + updatedStation.getName() + "' already exists");
            }

            updatedStation.setId(id);
            StationEntity saved = stationRepository.save(updatedStation);
            stationReferenceService.updateStationReferenceData(id, saved);
            return stationMapper.toDto(saved);
        }
        throw new RuntimeException("Station not found with id: " + id);
    }

    @Override
    public void deleteStation(String id) {
        stationRepository.deleteById(id);
    }
}