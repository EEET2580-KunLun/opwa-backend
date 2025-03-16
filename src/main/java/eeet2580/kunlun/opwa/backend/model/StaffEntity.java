package eeet2580.kunlun.opwa.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "staff")
public class StaffEntity {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nationalId;
    private String role; // ADMIN, OPERATOR, TICKET_AGENT
    private AddressEntity residenceAddressEntity;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private boolean employed;
    private String shift;
}

