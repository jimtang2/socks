package studio.lab9.socks.handler;

import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus; 
import studio.lab9.socks.model.SystemMessage;
import java.io.IOException;
import java.lang.management.ManagementFactory;

@Component
@SuppressWarnings("deprecation") // Suppress warnings for OperatingSystemMXBean
public class SystemWebSocketHandler extends AbstractWebSocketHandler {
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

    @Scheduled(fixedRate = 1000)
    public void broadcastSystemMessage() {
        for (WebSocketSession session : sessions) {
            if (!session.isOpen() || !sessionTimestamps.containsKey(session)) {
                sessions.remove(session);
                sessionTimestamps.remove(session);
                logger.debug("Removed inactive or closed session: {}. Current sessions: {}", 
                             session.getId(), sessions.size());
                continue;
            }
            try {
                // System metrics from OperatingSystemMXBean
                long totalMem = osBean.getTotalPhysicalMemorySize();
                long freeMem = osBean.getFreePhysicalMemorySize();
                double usedMemPercent = ((double) (totalMem - freeMem) / totalMem) * 100;
                double cpuLoad = osBean.getSystemCpuLoad() >= 0 ? osBean.getSystemCpuLoad() : 0.0;
                double processCpuLoad = osBean.getProcessCpuLoad() >= 0 ? osBean.getProcessCpuLoad() : 0.0;
                // OSHI metrics
                CentralProcessor processor = systemInfo.getHardware().getProcessor();
                long cpuSpeed = processor.getCurrentFreq().length > 0 ? processor.getCurrentFreq()[0] / 1_000_000 : 0;
                int cpuCores = osBean.getAvailableProcessors();
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
                logger.debug("Broadcast sent to session: {}", session.getId());
            } catch (IOException e) {
                logger.error("Error sending system message to session {}: {}", session.getId(), e.getMessage());
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