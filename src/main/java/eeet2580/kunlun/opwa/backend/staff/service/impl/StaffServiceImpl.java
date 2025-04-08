package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.staff.service.PictureService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final PictureService pictureService;

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

    @Override
    public String uploadAvatar(MultipartFile file, String email) throws IOException {
        var staffOptional = getStaffByEmail(email);

        if (staffOptional.isEmpty()) {
            throw new RuntimeException("Staff not found with email: " + email);
        }

        StaffEntity staff = staffOptional.get();
        String avatarUrl = pictureService.uploadPicture(file, staff.getId());
        staff.setAvatarUrl(avatarUrl);
        updateStaff(staff.getId(), staff);

        return avatarUrl;
    }
}
