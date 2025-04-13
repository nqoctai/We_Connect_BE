package mobile.doan.supertodolist.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    // Loại thông báo
    private String type;

    // Nội dung thông báo
    private Object content;
}