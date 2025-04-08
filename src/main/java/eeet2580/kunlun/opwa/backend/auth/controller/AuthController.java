package eeet2580.kunlun.opwa.backend.auth.controller;

import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginReq;
import eeet2580.kunlun.opwa.backend.auth.dto.req.RefreshTokenReq;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseRes<StaffEntity>> register(
            @RequestParam("token") String token,
            @Valid @RequestBody StaffReq req) {

        StaffEntity staff = authService.registerStaff(req, token);
        BaseRes<StaffEntity> response = new BaseRes<>(HttpStatus.OK.value(), "Account created successfully.", staff);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseRes<TokenRes>> login(@Valid @RequestBody LoginReq req) {
        TokenRes token = authService.login(req);
        BaseRes<TokenRes> response = new BaseRes<>(HttpStatus.OK.value(), "Login successful", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseRes<TokenRes>> refreshToken(
            @Valid @RequestBody RefreshTokenReq req) {
        try {
            TokenRes tokens = authService.refreshToken(req.getRefreshToken());
            BaseRes<TokenRes> response = new BaseRes<>(
                    HttpStatus.OK.value(), "Token refreshed successfully", tokens);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            BaseRes<TokenRes> response = new BaseRes<>(
                    HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
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