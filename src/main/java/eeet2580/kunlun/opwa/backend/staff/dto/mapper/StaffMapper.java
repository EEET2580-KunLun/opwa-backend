package eeet2580.kunlun.opwa.backend.staff.dto.mapper;

import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StaffMapper {

    public StaffRes toRes(StaffEntity entity) {
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
        dto.setNationalId(maskNationalId(entity.getNationalId()));
        dto.setRole(entity.getRole());
        dto.setResidenceAddressEntity(entity.getResidenceAddressEntity());
        dto.setPhoneNumber(maskPhoneNumber(entity.getPhoneNumber()));
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setEmployed(entity.isEmployed());
        dto.setShift(entity.getShift());
        dto.setProfilePicture(entity.getAvatarUrl());
        dto.setNationalIdFrontPicture(entity.getNationalIdFrontImage());
        dto.setNationalIdBackPicture(entity.getNationalIdBackImage());

        return dto;
    }

    public List<StaffRes> toResList(List<StaffEntity> entities) {
        return entities.stream()
                .map(this::toRes)
                .collect(Collectors.toList());
    }

    public StaffEntity fromReq(StaffReq req) {
        if (req == null) {
            return null;
        }

        StaffEntity entity = new StaffEntity();
        entity.setUsername(req.getUsername());
        entity.setEmail(req.getEmail());
        entity.setFirstName(req.getFirstName());
        entity.setMiddleName(req.getMiddleName());
        entity.setLastName(req.getLastName());
        entity.setNationalId(req.getNationalId());
        entity.setPhoneNumber(req.getPhoneNumber());
        entity.setDateOfBirth(req.getDateOfBirth());
        entity.setEmployed(req.isEmployed());
        entity.setRole(req.getRole());
        entity.setShift(req.getShift());
        entity.setResidenceAddressEntity(req.getAddress());

        return entity;
    }

    private String maskNationalId(String encryptedNationalId) {
        try {
            String decrypted = new String(Base64.getDecoder().decode(encryptedNationalId));
            // Show only last 4 digits
            return "********" + decrypted.substring(8);
        } catch (Exception e) {
            return "************";
        }
    }

    private String maskPhoneNumber(String encryptedPhoneNumber) {
        try {
            String decrypted = new String(Base64.getDecoder().decode(encryptedPhoneNumber));
            // Show only last 4 digits
            return "******" + decrypted.substring(6);
        } catch (Exception e) {
            return "**********";
        }
    }
}