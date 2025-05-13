package eeet2580.kunlun.opwa.backend.notification.controller;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import eeet2580.kunlun.opwa.backend.notification.model.SuspensionNotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.repository.SuspensionNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO: This will be expanded when PAWA integration is ready
 */

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class SuspensionNotificationController {

    private final SuspensionNotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<BaseRes<List<SuspensionNotificationEntity>>> getAllNotifications() {
        List<SuspensionNotificationEntity> notifications = notificationRepository.findAll();
        BaseRes<List<SuspensionNotificationEntity>> response = new BaseRes<>(
                HttpStatus.OK.value(), "Notifications retrieved successfully", notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/line/{lineId}")
    public ResponseEntity<BaseRes<List<SuspensionNotificationEntity>>> getNotificationsByLine(
            @PathVariable String lineId) {
        List<SuspensionNotificationEntity> notifications = notificationRepository.findByLineId(lineId);
        BaseRes<List<SuspensionNotificationEntity>> response = new BaseRes<>(
                HttpStatus.OK.value(), "Notifications for line retrieved successfully", notifications);
        return ResponseEntity.ok(response);
    }
}
