package eeet2580.kunlun.opwa.backend.staff.service;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;

import java.util.Optional;

public interface StaffInviteService {
    String generateInvite();

    Optional<StaffInviteTokenEntity> getInvite(String token);

    boolean isInviteValid(String token);

    void deleteInvite(String token);
}
