package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.net.ns.Packet;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.wan.PPP;
import org.openide.text.CloneableEditorSupport.Pane;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStream;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStreamPool;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.SessionStream;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.SessionStreamPool;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.TCPStream;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.TCPStreamPool;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
/**
 * pcap文件操作类
 * @author wsc
 *
 */
public class PcapUtils {
	private boolean SessionLevel=true;   //判断读取的数据是否是业务层数据
	public static void main(String [] args){
		String fpath = "F:\\pcap";
		PcapUtils pcapUtils = new PcapUtils();
		pcapUtils.readInput(fpath,0);
	}
	/**
	 * parse given file 
	 * @param fpath file path
	 * @param type stand for file properties,1 means trunkFiles and 0 means others  
	 */
	private void readInput(String fpath,int type){
		File ff = new File(fpath);
		if(ff.isFile()){
			System.out.println(new Date() +"\t开始读取:" + ff.getName());
			if(SessionLevel){
				if(type==0){
					read2File(ff.getPath(),ff.getName());
				}else if(type==1){
					directRead2File(ff.getPath(), ff.getName());
				}
			}else{
				read1File(ff.getPath());
			}	
			System.out.println(new Date() + "\t读取完毕：" + ff.getName());
		}else{
			File []files = ff.listFiles();
			for(File f : files){
				String path=f.getPath();
				if(f.getName().startsWith("trunk")){
					continue;
				}
				if(path.endsWith("pcap"))
				{
					if (f.isFile()){
						System.out.println(new Date() +"\t开始读取:" + f.getName());
						if(SessionLevel){
							if(type==0){
								read2File(f.getPath(),f.getName());
							}else if(type==1){
								directRead2File(f.getPath(), f.getName());
							}
							
						}else{
							read1File(f.getPath());
						}	
						System.out.println(new Date() + "\t读取完毕:" + f.getName());
					}
					if(f.isDirectory())
						readInput(f.getAbsolutePath(),type);
				}
			}
		}
		System.out.println("done");
	}
	
	/**
	 * 从文件中读取网络层层数据
	 * @param fpath 文件路径
	 */
	private void read1File(String fpath){
		StringBuilder errSb = new StringBuilder();
		Pcap pcap = Pcap.openOffline(fpath, errSb);
		PcapPacket packet = new PcapPacket(org.jnetpcap.nio.JMemory.Type.POINTER);
		long num = 0;
		if (pcap == null){
			System.out.println("Error:\t" + errSb.toString());
			return;
		}
		while(pcap.nextEx(packet) == Pcap.NEXT_EX_OK){
			num ++;
			System.out.println("row:"+num);
			IPStream ipEvent = new IPStream();
			boolean hasIP = false;
			boolean hasTcpOrUdp = false;
			boolean isTcpFIN = false;
			long timeStamp = packet.getCaptureHeader().timestampInMillis();
			ipEvent.setTimeStart(timeStamp);
			Ip4 ip4 = new Ip4();
			Tcp tcp = new Tcp();
			Udp udp = new Udp();
			if((hasIP = packet.hasHeader(ip4)) == true){
				hasIP = true;
				ipEvent.setSrcIP(ipBytes2Str(ip4.source()));
				ipEvent.setDstIP(ipBytes2Str(ip4.destination()));
				ipEvent.setTraffic(ip4.length());
				long TTL=ip4.ttl();
				if(TTL<=64){
					ipEvent.setHops(64-TTL);
				}else if(TTL<=128){
					ipEvent.setHops(128-TTL);
				}else{
					ipEvent.setHops(256-TTL);
				}
			}
			if (packet.hasHeader(tcp)){
				hasTcpOrUdp = true;
				ipEvent.setProtoType("tcp");
				if(tcp.flags_FIN())
					isTcpFIN = true;
			}
			if (packet.hasHeader(udp)){
				hasTcpOrUdp = true;
				ipEvent.setProtoType("udp");
			}
			
			if (hasIP && hasTcpOrUdp){
				IPStream stream = IPStreamPool.instance.createOrGetIPStream(ipEvent);
				IPStreamPool.instance.updateTraffic(stream, ipEvent, isTcpFIN);
			}
		}
		pcap.close();
		System.out.println(fpath + " --> 共" + num + "个包！");
	}
	
