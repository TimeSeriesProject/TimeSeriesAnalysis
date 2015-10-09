package cn.InstFS.wkr.NetworkMining.PcapStatistics;

public class TCPStream {
	private long time;
	private String srcIp;
	private String dstIP;
	private String srcPort;
	private String dstPort;
	private String protoType;
	private int traffic;
	private int hops;
	public void setElements(long time,String srcIP,String dstIP,String srcPort,String dstPort,String protoType,int traffic,int hops){
		this.time=time;
		this.srcIp=srcIP;
		this.dstIP=dstIP;
		this.srcPort=srcPort;
		this.dstPort=dstPort;
		this.protoType=protoType;
		this.traffic=traffic;
		this.hops=hops;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public String getDstIP() {
		return dstIP;
	}
	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}
	public String getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstPort() {
		return dstPort;
	}
	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
	public String getProtoType() {
		return protoType;
	}
	public void setProtoType(String protoType) {
		this.protoType = protoType;
	}
	public int getTraffic() {
		return traffic;
	}
	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	public int getHops() {
		return hops;
	}
	public void setHops(int hops) {
		this.hops = hops;
	}

}
