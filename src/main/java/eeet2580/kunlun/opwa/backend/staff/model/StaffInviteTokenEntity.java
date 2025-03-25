package eeet2580.kunlun.opwa.backend.staff.model;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inviteTokens")

public class StaffInviteTokenEntity {
    @Id
    private String id;
    private Role role;
    private String token;
    private boolean used;
    private LocalDateTime expiresAt;
}
