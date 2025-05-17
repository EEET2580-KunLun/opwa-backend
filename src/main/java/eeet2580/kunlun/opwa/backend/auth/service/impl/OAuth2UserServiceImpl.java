package eeet2580.kunlun.opwa.backend.auth.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2UserServiceImpl(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(attributes, userRequest.getClientRegistration().getRegistrationId());

        Optional<StaffEntity> staffOptional = staffRepository.findByEmail(email);

        if (staffOptional.isPresent()) {
            return staffOptional.get();
        } else {
            StaffEntity newStaff = createUserFromOAuth2(attributes, userRequest.getClientRegistration().getRegistrationId());
            return staffRepository.save(newStaff);
        }
    }


    private StaffEntity createUserFromOAuth2(Map<String, Object> attributes, String provider) {
        String email = extractEmail(attributes, provider);
        String name = extractName(attributes, provider);

        StaffEntity staff = new StaffEntity();
        staff.setEmail(email);

        //this causes username to be longer than 20 characters
//        staff.setUsername(email.split("@")[0] + UUID.randomUUID().toString().substring(0, 8));

        staff.setUsername(email);
        staff.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        // Set name fields based on provider's information
//        String[] nameParts = name.split(" ");
//        if (nameParts.length > 0) {
//            staff.setFirstName(nameParts[0]);
//            if (nameParts.length > 1) {
//                staff.setLastName(nameParts[nameParts.length - 1]);
//            }
//        }


        staff.setRole(StaffEntity.Role.OPERATOR);
        staff.setEmployed(true);

        // Set avatar if available
        staff.setAvatarUrl(extractAvatarUrl(attributes, provider));

        return staff;
    }

    private String extractEmail(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("github".equals(provider)) {
            return (String) attributes.get("email");
        }
        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }

    private String extractName(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        } else if ("github".equals(provider)) {
            return (String) attributes.get("name");
        }
        return "Unknown User";
    }

    private String extractAvatarUrl(Map<String, Object> attributes, String provider) {
        if ("google".equals(provider)) {
            return (String) attributes.get("picture");
        } else if ("github".equals(provider)) {
            return (String) attributes.get("avatar_url");
        }
        return null;
    }
}