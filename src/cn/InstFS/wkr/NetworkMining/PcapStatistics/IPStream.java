package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.util.Date;

/**
 * IPStream 用于统计IP通行信息
 * @author wsc
 *
 */
public class IPStream{
	private long timeStart;
	private long timeEnd;
	
	private String srcIP;
	private String dstIP;
	private String protoType;
	private long traffic;
	
	private long hops;//ip包经历的跳数
	// getters and setters
	public void setKeyElement(String srcIP, String dstIP, String protoType,long hops,long traffic,long timeStart){
		this.srcIP = srcIP.trim();
		this.dstIP = dstIP.trim();
		this.protoType = protoType.trim();
		this.timeStart = timeStart;
		this.hops=hops;
		this.traffic=traffic;
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
	public long getHops() {
		return hops;
	}
	public void setHops(long hops) {
		this.hops = hops;
	}
	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}
	public String getProtoType() {
		return protoType;
	}
	public void setProtoType(String protoType) {
		this.protoType = protoType;
	}
	public long getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}
	public long getTraffic() {
		return traffic;
	}
	public void setTraffic(long traffic) {
		this.traffic = traffic;
	}
	public long getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(long timeEnd) {
		this.timeEnd = timeEnd;
	}
}