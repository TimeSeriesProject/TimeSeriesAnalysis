package cn.InstFS.wkr.NetworkMining.PcapStatistics;


public class SessionStream {
	private boolean isFIN=false;
	private long startStamp;
	private long finishStamp;
	private long traffic;
	private long interval;
	private String srcIP;
	private String dstIP;
	private int servicePort;
	private String protoType;
	
	public SessionStream(String srcIP,String dstIP,int serveicePort){
		this.srcIP=srcIP;
		this.dstIP=dstIP;
		this.servicePort=serveicePort;
	}
	
	public SessionStream(){
		
	}

	public boolean isFIN() {
		return isFIN;
	}

	public void setFIN(boolean isFIN) {
		this.isFIN = isFIN;
	}

	public long getStartStamp() {
		return startStamp;
	}

	public void setStartStamp(long startStamp) {
		this.startStamp = startStamp;
	}

	public long getFinishStamp() {
		return finishStamp;
	}

	public void setFinishStamp(long finishStamp) {
		this.finishStamp = finishStamp;
	}

	public long getTraffic() {
		return traffic;
	}

	public void setTraffic(long traffic) {
		this.traffic = traffic;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
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

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public String getProtoType() {
		return protoType;
	}

	public void setProtoType(String protoType) {
		this.protoType = protoType;
	}
}
