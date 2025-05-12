package eeet2580.kunlun.opwa.backend.auth.controller;

import eeet2580.kunlun.opwa.backend.auth.config.JwtTokenUtil;
import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginReq;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        // The mere fact that this endpoint returns 200 OK means the token is valid
        // because the JwtRequestFilter will have already checked the token
        return ResponseEntity.ok(Map.of("valid", true));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseRes<StaffEntity>> register(
            @RequestParam("token") String token,
            @Valid @RequestBody StaffReq req) {

        StaffEntity staff = authService.registerStaff(req, token);
        BaseRes<StaffEntity> response = new BaseRes<>(HttpStatus.OK.value(), "Account created successfully.", staff);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseRes<TokenRes>> login(@Valid @RequestBody LoginReq req, HttpServletResponse response) {
        System.out.println("Login request received: " + req.getEmail());
        TokenRes tokens = authService.login(req);

        response.addCookie(jwtTokenUtil.getCookieFromToken("jwt_token", tokens));
        response.addCookie(jwtTokenUtil.getCookieFromToken("refresh_token", tokens));

        // Create a response object without the sensitive token information
        TokenRes safeResponse = new TokenRes();
        safeResponse.setStaff(tokens.getStaff());
        safeResponse.setExpiresIn(tokens.getExpiresIn());

        // Return proper BaseRes with safe response
        BaseRes<TokenRes> responseBody = new BaseRes<>(HttpStatus.OK.value(), "Login successful", safeResponse);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseRes<TokenRes>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("Refresh token request received");
            // Extract refresh token from cookie
            String refreshToken = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refresh_token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), "Refresh token not found", null));
            }

            // Process the refresh token and get new tokens
            TokenRes tokens = authService.refreshToken(refreshToken);

            response.addCookie(jwtTokenUtil.getCookieFromToken("jwt_token", tokens));
            response.addCookie(jwtTokenUtil.getCookieFromToken("refresh_token", tokens));

            // Return safe response (without exposing tokens)
            TokenRes safeResponse = new TokenRes();
            safeResponse.setStaff(tokens.getStaff());
            safeResponse.setExpiresIn(tokens.getExpiresIn());

            return ResponseEntity.ok(new BaseRes<>(
                    HttpStatus.OK.value(), "Token refreshed successfully", safeResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage() + "mf", null));
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

    // Modify later
    // Clear the cookie when logging out
    @PostMapping("/logout")
    public ResponseEntity<BaseRes<?>> logout(HttpServletResponse response) {
        // Create a cookie with the same name but zero max age to delete it
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // Clear refresh token cookie
        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        // Return proper BaseRes with safe response
        BaseRes<?> responseBody = new BaseRes<>(HttpStatus.OK.value(), "Logout successful",null);
        return ResponseEntity.ok(responseBody);
    }
}