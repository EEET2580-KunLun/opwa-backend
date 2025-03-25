package eeet2580.kunlun.opwa.backend.staff.repository;

import eeet2580.kunlun.opwa.backend.staff.model.StaffInviteTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StaffInviteTokenRepository extends MongoRepository<StaffInviteTokenEntity, String> {
    Optional<StaffInviteTokenEntity> findByToken(String token);
}
