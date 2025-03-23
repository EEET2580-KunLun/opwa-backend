package eeet2580.kunlun.opwa.backend.service;

import eeet2580.kunlun.opwa.backend.dto.req.LoginDTO;
import eeet2580.kunlun.opwa.backend.dto.req.StaffDTO;
import eeet2580.kunlun.opwa.backend.dto.resp.ResponseDTO;
import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    StaffEntity registerStaff(StaffDTO dto);
    StaffEntity findByEmail(String email);
    StaffEntity findByUsername(String username);
    ResponseDTO<String> login(LoginDTO loginDto);
}