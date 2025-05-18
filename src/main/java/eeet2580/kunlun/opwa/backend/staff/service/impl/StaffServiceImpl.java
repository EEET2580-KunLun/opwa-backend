package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.common.dto.resp.PagedResponse;
import eeet2580.kunlun.opwa.backend.staff.dto.mapper.StaffMapper;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReqForUpdating;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadIdRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.staff.service.PictureService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final PictureService pictureService;
    private final StaffMapper staffMapper;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    public PagedResponse<StaffRes> getAllStaffs(int page, int size, String sortBy, String direction) {
//        Sort sort = Sort.by(direction.equalsIgnoreCase("ASC") ?
//                Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<StaffEntity> staffsPage = staffRepository.findAll(pageable);
//
//        List<StaffRes> content = staffMapper.toResList(staffsPage.getContent());
//
//        return new PagedResponse<>(
//                content,
//                staffsPage.getNumber(),
//                staffsPage.getSize(),
//                staffsPage.getTotalElements(),
//                staffsPage.getTotalPages(),
//                staffsPage.isLast()
//        );
//    }
    // This method is used to get all staffs with pagination and sorting
    @Override
    public PagedResponse<StaffRes> getAllStaffs(int page, int size, String sortBy, String direction, Boolean active) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("ASC") ?
                Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StaffEntity> staffsPage;

        if (active != null) {
            staffsPage = staffRepository.findByEmployed(active, pageable);
        } else {
            staffsPage = staffRepository.findAll(pageable);
        }

        List<StaffRes> content = staffMapper.toResList(staffsPage.getContent());

        return new PagedResponse<>(
                content,
                staffsPage.getNumber(),
                staffsPage.getSize(),
                staffsPage.getTotalElements(),
                staffsPage.getTotalPages(),
                staffsPage.isLast()
        );
    }

    @Override
    public Optional<StaffEntity> getStaffById(String id) {
        return staffRepository.findById(id);
    }

    @Override
    public StaffEntity createStaff(StaffReq req) {
        StaffEntity staff = staffMapper.fromReq(req);

        // Check for duplicate email
        if (staffRepository.findByEmail(staff.getEmail()).isPresent()) {
            throw new RuntimeException("Staff with email " + staff.getEmail() + " already exists");
        }

        // Encode password
        staff.setPassword(passwordEncoder.encode(staff.getPassword()));

        // Encrypt sensitive data
        if (staff.getNationalId() != null) {
            staff.setNationalId(encryptNationalId(staff.getNationalId()));
        }

        if (staff.getPhoneNumber() != null) {
            staff.setPhoneNumber(encryptPhoneNumber(staff.getPhoneNumber()));
        }

        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public StaffEntity createStaffWithImages(StaffReq req,
                                             MultipartFile profilePhoto,
                                             MultipartFile frontIdImage,
                                             MultipartFile backIdImage) throws IOException {
        try {
            // Validate file sizes
            validateFileSize(frontIdImage, "Front ID image");
            validateFileSize(backIdImage, "Back ID image");
            if (profilePhoto != null) {
                validateFileSize(profilePhoto, "Profile photo");
            }

            // Create staff first
            StaffEntity savedStaff = createStaff(req);

            // Upload images
            if (profilePhoto != null) {
                String avatarUrl = pictureService.uploadPicture(profilePhoto, savedStaff.getId());
                savedStaff.setAvatarUrl(avatarUrl);
            }

            String frontIdUrl = pictureService.uploadPicture(frontIdImage, savedStaff.getId());
            String backIdUrl = pictureService.uploadPicture(backIdImage, savedStaff.getId());

            savedStaff.setNationalIdFrontImage(frontIdUrl);
            savedStaff.setNationalIdBackImage(backIdUrl);

            // Save with image URLs
            return staffRepository.save(savedStaff);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process images during staff creation", e);
        }
    }

    @Override
    public StaffEntity updateStaff(String id,
                                   StaffReqForUpdating req,
                                   MultipartFile profilePhoto,
                                   MultipartFile frontIdImage,
                                   MultipartFile backIdImage) {

        try {
            // Create staff first
            StaffEntity updatedStaff = staffMapper.fromReq(req);

            if (frontIdImage != null && backIdImage != null) {
                uploadIdPictures(id, req.getEmail(), frontIdImage, backIdImage);
            }

            if (profilePhoto != null) {
                uploadAvatar(profilePhoto, id, req.getEmail());
            }

            StaffEntity existedStaff = staffRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Staff not found with: " + id));

            // Only update fields that are provided (not null)
            if (updatedStaff.getEmail() != null) {
                existedStaff.setEmail(updatedStaff.getEmail());
            }
            if (updatedStaff.getUsername() != null) {
                existedStaff.setUsername(updatedStaff.getUsername());
            }
            if (updatedStaff.getPassword() != null && !updatedStaff.getPassword().isEmpty()) {
                System.out.println("updating password");
                existedStaff.setPassword(passwordEncoder.encode(updatedStaff.getPassword()));
                existedStaff = staffRepository.save(existedStaff);
            }
            if (updatedStaff.getFirstName() != null) {
                existedStaff.setFirstName(updatedStaff.getFirstName());
            }
            if (updatedStaff.getMiddleName() != null) {
                existedStaff.setMiddleName(updatedStaff.getMiddleName());
            }
            if (updatedStaff.getLastName() != null) {
                existedStaff.setLastName(updatedStaff.getLastName());
            }
            if (updatedStaff.getDateOfBirth() != null) {
                existedStaff.setDateOfBirth(updatedStaff.getDateOfBirth());
            }
            if (updatedStaff.getRole() != null) {
                existedStaff.setRole(updatedStaff.getRole());
            }
            if (updatedStaff.getShift() != null) {
                existedStaff.setShift(updatedStaff.getShift());
            }
            // Handle boolean field - employed
            existedStaff.setEmployed(updatedStaff.isEmployed());

            if (updatedStaff.getResidenceAddressEntity() != null) {
                existedStaff.setResidenceAddressEntity(updatedStaff.getResidenceAddressEntity());
            }

            // Handle phone number specially
            if (updatedStaff.getPhoneNumber() != null) {
                if (updatedStaff.getPhoneNumber().startsWith("******")) {
                    // Phone number unchanged (still masked) - keep original
                } else {
                    // New phone number - encrypt it
                    existedStaff.setPhoneNumber(encryptPhoneNumber(updatedStaff.getPhoneNumber()));
                }
            }

            // Handle national ID specially
            if (updatedStaff.getNationalId() != null) {
                if (updatedStaff.getNationalId().startsWith("********")) {
                    // National ID unchanged (still masked) - keep original
                } else {
                    // New national ID - encrypt it
                    existedStaff.setNationalId(encryptNationalId(updatedStaff.getNationalId()));
                }
            }

            // Save with image URLs
            return staffRepository.save(existedStaff);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process images during staff creation", e);
        }
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
    @Transactional
    public String uploadAvatar(MultipartFile file, String staffId, String currentUserEmail) throws IOException {
        StaffEntity staff = getStaffById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Check if current user can update this staff's avatar
        if (!canModifyStaff(staff, currentUserEmail)) {
            throw new SecurityException("You don't have permission to modify this staff member");
        }

        String avatarUrl = pictureService.uploadPicture(file, staffId);
        pictureService.removePicture(staff.getAvatarUrl());
        staff.setAvatarUrl(avatarUrl);
        staffRepository.save(staff);

        return avatarUrl;
    }

    @Override
    @Transactional
    public void removeAvatar(String staffId, String currentUserEmail) {
        var targetStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

        // Check if current user can modify this staff's avatar
        if (!canModifyStaff(targetStaff, currentUserEmail)) {
            throw new SecurityException("You don't have permission to modify this staff member");
        }

        pictureService.removePicture(targetStaff.getAvatarUrl());
        targetStaff.setAvatarUrl(null);
        staffRepository.save(targetStaff);
    }

    @Override
    @Transactional
    public UploadIdRes uploadIdPictures(String staffId,
                                        String currentUserEmail,
                                        MultipartFile frontIdImage,
                                        MultipartFile backIdImage) throws IOException {

        var targetStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

        if (!canModifyStaff(targetStaff, currentUserEmail)) {
            throw new SecurityException("You don't have permission to modify this staff member");
        }

        try {
            // Validate file sizes
            validateFileSize(frontIdImage, "Front ID image");
            validateFileSize(backIdImage, "Back ID image");

            pictureService.removePicture(targetStaff.getNationalIdFrontImage());
            pictureService.removePicture(targetStaff.getNationalIdBackImage());

            String frontIdUrl = pictureService.uploadPicture(frontIdImage, targetStaff.getId());
            String backIdUrl = pictureService.uploadPicture(backIdImage, targetStaff.getId());

            targetStaff.setNationalIdFrontImage(frontIdUrl);
            targetStaff.setNationalIdBackImage(backIdUrl);

            // Save with image URLs
            staffRepository.save(targetStaff);

            return new UploadIdRes(frontIdUrl, backIdUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process images during staff creation", e);
        }
    }

    @Override
    @Transactional
    public void removeIdPictures(String staffId,
                                 String currentUserEmail) {

        var targetStaff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

        if (!canModifyStaff(targetStaff, currentUserEmail)) {
            throw new SecurityException("You don't have permission to modify this staff member");
        }

        pictureService.removePicture(targetStaff.getNationalIdFrontImage());
        pictureService.removePicture(targetStaff.getNationalIdBackImage());

        targetStaff.setNationalIdFrontImage(null);
        targetStaff.setNationalIdBackImage(null);
    }

    private void validateFileSize(MultipartFile file, String fileType) {
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException(fileType + " must be less than 5MB");
        }
    }

    /**
     * A staff member can modify their own data
     * Admins and Master Admins can modify any staff member's data
     * Other roles have restrictions
     */
    private boolean canModifyStaff(StaffEntity staff, String currentUserEmail) {
        // Admin or the staff member themselves can modify
        return currentUserEmail.equals(staff.getEmail()) ||
                staffRepository.findByEmail(currentUserEmail)
                        .map(currentUser -> currentUser.getRole() == StaffEntity.Role.ADMIN ||
                                currentUser.getRole() == StaffEntity.Role.MASTER_ADMIN)
                        .orElse(false);
    }

    private String encryptNationalId(String nationalId) {
        // Using Base64 for demo. In production, use AES encryption
        return Base64.getEncoder().encodeToString(nationalId.getBytes());
    }

    private String encryptPhoneNumber(String phoneNumber) {
        // Using Base64 for demo. In production, use AES encryption
        return Base64.getEncoder().encodeToString(phoneNumber.getBytes());
    }
}
