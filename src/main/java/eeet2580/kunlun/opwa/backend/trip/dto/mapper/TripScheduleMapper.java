package eeet2580.kunlun.opwa.backend.trip.dto.mapper;

import org.springframework.stereotype.Component;
import eeet2580.kunlun.opwa.backend.trip.dto.resp.TripScheduleRes;
import eeet2580.kunlun.opwa.backend.trip.model.TripScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TripScheduleMapper {
    public TripScheduleRes toDto(TripScheduleEntity entity) {
        if (entity == null) {
            return null;
        }

        TripScheduleRes dto = new TripScheduleRes();
        dto.setId(entity.getId());
        dto.setLineId(entity.getLineId());
        dto.setLineName(entity.getLineName());
        dto.setTripCode(entity.getTripCode());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setArrivalTime(entity.getArrivalTime());
        dto.setSuspended(entity.isSuspended());

        if (entity.getStationStops() != null) {
            dto.setStationStops(entity.getStationStops().stream()
                    .map(this::toStationStopDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<TripScheduleRes> toDtoList(List<TripScheduleEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TripScheduleRes.StationStopRes toStationStopDto(TripScheduleEntity.StationStop entity) {
        TripScheduleRes.StationStopRes dto = new TripScheduleRes.StationStopRes();
        dto.setStationId(entity.getStationId());
        dto.setStationName(entity.getStationName());
        dto.setSequence(entity.getSequence());
        dto.setArrivalTime(entity.getArrivalTime());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setLocation(entity.getLocation());
        return dto;
    }
}
