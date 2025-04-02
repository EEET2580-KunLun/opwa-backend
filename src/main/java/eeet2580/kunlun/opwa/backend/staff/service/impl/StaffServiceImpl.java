package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public List<StaffEntity> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public Optional<StaffEntity> getStaffById(String id) {
        return staffRepository.findById(id);
    }

    @Override
    public StaffEntity createStaff(StaffEntity staff) {
        return staffRepository.save(staff);
    }

    @Override
    public StaffEntity updateStaff(String id, StaffEntity updatedStaff) {
        if (staffRepository.existsById(id)) {
            updatedStaff.setId(id);
            return staffRepository.save(updatedStaff);
        }
        throw new RuntimeException("Staff not found with: " + id);
    }

    @Override
    public void deleteStaff(String id) {
        staffRepository.deleteById(id);
    }

    @Override
    public Optional<StaffEntity> getStaffByEmail(String email) {
        return staffRepository.findByEmail(email);
    }
}
