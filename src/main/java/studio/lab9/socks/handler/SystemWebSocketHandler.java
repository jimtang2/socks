package studio.lab9.socks.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
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
@SuppressWarnings("deprecation") // Suppress warnings for OperatingSystemMXBean
public class SystemWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MarketWebSocketHandler marketWebSocketHandler;
    private final OperatingSystemMXBean osBean;
    private final SystemInfo systemInfo;
    private long lastNetworkBytesSent;
    private long lastNetworkBytesRecv;
    private long lastDiskBytes;
    private long lastSampleTime;

    public SystemWebSocketHandler(MarketWebSocketHandler marketWebSocketHandler) {
        this.marketWebSocketHandler = marketWebSocketHandler;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.systemInfo = new SystemInfo();
        this.lastNetworkBytesSent = getTotalNetworkBytesSent();
        this.lastNetworkBytesRecv = getTotalNetworkBytesRecv();
        this.lastDiskBytes = getTotalDiskBytes();
        this.lastSampleTime = System.currentTimeMillis();
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
                    // System metrics from OperatingSystemMXBean
                    long totalMem = osBean.getTotalPhysicalMemorySize();
                    long freeMem = osBean.getFreePhysicalMemorySize();
                    double usedMemPercent = ((double) (totalMem - freeMem) / totalMem) * 100;
                    double cpuLoad = osBean.getSystemCpuLoad() >= 0 ? osBean.getSystemCpuLoad() : 0.0;
                    double processCpuLoad = osBean.getProcessCpuLoad() >= 0 ? osBean.getProcessCpuLoad() : 0.0;
                    logger.debug("Total memory: {}, Free memory: {}, Used mem: {}%", totalMem, freeMem, usedMemPercent);

                    // OSHI metrics
                    CentralProcessor processor = systemInfo.getHardware().getProcessor();
                    long cpuSpeed = processor.getCurrentFreq().length > 0 ? processor.getCurrentFreq()[0] / 1_000_000 : 0; // MHz
                    int cpuCores = osBean.getAvailableProcessors(); // From MXBean for simplicity
                    long currentNetworkBytesSent = getTotalNetworkBytesSent();
                    long currentNetworkBytesRecv = getTotalNetworkBytesRecv();
                    long currentDiskBytes = getTotalDiskBytes();
                    long currentTime = System.currentTimeMillis();
                    double timeDiffSeconds = (currentTime - lastSampleTime) / 1000.0;
                    long networkEgressSpeed = timeDiffSeconds > 0 ? (long) ((currentNetworkBytesSent - lastNetworkBytesSent) / timeDiffSeconds) : 0;
                    long networkIngressSpeed = timeDiffSeconds > 0 ? (long) ((currentNetworkBytesRecv - lastNetworkBytesRecv) / timeDiffSeconds) : 0;
                    long diskIoRate = timeDiffSeconds > 0 ? (long) ((currentDiskBytes - lastDiskBytes) / timeDiffSeconds) : 0;
                    long uptime = systemInfo.getOperatingSystem().getSystemUptime();

                    // Update last values
                    lastNetworkBytesSent = currentNetworkBytesSent;
                    lastNetworkBytesRecv = currentNetworkBytesRecv;
                    lastDiskBytes = currentDiskBytes;
                    lastSampleTime = currentTime;

                    SystemMessage message = new SystemMessage(
                        marketWebSocketHandler.getActiveSessionCount(),
                        totalMem,
                        usedMemPercent,
                        cpuLoad,
                        processCpuLoad * 100,
                        cpuSpeed,
                        cpuCores,
                        networkEgressSpeed,
                        networkIngressSpeed,
                        uptime,
                        diskIoRate
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

    private long getTotalNetworkBytesSent() {
        return systemInfo.getHardware().getNetworkIFs().stream()
            .mapToLong(NetworkIF::getBytesSent)
            .sum();
    }

    private long getTotalNetworkBytesRecv() {
        return systemInfo.getHardware().getNetworkIFs().stream()
            .mapToLong(NetworkIF::getBytesRecv)
            .sum();
    }

    private long getTotalDiskBytes() {
        return systemInfo.getHardware().getDiskStores().stream()
            .mapToLong(store -> store.getReadBytes() + store.getWriteBytes())
            .sum();
    }
}