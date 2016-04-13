package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import oracle.net.ns.Packet;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JBinding;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.wan.PPP;
import org.openide.text.CloneableEditorSupport.Pane;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStream;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStreamPool;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.TCPStream;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.TCPStreamPool;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
class Parser implements Callable
{
	InputStream is=null;
	String file;
	ConcurrentHashMap<RecordKey,Integer>records ;
	private HashMap<String,BufferedWriter> bws;
	String path;
	Parser(InputStream is,String file,ConcurrentHashMap<RecordKey,Integer>records,HashMap<String,BufferedWriter> bws,String path)
	{
		this.is=is;
		this.file=file;
		this.records=records;
		this.bws=bws;
		this.path=path;
	}
	public Boolean call()
	{
		try {
			PcapParser.unpack(is,file,records,bws,path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
class RouteGen implements Callable
{
	
	String path;
	String outpath;
	ConcurrentHashMap<RecordKey,Integer>records ;
	TreeMap<PcapData,String> datas= new TreeMap<PcapData,String>();
	RouteGen(String path,String outpath,ConcurrentHashMap<RecordKey,Integer>records )
	{
		this.path=path;
		this.outpath=outpath;
		this.records=records;
	}
	private void updaterecords(PcapData pre)
	{
		RecordKey tmpKey1= new RecordKey(pre.getSrcIP(),pre.getDstIP(),pre.getDstPort(),pre.getTime_s()/3600);
    	RecordKey tmpKey2= new RecordKey(pre.getDstIP(),pre.getSrcIP(),pre.getDstPort(),pre.getTime_s()/3600);
//    	System.out.println(records);
    	if(!records.containsKey(tmpKey1))
    	{
    		records.put(tmpKey1, 0);
    	}
    	if(!records.containsKey(tmpKey2))
    	{
    		records.put(tmpKey2, 0);
    	}
    	records.put(tmpKey1, records.get(tmpKey1)+pre.getTraffic());
    	records.put(tmpKey2, records.get(tmpKey2)+pre.getTraffic());
	}
	private void gen() throws IOException
	{
		File f =new File(path);
		String file= f.getName();
		int index = file.lastIndexOf(".");
		file = file.substring(0,index);
		OutputStreamWriter o =new OutputStreamWriter(new FileOutputStream(outpath+"\\route"+"\\"+file+".csv"),"UTF-8");
		BufferedWriter bw = new BufferedWriter(o);
		PcapData pre =null;
		HashSet<String> set =null;
		String curLine;
		curLine ="Time(S),srcIP,dstIP,traffic,hops";
		bw.write(curLine);
		bw.newLine();
		int num=0;
		int count=0;
		for(Map.Entry<PcapData, String>entry:datas.entrySet())
		{
			count++;
			if(count%100000==0)
				System.out.println("genroute "+count);
			PcapData data =entry.getKey();
			if(pre==null)
			{
				curLine=","+entry.getValue()+":"+data.getTTL(); 
				pre=entry.getKey();
				num=1;
				continue;
			}
			
			if(!pre.getSrcIP().equals(data.getSrcIP())||!pre.getDstIP().equals(data.getDstIP())||pre.getSrcPort()!=data.getSrcPort()||pre.getDstPort()!=data.getDstPort())
			{
				updaterecords(pre);
				bw.write(String.valueOf(pre.getTime_s())+","+pre.getSrcIP()+","+pre.getDstIP()+","+pre.getTraffic()+","+num+curLine);
				bw.newLine();
				curLine=","+entry.getValue()+":"+data.getTTL();
				pre=data;
				num=1;
			}
			else if((double)data.getTime_s()+data.getTime_ms()/1000000.0>pre.getTime_s()+pre.getTime_ms()/1000000.0+2.0)
			{
				updaterecords(pre);
		    	
				bw.write(String.valueOf(pre.getTime_s())+","+pre.getSrcIP()+","+pre.getDstIP()+","+pre.getTraffic()+","+num+curLine);
				bw.newLine();
				curLine=","+entry.getValue()+":"+data.getTTL();
				pre=data;
				num=1;
			}
			else
			{
				curLine+=","+entry.getValue()+":"+data.getTTL();
				num++;
			}
		}
		updaterecords(pre);
		
		bw.write(String.valueOf(pre.getTime_s())+","+pre.getSrcIP()+","+pre.getDstIP()+","+pre.getTraffic()+","+num+curLine);
		bw.close();
	}
	public Boolean call()
	{
		try {
			
			InputStreamReader in =new InputStreamReader(new FileInputStream(path),"UTF-8");
			BufferedReader bin = new BufferedReader(in);
			String curLine =null;
			int count=0;
			
			while((curLine=bin.readLine())!=null)
			{
				count++;
				if(count%100000==0)
					System.out.println("readsrc "+count);
//				System.out.println(curLine);
				if(curLine.length()<2)
					continue;
				String str[]= curLine.split(",");
//				System.out.println(str.length);
				PcapData data = new PcapData();
//				for(int i=0;i<str.length;i++)
//					System.out.println(str[i]);
				data.setSrcIP(str[0]);
				data.setDstIP(str[1]);
				data.setSrcPort(Integer.parseInt(str[2]));
				data.setDstPort(Integer.parseInt(str[3]));
				data.setTime_s(Long.parseLong(str[4]));
				data.setTime_ms(Long.parseLong(str[5]));
				data.setTTL(Integer.parseInt(str[8]));
				data.setTraffic(Integer.valueOf(str[7]));
				datas.put(data,str[6]);
				
			}
			gen();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("........");
			e.printStackTrace();
		}
		return true;
	}
}
/**
 * pcap文件操作类
 * @author wsc
 *
 */
public class PcapUtils {
	private boolean SessionLevel=true;   //判断读取的数据是否是业务层数据
	private ConcurrentHashMap<RecordKey,Integer> records=new ConcurrentHashMap<RecordKey,Integer>();
	TreeMap<RecordKey,Integer> sortedrecords=new TreeMap<RecordKey,Integer> ();
	private ArrayList<File> fileList=new ArrayList<File>();
	private HashMap<String,BufferedWriter> bws=new HashMap<String,BufferedWriter>();
	
	public static void main(String [] args) throws FileNotFoundException{
		String fpath = "C:\\data\\pcap";
		PcapUtils pcapUtils = new PcapUtils();
		//pcapUtils.readInput(fpath,1);
		pcapUtils.readInput(fpath, "C:\\data\\out");
		//pcapUtils.generateRoute("C:\\data\\out\\routesrc","C:\\data\\out");
	}
	
	private void generateRoute(String fpath,String outPath)
	{
//		System.out.println("ggg");
		fileList.clear();
		getFileList(fpath,"txt");
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<Boolean>> results= new ArrayList<Future<Boolean>>(); 
		for(int i=0;i<fileList.size();i++)
		{
//			System.out.println(fileList.get(i).getAbsolutePath());
			RouteGen routeGen= new RouteGen(fileList.get(i).getAbsolutePath(),outPath,records); 
			routeGen.call();
			results.add(exec.submit(routeGen));
		}
		for(int i=0;i<results.size();i++)
		{
			try {
				results.get(i).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
			}
			finally{
				exec.shutdown();
			}
		}
	}
	private void parsePcap(String fpath,String outpath) throws FileNotFoundException
	{
		getFileList(fpath,"pcap");
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<Boolean>> results= new ArrayList<Future<Boolean>>(); 
		for(int i=0;i<fileList.size();i++)
		{
			InputStream is=new FileInputStream(fileList.get(i));
			String file= fileList.get(i).getName();
			int index = file.lastIndexOf(".");
			file = file.substring(0,index);
			
			System.out.println(file);
			
			Parser parser= new Parser(is,file,records,bws,outpath);
			results.add(exec.submit(parser));
			
		}
		for(int i=0;i<results.size();i++)
		{
			try {
				results.get(i).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
			}
			finally{
				exec.shutdown();
			}
		}
	}
	private void generateTraffic(String outpath)
	{
		
		for(Map.Entry<RecordKey, Integer>entry:records.entrySet())
		{
			sortedrecords.put(entry.getKey(),entry.getValue());
		}
		for(Map.Entry<String, BufferedWriter>entry:bws.entrySet())
		{
			try {
				entry.getValue().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("sorted"+sortedrecords.size());
		RecordKey prekey = null;
		OutputStreamWriter o =null;
		BufferedWriter bw=null;
		int sum=0;
		StringBuilder curLine=new StringBuilder();
		for(Map.Entry<RecordKey, Integer>entry:sortedrecords.entrySet())
		{
			RecordKey key = entry.getKey();
			 if(prekey==null||!prekey.getSrcIp().equals(key.getSrcIp())||!prekey.getTime().equals(key.getTime())||!prekey.getDstIp().equals(key.getDstIp()))
			{
			    try {
			    	if(prekey!=null)
			    	{
				    	curLine.append("sum:"+sum);
						bw.write(curLine.toString());
						bw.newLine();
			    	}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sum=0;
				curLine.delete( 0, curLine.length() );
				curLine.append(key.getTime()+","+key.getSrcIp()+","+key.getDstIp()+",");
			}
			if(prekey==null||!(prekey.getSrcIp()).equals(key.getSrcIp()))
			{
				 try {
					if(prekey!=null)
						 bw.close();
					o =new OutputStreamWriter(new FileOutputStream(outpath+"\\traffic"+"\\"+key.getSrcIp()+".txt"),"UTF-8");
					bw = new BufferedWriter(o);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		  
			sum+=entry.getValue();
			curLine.append(key.getProtocol()+":"+String.valueOf(entry.getValue())+";");
			prekey=key;
		}
		if(prekey!=null)
		{
			curLine.append("sum:"+sum);
			try {
				bw.write(curLine.toString());
				bw.newLine();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public void readInput(String fpath,String outpath) throws FileNotFoundException
	{
		
	    File folder = new File(outpath+"\\routesrc");
	    boolean suc= (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	    
		folder = new File(outpath+"\\route");
		suc= (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();

		folder = new File(outpath+"\\traffic");
		suc= (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
		
		parsePcap(fpath,outpath);
		generateRoute(outpath+"\\routesrc",outpath);
		generateTraffic(outpath);
	}
	private void getFileList(String fpath,String type)
	{
		File ff = new File(fpath);
		if(ff.isFile()&&fpath.endsWith(type))
		{
			fileList.add(ff);
		}
		else if(ff.isDirectory())
		{
			File []files = ff.listFiles();
			for(File f : files)
			{
				String path=f.getPath();
				getFileList(f.getAbsolutePath(),type);
			}
		}
	}
	
	/**
	 * parse given file 
	 * @param fpath file path
	 * @param type stand for file properties,1 means trunkFiles and 0 means others  
	 * @throws FileNotFoundException 
	 */
	private void readInput(String fpath,int type) throws FileNotFoundException{
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
		//System.out.println(title);
		TCPStreamPool streamPool=new TCPStreamPool(title);
		StringBuilder errSb = new StringBuilder();
		Pcap pcap = Pcap.openOffline(fpath, errSb);
	//	System.out.println("god"+fpath);
		PcapPacket packet = new PcapPacket(JMemory.POINTER);
		long num = 0;
		if (pcap == null){

		//	System.out.println("good");
			System.out.println("Error:\t" + errSb.toString());
			return;
		}
		while(pcap.nextEx(packet) == Pcap.NEXT_EX_OK ){
			TCPStream event=new TCPStream();
			boolean smtp = false;
			boolean isTcpFIN = false;
			Ip4 ip4 = new Ip4();
			Udp udp = new Udp();
			PPP ppp = new PPP();
			System.out.println(packet.getHeaderIdByIndex(1)+" "+Ip4.ID);
			System.out.println(packet.hasHeader(ppp));
		//	packet.get
		//	System.out.println(ppp.hasSubHeader(ip4));
			//System.out.println(packet.hassubHeader(Ip4.ID));
			//JBinding b[]=new JBinding[20];

		//	b=JRegistry.getBindings(2);
			byte b[]=new byte[2000];
			b=packet.getByteArray(0, packet.getAllocatedMemorySize());
			for(int i=0;i<b.length;i++)
			{
				System.out.println(b[i]);
			}
			
			System.out.println(packet.getByteArray(0, packet.getAllocatedMemorySize()));
			if (packet.hasHeader(ip4)&&packet.hasHeader(udp)){	
				System.out.println("gggggg");
				smtp = true;
				int TTL=ip4.ttl();
				int hops=64-TTL;
				long timeStamp = packet.getCaptureHeader().timestampInMillis();
				event.setTime(timeStamp);
				event.setHops(hops);
				event.setSrcIp(ipBytes2Str(ip4.source()));
				event.setDstIP(ipBytes2Str(ip4.destination()));
				event.setTraffic(ip4.length());
				event.setSrcPort(udp.source()+"");
				event.setDstPort(udp.destination()+"");
				int protoType=(udp.source()<udp.destination())?udp.source():udp.destination();
				event.setProtoType(protoType+"");	
			}
			if (smtp){
				//大于80B的包视为数据包
				if(event.getTraffic()>=30){
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
	
	private void directRead2File(String path,String name) throws FileNotFoundException{
		String title=name.split("\\.")[0];
		TCPStreamPool streamPool=new TCPStreamPool(title);
		//String file="trunkPcap/"+name;
		InputStream is=new FileInputStream(new File(path));
		//InputStream is=this.getClass().getResourceAsStream(path);
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

