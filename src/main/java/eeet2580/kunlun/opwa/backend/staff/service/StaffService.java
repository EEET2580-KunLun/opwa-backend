package eeet2580.kunlun.opwa.backend.staff.service;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;

import java.util.List;
import java.util.Optional;

public interface StaffService {
    List<StaffEntity> getAllStaff();

    Optional<StaffEntity> getStaffById(String id);

    StaffEntity createStaff(StaffEntity staff);

    StaffEntity updateStaff(String id, StaffEntity updatedStaff);

    void deleteStaff(String id);
}
