package eeet2580.kunlun.opwa.backend.auth.config;

import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    /**
     * Use the Keys.hmacShaKeyFor() from the JJWT library to create a HMAC-SHA key
     * */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate tokens for staff members
    public String generateToken(StaffEntity staff) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", staff.getRole().name());
        return Jwts.builder()
                .claims(claims) // claims store info about authenticated user
                .subject(staff.getEmail()) // set to the staff member's email address
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey()) // create signature
                .compact();
    }

    public String generateRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public Date getRefreshTokenExpiry() {
        return new Date(System.currentTimeMillis() + refreshExpiration);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token).getExpiration().before(new Date());
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    // Set refresh/access token in http-only cookie
    public Cookie getCookieFromToken(String type, TokenRes tokenRes) {
        Cookie cookie;
        switch (type) {
            case "jwt_token" -> {
                cookie = new Cookie(type, tokenRes.getAccessToken());
                cookie.setMaxAge((int) (tokenRes.getExpiresIn()));
            }
            case "refresh_token" -> {
                cookie = new Cookie(type, tokenRes.getRefreshToken());
                cookie.setMaxAge((int) (getRefreshTokenExpiry().getTime() - System.currentTimeMillis()) / 1000);
            }
            default -> throw new IllegalArgumentException("Invalid cookie type: " + type);
        }
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }
}