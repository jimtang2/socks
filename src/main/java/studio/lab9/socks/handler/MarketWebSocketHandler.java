package studio.lab9.socks.handler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus; 
import studio.lab9.socks.model.MarketData;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class MarketWebSocketHandler extends AbstractWebSocketHandler {
    private final Random random = new Random();
    private final String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN"};

    @Scheduled(fixedRate = 200)
    public void broadcastMarketData() {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen() || !sessionTimestamps.containsKey(session)) {
                sessions.remove(session);
                sessionTimestamps.remove(session);
                logger.debug("Removed inactive or closed session: {}. Current sessions: {}", 
                             session.getId(), sessions.size());
                continue;
            }
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
                logger.debug("Broadcast sent to session: {}", session.getId());
            } catch (IOException e) {
                logger.error("Error sending market data to session {}: {}", session.getId(), e.getMessage());
                try {
                    if (session.isOpen()) {
                        session.close(new CloseStatus(1000, "Broadcast error"));
                    }
                    sessions.remove(session);
                    sessionTimestamps.remove(session);
                    logger.info("Closed and removed session due to broadcast error: {}", session.getId());
                } catch (IOException ex) {
                    logger.error("Error closing session {}: {}", session.getId(), ex.getMessage());
                    sessions.remove(session);
                    sessionTimestamps.remove(session);
                }
            }
        }
    }
}