package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.staff.dto.mapper.StaffMapper;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.InviteLinkRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.UploadAvatarRes;
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
import org.springframework.web.bind.annotation.*;
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
        List<StaffRes> staffDtoList = staffMapper.toDtoList(staffList);
        BaseRes<List<StaffRes>> response = new BaseRes<>(HttpStatus.OK.value(), "Staff list retrieved successfully", staffDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseRes<StaffRes>> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id)
                .map(staff -> {
                    StaffRes staffDto = staffMapper.toDto(staff);
                    BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.OK.value(), "Staff retrieved successfully", staffDto);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Staff not found", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseRes<StaffRes>> createStaff(
            @ModelAttribute @Valid StaffReq request,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "frontIdImage", required = true) MultipartFile frontIdImage,
            @RequestPart(value = "backIdImage", required = true) MultipartFile backIdImage) {

        // Create StaffEntity from the request
        StaffEntity staff = new StaffEntity();
        staff.setEmail(request.getEmail());
        staff.setUsername(request.getUsername());
        staff.setPassword(request.getPassword());
        staff.setFirstName(request.getFirstName());
        staff.setMiddleName(request.getMiddleName());
        staff.setLastName(request.getLastName());
        staff.setNationalId(request.getNationalId());
        staff.setRole(request.getRole());
        staff.setResidenceAddressEntity(request.getAddress());
        staff.setPhoneNumber(request.getPhoneNumber());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setEmployed(request.isEmployed());
        staff.setShift(request.getShift());

        // Create staff with images
        //TODO: the createStaffWithImages has not been implemented in the staffServiceImpl yet
        StaffEntity createdStaff = staffService.createStaffWithImages(
                staff, profilePhoto, frontIdImage, backIdImage);
        StaffRes staffDto = staffMapper.toDto(createdStaff);
        BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.CREATED.value(), "Staff created successfully", staffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseRes<StaffRes>> updateStaff(@PathVariable String id, @RequestBody StaffEntity staff) {
        try {
            StaffEntity updatedStaff = staffService.updateStaff(id, staff);
            StaffRes staffDto = staffMapper.toDto(updatedStaff);
            BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.OK.value(), "Staff updated successfully", staffDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.NOT_FOUND.value(), "Staff not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseRes<Void>> deleteStaff(@PathVariable String id) {
        if (!staffService.getStaffById(id).isPresent()) {
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

    @PostMapping("/{staffId}/avatar")
    public ResponseEntity<BaseRes<UploadAvatarRes>> uploadAvatar(
            @PathVariable String staffId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            String avatarUrl = staffService.uploadAvatar(file, staffId, currentUserEmail);

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
}