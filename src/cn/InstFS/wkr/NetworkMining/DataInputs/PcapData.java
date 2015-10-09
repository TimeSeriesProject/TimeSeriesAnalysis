package cn.InstFS.wkr.NetworkMining.DataInputs;

public class PcapData {
	 private long time_s;//时间戳（秒）
	 private long time_ms;//时间戳（微妙）
	 private int pLength;//抓包长度
	 private int length;//实际长度
	 private int traffic;
	 private int TTL;
	 private String srcIP;
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
	 public String toString(){
	  StringBuilder s = new StringBuilder();
	  s.append("time_s=").append(this.time_s);
	  s.append("\ntime_ms=").append(this.time_ms);
	  s.append("\npLength=").append(this.pLength);
	  s.append("\nlength=").append(this.length);
	  return null;
	 }
}
