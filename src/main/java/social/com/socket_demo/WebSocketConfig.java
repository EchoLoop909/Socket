package social.com.socket_demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CallSocketHandler callSocketHandler;

    public WebSocketConfig(CallSocketHandler callSocketHandler) {
        this.callSocketHandler = callSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(callSocketHandler, "/ws/call")
                .setAllowedOrigins("*");
    }
}