	/**
	 * 从文件中读取业务层数据
	 * @param fpath 文件路径
	 */
	private void read2File(String fpath,String name){
		String title=name.split("\\.")[0];
		TCPStreamPool streamPool=new TCPStreamPool(title);
		StringBuilder errSb = new StringBuilder();
		Pcap pcap = Pcap.openOffline(fpath, errSb);
		PcapPacket packet = new PcapPacket(JMemory.POINTER);
		long num = 0;
		if (pcap == null){
			System.out.println("Error:\t" + errSb.toString());
			return;
		}
		while(pcap.nextEx(packet) == Pcap.NEXT_EX_OK ){
			TCPStream event=new TCPStream();
			boolean smtp = false;
			boolean isTcpFIN = false;
			Ip4 ip4 = new Ip4();
			Tcp tcp = new Tcp();
			if (packet.hasHeader(ip4)&&packet.hasHeader(tcp)){	
				smtp = true;
				int TTL=ip4.ttl();
				int hops=64-TTL;
				long timeStamp = packet.getCaptureHeader().timestampInMillis();
				event.setTime(timeStamp);
				event.setHops(hops);
				event.setSrcIp(ipBytes2Str(ip4.source()));
				event.setDstIP(ipBytes2Str(ip4.destination()));
				event.setTraffic(ip4.length());
				event.setSrcPort(tcp.source()+"");
				event.setDstPort(tcp.destination()+"");
				int protoType=(tcp.source()<tcp.destination())?tcp.source():tcp.destination();
				event.setProtoType(protoType+"");
				if(tcp.flags_FIN()){
					isTcpFIN=true;
				}	
			}
			if (smtp){
				//大于80B的包视为数据包
				if(event.getTraffic()>=62){
					TCPStream stream = streamPool.getOrCreateStream(event);
					streamPool.updateTraffic(stream, event, isTcpFIN);
//					num ++;
//					int type=(int)packet.getByte(76)+(int)packet.getByte(77)+(int)packet.getByte(78);
//					if(type==0){
//						System.out.println("row:"+num);
//						TCPStream stream = streamPool.getOrCreateStream(event);
//						streamPool.updateTraffic(stream, event, isTcpFIN);
//					}
//					else if(type==3) {
//						System.out.println("reply");
//					}else{
//						System.out.println("anything wrong?");
//					}
				}
			}
		}
		streamPool.endAllStream();
		pcap.close();
		System.out.println(fpath + " --> 共" + num + "个包！");
	}
	
	private void directRead2File(String path,String name){
		String title=name.split("\\.")[0];
		TCPStreamPool streamPool=new TCPStreamPool(title);
		String file="trunkPcap/"+name;
		InputStream is=this.getClass().getResourceAsStream(file);
		try {
			PcapParser.unpack(is, streamPool);
		} catch (IOException e) {
			e.printStackTrace();
		}
		streamPool.endAllStream();
		System.out.println(path+" 读取完毕");
	}
	private String ipBytes2Str(byte[] ipBytes){
		if (ipBytes == null)
			return "null";
		int len = ipBytes.length;
		if (len == -1)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++){
			sb.append(((int)ipBytes[i]) & 0xff);
			if ( i < len - 1)
				sb.append(".");
		}
		return sb.toString();
	}
	public PcapUtils() {
	}
	
	private static void listAllPcapDevices(){
		StringBuilder errbuf = new StringBuilder();
		List<PcapIf> ifs = new ArrayList<PcapIf>(); // Will hold list of devices
		int statusCode = Pcap.findAllDevs(ifs, errbuf);
		if (statusCode != Pcap.OK) {
			System.out.println("Error occured: " + errbuf.toString());
			return;
		} else {
			for (PcapIf if1 : ifs)
				System.out.println(if1.getDescription());
		}
	}
}

