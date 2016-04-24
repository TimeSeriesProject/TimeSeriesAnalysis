package cn.InstFS.wkr.NetworkMining.PcapStatistics;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PcapParser {
	
	public static void unpack(InputStream is,String file,ConcurrentHashMap<RecordKey,Integer>records,HashMap<String,BufferedWriter >bws,String path) throws IOException {
		   byte[] buffer_4 = new byte[4];
		   byte[] buffer_3 = new byte[3];
		   byte[] buffer_2 = new byte[2];
		   byte[] buffer_1 = new byte[1];
		   byte[] buffer_100=new byte[100];
		   byte[] buffer_10 =new byte[10];
		   byte[] buffer = new byte[5000];
		   PcapHeader header = new PcapHeader();
		   int m = is.read(buffer_4);
		   if(m != 4){
		      return;
		   }
		   int count=0;
		   reverseByteArray(buffer_4);
		   header.setMagic(byteArrayToInt(buffer_4, 0));
		   m = is.read(buffer_2);
		   reverseByteArray(buffer_2);
		   header.setMagor_version(byteArrayToShort(buffer_2, 0));
		   m = is.read(buffer_2);
		   reverseByteArray(buffer_2);
		   header.setMinor_version(byteArrayToShort(buffer_2, 0));
		   m = is.read(buffer_4);
		   reverseByteArray(buffer_4);
		   header.setTimezone(byteArrayToInt(buffer_4, 0));
		   m = is.read(buffer_4);
		   reverseByteArray(buffer_4);
		   header.setSigflags(byteArrayToInt(buffer_4, 0));
		   m = is.read(buffer_4);
		   reverseByteArray(buffer_4);
		   header.setSnaplen(byteArrayToInt(buffer_4, 0));
		   m = is.read(buffer_4);
		   reverseByteArray(buffer_4);
		   header.setLinktype(byteArrayToInt(buffer_4, 0));
		//   System.out.println(header.getLinktype());
		   StringBuilder sb=new StringBuilder();
		   int datalength;
		   long num=0;
		   while (m > 0) {
			    datalength=0;
		        PcapData data = new PcapData();
			    m = is.read(buffer_4);
			    if (m < 0) {
		            break;
			    }
			    count++;
//			    if(count%100000==0)
//			    	System.out.println(count);
		    	reverseByteArray(buffer_4);
			    data.setTime_s(byteArrayToLong(buffer_4, 0));
			    m = is.read(buffer_4);
			    reverseByteArray(buffer_4);
			    data.setTime_ms(byteArrayToLong(buffer_4, 0));
			    m = is.read(buffer_4);
			    reverseByteArray(buffer_4);
			    data.setpLength(byteArrayToInt(buffer_4, 0));
			    m = is.read(buffer_4);
			    reverseByteArray(buffer_4);
			    data.setLength(byteArrayToInt(buffer_4, 0));
			    if(header.getLinktype()==9)
			    {
			    	m=is.read(buffer,0,2);
			    	datalength+=2;
			    	if(buffer[0]!=0||buffer[1]!=0x21)
			    	{
			    		m=is.read(buffer, 0, data.getpLength()-datalength);
			    		continue;
			    	}
			    }
			    else if(header.getLinktype()==1)
			    {
			    	 m=is.read(buffer,0,14);
			    	 datalength+=14;
			    	 if(buffer[12]!=0x08||buffer[13]!=0x00)
			    	 {
			    		 m=is.read(buffer, 0, data.getpLength()-datalength);
//			    		 System.out.println(m);
				    		continue;
			    	 }
			    }
			    m=is.read(buffer_2);
			    datalength+=2;
			    m=is.read(buffer_2);
			    datalength+=2;
			    data.setTraffic(byteArrayToShort(buffer_2, 0));
			    m=is.read(buffer_4);//skip in order to read TTL
			    datalength+=4;
			    m=is.read(buffer_1);
			    datalength+=1;
			    data.setTTL(64-buffer_1[0]);
			    m=is.read(buffer_3);
			    datalength+=3;
			    m=is.read(buffer_4);
			    datalength+=4;
			    data.setSrcIP(byteArrayToIP(buffer_4, sb));
			    sb.delete(0, sb.length());
			    m=is.read(buffer_4);
			    datalength+=4;
			    data.setDstIP(byteArrayToIP(buffer_4, sb));
			    sb.delete(0, sb.length());
			    m=is.read(buffer_2);
			    datalength+=2;
			    data.setSrcPort(byteArrayToPort(buffer_2, 0));
			    m=is.read(buffer_2);
			    datalength+=2;
			    data.setDstPort(byteArrayToPort(buffer_2, 0));
			    is.read(buffer,0,data.getpLength()-datalength);
			    if(data.getTraffic()>30){
			    	BufferedWriter bw;
			    	if(!bws.containsKey(data.getSrcIP()+"_"+data.getDstIP()))
			    	{
			    		synchronized(bws)
			    		{
			    			if(!bws.containsKey(data.getSrcIP()+"_"+data.getDstIP()))
			    			{
			    				OutputStreamWriter o =new OutputStreamWriter(new FileOutputStream(path+"\\routesrc\\"+data.getSrcIP()+"_"+data.getDstIP()+".txt"),"UTF-8");
			    				bw = new BufferedWriter(o);
			    				bws.put(data.getSrcIP()+"_"+data.getDstIP(), bw);
			    			}
			    		}
			    	}
			        String curLine = new String();
			        synchronized(bw=bws.get(data.getSrcIP()+"_"+data.getDstIP()))
	        		{
			        	curLine=data.getSrcIP()+","+data.getDstIP()+","+data.getSrcPort()+","+data.getDstPort()+","+data.getTime_s()+","+data.getTime_ms()+","+file+","+data.getTraffic()+","+data.getTTL();
			        	bw.write(curLine);
//			        	if(data.getSrcIP().equals("10.0.10.2")&&data.getDstIP().equals("10.0.2.2"))
//			        	{
//			        		System.out.println(curLine);
//			        		
//			        	}
			        	bw.newLine();
//			        	bw.flush();
	        		}
			    	
			    }
		   }
		   is.close();
		 }

	public static void unpack(InputStream is,TCPStreamPool streamPool) throws IOException {
	   byte[] buffer_4 = new byte[4];
	   byte[] buffer_3 = new byte[3];
	   byte[] buffer_2 = new byte[2];
	   byte[] buffer_1 = new byte[1];
	   byte[] buffer_100=new byte[100];
	   byte[] buffer_10 =new byte[10];
	   byte[] buffer = new byte[5000];
	   PcapHeader header = new PcapHeader();
	   int m = is.read(buffer_4);
	   if(m != 4){
	      return;
	   }
	   reverseByteArray(buffer_4);
	   header.setMagic(byteArrayToInt(buffer_4, 0));
	   m = is.read(buffer_2);
	   reverseByteArray(buffer_2);
	   header.setMagor_version(byteArrayToShort(buffer_2, 0));
	   m = is.read(buffer_2);
	   reverseByteArray(buffer_2);
	   header.setMinor_version(byteArrayToShort(buffer_2, 0));
	   m = is.read(buffer_4);
	   reverseByteArray(buffer_4);
	   header.setTimezone(byteArrayToInt(buffer_4, 0));
	   m = is.read(buffer_4);
	   reverseByteArray(buffer_4);
	   header.setSigflags(byteArrayToInt(buffer_4, 0));
	   m = is.read(buffer_4);
	   reverseByteArray(buffer_4);
	   header.setSnaplen(byteArrayToInt(buffer_4, 0));
	   m = is.read(buffer_4);
	   reverseByteArray(buffer_4);
	   header.setLinktype(byteArrayToInt(buffer_4, 0));
	   System.out.println(header.getLinktype());
	   StringBuilder sb=new StringBuilder();
	   int datalength;
	   long num=0;
	   while (m > 0) {
		    datalength=0;
	        PcapData data = new PcapData();
		    m = is.read(buffer_4);
		    if (m < 0) {
	            break;
		    }
	    	reverseByteArray(buffer_4);
		    data.setTime_s(byteArrayToLong(buffer_4, 0));
		    m = is.read(buffer_4);
		    reverseByteArray(buffer_4);
		    data.setTime_ms(byteArrayToLong(buffer_4, 0));
		    m = is.read(buffer_4);
		    reverseByteArray(buffer_4);
		    data.setpLength(byteArrayToInt(buffer_4, 0));
		    m = is.read(buffer_4);
		    reverseByteArray(buffer_4);
		    data.setLength(byteArrayToInt(buffer_4, 0));
		    if(header.getLinktype()==9) //ppp
		    {
		    	m=is.read(buffer,0,2);
		    	datalength+=2;
		    	if(buffer[0]!=0||buffer[1]!=0x21)
		    	{
		    		m=is.read(buffer, 0, data.getpLength()-datalength);
		    		continue;
		    	}
		    }
		    else if(header.getLinktype()==1) //以太网
		    {
		    	 m=is.read(buffer,0,14);
		    	 datalength+=14;
		    	 if(buffer[12]!=0x08||buffer[13]!=0x00)
		    	 {
		    		 m=is.read(buffer, 0, data.getpLength()-datalength);
		    		 System.out.println(m);
			    		continue;
		    	 }
		    }
		    m=is.read(buffer_2);
		    datalength+=2;
		    m=is.read(buffer_2);
		    datalength+=2;
		    data.setTraffic(byteArrayToShort(buffer_2, 0));
		    m=is.read(buffer_4);//skip in order to read TTL
		    datalength+=4;
		    m=is.read(buffer_1);
		    datalength+=1;
		    data.setTTL(64-buffer_1[0]);
		    m=is.read(buffer_3);
		    datalength+=3;
		    m=is.read(buffer_4);
		    datalength+=4;
		    data.setSrcIP(byteArrayToIP(buffer_4, sb));
		    sb.delete(0, sb.length());
		    m=is.read(buffer_4);
		    datalength+=4;
		    data.setDstIP(byteArrayToIP(buffer_4, sb));
		    sb.delete(0, sb.length());
		    m=is.read(buffer_2);
		    datalength+=2;
		    data.setSrcPort(byteArrayToPort(buffer_2, 0));
		    m=is.read(buffer_2);
		    datalength+=2;
		    data.setDstPort(byteArrayToPort(buffer_2, 0));
//		    int left=data.getpLength()-datalength;//packet data中未读的流长度
		    
//		    int HanNum=left/100;
//		    if(HanNum>0){
//		    	left%=100;
//		    }
//		    int TenNum=left/10;
//		    if(TenNum>0){
//		    	left%=10;
//		    }
//		    int OneNum=left;
//		    for(int i=0;i<HanNum;i++){
//		    	is.read(buffer_100);
//		    }
//		    for(int i=0;i<TenNum;i++){
//		    	is.read(buffer_10);
//		    }
//		    for(int i=0;i<OneNum;i++){
//		    	is.read(buffer_1);
//		    }
		    is.read(buffer,0,data.getpLength()-datalength);
		    if(data.getTraffic()>30){
		    	TCPStream event=streamPool.dataToTcpStream(data);
		    	TCPStream stream=streamPool.getOrCreateStream(data);
		    	streamPool.updateTraffic(stream,event,false);
		    }
	   }
	 }

	 private static int byteArrayToInt(byte[] b, int offset) {
		  int value = 0;
		  for (int i = 0; i < 4; i++) {
		   int shift = (4 - 1 - i) * 8;
		   value += (b[i + offset] & 0x000000FF) << shift; 
		  }
		  return value;
	 }
	 
	 private static long byteArrayToLong(byte[] b,int offset){
		 long value=0;
		 for(int i=0;i<4;i++){
			 int shift=(4-1-i)*8;
			 long temp=b[i+offset]&0x00000000000000FF;
			 value+=temp<<shift;
		 }
		 if(value<0){
			 System.out.println("here");
		 }
		 return value;
	 }
	 
	 private static short byteArrayToShort(byte[] b, int offset) {
		  short value = 0;
		  for (int i = 0; i < 2; i++) {
		   int shift = (2 - 1 - i) * 8;
		   value += (b[i + offset] & 0x000000FF) << shift;
		  }
		  return value;
	 }
	 
	 private static int byteArrayToPort(byte[] b,int offset){
		 int port=0;
		 for(int i=0;i<2;i++){
			 int shift=(2-i-1)*8;
			 port+=(b[i+offset]&0x000000FF)<<shift;
		 }
		 return port;
	 }
	 
	 private static String byteArrayToIP(byte[] b,StringBuilder sb){
		 for(int i=0;i<3;i++){
			 sb.append(b[i]+".");
		 }
		 sb.append(b[3]);
		 return sb.toString();
	 }

		 /**
		  * 反转数组
		  * @param arr
		  */
	 private static void reverseByteArray(byte[] arr){
	      byte temp;
		  int n = arr.length;
		  for(int i=0; i<n/2; i++){
			  temp = arr[i];
			  arr[i] = arr[n-1-i];
			  arr[n-1-i] = temp;
		  }
	 }
}
