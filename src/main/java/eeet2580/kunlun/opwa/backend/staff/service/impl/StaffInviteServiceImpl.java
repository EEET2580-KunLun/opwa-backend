package eeet2580.kunlun.opwa.backend.staff.service.impl;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffInviteTokenRepository;
import eeet2580.kunlun.opwa.backend.staff.service.StaffInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffInviteServiceImpl implements StaffInviteService {

    @Autowired
    private StaffInviteTokenRepository inviteRepo;

    @Override
    public String generateInvite() {
        String token = UUID.randomUUID().toString();
        StaffInviteTokenEntity invite = new StaffInviteTokenEntity(
                null,
                token);
        inviteRepo.save(invite);
        return token;
    }

    @Override
    public Optional<StaffInviteTokenEntity> getInvite(String token) {
        return inviteRepo.findByToken(token);
    }
}
