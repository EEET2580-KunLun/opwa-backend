package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity.Role;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffInviteTokenRepository;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffInviteServiceImpl implements StaffInviteService {

    @Autowired
    private StaffInviteTokenRepository inviteRepo;

    @Override
    public String generateInvite(Role role) {
        String token = UUID.randomUUID().toString();
        StaffInviteTokenEntity invite = new StaffInviteTokenEntity(
                null,
                role,
                token,
                false,
                LocalDateTime.now().plusDays(3));
        inviteRepo.save(invite);
        return token;
    }

    @Override
    public boolean isValidToken(String token) {
        return inviteRepo.findByToken(token)
                .filter(invite -> !invite.isUsed() && invite.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    public void markTokenUsed(String token) {
        inviteRepo.findByToken(token).ifPresent(invite -> {
            invite.setUsed(true);
            inviteRepo.save(invite);
        });
    }

    @Override
    public Optional<StaffInviteTokenEntity> getInvite(String token) {
        return inviteRepo.findByToken(token);
    }
}
