package eeet2580.kunlun.opwa.backend.staff.service;

import eeet2580.kunlun.opwa.backend.common.dto.resp.PagedResponse;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReqForUpdating;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadIdRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface StaffService {
    PagedResponse<StaffRes> getAllStaffs(int page, int size, String sortBy, String direction);

    Optional<StaffEntity> getStaffById(String id);

    StaffEntity createStaff(StaffReq req);

    StaffEntity updateStaff(String id,
                            StaffReqForUpdating req,
                            MultipartFile profilePhoto,
                            MultipartFile frontIdImage,
                            MultipartFile backIdImage);

    void deleteStaff(String id);

    Optional<StaffEntity> getStaffByEmail(String email);

    String uploadAvatar(MultipartFile file, String staffId, String currentUserEmail) throws IOException;

    void removeAvatar(String staffId, String currentUserEmail);

    StaffEntity createStaffWithImages(StaffReq req,
                                      MultipartFile profilePhoto,
                                      MultipartFile frontIdImage,
                                      MultipartFile backIdImage) throws IOException;

    UploadIdRes uploadIdPictures(String staffId,
                                 String currentUserEmail,
                                 MultipartFile frontIdImage,
                                 MultipartFile backIdImage) throws IOException;

    void removeIdPictures(String staffId,
                          String currentUserEmail);
}
