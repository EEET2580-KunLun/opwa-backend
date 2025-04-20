package eeet2580.kunlun.opwa.backend.station.repository;

import eeet2580.kunlun.opwa.backend.station.model.StationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationRepository extends MongoRepository<StationEntity, String> {
}