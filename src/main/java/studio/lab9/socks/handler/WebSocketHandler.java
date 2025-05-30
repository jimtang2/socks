package studio.lab9.socks.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import studio.lab9.socks.model.MessageType;
import studio.lab9.socks.model.WebSocketMessage;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketMessage wsMessage = mapper.readValue(message.getPayload(), WebSocketMessage.class);
        String response;
        switch (wsMessage.getType()) {
            case CHAT:
                response = "Chat: " + wsMessage.getContent();
                break;
            case NOTIFICATION:
                response = "Notification: " + wsMessage.getContent();
                break;
            default:
                response = "Unknown message type";
        }
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("Connected to WebSocket server"));
    }
}