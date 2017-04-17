package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

public class PcapData implements Comparable {
    private long time_s;//时间戳（秒）
    private long time_ms;//时间戳（微妙）
    private int pLength;//抓包长度
    private int length;//实际长度
    private int traffic;
    private int identification;//ip协议中字段，用于判断是否是同一个包
    private int TTL;
    private String srcIP;
    private String dstIP;
    private int srcPort;
    private int dstPort;
    private String pcapFile;//文件名
    private int protocol;//协议，17（UDP）还是6（TCP）
    private int flags;//标志，TCP才有。判断是否是SYN、ACK等三次握手的帧
    private int seq;//TCP的seq值,初始化，防止UDP判断出错
    private int ack;//TCP的ack值
    private int geted = 0;//是否遍历过，1代表遍历过，0代表没有

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    public int getIdentification() {
        return identification;
    }

    public void setIdentification(int identification) {
        this.identification = identification;
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

    public int getAck() {
        return ack;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getGeted() {
        return geted;
    }

    public void setGeted(int geted) {
        this.geted = geted;
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
//        String str = srcIP + "_" + dstIP + "_" + srcPort + "_" + dstPort;
//        String str2 = data.srcIP + "_" + data.dstIP + "_" + data.srcPort + "_" + data.dstPort;
//        if (str.equals(str2)) {
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
//        } else {
//            return str.compareTo(str2);
//        }
    }

    public String getPcapFile() {
        return pcapFile;
    }

    public void setPcapFile(String pcapFile) {
        this.pcapFile = pcapFile;
    }
}
