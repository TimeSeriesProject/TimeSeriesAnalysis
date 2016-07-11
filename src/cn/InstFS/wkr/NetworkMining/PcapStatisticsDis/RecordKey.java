package cn.InstFS.wkr.NetworkMining.PcapStatisticsDis;

public class RecordKey implements Comparable {
    private String srcIp;
    private String dstIp;
    private Integer protocol;
    private Long time;

    RecordKey(String srcIp, String dstIp, Integer protocol, Long time) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.protocol = protocol;
        this.time = time;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public void setProtocol(Integer protocol) {
        this.protocol = protocol;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        return (srcIp + "_" + dstIp + "_" + protocol + "_" + time).hashCode();
    }

    @Override
    public boolean equals(Object oneObject) {
        RecordKey tmp = (RecordKey) oneObject;
        return (srcIp + "_" + dstIp + "_" + protocol + "_" + time).equals(tmp.srcIp + "_" + tmp.dstIp + "_" + tmp.protocol + "_" + tmp.time);
    }

    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        RecordKey tmp = (RecordKey) arg0;
        if ((srcIp).compareTo(tmp.srcIp) != 0) {
            return (srcIp).compareTo(tmp.srcIp);
        }
        if ((time).compareTo(tmp.time) != 0)
            return (time).compareTo(tmp.time);
        return (dstIp + "_" + protocol).compareTo(tmp.dstIp + "_" + tmp.protocol);
    }

    public String toString() {
        return srcIp + "_" + dstIp + "_" + protocol + "_" + time;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        RecordKey key = new RecordKey("gg", "dd", 123, 333L);
        System.out.println(key.toString());
    }

}



