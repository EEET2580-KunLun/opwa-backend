package eeet2580.kunlun.opwa.backend.auth.service;

import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginDTO;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.ResponseDTO;
import eeet2580.kunlun.opwa.backend.staff.dto.StaffDTO;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    StaffEntity registerStaff(StaffDTO dto, String token);

    StaffEntity findByEmail(String email);

    StaffEntity findByUsername(String username);

    String login(LoginDTO loginDto);
}