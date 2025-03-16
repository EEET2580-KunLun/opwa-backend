package eeet2580.kunlun.opwa.backend.service;

import eeet2580.kunlun.opwa.backend.dto.req.CreateStaffDTO;
import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface StaffService extends UserDetailsService {
    StaffEntity createStaff(CreateStaffDTO dto);
}

