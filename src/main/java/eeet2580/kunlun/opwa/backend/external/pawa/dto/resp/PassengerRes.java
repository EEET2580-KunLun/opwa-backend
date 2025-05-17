package eeet2580.kunlun.opwa.backend.external.pawa.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRes {
    private String id;
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String passengerType;
    private String nationalID;
    private String phoneNumber;
    private String dateOfBirth;
    private String studentID;
    private Boolean isDisability;
    private Boolean isRevolutionaryContribution;
    private List<Object> purchaseHistory;
    private Boolean isVerified;
    private Boolean isStudent;
    private Boolean ageValid;
}