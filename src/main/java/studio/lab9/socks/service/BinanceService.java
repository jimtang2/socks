package studio.lab9.socks.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import studio.lab9.socks.handler.CryptoWebSocketHandler;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final CryptoWebSocketHandler cryptoWebSocketHandler;
    private static final String BINANCE_WS_URL = "wss://stream.binance.com:9443/ws/btcusdt@miniTicker/ethusdt@miniTicker/bnbusdt@miniTicker/solusdt@miniTicker/xrpusdt@miniTicker/dogeusdt@miniTicker/tonusdt@miniTicker/adausdt@miniTicker/trxusdt@miniTicker/avaxusdt@miniTicker";
    private static final Logger logger = LoggerFactory.getLogger(BinanceService.class);

    @PostConstruct
    public void connectToBinance() {
        logger.info("Starting Binance WebSocket connection to: {}", BINANCE_WS_URL);
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.execute(new BinanceWebSocketHandler(), BINANCE_WS_URL)
                .orTimeout(10, TimeUnit.SECONDS)
                .whenComplete((session, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to connect to Binance: {}", throwable.getMessage(), throwable);
                        reconnect();
                    } else {
                        logger.info("Successfully connected to Binance WebSocket");
                    }
                });
    }

    private void reconnect() {
        try {
            logger.info("Reconnecting to Binance in 5 seconds...");
            Thread.sleep(5000);
            connectToBinance();
        } catch (InterruptedException e) {
            logger.error("Reconnect interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private class BinanceWebSocketHandler extends TextWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            logger.info("Binance WebSocket session established: {}", session.getId());
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            // logger.debug("Received Binance data: {}", payload);
            try {
                cryptoWebSocketHandler.broadcast(payload); // Broadcast to /crypto clients
                // logger.info("Broadcasted Binance data to /crypto clients");
            } catch (Exception e) {
                logger.error("Failed to broadcast to /crypto clients: {}", e.getMessage(), e);
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
            logger.warn("Binance WebSocket closed: {}. Status: {}", session.getId(), status);
            reconnect();
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            logger.error("Binance WebSocket transport error: {}", exception.getMessage(), exception);
        }
    }
}