package mobile.doan.supertodolist.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthHandler implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorizationHeader = accessor.getFirstNativeHeader("X-Authorization");
            System.out.println("WebSocket connection with auth header: " + authorizationHeader);

            if (StringUtils.hasText(authorizationHeader)) {
                try {
                    // Giải mã JWT token
                    Jwt jwt = jwtDecoder.decode(authorizationHeader);

                    // Tạo Authentication object
                    Authentication auth = new JwtAuthenticationToken(jwt);

                    // Đặt authentication vào message
                    accessor.setUser(auth);

                    // Đặt vào SecurityContext để sử dụng trong các handler khác
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("WebSocket authentication successful for user: " + jwt.getSubject());
                } catch (Exception e) {
                    System.out.println("WebSocket authentication error: " + e.getMessage());
                    // Không ném exception để cho phép kết nối không xác thực (sẽ bị giới hạn sau)
                }
            }
        }

        return message;
    }
}