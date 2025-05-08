package eeet2580.kunlun.opwa.backend.notification.repository;

import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {

    @Query("{'lineId': ?0}")
    List<NotificationEntity> findByLineId(String lineId);

    @Query("{'acknowledged': false}")
    List<NotificationEntity> findUnacknowledged();

    @Query("{'affectedStations': ?0}")
    List<NotificationEntity> findByAffectedStation(String stationId);

    List<NotificationEntity> findByAcknowledgedFalse();

    List<NotificationEntity> findByAffectedStationsContaining(String stationId);

}
