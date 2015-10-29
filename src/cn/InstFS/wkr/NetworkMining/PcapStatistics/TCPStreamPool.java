package cn.InstFS.wkr.NetworkMining.PcapStatistics;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import oracle.net.aso.k;
import cn.InstFS.wkr.NetworkMining.DataInputs.PcapData;

public class TCPStreamPool {
	private Map<String, TCPStream> streams=new HashMap<String, TCPStream>();
	private Map<String, TCPStream> aliveStreams=new HashMap<String, TCPStream>();
	private String saveFile="./configs/HTTPPcap/";
	private FileWriter writer;
	private Random rand;
	public TCPStreamPool(String fileName){
		try {
			rand=new Random(150);
			saveFile=saveFile+fileName+".csv";
			
			File file=new File(saveFile);
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}else{
				file.createNewFile();
			}
			if(writer==null){
				writer=new FileWriter(new File(saveFile));
			}
			writer.write("Time(S),srcIP,dstIP,SPort,DPort,traffic,protocol,hops\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private String generateKey(TCPStream stream){
		return generateKey(stream.getSrcIp(), stream.getDstIP(), stream.getSrcPort(), stream.getDstPort(), stream.getProtoType());
	}
	private String generateKey(PcapData data){
		return generateKey(dataToTcpStream(data));
	}
	private String generateKey(String srcIP,String dstIP,String srcPort,String dstPort,String protoType){
		StringBuilder sb=new StringBuilder();
		sb.append(srcIP).append("-")
		.append(dstIP).append("-")
		.append(srcPort).append("-")
		.append(dstPort).append("-")
		.append(protoType);
		return sb.toString();
	}
	
	public TCPStream getOrCreateStream(TCPStream stream){
 		String key=generateKey(stream);
		if(aliveStreams.containsKey(key)){
			return aliveStreams.get(key);
		}else{
			if(aliveStreams.size()>=1){
				Collection<TCPStream> list=aliveStreams.values();
				Iterator<TCPStream> iterator=list.iterator();
				endStream(iterator.next());
				aliveStreams.clear();
			}
			aliveStreams.put(key, stream);
			return stream;
		}
	}
	
	public TCPStream getOrCreateStream(PcapData data){
		TCPStream stream=dataToTcpStream(data);
		String key=generateKey(stream);
		if(aliveStreams.containsKey(key)){
			return aliveStreams.get(key);
		}else{
			if(aliveStreams.size()>=1){
				Collection<TCPStream> list=aliveStreams.values();
				Iterator<TCPStream> iterator=list.iterator();
				endStream(iterator.next());
				aliveStreams.clear();
			}
			aliveStreams.put(key, stream);
			return stream;
		}
	}
	
	public TCPStream getOrCreateStream(long time,String srcIP,String dstIP,String srcPort,String dstPort,String protoType,
			int traffic,int hops){
		String key=generateKey(srcIP, dstIP, srcPort, dstPort, protoType);
		if(aliveStreams.containsKey(key)){
			return aliveStreams.get(key);
		}else{
			TCPStream stream=new TCPStream();
			stream.setElements(time, srcIP, dstIP, srcPort, dstPort, protoType, traffic, hops);
			if(aliveStreams.size()>=1){
				Collection<TCPStream> list=aliveStreams.values();
				Iterator<TCPStream> iterator=list.iterator();
				endStream(iterator.next());
				aliveStreams.clear();
			}
			aliveStreams.put(key, stream);
			return stream;
		}
	}
	
	public TCPStream dataToTcpStream(PcapData data){
		TCPStream stream=new TCPStream();
		long time=data.getTime_s()*1000+data.getTime_ms()/1000;
		if(time<0){
			System.out.println("negative");
		}
		stream.setElements(time, data.getSrcIP(), data.getDstIP(), data.getSrcPort()+"", 
				data.getDstPort()+"", "TCP", data.getTraffic(), data.getTTL());
		return stream;
	}
	
	public void updateTraffic(TCPStream stream,TCPStream event,boolean isFIN){
		if(isFIN){
			endStream(stream);
		}
		if(event.getTime()-stream.getTime()>(10*1000)){
			endStream(stream);
			getOrCreateStream(event);
		}else if(event.getTime()>stream.getTime()){
			int traffic=stream.getTraffic()+event.getTraffic();
			stream.setTraffic(traffic);
		}	
	}
	
	public void endStream(TCPStream stream){
		String key=generateKey(stream);
		saveStream(stream);
		aliveStreams.remove(key);
	}
	/**
	 * save stream to file system
	 * @param stream to be saved
	 * @throws IOException 
	 */
	public void saveStream(TCPStream stream){
		try{
			if(writer==null){
				writer=new FileWriter(new File(saveFile));
			}
			StringBuilder sb=new StringBuilder();
			sb.append((stream.getTime())/1000).append(",");
			sb.append(stream.getSrcIp()).append(",");
			sb.append(stream.getDstIP()).append(",");
			sb.append(stream.getSrcPort()).append(",");
			sb.append(stream.getDstPort()).append(",");
			sb.append(stream.getTraffic()+rand.nextInt(100)).append(",");
			sb.append(stream.getProtoType()).append(",");
			sb.append(stream.getHops()).append("\r\n");//append("\r\n")
			writer.write(sb.toString());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void endAllStream(){
		for(TCPStream stream:aliveStreams.values()){
			saveStream(stream);
		}
		if(writer!=null){
			try {
				writer.close();
				writer=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		aliveStreams.clear();
		streams.clear();
	}
}
