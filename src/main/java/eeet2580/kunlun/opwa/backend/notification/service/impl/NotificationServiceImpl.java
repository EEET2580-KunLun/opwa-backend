package eeet2580.kunlun.opwa.backend.notification.service.impl;

import eeet2580.kunlun.opwa.backend.notification.dto.req.AcknowledgementRequest;
import eeet2580.kunlun.opwa.backend.notification.dto.resp.SuspensionNoticeResponse;
import eeet2580.kunlun.opwa.backend.notification.dto.mapper.NotificationMapper;
import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;
import eeet2580.kunlun.opwa.backend.notification.repository.NotificationRepository;
import eeet2580.kunlun.opwa.backend.notification.service.NotificationService;
import eeet2580.kunlun.opwa.backend.notification.utils.RetryManager;
import eeet2580.kunlun.opwa.backend.notification.websocket.WebSocketGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final RetryManager retryManager;
    private final WebSocketGateway webSocketGateway;

    @Override
    public SuspensionNoticeResponse create(SuspensionNoticeResponse dto) {
        NotificationEntity entity = new NotificationEntity();
        entity.setLineId(dto.getLineId());
        entity.setAffectedStations(dto.getAffectedStations());
        entity.setReason(NotificationEntity.Reason.valueOf(dto.getReason()));
        entity.setDescription(dto.getDescription());
        entity.setExpectedRestorationTime(dto.getExpectedRestorationTime());
        entity.setAcknowledged(false);
        entity.setNotificationSent(true);
        entity.setRetryCount(0);

        NotificationEntity saved = repository.save(entity);
        retryManager.handleRetries(saved);
        webSocketGateway.broadcast(NotificationMapper.toDto(saved));

        return NotificationMapper.toDto(saved);
    }

    @Override
    public List<SuspensionNoticeResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SuspensionNoticeResponse> getById(String id) {
        return repository.findById(id)
                .map(NotificationMapper::toDto);
    }

    @Override
    public void acknowledge(AcknowledgementRequest request) {
        repository.findById(request.getNotificationId()).ifPresent(notification -> {
            notification.setAcknowledged(request.isAcknowledged());
            repository.save(notification);
        });
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public NotificationEntity save(NotificationEntity notification) {
        return repository.save(notification);
    }

    @Override
    public List<SuspensionNoticeResponse> getByLineId(String lineId) {
        return repository.findByLineId(lineId)
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SuspensionNoticeResponse> getUnacknowledged() {
        return repository.findByAcknowledgedFalse()
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SuspensionNoticeResponse> getByStation(String stationId) {
        return repository.findByAffectedStationsContaining(stationId)
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void acknowledge(String id) {
        acknowledge(new AcknowledgementRequest(id, true));
    }

}
