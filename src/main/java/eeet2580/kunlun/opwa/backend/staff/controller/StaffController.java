package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.staff.dto.mapper.StaffMapper;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.InviteLinkRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadAvatarRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadIdRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/staffs")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final StaffInviteService staffInviteService;

    @GetMapping
    public ResponseEntity<BaseRes<List<StaffRes>>> getAllStaff() {
        List<StaffEntity> staffList = staffService.getAllStaff();
        List<StaffRes> staffDtoList = staffMapper.toResList(staffList);
        BaseRes<List<StaffRes>> response = new BaseRes<>(HttpStatus.OK.value(), "Staff list retrieved successfully", staffDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<StaffRes>> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id)
                .map(staff -> {
                    StaffRes staffDto = staffMapper.toRes(staff);
                    BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.OK.value(), "Staff retrieved successfully", staffDto);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Staff not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<BaseRes<StaffRes>> createStaff(
            @ModelAttribute @Valid StaffReq request) {

        // Create StaffEntity from the request
        StaffEntity staff = staffMapper.fromReq(request);
        StaffEntity createdStaff = staffService.createStaff(staff);
        StaffRes staffDto = staffMapper.toRes(createdStaff);
        BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.CREATED.value(), "Staff created successfully", staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/with-pictures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseRes<StaffRes>> createStaffWithPictures(
            @ModelAttribute @Valid StaffReq request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "frontIdPicture") MultipartFile frontIdPicture,
            @RequestPart(value = "backIdPicture") MultipartFile backIdPicture
    ) {

        // Create StaffEntity from the request
        StaffEntity staff = staffMapper.fromReq(request);
        StaffEntity createdStaff = staffService.createStaffWithImages(staff, profilePicture, frontIdPicture, backIdPicture);
        StaffRes staffDto = staffMapper.toRes(createdStaff);
        BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.CREATED.value(), "Staff created successfully", staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<StaffRes>> updateStaff(@PathVariable String id, @RequestBody StaffEntity staff) {
        try {
            StaffEntity updatedStaff = staffService.updateStaff(id, staff);
            StaffRes staffDto = staffMapper.toRes(updatedStaff);
            BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.OK.value(), "Staff updated successfully", staffDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Staff not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteStaff(@PathVariable String id) {
        if (staffService.getStaffById(id).isEmpty()) {
            BaseRes<Void> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Staff not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        staffService.deleteStaff(id);
        BaseRes<Void> response = new BaseRes<>(HttpStatus.NO_CONTENT.value(), "Staff deleted successfully", null);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @PostMapping("/invite")
    public ResponseEntity<BaseRes<InviteLinkRes>> inviteStaff(HttpServletRequest request) {
        String token = staffInviteService.generateInvite();
        String baseUrl = request.getScheme()
                + "://"
                + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                ? ":" + request.getServerPort()
                : "");
        String link = baseUrl + "/auth/register?token=" + token;
        InviteLinkRes inviteLinkRes = new InviteLinkRes(link);
        BaseRes<InviteLinkRes> response = new BaseRes<>(HttpStatus.OK.value(), "Invitation generated successfully", inviteLinkRes);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{staffId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseRes<UploadAvatarRes>> uploadAvatar(
            @PathVariable String staffId,
            @RequestPart(value = "profilePicture") MultipartFile profilePicture,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            String avatarUrl = staffService.uploadAvatar(profilePicture, staffId, currentUserEmail);

            UploadAvatarRes avatarUrlRes = new UploadAvatarRes(avatarUrl);
            BaseRes<UploadAvatarRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Avatar uploaded successfully", avatarUrlRes);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            BaseRes<UploadAvatarRes> response = new BaseRes<>(
                    HttpStatus.BAD_REQUEST.value(), "Failed to upload avatar: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (SecurityException e) {
            BaseRes<UploadAvatarRes> response = new BaseRes<>(
                    HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (RuntimeException e) {
            BaseRes<UploadAvatarRes> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{staffId}/avatar")
    public ResponseEntity<BaseRes<Void>> removeAvatar(
            @PathVariable String staffId,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            staffService.removeAvatar(staffId, currentUserEmail);

            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Avatar removed successfully", null);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (RuntimeException e) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping(path = "/{staffId}/national-id", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseRes<UploadIdRes>> uploadIdPictures(
            @PathVariable String staffId,
            @RequestPart(value = "frontIdPicture") MultipartFile frontIdPicture,
            @RequestPart(value = "backIdPicture") MultipartFile backIdPicture,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            UploadIdRes res = staffService.uploadIdPictures(staffId, currentUserEmail, frontIdPicture, backIdPicture);
            BaseRes<UploadIdRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "ID pictures uploaded successfully", res);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            BaseRes<UploadIdRes> response = new BaseRes<>(
                    HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (RuntimeException e) {
            BaseRes<UploadIdRes> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{staffId}/national-id")
    public ResponseEntity<BaseRes<Void>> removeIdPictures(
            @PathVariable String staffId,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            staffService.removeIdPictures(staffId, currentUserEmail);

            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.OK.value(), "ID pictures removed successfully", null);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.FORBIDDEN.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (RuntimeException e) {
            BaseRes<Void> response = new BaseRes<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}