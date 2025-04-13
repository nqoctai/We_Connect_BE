package mobile.doan.supertodolist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthHandler webSocketAuthHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Define the prefix for the endpoint that the client will subscribe to to
        // receive messages
        registry.enableSimpleBroker("/topic", "/queue");

        // Define the prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/app");

        // Define the prefix for user-specific endpoints
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint, the URL that clients will use to connect to the
        // WebSocket server
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000", "http://localhost:4173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Đăng ký interceptor để xác thực kết nối WebSocket
        registration.interceptors(webSocketAuthHandler);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Tạo và cấu hình MappingJackson2MessageConverter
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        messageConverters.add(converter);
        return true;
    }
}