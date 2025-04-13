package mobile.doan.supertodolist.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.doan.supertodolist.dto.websocket.WebSocketMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Gửi thông báo khi có lời mời kết bạn mới
     * 
     * @param userId ID của người nhận lời mời
     * @param data   Dữ liệu lời mời kết bạn
     */
    public void sendFriendRequestNotification(Long userId, Object data) {
        try {
            // Tạo thông báo đúng định dạng
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("NEW_FRIEND_REQUEST")
                    .content(data)
                    .build();

            String topic = "/topic/friend-requests/" + userId;
            log.info("Sending friend request notification to topic: {}", topic);

            // Gửi thông báo tới topic của user
            messagingTemplate.convertAndSend(topic, message);

            // Gửi thông báo tới topic debug để dễ theo dõi
            messagingTemplate.convertAndSend("/topic/debug", message);

            log.info("WebSocket notification sent successfully to user {}", userId);
        } catch (Exception e) {
            log.error("Error sending notification via WebSocket: {}", e.getMessage(), e);
        }
    }
}