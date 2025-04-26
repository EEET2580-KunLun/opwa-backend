package eeet2580.kunlun.opwa.backend.auth.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import eeet2580.kunlun.opwa.backend.staff.dto.resp.StaffRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenRes {
    private StaffRes staff;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}