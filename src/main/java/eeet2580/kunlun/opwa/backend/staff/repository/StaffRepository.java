package eeet2580.kunlun.opwa.backend.staff.repository;

import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends MongoRepository<StaffEntity, String> {
    Optional<StaffEntity> findByEmail(String email);

    Optional<StaffEntity> findByUsername(String username);

    Optional<StaffEntity> findByRoleAndEmail(StaffEntity.Role role, String email);

    Optional<StaffEntity> findByRefreshToken(String refreshToken);

    Page<StaffEntity> findByEmployed(boolean employed, Pageable pageable);
}
