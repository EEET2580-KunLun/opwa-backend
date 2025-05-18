package eeet2580.kunlun.opwa.backend.auth.service;

import eeet2580.kunlun.opwa.backend.auth.dto.req.LoginReq;
import eeet2580.kunlun.opwa.backend.auth.dto.resp.TokenRes;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    TokenRes login(LoginReq req);

    TokenRes validate(StaffEntity staff);

    TokenRes refreshToken(String refreshToken);
}