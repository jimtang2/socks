package studio.lab9.socks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import studio.lab9.socks.handler.MarketWebSocketHandler;
import studio.lab9.socks.handler.SystemWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MarketWebSocketHandler marketWebSocketHandler;
    private final SystemWebSocketHandler systemWebSocketHandler;

    public WebSocketConfig(MarketWebSocketHandler marketWebSocketHandler, SystemWebSocketHandler systemWebSocketHandler) {
        this.marketWebSocketHandler = marketWebSocketHandler;
        this.systemWebSocketHandler = systemWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(marketWebSocketHandler, "/markets").setAllowedOrigins("*");
        registry.addHandler(systemWebSocketHandler, "/system").setAllowedOrigins("*");
    }
}