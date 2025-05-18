package eeet2580.kunlun.opwa.backend.staff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inviteTokens")

public class StaffInviteTokenEntity {
    @Id
    private String id;
    private String token;
    private Date expiresAt;
}
