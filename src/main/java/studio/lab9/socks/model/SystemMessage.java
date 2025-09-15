package studio.lab9.socks.model;

public class SystemMessage {
    private int markets_sessions;
    private long mem;
    private double used_mem;
    private double cpu;
    private double used_cpu;

    public SystemMessage(int markets_sessions, long mem, double used_mem, double cpu, double used_cpu) {
        this.markets_sessions = markets_sessions;
        this.mem = mem;
        this.used_mem = used_mem;
        this.cpu = cpu;
        this.used_cpu = used_cpu;
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
}