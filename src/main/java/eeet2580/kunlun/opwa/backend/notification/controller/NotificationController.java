package eeet2580.kunlun.opwa.backend.notification.controller;

import eeet2580.kunlun.opwa.backend.notification.dto.resp.SuspensionNoticeResponse;
import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // Create or update a notification
    @PostMapping
    public ResponseEntity<NotificationEntity> create(@RequestBody NotificationEntity notification) {
        return ResponseEntity.ok(service.save(notification));
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<SuspensionNoticeResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<SuspensionNoticeResponse> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("Notification deleted successfully.");
    }

    // Get by Line ID
    @GetMapping("/line/{lineId}")
    public ResponseEntity<List<SuspensionNoticeResponse>> getByLineId(@PathVariable String lineId) {
        return ResponseEntity.ok(service.getByLineId(lineId));
    }

    // Get unacknowledged notifications
    @GetMapping("/unacknowledged")
    public ResponseEntity<List<SuspensionNoticeResponse>> getUnacknowledged() {
        return ResponseEntity.ok(service.getUnacknowledged());
    }

    // Get by affected station
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<SuspensionNoticeResponse>> getByStation(@PathVariable String stationId) {
        return ResponseEntity.ok(service.getByStation(stationId));
    }

    // Mark as acknowledged
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Void> acknowledge(@PathVariable String id) {
        service.acknowledge(id);
        return ResponseEntity.noContent().build();
    }
}
