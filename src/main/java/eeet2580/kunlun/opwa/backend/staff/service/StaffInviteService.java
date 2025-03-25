package eeet2580.kunlun.opwa.backend.staff.service;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity.Role;

import java.util.Optional;

public interface StaffInviteService {
    String generateInvite(Role role);

    boolean isValidToken(String token);

    void markTokenUsed(String token);

    Optional<StaffInviteTokenEntity> getInvite(String token);
}
