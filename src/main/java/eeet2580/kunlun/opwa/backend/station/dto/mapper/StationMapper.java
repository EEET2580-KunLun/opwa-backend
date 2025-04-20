package eeet2580.kunlun.opwa.backend.station.dto.mapper;

import eeet2580.kunlun.opwa.backend.station.dto.req.StationReq;
import eeet2580.kunlun.opwa.backend.station.dto.resp.StationRes;
import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StationMapper {

    public StationRes toDto(StationEntity entity) {
        if (entity == null) {
            return null;
        }

        StationRes dto = new StationRes();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setActive(entity.isActive());
        dto.setLocation(entity.getLocation());

        return dto;
    }

    public List<StationRes> toDtoList(List<StationEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StationEntity toEntity(StationReq req) {
        if (req == null) {
            return null;
        }

        StationEntity entity = new StationEntity();
        entity.setName(req.getName());
        entity.setAddress(req.getAddress());
        entity.setActive(req.isActive());
        entity.setLocation(req.getLocation());

        return entity;
    }
}