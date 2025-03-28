package eeet2580.kunlun.opwa.backend.auth.controller;

import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginDTO;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.ResponseDTO;
import eeet2580.kunlun.opwa.backend.staff.dto.StaffDTO;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<StaffEntity>> register(
            @RequestParam("token") String token,
            @Valid @RequestBody StaffDTO staffDto) {

        StaffEntity staff = authService.registerStaff(staffDto, token);
        ResponseDTO<StaffEntity> response = new ResponseDTO<>("200", "Account created successfully.", staff);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@Valid @RequestBody LoginDTO loginDto) {
        ResponseDTO<String> response = authService.login(loginDto);
        return ResponseEntity.status(response.getStatus().equals("200") ? 200 : 401).body(response);
    }

    // for testing authorization
    @GetMapping("/master-admin")
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    public ResponseEntity<String> masterAdminEndpoint() {
        return ResponseEntity.ok("Master Admin access");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Admin access");
    }

    @GetMapping("/operator")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<String> operatorEndpoint() {
        return ResponseEntity.ok("Operator access");
    }

    @GetMapping("/ticket-agent")
    @PreAuthorize("hasRole('TICKET_AGENT')")
    public ResponseEntity<String> ticketAgentEndpoint() {
        return ResponseEntity.ok("Ticket Agent access");
    }
}