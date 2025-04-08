package eeet2580.kunlun.opwa.backend.staff.dto.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import eeet2580.kunlun.opwa.backend.staff.model.AddressEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffRes {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nationalId;
    private StaffEntity.Role role;
    private AddressEntity residenceAddressEntity;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private boolean employed;
    private String shift;
    private String avatarUrl;
}