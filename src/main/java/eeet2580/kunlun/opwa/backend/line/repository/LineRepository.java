package eeet2580.kunlun.opwa.backend.line.repository;

import eeet2580.kunlun.opwa.backend.line.model.LineEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends MongoRepository<LineEntity, String> {
    @Query("{'stations.stationId': ?0}")
    List<LineEntity> findByStationsStationId(String stationId);

    boolean existsByName(String name);

    Optional<LineEntity> findByName(String name);
}