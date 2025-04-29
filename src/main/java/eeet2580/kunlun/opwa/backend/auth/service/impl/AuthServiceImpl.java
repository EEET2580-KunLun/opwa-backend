package eeet2580.kunlun.opwa.backend.auth.service.impl;

import eeet2580.kunlun.opwa.backend.auth.config.JwtTokenUtil;
import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginReq;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.staff.dto.mapper.StaffMapper;
import eeet2580.kunlun.opwa.backend.staff.dto.req.StaffReq;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final StaffRepository staffRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        StaffEntity staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        return new org.springframework.security.core.userdetails.User(
                staff.getEmail(),
                staff.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole())));
    }

    @Override
    public StaffEntity registerStaff(StaffReq req, String token) {
        // Check if email already exists
        if (staffRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if username already exists
        if (staffRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        StaffEntity staff = new StaffEntity();
        staff.setEmail(req.getEmail());
        staff.setUsername(req.getUsername());
        staff.setPassword(passwordEncoder.encode(req.getPassword()));

        staff.setFirstName(req.getFirstName());
        staff.setMiddleName(req.getMiddleName());
        staff.setLastName(req.getLastName());
        staff.setNationalId(req.getNationalId());
        staff.setPhoneNumber(req.getPhoneNumber());
        staff.setDateOfBirth(req.getDateOfBirth());
        staff.setEmployed(req.isEmployed());
        staff.setRole(req.getRole());
        staff.setShift(req.getShift());
        staff.setResidenceAddressEntity(req.getAddress());

        return staffRepository.save(staff);
    }

    @Override
    public StaffEntity findByEmail(String email) {
        return staffRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

    @Override
    public StaffEntity findByUsername(String username) {
        return staffRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    // Validates user credentials
    @Override
    public TokenRes login(LoginReq req) {
        try {
            StaffEntity staff = staffRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + req.getEmail()));

            if (!passwordEncoder.matches(req.getPassword(), staff.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            return buildTokenResFromStaff(staff);
        } catch (WeakKeyException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    @Override
    public TokenRes refreshToken(String refreshToken) {
        StaffEntity staff = staffRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (staff.getRefreshTokenExpiry().before(new Date())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        return buildTokenResFromStaff(staff);
    }

    // Generate JWT tokens and create refresh tokens
    private TokenRes buildTokenResFromStaff(StaffEntity staff) {
        String accessToken = jwtTokenUtil.generateToken(staff);
        String refreshToken = jwtTokenUtil.generateRefreshToken();

        StaffMapper staffMapper = new StaffMapper();
        StaffRes staffRes = staffMapper.toDto(staff);

        // Update refresh token in database
        Date refreshTokenExpiry = jwtTokenUtil.getRefreshTokenExpiry();
        staff.setRefreshToken(refreshToken);
        staff.setRefreshTokenExpiry(refreshTokenExpiry);
        staffRepository.save(staff);

        // Create token response with the tokens for cookie creation
        // but not for including in the response body sent to client
        TokenRes tokenResponse = new TokenRes();
        tokenResponse.setStaff(staffRes);
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiresIn(jwtTokenUtil.getExpiration() / 1000);

        return tokenResponse;
    }
}