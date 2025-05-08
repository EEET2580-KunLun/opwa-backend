package eeet2580.kunlun.opwa.backend.notification.websocket;

import eeet2580.kunlun.opwa.backend.notification.dto.resp.SuspensionNoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketGateway {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_DESTINATION = "/topic/notifications";

    public void broadcast(SuspensionNoticeResponse message) {
        messagingTemplate.convertAndSend(TOPIC_DESTINATION, message);
    }
}
