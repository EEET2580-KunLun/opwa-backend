package eeet2580.kunlun.opwa.backend.service.impl;

import eeet2580.kunlun.opwa.backend.config.JwtTokenUtil;
import eeet2580.kunlun.opwa.backend.dto.req.LoginDTO;
import eeet2580.kunlun.opwa.backend.dto.req.StaffDTO;
import eeet2580.kunlun.opwa.backend.dto.resp.ResponseDTO;
import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole()))
        );
    }

    @Override
    public StaffEntity registerStaff(StaffDTO dto) {
        // Check if email already exists
        if (staffRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if username already exists
        if (staffRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        StaffEntity staff = new StaffEntity();
        staff.setEmail(dto.getEmail());
        staff.setUsername(dto.getUsername());
        staff.setPassword(passwordEncoder.encode(dto.getPassword()));

        staff.setFirstName(dto.getFirstName());
        staff.setMiddleName(dto.getMiddleName());
        staff.setLastName(dto.getLastName());
        staff.setNationalId(dto.getNationalId());
        staff.setPhoneNumber(dto.getPhoneNumber());
        staff.setDateOfBirth(dto.getDateOfBirth());
        staff.setEmployed(dto.isEmployed());
        staff.setRole(dto.getRole());
        staff.setShift(dto.getShift());
        staff.setResidenceAddressEntity(dto.getAddress());

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

    @Override
    public ResponseDTO<String> login(LoginDTO loginDto) {
        try {
            StaffEntity staff = staffRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

            System.out.println("User found: " + staff.getEmail());
            System.out.println("Hashed password in DB: " + staff.getPassword());

            boolean matches = passwordEncoder.matches(loginDto.getPassword(), staff.getPassword());
            System.out.println("Password matches: " + matches);

            if (!matches) {
                return new ResponseDTO<>("401", "Invalid email or password", null);
            }

            String token = jwtTokenUtil.generateToken(staff);
            return new ResponseDTO<>("200", "Login successful", token);
        } catch (Exception e) {
            return new ResponseDTO<>("401", "Invalid email or password", null);
        }
    }
}