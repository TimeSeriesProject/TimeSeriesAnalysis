package Distributed;

/**
 * Created by zsc on 2016/8/18.
 */
public class TrafficKey implements Comparable {
    private Long time;
    private String srcIp;
    private String dstIp;
    private String protocol;


    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }



    @Override
    public int hashCode() {
        return (srcIp + "_" + dstIp + "_" + protocol + "_" + time).hashCode();
    }

    @Override
    public boolean equals(Object oneObject) {
        TrafficKey tmp = (TrafficKey) oneObject;
        return (srcIp + "_" + dstIp + "_" + protocol + "_" + time).equals(tmp.srcIp + "_" + tmp.dstIp + "_" + tmp.protocol + "_" + tmp.time);
    }

    public int compareTo(Object arg0) {
        TrafficKey tmp = (TrafficKey) arg0;
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
}
