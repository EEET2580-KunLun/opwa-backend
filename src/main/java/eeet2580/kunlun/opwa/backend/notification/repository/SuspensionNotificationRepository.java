package eeet2580.kunlun.opwa.backend.notification.repository;


import eeet2580.kunlun.opwa.backend.notification.model.SuspensionNotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspensionNotificationRepository extends MongoRepository<SuspensionNotificationEntity, String> {
    // Find noti that have not been acknowledged by PAWA
    List<SuspensionNotificationEntity> findByAcknowledgedFalse();

    // Find notifications for a specific line
    List<SuspensionNotificationEntity> findByLineId(String lineId);
}
