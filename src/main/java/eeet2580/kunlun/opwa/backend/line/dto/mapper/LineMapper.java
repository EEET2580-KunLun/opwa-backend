package eeet2580.kunlun.opwa.backend.line.dto.mapper;

import eeet2580.kunlun.opwa.backend.line.dto.req.LineReq;
import eeet2580.kunlun.opwa.backend.line.dto.req.StationInLineReq;
import eeet2580.kunlun.opwa.backend.line.dto.resp.LineRes;
import eeet2580.kunlun.opwa.backend.line.dto.resp.StationInLineRes;
import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LineMapper {

    public LineRes toDto(LineEntity entity) {
        if (entity == null) {
            return null;
        }

        LineRes dto = new LineRes();
        dto.setId(entity.getId());
        dto.setStaffId(entity.getStaffId());
        dto.setName(entity.getName());
        dto.setFirstDepartureTime(entity.getFirstDepartureTime());
        dto.setFrequency(entity.getFrequency());
        dto.setStatus(entity.getStatus());

        if (entity.getStations() != null) {
            dto.setStations(entity.getStations().stream()
                    .map(this::toStationInLineDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<LineRes> toDtoList(List<LineEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public LineEntity toEntity(LineReq req) {
        if (req == null) {
            return null;
        }

        LineEntity entity = new LineEntity();
        entity.setName(req.getName());
        entity.setStaffId(req.getStaffId());
        entity.setFirstDepartureTime(req.getFirstDepartureTime());
        entity.setFrequency(req.getFrequency());
        entity.setStatus(req.getStatus());

        if (req.getStations() != null) {
            entity.setStations(req.getStations().stream()
                    .map(this::toStationInLineEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private StationInLineRes toStationInLineDto(LineEntity.StationInLine entity) {
        StationInLineRes dto = new StationInLineRes();
        dto.setStationId(entity.getStationId());
        dto.setStationName(entity.getStationName());
        dto.setSequence(entity.getSequence());
        dto.setTimeFromPreviousStation(entity.getTimeFromPreviousStation());
        dto.setLocation(entity.getLocation());
        return dto;
    }

    private LineEntity.StationInLine toStationInLineEntity(StationInLineReq req) {
        LineEntity.StationInLine entity = new LineEntity.StationInLine();
        entity.setStationId(req.getStationId());
        entity.setStationName(req.getStationName());
        entity.setSequence(req.getSequence());
        entity.setTimeFromPreviousStation(req.getTimeFromPreviousStation());
        entity.setLocation(req.getLocation());
        return entity;
    }
}