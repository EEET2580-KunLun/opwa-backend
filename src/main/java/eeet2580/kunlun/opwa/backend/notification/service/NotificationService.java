package eeet2580.kunlun.opwa.backend.notification.service;

import eeet2580.kunlun.opwa.backend.notification.dto.req.AcknowledgementRequest;
import eeet2580.kunlun.opwa.backend.notification.dto.resp.SuspensionNoticeResponse;
import eeet2580.kunlun.opwa.backend.notification.model.NotificationEntity;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    List<SuspensionNoticeResponse> getAll();

    Optional<SuspensionNoticeResponse> getById(String id);

    SuspensionNoticeResponse create(SuspensionNoticeResponse dto);

    void acknowledge(AcknowledgementRequest request);

    void delete(String id);

    NotificationEntity save(NotificationEntity notification);

    List<SuspensionNoticeResponse> getByLineId(String lineId);

    List<SuspensionNoticeResponse> getUnacknowledged();

    List<SuspensionNoticeResponse> getByStation(String stationId);

    void acknowledge(String id);

}
