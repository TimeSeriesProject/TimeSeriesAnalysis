package cn.InstFS.wkr.NetworkMining.PcapStatistics;

public class SessionKey implements Comparable<SessionKey> {
	private String srcIP;
	private String dstIP;
	private int servicePort;
	private String protoType;
	
	public SessionKey(String srcIP,String dstIP,int servicePort,String protoType){
		this.srcIP=srcIP;
		this.dstIP=dstIP;
		this.servicePort=servicePort;
		this.protoType=protoType;
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
	
	@Override
	public int compareTo(SessionKey o) {
		if((o.srcIP.equals(srcIP)&&o.dstIP.equals(dstIP)&&o.servicePort==servicePort&&o.protoType.equals(protoType))){
			return 0;
		}else if ((o.srcIP.equals(dstIP)&&o.dstIP.equals(srcIP)&&o.servicePort==servicePort&&o.protoType.equals(protoType))) {
			return 0;
		}else{
			if(srcIP.compareTo(o.srcIP)!=0){
				return srcIP.compareTo(o.srcIP);
			}else if (dstIP.compareTo(o.dstIP)!=0) {
				return dstIP.compareTo(o.dstIP);
			}else if(servicePort!=o.servicePort){
				if(servicePort>o.servicePort){
					return 1;
				}else{
					return -1;
				}
			}else{
				return protoType.compareTo(o.protoType);
			}
		}
	}
}
