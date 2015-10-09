package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum SessionStreamPool {
	instance;
	private Map<SessionKey, SessionStream> aliveStreams=new HashMap<SessionKey, SessionStream>();
	private Map<SessionKey, Long> timeSpan=new HashMap<SessionKey, Long>();
	private boolean writeToFile=true;
	private FileWriter fw;
	private String logFile="./configs/sessionTemp.txt";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	public SessionKey generateKey(String srcIP,String dstIP,int servicePort,String protoType){
		return new SessionKey(srcIP, dstIP, servicePort, protoType);
	}
	
	public SessionKey generateKey(SessionStream stream){
		return generateKey(stream.getSrcIP(),stream.getDstIP(),stream.getServicePort(),stream.getProtoType());
	}
	
	public SessionStream createOrGetSessionStream(SessionStream stream){
		SessionKey key=generateKey(stream);
		if(aliveStreams.containsKey(key)){
			return aliveStreams.get(key);
		}else {
			SessionStream newStream=new SessionStream();
			newStream.setSrcIP(stream.getSrcIP());
			newStream.setDstIP(stream.getDstIP());
			newStream.setServicePort(stream.getServicePort());
			newStream.setFinishStamp(stream.getStartStamp());
			newStream.setStartStamp(stream.getStartStamp());
			newStream.setFIN(stream.isFIN());
			newStream.setInterval(0);
			newStream.setProtoType(stream.getProtoType());
			newStream.setTraffic(stream.getTraffic());
			//设置时间间隔
			if(timeSpan.get(key)!=null){
				newStream.setInterval(newStream.getStartStamp()-timeSpan.get(key));
			}else{
				newStream.setInterval(0); 
			}
			return newStream;
		}
	}
	
	public void updateStream(SessionStream stream, SessionStream event, boolean isFin){
		if(event.getStartStamp()-stream.getFinishStamp()>6*60*1000){
			event.setInterval(event.getStartStamp()-stream.getFinishStamp());//和上一次通话的间隔
			endStream(stream);
			SessionKey key=generateKey(event);
			aliveStreams.put(key, event);
			if(isFin){
				timeSpan.put(key, stream.getFinishStamp());
				endStream(event);
			}
		}else if(event.getStartStamp()-stream.getFinishStamp()>0){
			stream.setTraffic(stream.getTraffic()+event.getTraffic());
			stream.setFinishStamp(event.getFinishStamp());
			if(isFin){
				SessionKey key=generateKey(stream);
				timeSpan.put(key, stream.getFinishStamp());
				endStream(stream);
			}
		}
		
	}
	
	private void endStream(SessionStream stream){
		SessionKey key=generateKey(stream);
		if(writeToFile){
			writeToFile(stream);
		}
		aliveStreams.remove(key);
	}
	
	public void endAllStream(){
		for(SessionStream stream:aliveStreams.values()){
			endStream(stream);
		}
		if(fw!=null){
			try {
				fw.close();
				fw=null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		aliveStreams.clear();
		timeSpan.clear();
	}
	
	private void writeToFile(SessionStream stream){
		try {
			File f = new File(logFile);
			if (f.exists())
				f.delete();
			if (fw == null)
				fw = new FileWriter(new File(logFile), true);
			Date d = new Date(stream.getStartStamp());
			StringBuilder sb = new StringBuilder();
			sb.append(stream.getSrcIP()).append("-");
			sb.append(stream.getDstIP()).append("\t");
			sb.append(stream.getServicePort()).append("\t");
			sb.append(stream.getTraffic()).append("\t");
			sb.append(stream.getInterval()).append("\t");
			sb.append(sdf.format(d)).append("\t");
		    sb.append(stream.getFinishStamp()-stream.getStartStamp()).append("\t");

			fw.write(sb.toString());
		} catch (IOException e) {
			if (fw != null)
				try{fw.close();fw = null;}catch(Exception ee){}
		}finally{			
		}
	}
	
    private void writeToOracle(SessionStream stream){
		
	}
}

