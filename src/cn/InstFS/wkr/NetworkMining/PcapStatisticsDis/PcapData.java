package cn.InstFS.wkr.NetworkMining.PcapStatisticsDis;

public class PcapData implements Comparable {
    private long time_s;//时间戳（秒）
    private long time_ms;//时间戳（微妙）
    private int pLength;//抓包长度
    private int length;//实际长度
    private int traffic;
    private int TTL;
    private String srcIP;
    private String pcapFile;

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int tTL) {
        TTL = tTL;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    private String dstIP;
    private int srcPort;
    private int dstPort;

    public long getTime_s() {
        return time_s;
    }

    public void setTime_s(long time_s) {
        this.time_s = time_s;
    }

    public long getTime_ms() {
        return time_ms;
    }

    public void setTime_ms(long time_ms) {
        this.time_ms = time_ms;
    }

    public int getpLength() {
        return pLength;
    }

    public void setpLength(int pLength) {
        this.pLength = pLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("time_s=").append(this.time_s);
        s.append("\ntime_ms=").append(this.time_ms);
        s.append("\npLength=").append(this.pLength);
        s.append("\nlength=").append(this.length);
        return null;
    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        PcapData data = (PcapData) arg0;
        String str = srcIP + "_" + dstIP + "_" + srcPort + "_" + dstPort;
        String str2 = data.srcIP + "_" + data.dstIP + "_" + data.srcPort + "_" + data.dstPort;
        if (str.equals(str2)) {
            if (time_s == data.getTime_s()) {
                if (time_ms == data.getTime_ms()) {
                    if (TTL == data.getTTL()) {
                        if (pcapFile == data.getPcapFile())
                            return 0;
                        else
                            return pcapFile.compareTo(data.getPcapFile());
                    } else
                        return TTL < data.getTTL() ? -1 : 1;
                }
                return time_ms < data.getTime_ms() ? -1 : 1;
            } else
                return time_s < data.getTime_s() ? -1 : 1;
        } else {
            return str.compareTo(str2);
        }
    }

    public String getPcapFile() {
        return pcapFile;
    }

    public void setPcapFile(String pcapFile) {
        this.pcapFile = pcapFile;
    }
}
