package eeet2580.kunlun.opwa.backend.auth.controller;

import eeet2580.kunlun.opwa.backend.auth.config.JwtTokenUtil;
import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginReq;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.staff.dto.mapper.StaffMapper;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import eeet2580.kunlun.opwa.backend.staff.service.StaffService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final StaffInviteService staffInviteService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<BaseRes<StaffRes>> register(
            @RequestParam(value = "token", required = false) String token,
            @Valid @RequestBody StaffReq req) {

        if (token == null || token.isEmpty() || staffInviteService.getInvite(token).isEmpty()) {
            BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.BAD_REQUEST.value(),
                    "You need a valid token to create staff", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        StaffEntity staff = staffService.createStaff(req);
        staffInviteService.deleteInvite(token);
        StaffRes staffDto = staffMapper.toRes(staff);

        BaseRes<StaffRes> response = new BaseRes<>(HttpStatus.OK.value(), "Account created successfully.", staffDto);
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
        BaseRes<?> responseBody = new BaseRes<>(HttpStatus.OK.value(), "Logout successful", null);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/validate")
    public ResponseEntity<BaseRes<TokenRes>> validateToken(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Validate token request received");
        // Extract JWT token from cookie
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        // If no token found, return unauthorized
        if (token == null) {
            System.out.println("Validate token request exception: " + "JWT token not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), "JWT token not found", null));
        }

        try {
            // Extract username from token (email in your case)
            String email = jwtTokenUtil.getEmailFromToken(token);

            // Get staff entity from service
            StaffEntity staff = staffService.getStaffByEmail(email).orElseThrow();

            // Generate new tokens
            TokenRes tokens = authService.validate(staff);
            // Add cookies
            response.addCookie(jwtTokenUtil.getCookieFromToken("jwt_token", tokens));
            response.addCookie(jwtTokenUtil.getCookieFromToken("refresh_token", tokens));

            // Create a safe response (without exposing tokens)
            TokenRes safeResponse = new TokenRes();
            safeResponse.setStaff(tokens.getStaff());
            safeResponse.setExpiresIn(tokens.getExpiresIn());

            return ResponseEntity.ok(new BaseRes<>(
                    HttpStatus.OK.value(), "Token is valid", safeResponse));
        } catch (Exception e) {
            System.out.println("Validate token request exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
        }
    }

    @GetMapping("/invite/{token}")
    public ResponseEntity<BaseRes<Map<String, Boolean>>> getInviteByToken(@PathVariable String token) {
        boolean isValid = staffInviteService.isInviteValid(token);

        Map<String, Boolean> result = new HashMap<>();
        result.put("is_valid", isValid);

        BaseRes<Map<String, Boolean>> response = new BaseRes<>(
                HttpStatus.OK.value(),
                isValid ? "Token is valid" : "Token is invalid",
                result);

        return ResponseEntity.ok(response);
    }
}