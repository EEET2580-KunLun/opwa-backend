package eeet2580.kunlun.opwa.backend.notification.utils;

import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RetryManager {

    private final NotificationRepository repository;

    private static final int MAX_RETRIES = 3;

    public void handleRetries(NotificationEntity notification) {
        if (!Boolean.TRUE.equals(notification.getAcknowledged()) &&
                (notification.getRetryCount() == null || notification.getRetryCount() < MAX_RETRIES)) {

            notification.setRetryCount(notification.getRetryCount() == null ? 1 : notification.getRetryCount() + 1);
            notification.setLastRetry(LocalDateTime.now());

            repository.save(notification);
        }
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void retryUnacknowledgedNotifications() {
        List<NotificationEntity> unacked = repository.findUnacknowledged();

        for (NotificationEntity notification : unacked) {
            handleRetries(notification);
        }
    }
}
