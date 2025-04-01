package eeet2580.kunlun.opwa.backend.staff.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inviteTokens")

public class StaffInviteTokenEntity {
    @Id
    private String id;
    private String token;
}
