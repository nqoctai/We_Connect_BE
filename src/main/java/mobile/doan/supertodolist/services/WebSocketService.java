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
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Gửi thông báo đến một người dùng cụ thể
     *
     * @param userId  ID của người dùng nhận thông báo
     * @param type    Loại thông báo
     * @param content Nội dung thông báo
     */
    public void sendPrivateNotification(Long userId, String type, Object content) {
        try {
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(type)
                    .content(content)
                    .build();

            log.info("Sending private notification to user {}: {}", userId, message);

            // Đường dẫn đúng để gửi tới một user cụ thể
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    message);

            // Gửi thêm một bản sao đến topic chung để debug - chỉ gửi message, không phải
            // chuỗi
            messagingTemplate.convertAndSend("/topic/debug", message);
        } catch (Exception e) {
            log.error("Error sending WebSocket notification to user {}: {}", userId,
                    e.getMessage());
        }
    }

    /**
     * Gửi thông báo đến tất cả người dùng đang kết nối
     *
     * @param type    Loại thông báo
     * @param content Nội dung thông báo
     */
    public void sendGlobalNotification(String type, Object content) {
        try {
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(type)
                    .content(content)
                    .build();

            log.info("Sending global notification: {}", message);
            messagingTemplate.convertAndSend("/topic/global-notifications", message);
        } catch (Exception e) {
            log.error("Error sending global notification: {}", e.getMessage());
        }
    }
}