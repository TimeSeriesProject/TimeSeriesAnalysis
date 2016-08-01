package cn.InstFS.wkr.NetworkMining.PcapStatisticsOpt;

/**
 * Created by zsc on 2016/7/18.
 */
public class PcapNode implements Comparable {
    private long time_s;//时间戳（秒）
    private String fileName;//pcap文件名（例：13-0）
    private int traffic;//流量


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTime_s() {
        return time_s;
    }

    public void setTime_s(long time_s) {
        this.time_s = time_s;
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    @Override
    public int compareTo(Object arg0) {
        PcapNode node = (PcapNode) arg0;
        if (time_s == node.getTime_s()) {
            if (fileName == node.getFileName())
                return 0;
            else {
                return fileName.compareTo(node.getFileName());
            }
        } else {
            return time_s < node.getTime_s() ? -1 : 1;
        }
    }
}
