package eeet2580.kunlun.opwa.backend.staff.dto.mapper;

import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StaffMapper {

    public StaffRes toDto(StaffEntity entity) {
        if (entity == null) {
            return null;
        }

        StaffRes dto = new StaffRes();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setNationalId(entity.getNationalId());
        dto.setRole(entity.getRole());
        dto.setResidenceAddressEntity(entity.getResidenceAddressEntity());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setEmployed(entity.isEmployed());
        dto.setShift(entity.getShift());
        dto.setAvatarUrl(entity.getAvatarUrl());

        return dto;
    }

    public List<StaffRes> toDtoList(List<StaffEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}