package eeet2580.kunlun.opwa.backend.station.repository;

import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StationRepository extends MongoRepository<StationEntity, String> {
    boolean existsByName(String name);

    Optional<StationEntity> findByName(String name);
}