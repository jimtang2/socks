package studio.lab9.socks.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import studio.lab9.socks.model.MarketData;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MarketWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MarketWebSocketHandler.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    private final String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN"};

    public MarketWebSocketHandler() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        logger.info("ObjectMapper initialized with JavaTimeModule: {}", objectMapper.getRegisteredModuleIds());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("New WebSocket connection established: {}. Current sessions: {}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.info("WebSocket connection closed: {}. Current sessions: {}", session.getId(), sessions.size());
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastMarketData() {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    MarketData data = new MarketData(
                        symbols[random.nextInt(symbols.length)],
                        100 + random.nextDouble() * 900,
                        System.currentTimeMillis() / 1000,
                        random.nextDouble() * 100_000_000,
                        random.nextDouble() * 2 - 1,
                        random.nextDouble() * 20 - 10
                    );
                    Map<String, Object> message = new HashMap<>();
                    message.put("m", "qsd");
                    message.put("p", List.of("qs_" + UUID.randomUUID().toString().substring(0, 12),
                                            Map.of("n", data.getN(), "s", "ok", "v", data)));
                    String json = objectMapper.writeValueAsString(message);
                    String prefixedMessage = String.format("~m~%d~m~%s", json.length(), json);
                    session.sendMessage(new TextMessage(prefixedMessage));
                } catch (IOException e) {
                    logger.error("Error sending market data to session {}: {}", session.getId(), e.getMessage());
                }
            } else {
                sessions.remove(session);
            }
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}