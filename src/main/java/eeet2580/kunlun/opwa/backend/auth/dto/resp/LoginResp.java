package eeet2580.kunlun.opwa.backend.auth.dto.resp;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;


// Model class for login response

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {
    private boolean success;
    private String token;
    private String role;
    private StaffRes user;
    private String message;
}
