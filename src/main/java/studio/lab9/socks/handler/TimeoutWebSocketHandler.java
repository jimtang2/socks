package studio.lab9.socks.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.List; // Added import
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TimeoutWebSocketHandler extends TextWebSocketHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    protected final Map<WebSocketSession, Long> sessionTimestamps = new ConcurrentHashMap<>();
    protected final ObjectMapper objectMapper;
    protected static final long IDLE_TIMEOUT = 3_000L; // 3 seconds
    protected static final long CHECK_INTERVAL = 2_000L; // Check every 2 seconds

    public TimeoutWebSocketHandler() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        logger.info("ObjectMapper initialized with JavaTimeModule: {}", objectMapper.getRegisteredModuleIds());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sessionTimestamps.put(session, System.currentTimeMillis());
        logger.info("New WebSocket connection established: {}. Current sessions: {}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionTimestamps.remove(session);
        logger.info("WebSocket connection closed: {}. Status: {}. Current sessions: {}", 
                    session.getId(), status, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // Strip ~m~ prefix if present
            String payload = message.getPayload().replaceAll("^~m~\\d+~m~", "");
            Map<String, Object> msgMap = objectMapper.readValue(payload, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            if (msgMap.containsKey("t") && msgMap.get("t") instanceof Number &&
                msgMap.containsKey("c") && msgMap.get("c") instanceof Number) {
                sessionTimestamps.put(session, System.currentTimeMillis());
                logger.debug("Heartbeat received from session {}: {}", session.getId(), payload);
            }
        } catch (Exception e) {
            logger.error("Error parsing message from session {}: {}", session.getId(), e.getMessage());
        }
    }

    @Scheduled(fixedRate = CHECK_INTERVAL)
    public void checkIdleConnections() {
        long now = System.currentTimeMillis();
        sessionTimestamps.entrySet().removeIf(entry -> {
            WebSocketSession session = entry.getKey();
            Long lastTime = entry.getValue();
            if (now - lastTime > IDLE_TIMEOUT) {
                try {
                    if (session.isOpen()) {
                        session.close(new CloseStatus(1000, "Idle timeout"));
                        logger.info("Closed idle session: {}. Current sessions: {}", 
                                    session.getId(), sessions.size());
                    }
                    sessions.remove(session);
                    return true;
                } catch (IOException e) {
                    logger.error("Error closing idle session {}: {}", session.getId(), e.getMessage());
                    sessions.remove(session);
                    return true;
                }
            }
            return false;
        });
        // Clean up sessions not in sessionTimestamps
        sessions.removeIf(session -> !sessionTimestamps.containsKey(session));
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}