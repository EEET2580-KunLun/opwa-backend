package eeet2580.kunlun.opwa.backend.staff.controller;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity.Role;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffInviteService staffInviteService;

    @GetMapping
    public List<StaffEntity> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffEntity> getStaffById(@PathVariable String id) {
        return staffService.getStaffById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public StaffEntity createStaff(@RequestBody StaffEntity staff) {
        return staffService.createStaff(staff);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffEntity> updateStaff(@PathVariable String id, @RequestBody StaffEntity staff) {
        try {
            return ResponseEntity.ok(staffService.updateStaff(id, staff));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable String id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @PostMapping("/invite")
    public ResponseEntity<String> inviteStaff(
            @RequestParam Role role,
            HttpServletRequest request) {

        String token = staffInviteService.generateInvite(role);

        String baseUrl = request.getScheme()
                + "://"
                + request.getServerName()
                + (request.getServerPort() != 80 && request.getServerPort() != 443
                        ? ":" + request.getServerPort()
                        : "");

        String link = baseUrl + "/auth/register?token=" + token;

        return ResponseEntity.ok("Invite link: " + link);
    }
}
