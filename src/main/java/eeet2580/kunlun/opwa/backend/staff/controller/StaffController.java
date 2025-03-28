package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffInviteService staffInviteService;

    @GetMapping
    public ResponseEntity<List<StaffEntity>> getAllStaff() {
        List<StaffEntity> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffEntity> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id)
                .map(staff -> {
                    return ResponseEntity.ok(staff);
                })
                .orElseGet(() -> {
                    return ResponseEntity.status(404).build();
                });
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
}
