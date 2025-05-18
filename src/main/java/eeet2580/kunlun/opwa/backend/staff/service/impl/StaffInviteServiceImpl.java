package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffInviteTokenRepository;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffInviteServiceImpl implements StaffInviteService {

    private final StaffInviteTokenRepository inviteRepo;

    @Override
    public String generateInvite() {
        String token = UUID.randomUUID().toString();

        // Create expiration date (4 hours from now)
        Date expiresAt = new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000);

        StaffInviteTokenEntity invite = new StaffInviteTokenEntity(
                null,
                token,
                expiresAt);  // Add expiration date

        inviteRepo.save(invite);
        return token;
    }

    @Override
    public Optional<StaffInviteTokenEntity> getInvite(String token) {
        return inviteRepo.findByToken(token);
    }

    @Override
    public boolean isInviteValid(String token) {
        Optional<StaffInviteTokenEntity> invite = inviteRepo.findByToken(token);
        return invite.isPresent() && invite.get().getExpiresAt().after(new Date());
    }

    @Override
    public void deleteInvite(String token) {
        inviteRepo.findByToken(token).ifPresent(inviteRepo::delete);
    }
}
