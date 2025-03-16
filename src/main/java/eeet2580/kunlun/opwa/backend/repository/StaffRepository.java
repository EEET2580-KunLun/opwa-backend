package eeet2580.kunlun.opwa.backend.repository;

import eeet2580.kunlun.opwa.backend.model.StaffEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends MongoRepository<StaffEntity, String> {
    Optional<StaffEntity> findByEmail(String email);
}

