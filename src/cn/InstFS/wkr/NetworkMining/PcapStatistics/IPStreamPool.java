package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;

public enum IPStreamPool {
	instance;
	
	private Map<String,IPStream>streams = new HashMap<String,IPStream>();
	private Map<String,Long>aliveStreamTime = new HashMap<String,Long>();
	private Map<String, IPStream> aliveStream=new HashMap<String, IPStream>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String logFile = "./configs/temp.txt";
	private static String DB_TABLE = "事件2";
	
	private FileWriter fw;
	private OracleUtils oracle;
	private static boolean writeToFile = true;	// 写入文件(true)或写入数据库
	static {
		File f = new File(logFile);
		if (f.exists())
			f.delete();
	}
	
	public Map<String,IPStream> getAllStreams(){
		return aliveStream; //返回现有的stream
	}
	/**
	 * 给定ip通信事件，查询是否有已经在统计的数据流<br>
	 * 若有，则返回<br>
	 * 若无，则创建数据流
	 * @param stream
	 * @return
	 */
	public IPStream createOrGetIPStream(IPStream stream){
		return createOrGetIPStream(stream.getSrcIP(), stream.getDstIP(),
				stream.getProtoType(),stream.getHops(),stream.getTraffic(),stream.getTimeStart());
	}
	public IPStream createOrGetIPStream(String srcIP, String dstIP, 
			String protoType,long hops,long traffic,long timeStamp){
		String key = getKeyFromTcpElems(srcIP,dstIP,protoType);
		if (aliveStream.containsKey(key)){
//			long d = aliveStreamTime.get(key);
//			key += "_" + d;
//			return streams.get(key);
			return aliveStream.get(key);
		}else{
			IPStream stream = new IPStream();
			stream.setKeyElement(srcIP, dstIP,protoType,hops,traffic,timeStamp);
			stream.setTimeEnd(timeStamp);
			stream.setTimeStart(timeStamp);
			aliveStream.put(key, stream);
//			aliveStreamTime.put(key, timeStamp);
//			key += "_" + timeStamp;
//			streams.put(key, stream);
			return stream;
		}
	}
	
	public IPStream getIPStream(String srcIP, String dstIP, 
			int srcPort, int dstPort, String protoType){
		String key = getKeyFromTcpElems(srcIP,dstIP,protoType);
//		if (!aliveStreamTime.containsKey(key))
//			return null;
//		long d = aliveStreamTime.get(key);
//		key += "_" + d;
//		return streams.get(key);
		
		if(aliveStream.containsKey(key)){
			return aliveStream.get(key);
		}else{
			return null;
		}
	}
	/**
	 * 更新数据流的流量等信息
	 * @注意 在更新之前，<br>
	 * 会首先检查新包的时间是否距离上一个包太长时间（如2分钟）（注意看下这个功能有没有被注释掉），如果太长，则结束该数据流，创建新的数据流<br>
	 * 还会根据TCP的FIN标志来判断流是否结束
	 * @param stream	待更新的数据流
	 * @param ipEvent	一次通联
	 */
	public IPStream updateTraffic(IPStream stream, IPStream ipEvent, boolean isTcpFIN){
		if (isTcpFIN){
			endStream(stream);
		}
		if (ipEvent.getTimeStart()-stream.getTimeEnd()>6*60*1000){//大于6分钟
			endStream(stream);
			IPStream stream_new = createOrGetIPStream(ipEvent);
			return stream_new;
		}else if(ipEvent.getTimeStart()-stream.getTimeEnd()>0){//区分ipEvent和Stream是不是同一个stream
			long traffic=stream.getTraffic() + ipEvent.getTraffic();
			stream.setTraffic(traffic);
			stream.setTimeEnd(ipEvent.getTimeStart());
		}
		
		return stream;
	}
	public void endStream(IPStream stream){
		String key = getKeyFromTcp5Elems(stream);
		write1Stream(stream);
		aliveStream.remove(key);
	}
	private String getKeyFromTcpElems(String srcIP, String dstIP, 
			String protoType){
		StringBuilder sb = new StringBuilder();
		sb.append(srcIP).append("-")
			.append(dstIP).append("\t")
			.append(protoType);
		return sb.toString();
	}
	private String getKeyFromTcp5Elems(IPStream stream){
		return getKeyFromTcpElems(stream.getSrcIP(), stream.getDstIP(),
			stream.getProtoType());
	}
	private void write1Stream(IPStream stream){
		if (writeToFile)
			write1Stream2File(stream);
		else
			write1Stream2Oracle(stream);
	}
	private void write1Stream2Oracle(IPStream stream){
		if (oracle == null){
			oracle = new OracleUtils();
			oracle.setDB_TABLE(DB_TABLE);
		}		
		oracle.openConn();
		String sql = "INSERT INTO " + oracle.DB_TABLE +
				" (事件发生时间,事件结束时间,所在网络层级,发送节点编号,接收节点编号,流量,协议类型) values"+
				" ('" + sdf.format(new Date(stream.getTimeStart())) +"',"+
				"'" + sdf.format(new Date(stream.getTimeEnd()))+ "'," +
				"'" + "200" + "'," +	// 协议层
				"'" + stream.getSrcIP() + "'," +
				"'" + stream.getDstIP() + "'," +
				stream.getTraffic() + "," +
				"'" + stream.getProtoType() + "'" +
				")";
		
		oracle.sqlUpdate(sql);
	}
	private void write1Stream2File(IPStream stream){		
		try {
			if (fw == null)
				fw = new FileWriter(new File(logFile), true);
			Date d = new Date(stream.getTimeStart());
			Date d2 = new Date(stream.getTimeEnd());
			StringBuilder sb = new StringBuilder();
			sb.append(sdf.format(d))
			.append(" - ")
			.append(sdf.format(d2)).append("\t");
			sb.append(getKeyFromTcp5Elems(stream));
			sb.append("\t").append(stream.getTraffic());
			sb.append("\t").append(stream.getHops()).append("\r\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			if (fw != null)
				try{fw.close();fw = null;}catch(Exception ee){}
		}finally{			
		}
	}
	
	public void endAllStreams(){
		for (IPStream stream : aliveStream.values())
			write1Stream(stream);
		if (fw != null)
			try {
				fw.close();
				fw = null;
			} catch (Exception ee) {
			}
		if (oracle != null){
			oracle.closeConn();
			oracle = null;
		}
		aliveStreamTime.clear();
		streams.clear();
	}
	
	
	
	
}
