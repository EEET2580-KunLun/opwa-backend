package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.service.PictureService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final StaffInviteService staffInviteService;
    private final PictureService pictureService;

    @GetMapping
    public ResponseEntity<List<StaffEntity>> getAllStaff() {
        List<StaffEntity> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffEntity> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id)
                .map(staff -> ResponseEntity.ok(staff))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody StaffEntity staff) {
        StaffEntity createdStaff = staffService.createStaff(staff);
        return ResponseEntity.status(201).body(createdStaff);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, @RequestBody StaffEntity staff) {
        try {
            StaffEntity updatedStaff = staffService.updateStaff(id, staff);
            System.out.println("Updated staff " + id);
            return ResponseEntity.ok(updatedStaff);
        } catch (RuntimeException e) {
            System.out.println("Failed to update: " + id + ". Staff not found.");
            return ResponseEntity.status(404).body(Map.of("error", "Failed to update " + id + ". Staff not found."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id) {
        if (!staffService.getStaffById(id).isPresent()) {
            System.out.println("Failed to delete " + id + ". Staff not found.");
            return ResponseEntity.status(404).body(Map.of("error", "Failed to delete " + id + ". Staff not found."));
        }
        staffService.deleteStaff(id);
        System.out.println("Deleted staff with ID: " + id);
        return ResponseEntity.status(204).body(Map.of("message", "Deleted staff with ID: " + id));
    }

    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @PostMapping("/invite")
    public ResponseEntity<Map<String, String>> inviteStaff(HttpServletRequest request) {
        String token = staffInviteService.generateInvite();
        String baseUrl = request.getScheme()
                + "://"
                + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                ? ":" + request.getServerPort()
                : "");
        String link = baseUrl + "/auth/register?token=" + token;
        return ResponseEntity.ok(Map.of("inviteLink", link));
    }

    @PostMapping("/avatar")
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
            String avatarUrl = pictureService.uploadPicture(file, staff.getId());
            staff.setAvatarUrl(avatarUrl);
            staffService.updateStaff(staff.getId(), staff);

            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload avatar: " + e.getMessage()));
        }
    }
}
