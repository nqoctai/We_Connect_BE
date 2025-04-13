package mobile.doan.supertodolist.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.dto.websocket.WebSocketMessage;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send-notification")
    public void sendNotification(@Payload WebSocketMessage message) {
        // Xử lý và gửi thông báo
        System.out.println("Received WebSocket message: " + message);
    }
}