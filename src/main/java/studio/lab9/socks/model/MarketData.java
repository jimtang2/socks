package studio.lab9.socks.model;

public class MarketData {
    private String n; // Symbol
    private double lp; // Last price
    private long t; // Timestamp (epoch seconds)
    private double volume; // Trading volume
    private double chp; // Change percent
    private double ch; // Change

    public MarketData(String n, double lp, long t, double volume, double chp, double ch) {
        this.n = n;
        this.lp = lp;
        this.t = t;
        this.volume = volume;
        this.chp = chp;
        this.ch = ch;
    }

    public String getN() { return n; }
    public void setN(String n) { this.n = n; }
    public double getLp() { return lp; }
    public void setLp(double lp) { this.lp = lp; }
    public long getT() { return t; }
    public void setT(long t) { this.t = t; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    public double getChp() { return chp; }
    public void setChp(double chp) { this.chp = chp; }
    public double getCh() { return ch; }
    public void setCh(double ch) { this.ch = ch; }
}