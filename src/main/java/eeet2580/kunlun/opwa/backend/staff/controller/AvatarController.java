package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.service.AvatarService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;
    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<StaffEntity> staffOptional = staffService.getStaffByEmail(email);

            if (staffOptional.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Staff not found"));
            }

            StaffEntity staff = staffOptional.get();
            String avatarUrl = avatarService.uploadAvatar(file, staff.getId());
            staff.setAvatarUrl(avatarUrl);
            staffService.updateStaff(staff.getId(), staff);

            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload avatar: " + e.getMessage()));
        }
    }
}
