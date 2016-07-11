package cn.InstFS.wkr.NetworkMining.PcapStatisticsDis;

import java.util.List;

public class Pcap {
	private PcapHeader header;
    private List<PcapData> data;
    public PcapHeader getHeader() {
       return header;
    }
    public void setHeader(PcapHeader header) {
       this.header = header;
    }
    public List<PcapData> getData() {
       return data;
    }
    public void setData(List<PcapData> data) {
       this.data = data;
    }
    @Override
    public String toString(){
       StringBuilder s = new StringBuilder();
       s.append("header{\n");
       s.append(header.toString());
       s.append("}\n");
       s.append("data part count=").append(data.size());
  	   return s.toString();
    }
}
