package studio.lab9.socks.model;

public class SystemMessage {
    private int markets_sessions;
    private long mem;
    private double used_mem;
    private double cpu;
    private double used_cpu;
    private long cpu_speed; // MHz
    private int cpu_cores; // Count
    private long network_egress_speed; // Bytes per second
    private long network_ingress_speed; // Bytes per second
    private long uptime; // Seconds
    private long disk_io_rate; // Bytes per second

    public SystemMessage(int markets_sessions, long mem, double used_mem, double cpu, double used_cpu,
                        long cpu_speed, int cpu_cores, long network_egress_speed, long network_ingress_speed,
                        long uptime, long disk_io_rate) {
        this.markets_sessions = markets_sessions;
        this.mem = mem;
        this.used_mem = used_mem;
        this.cpu = cpu;
        this.used_cpu = used_cpu;
        this.cpu_speed = cpu_speed;
        this.cpu_cores = cpu_cores;
        this.network_egress_speed = network_egress_speed;
        this.network_ingress_speed = network_ingress_speed;
        this.uptime = uptime;
        this.disk_io_rate = disk_io_rate;
    }

    public int getMarkets_sessions() { return markets_sessions; }
    public void setMarkets_sessions(int markets_sessions) { this.markets_sessions = markets_sessions; }
    public long getMem() { return mem; }
    public void setMem(long mem) { this.mem = mem; }
    public double getUsed_mem() { return used_mem; }
    public void setUsed_mem(double used_mem) { this.used_mem = used_mem; }
    public double getCpu() { return cpu; }
    public void setCpu(double cpu) { this.cpu = cpu; }
    public double getUsed_cpu() { return used_cpu; }
    public void setUsed_cpu(double used_cpu) { this.used_cpu = used_cpu; }
    public long getCpu_speed() { return cpu_speed; }
    public void setCpu_speed(long cpu_speed) { this.cpu_speed = cpu_speed; }
    public int getCpu_cores() { return cpu_cores; }
    public void setCpu_cores(int cpu_cores) { this.cpu_cores = cpu_cores; }
    public long getNetwork_egress_speed() { return network_egress_speed; }
    public void setNetwork_egress_speed(long network_egress_speed) { this.network_egress_speed = network_egress_speed; }
    public long getNetwork_ingress_speed() { return network_ingress_speed; }
    public void setNetwork_ingress_speed(long network_ingress_speed) { this.network_ingress_speed = network_ingress_speed; }
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
    public long getDisk_io_rate() { return disk_io_rate; }
    public void setDisk_io_rate(long disk_io_rate) { this.disk_io_rate = disk_io_rate; }
}