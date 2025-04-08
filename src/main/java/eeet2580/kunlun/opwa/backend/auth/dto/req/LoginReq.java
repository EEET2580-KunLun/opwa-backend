package eeet2580.kunlun.opwa.backend.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginReq {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}