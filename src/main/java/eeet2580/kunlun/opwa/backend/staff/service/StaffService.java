package eeet2580.kunlun.opwa.backend.staff.service;

import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadIdRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StaffService {
    List<StaffEntity> getAllStaff();

    Optional<StaffEntity> getStaffById(String id);

    StaffEntity createStaff(StaffEntity staff); // use the createStaffWithImages instead

    StaffEntity updateStaff(String id, StaffEntity updatedStaff);

    void deleteStaff(String id);

    Optional<StaffEntity> getStaffByEmail(String email);

    String uploadAvatar(MultipartFile file, String staffId, String currentUserEmail) throws IOException;

    void removeAvatar(String staffId, String currentUserEmail);

    StaffEntity createStaffWithImages(StaffEntity staff,
                                      MultipartFile profilePhoto,
                                      MultipartFile frontIdImage,
                                      MultipartFile backIdImage);

    UploadIdRes uploadIdPictures(String staffId,
                                 String currentUserEmail,
                                 MultipartFile frontIdImage,
                                 MultipartFile backIdImage);

    void removeIdPictures(String staffId,
                          String currentUserEmail);
}
