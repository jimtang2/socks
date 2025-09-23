package studio.lab9.socks.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Component
public class CryptoWebSocketHandler extends TimeoutWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(CryptoWebSocketHandler.class);

    public void broadcast(String message) throws IOException {
        // logger.debug("Broadcasting to /crypto clients: {}", message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message)); // Fixed: Direct send
            } else {
                logger.warn("Cannot send to closed session: {}", session.getId());
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message); // Handle heartbeats
    }

    public List<WebSocketSession> getSessions() {
        return sessions;
    }
}