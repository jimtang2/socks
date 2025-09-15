package studio.lab9.socks.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import studio.lab9.socks.model.SystemMessage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SystemWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MarketWebSocketHandler marketWebSocketHandler;
    private final OperatingSystemMXBean osBean;

    public SystemWebSocketHandler(MarketWebSocketHandler marketWebSocketHandler) {
        this.marketWebSocketHandler = marketWebSocketHandler;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("New WebSocket connection established for /system: {}. Current sessions: {}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.info("WebSocket connection closed for /system: {}. Current sessions: {}", session.getId(), sessions.size());
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastSystemMessage() {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    long totalMem = osBean.getTotalPhysicalMemorySize();
                    long freeMem = osBean.getFreePhysicalMemorySize();
                    double usedMemPercent = ((double) (totalMem - freeMem) / totalMem) * 100;
                    double cpuLoad = osBean.getSystemCpuLoad() >= 0 ? osBean.getSystemCpuLoad() : 0.0;
                    double processCpuLoad = osBean.getProcessCpuLoad() >= 0 ? osBean.getProcessCpuLoad() : 0.0;
                    logger.debug("Total memory: {}, Free memory: {}, Used mem: {}%", totalMem, freeMem, usedMemPercent);
                    SystemMessage message = new SystemMessage(
                        marketWebSocketHandler.getActiveSessionCount(),
                        totalMem,
                        usedMemPercent,
                        cpuLoad,
                        processCpuLoad * 100
                    );
                    String json = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    logger.error("Error sending system message to session {}: {}", session.getId(), e.getMessage());
                }
            } else {
                sessions.remove(session);
            }
        }
    }
}