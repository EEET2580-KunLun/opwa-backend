package eeet2580.kunlun.opwa.backend.service.impl;

import eeet2580.kunlun.opwa.backend.dto.req.CreateStaffDTO;
import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.repository.StaffRepository;
import eeet2580.kunlun.opwa.backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

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
    public StaffEntity createStaff(CreateStaffDTO dto) {
        StaffEntity staff = new StaffEntity();
        staff.setEmail(dto.getEmail());
        staff.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        // Set other fields
        return staffRepository.save(staff);
    }

}

