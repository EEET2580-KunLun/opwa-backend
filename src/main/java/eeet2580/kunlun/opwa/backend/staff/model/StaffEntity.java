package eeet2580.kunlun.opwa.backend.staff.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "staff")
public class StaffEntity implements OAuth2User {
    public enum Role {
        MASTER_ADMIN,
        ADMIN,
        OPERATOR,
        TICKET_AGENT
    }

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nationalId;
    private Role role;
    private AddressEntity residenceAddressEntity;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private boolean employed;
    private String shift;
    private String avatarUrl;

    private String refreshToken;
    private Date refreshTokenExpiry;

    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attr = new HashMap<>(attributes);

        attr.putIfAbsent("email", email);
        attr.putIfAbsent("name", getFullName());
        attr.putIfAbsent("id", id);

        return attr;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return email;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) {
            fullName.append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(lastName);
        }
        return fullName.toString().trim();
    }
}