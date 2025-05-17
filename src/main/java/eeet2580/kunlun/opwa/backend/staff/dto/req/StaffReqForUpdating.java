package eeet2580.kunlun.opwa.backend.staff.dto.req;

import eeet2580.kunlun.opwa.backend.staff.model.AddressEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class StaffReqForUpdating {
    @Email(regexp = "^[^\\s@]+@[\\w]+\\.(com|vn)$", message = "Invalid email. Must end with '.com' or '.vn'.")
    private String email;

    @Nullable
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!.]).{8,}", message = "Password must contain uppercase, lowercase, digit, special character, and minimum 8 characters.")
    private String password;

    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]{1,50}$", message = "Name must only contain alphabetic characters.")
    private String firstName;

    @Nullable
    @Pattern(regexp = "^$|^[a-zA-ZÀ-ỹ\\s]{1,50}$", message = "Invalid middle name.")
    private String middleName;

    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]{1,50}$", message = "Invalid last name.")
    private String lastName;

    @Pattern(regexp = "^(\\d{12}|\\*{8}\\d{4})$", message = "National ID must be exactly 12 digits or masked format (********1234).")
    private String nationalId;

    @Pattern(regexp = "^(0\\d{9}|\\*{6}\\d{4})$", message = "Invalid phone number format (e.g., 0921123456 or ******3456).")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past.")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    private boolean employed;

    private Role role;

    @Pattern(regexp = "^(DAY|EVENING|NIGHT)$", message = "Invalid shift.")
    private String shift;

    @Valid
    private AddressEntity address;
}
