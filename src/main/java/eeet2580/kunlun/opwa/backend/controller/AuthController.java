package eeet2580.kunlun.opwa.backend.controller;

import eeet2580.kunlun.opwa.backend.dto.req.CreateStaffDTO;
import eeet2580.kunlun.opwa.backend.dto.resp.ResponseDTO;
import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StaffService staffService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<StaffEntity>> register(@Valid @RequestBody CreateStaffDTO staffDto) {
        StaffEntity staff = staffService.createStaff(staffDto);
        ResponseDTO<StaffEntity> response = new ResponseDTO<>("200", "Account created successfully.", staff);
        return ResponseEntity.ok(response);
    }
}

