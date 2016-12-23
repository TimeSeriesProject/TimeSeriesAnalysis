package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Logger;
import RTreeUtil.TimeSeries;
import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStreamPool;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;


/**
 * 节点对的读取类
 * @author wsc
 */
public class nodePairReader implements IReader {
	TaskElement task;
	OracleUtils conn;
	DataItems data;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String[] ipPair;
	boolean textSource;
	private String filePath;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public nodePairReader(TaskElement task,String[] ipPair) {
		this.task=task;
		this.ipPair=ipPair;
		if(task.getDataSource().equals("DataBase")){
			textSource=false;
			conn=new OracleUtils();
			if(!conn.tryConnect()){
				throw new RuntimeException("数据库无法连接");
			}
		}else{
			textSource=true;
		}
	}
	
	public nodePairReader(){}
	
	public DataItems readInputAfter(Date date){
		Date dEnd;
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		if (task.getDateEnd() == null || task.getDateEnd().equals(cal.getTime()))
			dEnd = UtilsSimulation.instance.getCurTime();
		else
			dEnd = task.getDateEnd();
		if (dEnd.after(UtilsSimulation.instance.getCurTime()))
			dEnd = UtilsSimulation.instance.getCurTime();
		return readInputBetween(date, dEnd);
	}
	
	public DataItems readInputBefore(Date date){
		Date dStart;
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		
		// 如果没有设置在任务里设置时间，则采用软件总体的配置
		if (task.getDateStart()==null||task.getDateStart().equals(cal.getTime()))	
			dStart = UtilsSimulation.instance.getStartTime();
		else	// 如果在任务里设置了起始时间，则按任务的来
			dStart = task.getDateStart();
		
		return readInputBetween(dStart, date);
	}
	
	
	/**
	 * 读取两个时间段之间的数据
	 * @param date1 起始时间
	 * @param date2 结束时间
	 * @return
	 */
	public DataItems readInputBetween(Date date1, Date date2){
		if(textSource){
			Calendar calendar=Calendar.getInstance();
			calendar.set(2014, 9, 1, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Calendar start=Calendar.getInstance();
			Calendar end=Calendar.getInstance();
			start.setTime(date1);
			end.setTime(date2);
			long startTime=(start.getTimeInMillis()-calendar.getTimeInMillis())/1000;
			long endTime=(end.getTimeInMillis()-calendar.getTimeInMillis())/1000;
			String[] conditions=new String[2];
			conditions[0]=("Time(S)>="+startTime);
			conditions[1]=("Time(S)<="+endTime);
			return readInputByText(conditions);
		}else{
			String filter = "事件发生时间>'" + sdf.format(date1) + "' and " +
					"事件发生时间<='" + sdf.format(date2) + "'";
			return readInputBySql(filter);
		}
	}
	
	@Override
	public DataItems readInputBySql() {
		StringBuilder sqlsb=new StringBuilder();
		sqlsb.append("SELECT Time,").append(task.getMiningObject()).append(" from ")
		     .append(conn.DB_TABLE).append(" where ");
		if(task.getFilterCondition().length()>0){
			sqlsb.append(task.getFilterCondition()).append(" and ");
		}
		sqlsb.append(generateFilterSql(ipPair));
		sqlsb.append("1=1 order by 事件发生时间 asc");
		if(!conn.isOpen()){
			conn.openConn();
		}
		ResultSet rs = conn.sqlQuery(sqlsb.toString());
		if (rs == null){
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		try {
			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while(rs.next()){
				numRecords ++;
				StringBuilder sb = new StringBuilder();
				for (int i = 2; i <= numCols; i ++)
					if (rs.getString(i) != null)
						sb.append(rs.getString(i).trim() + ",");
				if (sb.length() > 0){
					Date d = parseTime(rs.getString(1).trim());
					if (d != null)
						data.add1Data(d, sb.substring(0, sb.length() - 1));    
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("共" + numRecords + "条记录！");
		System.out.println("读取完毕:" + data.getLength() + "条记录！");
		conn.closeConn();
		return DataItems.sortByTimeValue(data);
	}
	
	@Override
	public DataItems readInputBySql(String condition) {
		StringBuilder sqlsb=new StringBuilder();
		sqlsb.append("SELECT 事件发生时间,").append(task.getMiningObject()).append(" from ")
		     .append(conn.DB_TABLE).append(" where ");
		sqlsb.append(condition).append(" and ");
		if(task.getFilterCondition().length()>0){
			sqlsb.append(task.getFilterCondition()).append(" and ");
		}
		sqlsb.append(generateFilterSql(ipPair));
		sqlsb.append("1=1 order by 事件发生时间 asc");
		if(!conn.isOpen()){
			conn.openConn();
		}
		ResultSet rs = conn.sqlQuery(sqlsb.toString());
		if (rs == null){
			return null;
		}
		ResultSetMetaData meta = null;
		int numRecords = 0;
		try {
			meta = rs.getMetaData();
			int numCols = meta.getColumnCount();
			data = new DataItems();
			while(rs.next()){
				numRecords ++;
				StringBuilder sb = new StringBuilder();
				for (int i = 2; i <= numCols; i ++)
					if (rs.getString(i) != null)
						sb.append(rs.getString(i).trim() + ",");
				if (sb.length() > 0){
					Date d = parseTime(rs.getString(1).trim());
					if (d != null)
						data.add1Data(d, sb.substring(0, sb.length() - 1));    
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("共" + numRecords + "条记录！");
		System.out.println("读取完毕:" + data.getLength() + "条记录！");
		conn.closeConn();
		return DataItems.sortByTimeValue(data);
	}
	
	@Override
	public DataItems readInputByText() {
		DataItems dataItems=new DataItems();
		String minierObject=task.getMiningObject().trim();
		String protocol=task.getProtocol().trim();
		File sourceFile=null;
		if(filePath==null||filePath.equals("")){
			sourceFile=new File(task.getSourcePath());
			
		}else{
			sourceFile=new File(filePath);
		}
		if(sourceFile.isFile()){
			readFile(sourceFile.getAbsolutePath(), minierObject,protocol, dataItems);
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				readFile(file.getAbsolutePath(), minierObject,protocol, dataItems);
				System.out.println("read file "+file.getName());
			}
		}
		return DataItems.sortByTimeValue(dataItems);
		
	}
	/**
	 * 获取文件下所有通信节点对路径
	 * @return 所有通信节点对路径
	 */
	public Map<String, DataItems> readAllRoute(){
		Map<String, DataItems> dataMap=new HashMap<String, DataItems>();
		Map<String, Date> timeMap=new HashMap<String, Date>();
		String minierObject=task.getMiningObject();
		File sourceFile=null;
		if(filePath==null||filePath.equals("")){
			sourceFile=new File(task.getSourcePath());
			
		}else{
			sourceFile=new File(filePath);
		}
		if(sourceFile.isFile()){
			readFile(sourceFile.getAbsolutePath(), minierObject, dataMap,timeMap);
			System.out.println("read file "+sourceFile.getName());
		}
		return dataMap;
	}
	public Map<String,DataItems > readAllPairByText(String[] conditions)
	{
		Map<String,DataItems> ipPairItems = new HashMap<String,DataItems>();
		String minierObject=task.getMiningObject();
		File sourceFile=new File(task.getSourcePath());
		if(sourceFile.isFile()){
				readFile(sourceFile.getAbsolutePath(), minierObject, ipPairItems,conditions);

		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
			    	readFile(file.getAbsolutePath(), minierObject, ipPairItems,conditions);
				}
			}
		Map<String,DataItems> tmpipPairItems = new HashMap<String,DataItems>();
		for(Map.Entry<String, DataItems> entry:ipPairItems.entrySet())
		{
			System.out.println("god "+entry.getValue().getLength());
			tmpipPairItems.put(entry.getKey(), DataItems.sortByTimeValue(entry.getValue()));
		}
		return tmpipPairItems;
	}
	public Map<String,DataItems > readAllPairBetween(Date date1, Date date2){
		
			Calendar calendar=Calendar.getInstance();
			calendar.set(2014, 9, 1, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Calendar start=Calendar.getInstance();
			Calendar end=Calendar.getInstance();
			start.setTime(date1);
			end.setTime(date2);
			long startTime=(start.getTimeInMillis()-calendar.getTimeInMillis())/1000;
			long endTime=(end.getTimeInMillis()-calendar.getTimeInMillis())/1000;
			String[] conditions=new String[2];
			conditions[0]=("Time(S)>="+startTime);
			conditions[1]=("Time(S)<="+endTime);
			return readAllPairByText(conditions);
		
	}
	@Override
	public DataItems readInputByText(String[] conditions) {
		DataItems dataItems=new DataItems();
		String minierObject=task.getMiningObject();
		File sourceFile=null;
		if(filePath==null||filePath.equals("")){
			sourceFile=new File(task.getSourcePath());
		}else{
			sourceFile=new File(filePath);
		}
		if(sourceFile.isFile()){
			boolean isExist=false;
			for(String ip:ipPair){
				if(isFileContainsIp(ip, sourceFile.getName())){
					isExist=true;
					break;
				}
			}
			if(isExist){
				readFile(sourceFile.getAbsolutePath(), minierObject, dataItems,conditions);
			}
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				boolean isExist=false;
				for(String ip:ipPair){
					if(isFileContainsIp(ip, file.getName())){
//						System.out.println("read file "+file.getName());
						isExist=true;
						break;
					}
				}
				if(isExist){
			    	readFile(file.getAbsolutePath(), minierObject, dataItems,conditions);
				}
			}
		}
		return DataItems.sortByTimeValue(dataItems);
	}
	
	public List<String> directlyRead(String miningObject,String filePath) {
		File sourceFile=new File(filePath);
		List<String> items=new ArrayList<String>();
		if(sourceFile.isFile()){
			readFile(sourceFile.getAbsolutePath(), miningObject,items);
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				readFile(file.getAbsolutePath(),miningObject,items);
			}
		}
		return items;
	}
	
	private Date parseTime(int timeStr){
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		cal.add(Calendar.SECOND,timeStr);
		return cal.getTime();
	}
	
	private Date parseTime(String timeStr){
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.SECOND,Integer.parseInt(timeStr));
		return cal.getTime();
	}

	/**
	 * 转换日期
	 * @param timeSecond 距离startDate的秒数
	 * @param startDate 起始日期的0点时间戳
     * @return
     */
	private Date parseTime(long timeSecond, long startDate) {
		return new Date(timeSecond*1000 + startDate);
	}

	/**
	 * 将起止日期取整到当天0时
	 * @param startDate 起始日期
	 * @param endDate 截止日期
     * @return 向下取整后long型的起止日期
     */
	private long[] floorDate(Date startDate, Date endDate) {
		long[] timestamp = new long[2];
		String dateStr;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 00:00:00");

		try {
			dateStr = sdf.format(startDate);
			timestamp[0] = sdf.parse(dateStr).getTime();
			dateStr = sdf.format(endDate);
			timestamp[1] = sdf.parse(dateStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	/**
	 * 找到字符串数组中某个字符串的位置
	 * @param name 字符串
	 * @param names 字符串数组
	 * @return 字符串在数组中的位置  -1代表没找到
	 */
	private int NameToIndex(String name,String[] names){
		int length=names.length;
		for(int i=0;i<length;i++){
			if(names[i].equals(name)||names[i]==name){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 读取指定IP文件中所有协议流量的DataItems
	 * @param filePath IP文件地址
	 * @return Map<String,DataItems> ,其中key值为协议，value值为DataItems
	 */
	public HashMap<String, DataItems> readEachProtocolTrafficDataItems(String filePath){
		
		HashMap<String, DataItems>protocolDataItems=new HashMap<String, DataItems>();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		textUtils.readByrow();
		String line=null;
		int rows=0;//记录总共读取的行数
		while((line=textUtils.readByrow())!=null){
			String[] items=line.split(",");
			int timeSpan=Integer.parseInt(items[0]);
			rows=timeSpan-0;
			Date time=parseTime(timeSpan*3600);
			String protocolItems=items[items.length-1];
			String[] eachProtocol=protocolItems.split(";");
			for(String protocol:eachProtocol){
				String[] proAndTraffic=protocol.split(":");
				if(proAndTraffic.length==1){
					String traffic=proAndTraffic[0];
					proAndTraffic=new String[2];
					proAndTraffic[0]="1";
					proAndTraffic[1]=traffic;
				}
				if(protocolDataItems.containsKey(proAndTraffic[0])){
					DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
					DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
					//合并同一个IP，同一个协议的通信的流量要合并
					if(dataItem.getTime().toString().equals(time.toString())){
						int traffic=Integer.parseInt(dataItem.getData());
						int addTraffic=Integer.parseInt(proAndTraffic[1]);
						dataItems.getData().set(dataItems.getLength()-1,(traffic+addTraffic)+"");
					}else{
						dataItems.add1Data(time, proAndTraffic[1]);
					}	
				}else{
					DataItems dataItems=new DataItems();
					for(int i=rows-1;i>=0;i--){
						dataItems.add1Data(parseTime(timeSpan-i), "0");
					}
					dataItems.add1Data(time, proAndTraffic[1]);
					protocolDataItems.put(proAndTraffic[0], dataItems);
				}
			}
			Collection<DataItems>values=protocolDataItems.values();
			for(DataItems value:values){
				if(value.getLength()<rows){
					value.add1Data(time,"0");
				}
			}
		}
		if(protocolDataItems == null || protocolDataItems.size() == 0)
		{
			System.out.println("readEachProtocolTrafficDataItems");
			System.out.println("filePath:"+filePath);
		}
		return protocolDataItems;
	}
	
	/**
	 * 读取指定IP文件每对接点中所有协议流量的DataItems
	 * @param filePath IP文件地址
	 * @return Map<String,Map<String, DataItems>> ,其中key值为ip地址对，value值为Map<Key,DataItems>
	 * 其中key为协议  DataItems为时间序列
	 */
	public HashMap<String,Map<String, DataItems>> readEachIpPairProtocolTrafficDataItems(String filePath){
		
		HashMap<String, Map<String, DataItems>>ipPairProtocolDataItems=new HashMap<String, Map<String,DataItems>>();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		textUtils.readByrow();
		String line=null;
		int rows=0;//记录总共读取的行数
		while((line=textUtils.readByrow())!=null){
			String[] items=line.split(",");
			int timeSpan=Integer.parseInt(items[0]);
			rows=timeSpan-0;
			Date time=parseTime(timeSpan*3600);
			String protocolItems=items[items.length-1];
			String[] eachProtocol=protocolItems.split(";");
			String ipPair=items[1]+"-"+items[2];
			for(String protocol:eachProtocol){
				String[] proAndTraffic=protocol.split(":");
				if(ipPairProtocolDataItems.containsKey(ipPair)){
					Map<String, DataItems> protocolDataItems=ipPairProtocolDataItems.get(ipPair);
					if(protocolDataItems.containsKey(proAndTraffic[0])){
						DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
						DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
						if(dataItem.getTime().toString().equals(time.toString())){
							int traffic=Integer.parseInt(dataItem.getData());
							int addTraffic=Integer.parseInt(proAndTraffic[1]);
							dataItems.getData().set(dataItems.getLength()-1,(traffic+addTraffic)+"");
						}else{
							dataItems.add1Data(time, proAndTraffic[1]);
						}	
					}else{
						DataItems dataItems=new DataItems();
						for(int i=rows-1;i>=0;i--){
							dataItems.add1Data(parseTime(timeSpan-i), "0");
						}
						dataItems.add1Data(time, proAndTraffic[1]);
						protocolDataItems.put(proAndTraffic[0], dataItems);
					}
				}else{
					Map<String, DataItems> dataItemsMap=new HashMap<String, DataItems>();
					DataItems dataItems=new DataItems();
					for(int i=rows-1;i>=0;i--){
						dataItems.add1Data(parseTime(timeSpan-i), "0");
					}
					dataItems.add1Data(time, proAndTraffic[1]);
					dataItemsMap.put(proAndTraffic[0], dataItems);
					ipPairProtocolDataItems.put(ipPair, dataItemsMap);
				}
			}
			Iterator<Entry<String, Map<String, DataItems>>> iterator=
					ipPairProtocolDataItems.entrySet().iterator();
			while (iterator.hasNext()) {
				Map<String, DataItems> itemMap=iterator.next().getValue();
				Iterator<Entry<String, DataItems>> mapIterator=itemMap.entrySet().iterator();
				while(mapIterator.hasNext()){
					DataItems item=mapIterator.next().getValue();
					if(item.getLength()<rows){
						item.add1Data(time,"0");
					}
				}
			}
		}
		return ipPairProtocolDataItems;
	}
	
	
	/**
	 * 读取指定IP文件每对接点中所有通信次数的DataItems
	 * @param filePath IP文件地址
	 * @return Map<String,Map<String, DataItems>> ,其中key值为ip地址对，value值为Map<Key,DataItems>
	 * 其中key为协议  DataItems为通信次数时间序列
	 */
	public HashMap<String,Map<String, DataItems>> readEachIpPairProtocolTimesDataItems(String filePath){
		
		HashMap<String, Map<String, DataItems>>ipPairProtocolDataItems=new HashMap<String, Map<String,DataItems>>();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		textUtils.readByrow();
		String line=null;
		int rows=0;//记录总共读取的行数
		while((line=textUtils.readByrow())!=null){
			String[] items=line.split(",");
			int timeSpan=Integer.parseInt(items[0]);
			rows=timeSpan-0;
			Date time=parseTime(timeSpan*3600);
			String protocolItems=items[items.length-1];
			String[] eachProtocol=protocolItems.split(";");
			String ipPair=items[1]+"-"+items[2];
			for(String protocol:eachProtocol){
				String[] proAndTraffic=protocol.split(":");
				if(ipPairProtocolDataItems.containsKey(ipPair)){
					Map<String, DataItems> protocolDataItems=ipPairProtocolDataItems.get(ipPair);
					if(protocolDataItems.containsKey(proAndTraffic[0])){
						DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
						DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
						if(dataItem.getTime().toString().equals(time.toString())){
							int times=Integer.parseInt(dataItem.getData());
							int addTimes=Integer.parseInt(proAndTraffic[2]);
							dataItems.getData().set(dataItems.getLength()-1,(times+addTimes)+"");
						}else{
							dataItems.add1Data(time, proAndTraffic[2]);
						}	
					}else{
						DataItems dataItems=new DataItems();
						for(int i=rows-1;i>=0;i--){
							dataItems.add1Data(parseTime(timeSpan-i), "0");
						}
						dataItems.add1Data(time, proAndTraffic[2]);
						protocolDataItems.put(proAndTraffic[0], dataItems);
					}
				}else{
					Map<String, DataItems> dataItemsMap=new HashMap<String, DataItems>();
					DataItems dataItems=new DataItems();
					for(int i=rows-1;i>=0;i--){
						dataItems.add1Data(parseTime(timeSpan-i), "0");
					}
					dataItems.add1Data(time, proAndTraffic[2]);
					dataItemsMap.put(proAndTraffic[0], dataItems);
					ipPairProtocolDataItems.put(ipPair, dataItemsMap);
				}
			}
			Iterator<Entry<String, Map<String, DataItems>>> iterator=
					ipPairProtocolDataItems.entrySet().iterator();
			while (iterator.hasNext()) {
				Map<String, DataItems> itemMap=iterator.next().getValue();
				Iterator<Entry<String, DataItems>> mapIterator=itemMap.entrySet().iterator();
				while(mapIterator.hasNext()){
					DataItems item=mapIterator.next().getValue();
					if(item.getLength()<rows){
						item.add1Data(time,"0");
					}
				}
			}
		}
		return ipPairProtocolDataItems;
	}
	
	/**
	 * 读取指定IP文件中所有协议通信次数的DataItems
	 * @param filePath IP文件地址
	 * @return Map<String,DataItems> ,其中key值为协议，value值为DataItems
	 */
	public HashMap<String, DataItems> readEachProtocolTimesDataItems(String filePath){
		
		HashMap<String, DataItems>protocolDataItems=new HashMap<String, DataItems>();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		//textUtils.readByrow();
		String line=null;
		int rows=0;//记录总共读取的行数
		while((line=textUtils.readByrow())!=null){
			String[] items=line.split(",");
			int timeSpan=Integer.parseInt(items[0]);
			rows=timeSpan-0;
			Date time=parseTime(timeSpan*3600);
			String protocolItems=items[items.length-1];
			String[] eachProtocol=protocolItems.split(";");
			for(String protocolTraffic:eachProtocol){
				String[] proAndTraffic=protocolTraffic.split(":");
				String protocol=proAndTraffic[0];
				if(protocolDataItems.containsKey(protocol)){
					DataItems dataItems=protocolDataItems.get(protocol);
					DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
					//合并同一个IP，同一个协议的通信的流量要合并
					if(dataItem.getTime().toString().equals(time.toString())){
						int times=Integer.parseInt(dataItem.getData());
						int addTimes=Integer.parseInt(proAndTraffic[1]);
						dataItems.getData().set(dataItems.getLength()-1,(times+addTimes)+"");
					}else if(dataItem.getTime().before(time)){
						Date addtime=DataPretreatment.getDateAfter(dataItem.getTime(),3600*1000);
						while(!addtime.toString().equals(time.toString())&&addtime.before(time)){
							dataItems.add1Data(addtime,"0");
							addtime=DataPretreatment.getDateAfter(addtime, 3600*1000);
						}
						dataItems.add1Data(addtime, proAndTraffic[1]);
					}else{
						throw new RuntimeException("读入文件"+filePath+"第"+rows+"行发生时间错位");
					}	
				}else{
					DataItems dataItems=new DataItems();
					for(int i=rows-1;i>=0;i--){
						dataItems.add1Data(parseTime(timeSpan-i), "0");
					}
					dataItems.add1Data(time, proAndTraffic[1]);
					protocolDataItems.put(protocol, dataItems);
				}
			}
			Collection<DataItems>values=protocolDataItems.values();
			for(DataItems value:values){
				if(value.getLength()<rows){
					value.add1Data(time,"0");
				}
			}
		}
		if(protocolDataItems == null || protocolDataItems.size() == 0)
		{
			System.out.println("readEachProtocolTimesDataItems");
			System.out.println("filePath:"+filePath);
		}
		return protocolDataItems;
	}
	
	private void readFile(String filePath,String miningObejct,List<String> items){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(miningObejct, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		String line=null;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			items.add(columns[minerObjectIndex]);
		}
	}
	
	
	public Map<String, DataItems> readPath(String filePath, String mining){
		Map<String, DataItems> dataMap = new HashMap<String, DataItems>();
		Map<String, Date> timeMap = new HashMap<String, Date>();
		DataItems di;
		String minerObject = "path";
		if(mining.equals(MiningObject.MiningObject_Traffic.toString()))
			minerObject = "path:traffic";
		
		readFile(filePath, minerObject,dataMap, timeMap);
		
		return dataMap;
	}

	public Map<String, DataItems> readPath(String filePath, String mining, boolean isReadBetween, Date startDate, Date endDate) {
		Map<String, DataItems> dataMap = new HashMap<String, DataItems>();
		String minerObject = "path";
		if(mining.equals(MiningObject.MiningObject_Traffic.toString()))
			minerObject = "path:traffic";

		readFile(filePath, minerObject,dataMap, isReadBetween, startDate, endDate);

		return dataMap;
	}
	/**
	 * 读取给定文件下所有通信节点对的通信路径
	 * @param filePath  文件地址 
	 * @param minierObject 节点对通信路径
	 * @param dataMap  存放每对通信节点对的路径序列
	 * @param timeMap  按小时聚合，防止数据量过大
	 */
	private void readFile(String filePath,String minierObject,Map<String, DataItems> dataMap,Map<String, Date> timeMap){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		String[] miningObject = minierObject.split(":");  //miningObject针对路径次数与路径流量 若miningObject[1]为空，则指代路径次数
		int minerObjectIndex=NameToIndex(miningObject[0], columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TrafficColIndex=NameToIndex("traffic",columns);
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP DIP 属性在文件中未找到");
		}
		String line=null;
		
		DataItems dataItems=null;
		Date deadDate=null;
		List<Integer> nodeList=new ArrayList<Integer>();
		List<Integer> hopsList=new ArrayList<Integer>();
		StringBuilder sb=new StringBuilder();
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			int time=Integer.parseInt(columns[TimeColIndex]);
			String key=columns[SIPColIndex]+","+columns[DIPColIndex];
			if(dataMap.containsKey(key)){
				dataItems=dataMap.get(key);
				deadDate=timeMap.get(key);
			}else{
				dataItems=new DataItems();
				dataItems.setVarSet(new HashSet<String>());
				dataItems.setIsAllDataDouble(-1);
				dataMap.put(key, dataItems);
				deadDate=parseTime(time);
				deadDate=DataPretreatment.getDateAfter(deadDate, 3600*1000);
				timeMap.put(key, deadDate);
			}
			
			//跳过本节点 直接转到路由器节点
			for(int j=minerObjectIndex+1;j<columns.length-1;j++)
			{
				int len=nodeList.size();
				int node = Integer.valueOf(columns[j].split("-")[0]);
				int hops = Integer.valueOf(columns[j].split(":")[1]);
				if(len>0&&nodeList.get(len-1)==node){
					hopsList.set(len-1, hops);
				}else{
					nodeList.add(node);
					hopsList.add(hops);
				}
			}
			sb.append(columns[SIPColIndex]).append(",");
			int start=1;
			

			for(int i=0;i<nodeList.size();i++){
				int hops=hopsList.get(i);
				for(int pos=start;pos<hops;pos++)
					sb.append("*").append(",");
				sb.append(nodeList.get(i)).append(",");
				start=hops+1;
			}
			sb.append(columns[DIPColIndex]);
			String path=sb.toString();
			dataItems.getVarSet().add(path);
			int len=dataItems.getLength();
			int aggregateNum = 1;

			if(miningObject.length > 1 && miningObject[1].toLowerCase().equals("traffic"))
				aggregateNum = Integer.parseInt(columns[TrafficColIndex]);
			
			if(parseTime(time).before(deadDate)&&len>0){
				Map<String, Integer> data=dataItems.getNonNumData().get(len-1);
				if(data.containsKey(path)){
					int originValue=data.get(path);
					data.put(path, originValue+aggregateNum);
				}else{
					data.put(path,aggregateNum);
				}
			}else{
				if(len==0){
					dataItems.getTime().add(parseTime(time));
					Map<String, Integer> data=new HashMap<String, Integer>();
					data.put(path, aggregateNum);
					dataItems.getNonNumData().add(data);
				}else{
					while(!parseTime(time).before(deadDate)){
						dataItems.getTime().add(deadDate);
						Map<String, Integer> data=new HashMap<String, Integer>();
						dataItems.getNonNumData().add(data);
						deadDate=DataPretreatment.getDateAfter(deadDate, 3600*1000);
						timeMap.put(key, deadDate);
					}
					int size=dataItems.getNonNumData().size();
					Map<String, Integer> data=dataItems.getNonNumData().get(size-1);
					data.put(path, aggregateNum);
				}
			}
			sb.delete(0, sb.length());
			nodeList.clear();
			hopsList.clear();
		}
	}

	/**
	 *
	 * @param filePath ip文件夹地址
	 * @param minierObject
	 * @param dataMap
	 * @param isReadBetween
	 * @param startDate
     * @param endDate
     */
	private void readFile(String filePath,String minierObject,Map<String, DataItems> dataMap, boolean isReadBetween, Date startDate, Date endDate){
		Map<String, Date> timeMap = new HashMap<>();

		/**
		 * 读取文件时间段
		 */
		if(isReadBetween==false)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return ;
			}
			
			startDate = new Date(startTime);
			endDate = new Date(endTime);
		}

		long[] fileDays = floorDate(startDate, endDate);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay;


		String line=null;
		DataItems dataItems=null;
		Date deadDate=null;
		List<Integer> nodeList=new ArrayList<Integer>();
		List<Integer> hopsList=new ArrayList<Integer>();
		StringBuilder sb=new StringBuilder();
		for (int k = 0; fileDay <= endDay; k++){
			String fileName = fileDay+".csv";
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
//			System.out.println("readFile:"+ filePath+"\\"+ fileName);

			String header=textUtils.readByrow();
			if (header == null){
				fileDay += 86400000;
				continue;
			}
			String[] columns=header.split(",");
			String[] miningObject = minierObject.split(":");  //miningObject针对路径次数与路径流量 若miningObject[1]为空，则指代路径次数
			int minerObjectIndex=NameToIndex(miningObject[0], columns);
			if(minerObjectIndex==-1){
				throw new RuntimeException("未找到挖掘对象");
			}
			int TrafficColIndex=NameToIndex("traffic",columns);
			int TimeColIndex=NameToIndex("Time(S)", columns);
			int SIPColIndex=NameToIndex("srcIP", columns);
			int DIPColIndex=NameToIndex("dstIP", columns);
			if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
				throw new RuntimeException("Time SIP DIP 属性在文件中未找到");
			}


			while((line=textUtils.readByrow())!=null){
				columns=line.split(",");
				long time=Integer.parseInt(columns[TimeColIndex]) + (long)24*3600*k; //单位为s
				Date timeDate = parseTime(time, startDay);

				if (isReadBetween) {
					if (timeDate.compareTo(startDate) < 0 || timeDate.compareTo(endDate) > 0) {
						continue;
					}
				}
				String key=columns[SIPColIndex]+","+columns[DIPColIndex];
				if(dataMap.containsKey(key)){
					dataItems=dataMap.get(key);
					deadDate=timeMap.get(key);
				}else{
					dataItems=new DataItems();
					dataItems.setVarSet(new HashSet<String>());
					dataItems.setIsAllDataDouble(-1);
					dataMap.put(key, dataItems);
					deadDate= timeDate;
					deadDate=DataPretreatment.getDateAfter(deadDate, 3600*1000);
					timeMap.put(key, deadDate);
				}

				//跳过本节点 直接转到路由器节点
				for(int j=minerObjectIndex+1;j<columns.length-1;j++)
				{
					int len=nodeList.size();
					int node = Integer.valueOf(columns[j].split("-")[0]);
					int hops = Integer.valueOf(columns[j].split(":")[1]);
					if(len>0&&nodeList.get(len-1)==node){
						hopsList.set(len-1, hops);
					}else{
						nodeList.add(node);
						hopsList.add(hops);
					}
				}
				sb.append(columns[SIPColIndex]).append(",");
				int start=1;


				for(int i=0;i<nodeList.size();i++){
					int hops=hopsList.get(i);
					for(int pos=start;pos<hops;pos++)
						sb.append("*").append(",");
					sb.append(nodeList.get(i)).append(",");
					start=hops+1;
				}
				sb.append(columns[DIPColIndex]);
				String path=sb.toString();
				dataItems.getVarSet().add(path);
				int len=dataItems.getLength();
				int aggregateNum = 1;

				if(miningObject.length > 1 && miningObject[1].toLowerCase().equals("traffic"))
					aggregateNum = Integer.parseInt(columns[TrafficColIndex]);

				if(timeDate.before(deadDate)&&len>0){
					Map<String, Integer> data=dataItems.getNonNumData().get(len-1);
					if(data.containsKey(path)){
						int originValue=data.get(path);
						data.put(path, originValue+aggregateNum);
					}else{
						data.put(path,aggregateNum);
					}
				}else{
					if(len==0){
						dataItems.getTime().add(timeDate);
						Map<String, Integer> data=new HashMap<String, Integer>();
						data.put(path, aggregateNum);
						dataItems.getNonNumData().add(data);
					}else{
						while(!timeDate.before(deadDate)){
							dataItems.getTime().add(deadDate);
							Map<String, Integer> data=new HashMap<String, Integer>();
							dataItems.getNonNumData().add(data);
							deadDate=DataPretreatment.getDateAfter(deadDate, 3600*1000);
							timeMap.put(key, deadDate);
						}
						int size=dataItems.getNonNumData().size();
						Map<String, Integer> data=dataItems.getNonNumData().get(size-1);
						data.put(path, aggregateNum);
					}
				}
				sb.delete(0, sb.length());
				nodeList.clear();
				hopsList.clear();
			}
			fileDay += 86400000; // 下一天的文件名时间戳
		}



	}
	
	/**
	 * 读取文件
	 * @param filePath 文件路径
	 * @param minierObject 要读取的属性
	 * @param dataItems 读到的序列
	 */
	private void readFile(String filePath,String minierObject,String protocol,DataItems dataItems){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int protocolIndex=NameToIndex(protocol, columns);
		//int minerObjectIndex=NameToIndex(minierObject, columns);
		if(protocolIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP DIP 属性在文件中未找到");
		}
		String line=null;
		boolean fixCondition=true;

		if(minierObject.toLowerCase().contains("path")){
			int minerObjectIndex=NameToIndex(minierObject, columns);
			dataItems.setIsAllDataDouble(-1);
			List<Integer> nodeList=new ArrayList<Integer>();
			List<Integer> hopsList=new ArrayList<Integer>();
			StringBuilder sb=new StringBuilder();
			while((line=textUtils.readByrow())!=null){
				columns=line.split(",");
				fixCondition=true;
				for(String ip:ipPair){
					if(ip.endsWith(".0")){//针对局域网IP
						if(!(columns[SIPColIndex].substring(0, columns[SIPColIndex].lastIndexOf(".")+1).equals(ip.substring(0,ip.lastIndexOf(".")+1))||
								columns[DIPColIndex].substring(0, columns[DIPColIndex].lastIndexOf(".")+1).equals(ip.substring(0,ip.lastIndexOf(".")+1)))){
							fixCondition=false;
							break;
						}
					}else{                  //针对单节点IP
						if(!(columns[SIPColIndex].equals(ip)||columns[DIPColIndex].equals(ip))){
							fixCondition=false;
							break;
						}
					}
				}
				if(fixCondition){
					int time=Integer.parseInt(columns[TimeColIndex]);
					//跳过本节点 直接转到路由器节点
					for(int j=minerObjectIndex+1;j<columns.length-1;j++)
					{
						int len=nodeList.size();
						int node = Integer.valueOf(columns[j].split("-")[0]);
						int hops = Integer.valueOf(columns[j].split(":")[1]);
						if(len>0&&nodeList.get(len-1)==node){
							hopsList.set(len-1, hops);
						}else{
							nodeList.add(node);
							hopsList.add(hops);
						}
					}
					for(String ip:ipPair){
						if(ip.endsWith(".0")){
							if(ip.substring(0, ip.lastIndexOf(".")+1).equals(columns[SIPColIndex].substring(0, columns[SIPColIndex].lastIndexOf(".")+1))){
								sb.append(ip).append(",");
								break;
							}
						}else{
							if(ip.equals(columns[SIPColIndex])){
								sb.append(ip).append(",");
								break;
							}
						}
					}
					int start=1;
					

					for(int i=0;i<nodeList.size();i++){
						int hops=hopsList.get(i);
						for(int pos=start;pos<hops;pos++)
							sb.append("*").append(",");
						sb.append(nodeList.get(i)).append(",");
						start=hops+1;
					}
					for(String ip:ipPair){
						if(ip.endsWith(".0")){
							if(ip.substring(0, ip.lastIndexOf(".")+1).equals(columns[DIPColIndex].substring(0, columns[DIPColIndex].lastIndexOf(".")+1))){
								sb.append(ip);
								break;
							}
						}else{
							if(ip.equals(columns[DIPColIndex])){
								sb.append(ip);
								break;
							}
						}
					}
					
					dataItems.add1Data(parseTime(time), sb.toString());
					sb.delete(0, sb.length());
					nodeList.clear();
					hopsList.clear();
				}
			}
		}else{
			dataItems.setIsAllDataDouble(1);
			while((line=textUtils.readByrow())!=null){
				columns=line.split(",");
				fixCondition=true;
				for(String ip:ipPair){
					if(!(columns[SIPColIndex].equals(ip.trim())||columns[DIPColIndex].equals(ip.trim()))){
						fixCondition=false;
						break;
					}
				}
				if(fixCondition){
					int time=Integer.parseInt(columns[TimeColIndex])*3600;
					if(dataItems.getLength()==0){
						if(minierObject.equals("traffic")){
							dataItems.add1Data(parseTime(time), columns[protocolIndex]);
						}else if(minierObject.equals("times")){
							dataItems.add1Data(parseTime(time), "1");
						}
					}
					else if(dataItems.getTime().get(dataItems.getLength()-1).toString().
							equals(parseTime(time).toString())){
						
						if(minierObject.equals("traffic")){
							int oriTraffic=Integer.parseInt(dataItems.getData().get(dataItems.getLength()-1));
							int addTraffic=Integer.parseInt(columns[protocolIndex]);
							dataItems.getData().set(dataItems.getLength()-1, (oriTraffic+addTraffic)+"");
						}else if(minierObject.equals("times")){
							int oriTimes=Integer.parseInt(dataItems.getData().get(dataItems.getLength()-1));
							dataItems.getData().set(dataItems.getLength()-1, (oriTimes+1)+"");
						}
					}else{
						
						if(minierObject.equals("traffic")){
							dataItems.add1Data(parseTime(time), columns[protocolIndex]);
						}else if(minierObject.equals("times")){
							dataItems.add1Data(parseTime(time), "1");
						}
					}
				}
			}
		}
	}
	
	private void readFile(String filePath,String minierObject,Map<String,DataItems> ipPairItems,String[] conditions){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP 属性在文件中未找到");
		}
		
		String[] parseCondition=new String[conditions.length];
		
		//解析条件 conditions
		for(int i=0;i<conditions.length;i++){
			String condition=conditions[i];
			String compareOper="";
			Pattern pattern=Pattern.compile("[><=]+");
			Matcher matcher=pattern.matcher(condition);
			if(matcher.find()){
				compareOper=matcher.group(0);
			}
			String[] conditionColumns=condition.split("[><=]+");
			int conditionIndex=NameToIndex(conditionColumns[0], columns);
			if(conditionIndex==-1){
				throw new RuntimeException("查找条件设置错误");
			}
			switch (compareOper) {
			case ">":
				condition=conditionIndex+","+">"+","+conditionColumns[1];
				break;
			case "<":
				condition=conditionIndex+","+"<"+","+conditionColumns[1];
				break;
			case ">=":
				condition=conditionIndex+","+">="+","+conditionColumns[1];
				break;
			case "<=":
				condition=conditionIndex+","+"<="+","+conditionColumns[1];
				break;
			case "==":
				condition=conditionIndex+","+"=="+","+conditionColumns[1];
				break;
			case "!=":
				condition=conditionIndex+","+"!="+","+conditionColumns[1];
				break;
			default:
				throw new RuntimeException("查询条件无法确认");
			}
			parseCondition[i]=condition;
		}
		String line=null;
		boolean fixCondition;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");

			fixCondition=true;
			for(int i=0;i<parseCondition.length;i++){
				
				String[] conditionColumn=parseCondition[i].split(",");
				String compareOper=conditionColumn[1];
				int conditionIndex=Integer.parseInt(conditionColumn[0]);
				switch (compareOper) {
				case "<":
					if(!(Double.parseDouble(columns[conditionIndex])<
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case "<=":
					if(!(Double.parseDouble(columns[conditionIndex])<=
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case ">":
					if(!(Double.parseDouble(columns[conditionIndex])>
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case ">=":
					if(!(Double.parseDouble(columns[conditionIndex])>=
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case "==":
					if(!(columns[conditionIndex]==conditionColumn[2])){
						fixCondition=false;
					}
					break;
				case "!=":
					if(!(columns[conditionIndex]!=conditionColumn[2])){
						fixCondition=false;
					}
					break;
				default:
					break;
				}
			}
			if(fixCondition){
				if(columns[SIPColIndex].compareTo(columns[DIPColIndex])<0)
				{
					if(!ipPairItems.containsKey(columns[SIPColIndex]+columns[DIPColIndex]))
					{
						DataItems dataItems = new DataItems();
						ipPairItems.put(columns[SIPColIndex]+columns[DIPColIndex],dataItems);
					}
					
//					System.out.println(columns[SIPColIndex]+columns[DIPColIndex]);
					ipPairItems.get(columns[SIPColIndex]+columns[DIPColIndex]).add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
				}
				else
				{
					if(!ipPairItems.containsKey(columns[DIPColIndex]+columns[SIPColIndex]))
					{
						DataItems dataItems = new DataItems();
						ipPairItems.put(columns[DIPColIndex]+columns[SIPColIndex],dataItems);
					}
					ipPairItems.get(columns[DIPColIndex]+columns[SIPColIndex]).add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
				}
				
			}
		}
	}
	
	private void readFile(String filePath,String minierObject,DataItems dataItems,String[] conditions){
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(filePath);
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1&&!task.getMiningObject().equals("sendTimes")){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TimeColIndex=NameToIndex("Time(S)", columns);
		int SIPColIndex=NameToIndex("srcIP", columns);
		int DIPColIndex=NameToIndex("dstIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP 属性在文件中未找到");
		}
		
		String[] parseCondition=new String[conditions.length];
		
		//解析条件 conditions
		for(int i=0;i<conditions.length;i++){
			String condition=conditions[i];
			String compareOper="";
			Pattern pattern=Pattern.compile("[><=]+");
			Matcher matcher=pattern.matcher(condition);
			if(matcher.find()){
				compareOper=matcher.group(0);
			}
			String[] conditionColumns=condition.split("[><=]+");
			int conditionIndex=NameToIndex(conditionColumns[0], columns);
			if(conditionIndex==-1){
				throw new RuntimeException("查找条件设置错误");
			}
			switch (compareOper) {
			case ">":
				condition=conditionIndex+","+">"+","+conditionColumns[1];
				break;
			case "<":
				condition=conditionIndex+","+"<"+","+conditionColumns[1];
				break;
			case ">=":
				condition=conditionIndex+","+">="+","+conditionColumns[1];
				break;
			case "<=":
				condition=conditionIndex+","+"<="+","+conditionColumns[1];
				break;
			case "==":
				condition=conditionIndex+","+"=="+","+conditionColumns[1];
				break;
			case "!=":
				condition=conditionIndex+","+"!="+","+conditionColumns[1];
				break;
			default:
				throw new RuntimeException("查询条件无法确认");
			}
			parseCondition[i]=condition;
		}
		String line=null;
		boolean fixCondition;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			fixCondition=true;
			for(String ip:ipPair){
				if(!(columns[SIPColIndex].equals(ip)||columns[DIPColIndex].equals(ip))){
					fixCondition=false;
					break;
				}
			}
			if(!fixCondition){
				continue;
			}
			fixCondition=true;
			for(int i=0;i<parseCondition.length;i++){
				if(!fixCondition){
					break;
				}
				String[] conditionColumn=parseCondition[i].split(",");
				String compareOper=conditionColumn[1];
				int conditionIndex=Integer.parseInt(conditionColumn[0]);
				switch (compareOper) {
				case "<":
					if(!(Double.parseDouble(columns[conditionIndex])<
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case "<=":
					if(!(Double.parseDouble(columns[conditionIndex])<=
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case ">":
					if(!(Double.parseDouble(columns[conditionIndex])>
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case ">=":
					if(!(Double.parseDouble(columns[conditionIndex])>=
							Double.parseDouble(conditionColumn[2]))){
						fixCondition=false;
					}
					break;
				case "==":
					if(!(columns[conditionIndex]==conditionColumn[2])){
						fixCondition=false;
					}
					break;
				case "!=":
					if(!(columns[conditionIndex]!=conditionColumn[2])){
						fixCondition=false;
					}
					break;
				default:
					break;
				}
			}
			if(fixCondition){
				if(task.getMiningObject().equals("sendTimes"))
				{
					dataItems.add1Data(parseTime(columns[TimeColIndex]),"1.0");
				}
				else
					dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
			}
		}
	}
	
	public TaskElement getTask() {
		return task;
	}

	public void setTask(TaskElement task) {
		this.task = task;
	}

	public String[] getIpPair() {
		return ipPair;
	}

	public void setIpPair(String[] ipPair) {
		this.ipPair = ipPair;
	}

	public boolean isTextSource() {
		return textSource;
	}
	

	public void setTextSource(boolean textSource) {
		this.textSource = textSource;
	}
	
	/**
	 * 判断指定IP通信是否在相应的文件中
	 * @param ip 
	 * @param fileName 文件路径
	 * @return 
	 */
	private boolean isFileContainsIp(String ip,String fileName){
		if(ip.endsWith(".0")){
			String ipNet=ip.substring(0, ip.lastIndexOf("."));
			String fileIp=fileName.substring(0, fileName.lastIndexOf("."));
			String fileNet=fileIp.substring(0, fileIp.lastIndexOf("."));
			if(ipNet.equals(fileNet))
				return true;
			else 
				return false;
		}else{
			if(ip.equals(fileName.substring(0, fileName.lastIndexOf(".")))){
				return true;
			}else{
				return false;
			}
		}
	}
	
	private String generateFilterSql(String[] ips){
		StringBuilder sb=new StringBuilder();
		if(ips.length==2){
			sb.append("srcIP in('").append(ips[0]).append("','").append(ips[1]).append("') and ").
			append("dstIP in('").append(ips[0]).append("','").append(ips[1]).append("') and ");
			return sb.toString();
		}else if(ips.length==1){
			sb.append("srcIP='").append(ips[0]).append("' or dstIP='").append(ips[0]).append("' and ");
			return sb.toString();
		}else{
			throw new RuntimeException("ip 参数数量不符合要求");
		}
	}
	/**2016/7/29
	 * @author LYH
	 * @param filePath IP文件地址,timeGran 时间粒度
	 * @return Map<String,DataItems> ,其中key值为协议，value值为DataItems
	 * 实现节点挖掘是读取时间段数据
	 * **/
	public HashMap<String, DataItems> readEachProtocolTrafficDataItems(String filePath,boolean isReadBetween,Date date1,Date date2,int timeGran){
		//parseDateToHour pHour = new parseDateToHour(date1);
		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
		System.out.println("readEachProtocolTrafficDataItems.....");
		HashMap<String, DataItems>protocolDataItems=new HashMap<String, DataItems>();
		/**
		 * 读取文件时间段
		 */
		if(isReadBetween)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return protocolDataItems;
			}
			if(startTime == endTime) {
				endTime += 86400000;
			}
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		
		int start=0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		
		/**
		 * timeSpan是相对小时数，start是相对小时数,time是绝对时间
		 */
		boolean onlyOneFile = true;
		System.out.println("startDay:"+startDay+" endDay:"+endDay);
		for (int k = 0; fileDay <= endDay; k++,onlyOneFile = false) {
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			File file = new File(filePath+"\\"+ fileName);
			//如果该文件不存在，则跳过
			if(!file.exists())
			{
				
				fileDay += 86400000; // 下一天的文件名时间戳
				continue;
			}
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			String line=null;
			//List<Integer> indexs = new ArrayList<Integer>();
			
			while((line=textUtils.readByrow())!=null){
				
				String[] items=line.split(",");
				int timeSpan=Integer.parseInt(items[0]) + 24*k;			 
				Date time=parseTime(timeSpan*3600, startDay);
				/*读取时间区间数据*/
				if(!onlyOneFile && isReadBetween){
					if(time.compareTo(date1)<0||time.compareTo(date2)>0){
						continue;
					}
				}
				/*if(timeSpan-start>0){
					DataItems dataItems = new DataItems();
					for(int i=start ;i<timeSpan;i++){
						//对于读取的第一行行start之间添0
						indexs.add(i-start);
						dataItems.add1Data(parseTime((timeSpan-i)*3600), "0");
					}
				}*/
			//	indexs.add(timeSpan-start);		
								
				String protocolItems=items[items.length-1];
				String[] eachProtocol=protocolItems.split(";");			
				for(String protocol:eachProtocol){
					String[] proAndTraffic=protocol.split(":");
					
					if(proAndTraffic.length==1){
						String traffic=proAndTraffic[0];
						proAndTraffic=new String[2];
						proAndTraffic[0]="1";
						proAndTraffic[1]=traffic;
					}
					if(protocolDataItems.containsKey(proAndTraffic[0])){						
						
						DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
						//中间如果没有数据，补零至当前时间，也就是当前时间流量已初始化为0
						while(dataItems.getLength() <= timeSpan-start)
						{
							int j = dataItems.getLength();  //j为新增数据点的下标
							dataItems.add1Data(parseTime((j+start)*3600, startDay), "0");
						}
						
						//合并同一个IP，同一个协议的通信的流量要合并
						int index = dataItems.getTime().indexOf(time);
						double traffic=Double.parseDouble(dataItems.getData().get(index));
						double  addTraffic=Double.parseDouble(proAndTraffic[1]);
						
						dataItems.getData().set(index,(traffic+addTraffic)+"");
							
					}else{
						
						
						DataItems dataItems=new DataItems();
						//补第一个数据之前的0
						/*for(int i=start;i<timeSpan;i++)
						{
							dataItems.add1Data(parseTime(i*3600, startDay), "0");
						}*/
						dataItems.add1Data(time, proAndTraffic[1]);
						protocolDataItems.put(proAndTraffic[0], dataItems);
					}
				}
			}
			fileDay += 86400000; // 下一天的文件名时间戳
		}
		Iterator<String> port_iter = protocolDataItems.keySet().iterator();
		while(port_iter.hasNext()) {
			
			String port = port_iter.next();
			System.out.println("端口 "+port+" 包含的数据点数为："+protocolDataItems.get(port).getLength());
		}
		System.out.println("readEachProtocolTrafficDataItems 调用结束");
		return protocolDataItems;
		/**
		 * @author LYH
		 * 以下用来实现时间粒度可扩展性**/
//		HashMap<String, DataItems> newprotocolDataItems=new HashMap<String, DataItems>();		
//		for(Map.Entry<String, DataItems> entry:protocolDataItems.entrySet()){
//			//newprotocolDataItems.setKey(entry.getKey());
//			DataItems dataItems = new DataItems();
//			DataItem dataItem = new DataItem();
//			int data = 0;
//			int flag=0;
//			for(int index=0;index<entry.getValue().getData().size();index++){			
//				data = data+Integer.parseInt(entry.getValue().getData().get(index));
//				if((index+1)%timegran==0){
//					dataItem.setTime(entry.getValue().getTime().get(index));
//					dataItem.setData(Integer.toString(data));
//					dataItems.add1Data(dataItem);
//					dataItem = new DataItem();
//					data = 0;
//					flag = index;
//				}				
//			}
//			if(flag<entry.getValue().getData().size()-1){
//				dataItem.setTime(entry.getValue().getLastTime());
//				dataItem.setData(Integer.toString(data));
//				dataItems.add1Data(dataItem);
//				dataItem = new DataItem();
//				data = 0;
//			}
//			newprotocolDataItems.put(entry.getKey(), dataItems);
//			
//		}		
//		return newprotocolDataItems;
	}
	/**@author LYH
	 * 读取时间段内的数据
	 * 读取指定IP文件中所有协议通信次数的DataItems
	 * @param filePath IP文件地址
	 * @param isReadBetween 是否读取区间文件
	 * @param date1,date2 时间区间的开始和结束时间
	 * @return Map<String,DataItems> ,其中key值为协议，value值为DataItems
	 * 
	 */
	public HashMap<String, DataItems> readEachProtocolTimesDataItems(String filePath,boolean isReadBetween,Date date1,Date date2,int timeGran){

		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
		System.out.println("readEachProtocolTimesDataItems.....");
		HashMap<String, DataItems>protocolDataItems=new HashMap<String, DataItems>();
		/**
		 * 读取文件时间段
		 */
		if(isReadBetween==false)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
//				System.out.println("time "+time);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return protocolDataItems;
			}
			System.out.println("nodepairreader"+startTime+" "+endTime);
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		
		int start=0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		for (int k = 0; fileDay <= endDay; k++) {
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			File file = new File(filePath+"\\"+ fileName);
			//如果该文件不存在，则跳过
			if(!file.exists())
			{
				fileDay += 86400000; // 下一天的文件名时间戳
				continue;
			}
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			String line=null;

			while((line=textUtils.readByrow())!=null){
//				System.out.println(line);
				String[] items=line.split(",");
				int timeSpan=Integer.parseInt(items[0]) + 24*k; // 距离配置起始日startDay的小时数
				Date time=parseTime(timeSpan*3600, startDay);
//				System.out.println(timeSpan*3600*1000);
//				long xyz = timeSpan*1000*3600 + startDay;
//				System.out.println("time(long):"+xyz);
//				System.out.println("time:"+time);
			/*读取时间区间数据*/
				if(isReadBetween){
					if(time.compareTo(date1)<0 || time.compareTo(date2)>0){
						continue;
					}

				}
				//indexs.add((timeSpan-start));
				String protocolItems=items[items.length-1];
				String[] eachProtocol=protocolItems.split(";");
				for(String protocolTraffic:eachProtocol){
					String[] proAndTraffic=protocolTraffic.split(":");
					String protocol=proAndTraffic[0];
					if(protocolDataItems.containsKey(protocol)){
						DataItems dataItems=protocolDataItems.get(protocol);
						DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
						//合并同一个IP，同一个协议的通信的流量要合并
//						System.out.println("dataGetTime:"+dataItem.getTime()+"  time:"+time);
						if(dataItem.getTime().after(time)){
							System.out.println("异常文件path:"+filePath+"\\"+ fileName);
							throw new RuntimeException("读入文件发生时间错位");
						}
						else if(dataItem.getTime().before(time)){ // 比与原有DataItems最后项时间点更后，则中间时间点次数补0
							Date addtime=DataPretreatment.getDateAfter(dataItem.getTime(),3600*1000);
							while(!addtime.toString().equals(time.toString())&&addtime.before(time)){
								dataItems.add1Data(addtime,"0");
								addtime=DataPretreatment.getDateAfter(addtime, 3600*1000);
							}
							dataItems.add1Data(addtime, proAndTraffic[2]);
						}
						else{ // 与原有DataItems最后项为同一小时时间点，则次数相加
							int times=Integer.parseInt(dataItem.getData());
							//int addTimes=Integer.parseInt(proAndTraffic[2]);
							int addTimes=Integer.parseInt(proAndTraffic[2]);
							dataItems.getData().set(dataItems.getLength()-1,(times+addTimes)+"");
							
						}
					}else{ // 首次添加该协议
						DataItems dataItems=new DataItems();
						/*for(int i=start;i<timeSpan;i++) // 设定起始时间与真实有数据时间之间补0
						{
							dataItems.add1Data(parseTime(start, startDay), "0");
						}*/
						dataItems.add1Data(time, proAndTraffic[2]);
						protocolDataItems.put(protocol, dataItems);
					}
				}

			}

			fileDay += 86400000; // 下一天的文件名时间戳
		}
		System.out.println("readEachProtocolTimesDataItems 调用结束");
		return protocolDataItems;
		/**
		 * @author LYH
		 * 以下用来实现时间粒度可扩展性**/
//		HashMap<String, DataItems> newprotocolDataItems=new HashMap<String, DataItems>();		
//		for(Map.Entry<String, DataItems> entry:protocolDataItems.entrySet()){
//			
//			DataItems dataItems = new DataItems();
//			DataItem dataItem = new DataItem();
//			int data = 0;
//			int flag=0;
//			for(int index=0;index<entry.getValue().getData().size();index++){			
//				data = data+Integer.parseInt(entry.getValue().getData().get(index));
//				if((index+1)%timegran==0){
//					dataItem.setTime(entry.getValue().getTime().get(index));
//					dataItem.setData(Integer.toString(data));
//					dataItems.add1Data(dataItem);
//					dataItem = new DataItem();
//					data = 0;
//					flag = index;
//				}				
//			}
//			if(flag<entry.getValue().getData().size()-1){
//				dataItem.setTime(entry.getValue().getLastTime());
//				dataItem.setData(Integer.toString(data));
//				dataItems.add1Data(dataItem);
//				dataItem = new DataItem();
//				data = 0;
//			}
//			newprotocolDataItems.put(entry.getKey(), dataItems);
//			
//		}		
//		return newprotocolDataItems;
	}
	/**@author LYH
	 * 读取指定IP文件每对接点中所有协议流量的DataItems
	 * @param filePath IP文件地址,timeGran 时间粒度
	 * @return Map<String,Map<String, DataItems>> ,其中key值为ip地址对，value值为Map<Key,DataItems>
	 * 其中key为协议  DataItems为时间序列
	 */
	public HashMap<String,Map<String, DataItems>> readEachIpPairProtocolTrafficDataItems(String filePath,boolean isReadBetween,Date date1,Date date2,int timeGran){
		
		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
		System.out.println("readEachIpPairProtocolTrafficDataItems.....");
		HashMap<String,Map<String, DataItems>> ipPairProtocolDataItems=new HashMap<String,Map<String, DataItems>>();
		/**
		 * 读取文件时间段
		 */
		if(isReadBetween==false)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return ipPairProtocolDataItems;
			}
			
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		
		int start=0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		for (int k = 0; fileDay <= endDay; k++) {
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			String line=null;		
		//	List<Integer> indexs = new ArrayList<Integer>();
			while((line=textUtils.readByrow())!=null){
				String[] items=line.split(",");
				int timeSpan=Integer.parseInt(items[0])+24*k;			
				Date time=parseTime(timeSpan*3600,startDay);
				String protocolItems=items[items.length-1];
				String[] eachProtocol=protocolItems.split(";");
				String ipPair=items[1]+"-"+items[2];
				if(isReadBetween){
					if(time.compareTo(date1)<0 || time.compareTo(date2)>0){
						continue;
					}
					//start=pHour.getHour();
				}
		//		indexs.add(timeSpan-start);
				for(String protocol:eachProtocol){
					String[] proAndTraffic=protocol.split(":");
					if(ipPairProtocolDataItems.containsKey(ipPair)){
						
						Map<String, DataItems> protocolDataItems=ipPairProtocolDataItems.get(ipPair);
						if(protocolDataItems.containsKey(proAndTraffic[0])){
							
							DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
							//中间如果没有数据，补零至当前时间，也就是当前时间流量已初始化为0
							while(dataItems.getLength()<=timeSpan-start)
							{
								int j=dataItems.getLength();  //j为新增数据点的下标
								dataItems.add1Data(parseTime((j+start)*3600, startDay), "0");
							}
							
							DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
							int traffic=Integer.parseInt(dataItem.getData());
							int addTraffic=Integer.parseInt(proAndTraffic[1]);
							dataItems.getData().set(dataItems.getLength()-1,(traffic+addTraffic)+"");
							
						}else{
							DataItems dataItems=new DataItems();						
							//处理第一行
//							for(int i=indexs.get(0);i>start;i--){
//								dataItems.add1Data(parseTime((timeSpan-i)*3600), "0");
//							}
							/*for(int i=start;i<timeSpan;i++)
							{
								dataItems.add1Data(parseTime(i,startDay), "0");
							}*/
							dataItems.add1Data(time, proAndTraffic[1]);
							protocolDataItems.put(proAndTraffic[0], dataItems);
						}
					}else{
						Map<String, DataItems> dataItemsMap=new HashMap<String, DataItems>();
						DataItems dataItems=new DataItems();
						/*for(int i=start;i<timeSpan;i++)
						{
							dataItems.add1Data(parseTime(i,startDay), "0");
						}*/
						dataItems.add1Data(time, proAndTraffic[1]);
						dataItemsMap.put(proAndTraffic[0], dataItems);
						ipPairProtocolDataItems.put(ipPair, dataItemsMap);
					}
				}
			}
			fileDay += 86400000; // 下一天的文件名时间戳
		}
		System.out.println("readEachIpPairProtocolTrafficDataItems 调用结束");
		return ipPairProtocolDataItems;
		/**@author LYH
		 * 以下用于时间粒度扩展
		 * **/
//		HashMap<String, Map<String, DataItems>> newipPairProtocolDataItems = new HashMap<String, Map<String,DataItems>>();
//		for(Map.Entry<String, Map<String, DataItems>> entry:ipPairProtocolDataItems.entrySet()){
//			Map<String, DataItems> newprotocolDataItems = new HashMap<String, DataItems>();
//			for(Map.Entry<String, DataItems> subentry:entry.getValue().entrySet()){
//				DataItems dataItems = new DataItems();
//				DataItem dataItem = new DataItem();
//				int data = 0;
//				int flag=0;
//				for(int index=0;index<subentry.getValue().getData().size();index++){			
//					data = data+Integer.parseInt(subentry.getValue().getData().get(index));
//					if((index+1)%timegran==0){
//						dataItem.setTime(subentry.getValue().getTime().get(index));
//						dataItem.setData(Integer.toString(data));
//						dataItems.add1Data(dataItem);
//						dataItem = new DataItem();
//						data = 0;
//						flag = index;
//					}				
//				}
//				if(flag<subentry.getValue().getData().size()-1){
//					dataItem.setTime(subentry.getValue().getLastTime());
//					dataItem.setData(Integer.toString(data));
//					dataItems.add1Data(dataItem);
//					dataItem = new DataItem();
//					data = 0;
//				}
//				newprotocolDataItems.put(subentry.getKey(), dataItems);				
//			}
//			newipPairProtocolDataItems.put(entry.getKey(), newprotocolDataItems);
//		}
//		return newipPairProtocolDataItems;
	}
	/**
	 * 读取指定IP文件每对接点中所有通信次数的DataItems
	 * @param filePath IP文件地址
	 * @return Map<String,Map<String, DataItems>> ,其中key值为ip地址对，value值为Map<Key,DataItems>
	 * 其中key为协议  DataItems为通信次数时间序列
	 */
	public HashMap<String,Map<String, DataItems>> readEachIpPairProtocolTimesDataItems(String filePath,boolean isReadBetween,Date date1,Date date2,int timeGran){

		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
		System.out.println("readEachIpPairProtocolTimesDataItems.....");
		HashMap<String,Map<String, DataItems>> ipPairProtocolDataItems=new HashMap<String,Map<String, DataItems>>();
		if(isReadBetween==false)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return ipPairProtocolDataItems;
			}
			
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		
		int start=0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		for (int k = 0; fileDay <= endDay; k++) {
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			String line=null;

			System.out.println("FileName: "+ fileName);
			System.out.println("========================================");
			while((line=textUtils.readByrow())!=null){
				String[] items=line.split(",");
				int timeSpan=Integer.parseInt(items[0])+24*k;			
				Date time=parseTime(timeSpan*3600,startDay);
				String protocolItems=items[items.length-1];
				String[] eachProtocol=protocolItems.split(";");
				String ipPair=items[1]+"-"+items[2];
				if(isReadBetween){
					if(time.compareTo(date1)<0 || time.compareTo(date2)>0){
						continue;
					}
//					start=pHour.getHour();
				}	
//				indexs.add(timeSpan-start);
				for(String protocol:eachProtocol){
					String[] proAndTraffic=protocol.split(":");
					if(ipPairProtocolDataItems.containsKey(ipPair)){
						Map<String, DataItems> protocolDataItems=ipPairProtocolDataItems.get(ipPair);
						if(protocolDataItems.containsKey(proAndTraffic[0])){
							
							
							DataItems dataItems=protocolDataItems.get(proAndTraffic[0]);
							while(dataItems.getLength()<=timeSpan-start)
							{
								int j=dataItems.getLength();  //j为新增数据点的下标
								dataItems.add1Data(parseTime((j+start)*3600, startDay), "0");
							}
							DataItem dataItem=dataItems.getElementAt(dataItems.getLength()-1);
							
							int times=Integer.parseInt(dataItem.getData());
							int addTimes=Integer.parseInt(proAndTraffic[2]);
							dataItems.getData().set(dataItems.getLength()-1,(times+addTimes)+"");
						
								
						}else{
							DataItems dataItems=new DataItems();
//							for(int i=rows-1;i>=0;i--){
//								dataItems.add1Data(parseTime(timeSpan-i), "0");
//							}
							/*for(int i=start;i<timeSpan;i++)
							{
								dataItems.add1Data(parseTime(i,startDay), "0");
							}*/
							dataItems.add1Data(time, proAndTraffic[2]);
							protocolDataItems.put(proAndTraffic[0], dataItems);
						}
					}else{
						Map<String, DataItems> dataItemsMap=new HashMap<String, DataItems>();
						DataItems dataItems=new DataItems();
//						for(int i=indexs.get(0);i>start;i--){
//							dataItems.add1Data(parseTime((timeSpan-i)*3600), "0");
//						}
						/*for(int i=start;i<timeSpan;i++)
						{
							dataItems.add1Data(parseTime(i,startDay), "0");
						}*/
						dataItems.add1Data(time, proAndTraffic[2]);
						dataItemsMap.put(proAndTraffic[0], dataItems);
						ipPairProtocolDataItems.put(ipPair, dataItemsMap);
					}
				}
			}
			fileDay += 86400000; // 下一天的文件名时间戳
		}
		System.out.println("readEachIpPairProtocolTimesDataItems 调用结束");
		return ipPairProtocolDataItems;
		/**@author LYH
		 * 以下用于时间粒度扩展
		 * **/
//		HashMap<String, Map<String, DataItems>> newipPairProtocolDataItems = new HashMap<String, Map<String,DataItems>>();
//		for(Map.Entry<String, Map<String, DataItems>> entry:ipPairProtocolDataItems.entrySet()){
//			Map<String, DataItems> newprotocolDataItems = new HashMap<String, DataItems>();
//			for(Map.Entry<String, DataItems> subentry:entry.getValue().entrySet()){
//				DataItems dataItems = new DataItems();
//				DataItem dataItem = new DataItem();
//				int data = 0;
//				int flag=0;
//				for(int index=0;index<subentry.getValue().getData().size();index++){			
//					data = data+Integer.parseInt(subentry.getValue().getData().get(index));
//					if((index+1)%timegran==0){
//						dataItem.setTime(subentry.getValue().getTime().get(index));
//						dataItem.setData(Integer.toString(data));
//						dataItems.add1Data(dataItem);
//						dataItem = new DataItem();
//						data = 0;
//						flag = index;
//					}				
//				}
//				if(flag<subentry.getValue().getData().size()-1){
//					dataItem.setTime(subentry.getValue().getLastTime());
//					dataItem.setData(Integer.toString(data));
//					dataItems.add1Data(dataItem);
//					dataItem = new DataItem();
//					data = 0;
//				}
//				newprotocolDataItems.put(subentry.getKey(), dataItems);				
//			}
//			newipPairProtocolDataItems.put(entry.getKey(), newprotocolDataItems);
//		}
//		return newipPairProtocolDataItems;
	}
	
	
   
	
	
	
	public static void main(String[] args){
		Calendar cal=Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date startDate=cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH,100);
		Date endDate=cal.getTime();
		TaskElement task=new TaskElement();
		task.setDataSource("text");
		task.setSourcePath("./configs/smtpPcap");
		task.setMiningObject("traffic");
		String[] ips=new String[2];
		ips[0]="10.0.1.2";
		ips[1]="10.0.1.1";
		nodePairReader reader=new nodePairReader(task,ips);
		DataItems dataItems=reader.readInputBetween(startDate, endDate);
		System.out.println("over "+dataItems.getLength());
	}

	/**
	 * 将一个节点(这里只考虑路由)的所有协议的通信量合并在同一时刻合并。当节点在该时候都没有通讯时，则认为该节点消失了
	 * @param filePath
	 * @return
	 */
	public HashMap<String, DataItems> readEachNodeDisapearEmergeDataItems(String filePath,boolean isReadBetween,Date date1,Date date2,int timeGran) {

		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
//		int timeSpan = 3600;
		System.out.println("readEachNodeDisapearEmergeDataItems.....");
		HashMap<String, DataItems> protocolDataItems = new HashMap<String, DataItems>();
		if(isReadBetween==false)
		{
			long startTime=Long.MAX_VALUE;
			long endTime =0;
			File dir = new File(filePath);
		
			for(int i=0;i<dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[]=dir.list()[i].split("\\.");
				long time =Long.valueOf(str[0]);
				if(time<startTime)
					startTime=time;
				if(time>endTime)
					endTime=time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return protocolDataItems;
			}
			
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		int start=0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		DataItems sumDataItem = new DataItems();
		for (int k = 0; fileDay <= endDay; k++) {
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			File file = new File(filePath+"\\"+ fileName);
			//如果该文件不存在，则跳过
			if(!file.exists())
			{
				fileDay += 86400000; // 下一天的文件名时间戳
				continue;
			}
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			String line=null;
			/**
			 * 添加功能：统计节点出现与消失的情况，将当前节点所有协议的流量全部统计出来。
			 */
			
			
			while ((line = textUtils.readByrow()) != null) {
				
				String[] items = line.split(",");
				int timeSpan = Integer.parseInt(items[0])+24*k;
				if (timeSpan-start < 0) {
					continue;
				}
				while(sumDataItem.getLength()<=timeSpan-start)
				{
					int j=sumDataItem.getLength();  //j为新增数据点的下标
					sumDataItem.add1Data(parseTime((j+start)*3600, startDay), "0");
				}
				//得到上一次的值，已保证上次已加入序列
				int lastTraffic = Integer.parseInt(sumDataItem.getData().get(sumDataItem.getLength()-1));
				int currentTraffic = Integer.parseInt(items[2]);
				
				if(lastTraffic > 0 || currentTraffic > 0){
					sumDataItem.data.set(sumDataItem.getLength()-1, "1");
				} 
			}
	
			
			protocolDataItems.put("AllTraffic", sumDataItem);
//			System.out.println(sumDataItem.getData().toString());
			fileDay += 86400000; // 下一天的文件名时间戳
		}
		System.out.println("readEachNodeDisapearEmergeDataItems 调用结束");
		return protocolDataItems;
	}

	/**
	 * @author 艾长青
	 * @param absolutePath
	 * @param isReadBetween
	 * @param date1
	 * @param date2
	 * @param i
	 * @return
	 */
	public DataItems readIpSumTraffic(String filePath, boolean isReadBetween,
			Date date1, Date date2, int timeGran) {

		Logger.log("filePath",filePath);
		Logger.log("isReadBetween",String.valueOf(isReadBetween));
		Logger.log("startTime",date1.toString());
		Logger.log("endTime",date2.toString());
		System.out.println("readIpSumTraffic.....");
		DataItems SumDataItems = new DataItems();
		/**
		 * 读取文件时间段
		 */
		if(isReadBetween)
		{
			long startTime = Long.MAX_VALUE;
			long endTime = 0;
			File dir = new File(filePath);
		
			for(int i = 0;i < dir.list().length;i++)
			{
				//fileList.add(dir.list()[i]);
				String str[] = dir.list()[i].split("\\.");
				long time = Long.valueOf(str[0]);
				if(time < startTime)
					startTime = time;
				if(time > endTime)
					endTime = time;
				
			}
			if(dir.list().length==0)
			{
				System.out.println("No file!");
				return SumDataItems;
			}
			if(startTime == endTime) {
				
				endTime += 86400000;
			}
			date1 = new Date(startTime);
			date2 = new Date(endTime);
		}
		
		int start = 0;
		
		long[] fileDays = floorDate(date1, date2);
		long fileDay = fileDays[0];
		long endDay = fileDays[1];
		long startDay = fileDay; // 数据起始日0点
		parseDateToHour pHour = new parseDateToHour(date1, new Date(startDay));
		start=pHour.getHour();
		for (int k = 0; fileDay <= endDay; k++) {
			
			String fileName = fileDay+".txt";
			Logger.log("当前处理文件", fileName);
			
			File file = new File(filePath+"\\"+ fileName);
			//如果该文件不存在，则跳过
			if(!file.exists())
			{
				
				fileDay += 86400000; // 下一天的文件名时间戳
				continue;
			}
			
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(filePath+"\\"+ fileName);
			
			String line = null;		
		//	List<Integer> indexs = new ArrayList<Integer>();
			while((line = textUtils.readByrow()) != null){
				
				String[] items = line.split(",");
				int timeSpan = Integer.parseInt(items[0])+24*k;			
				Date time = parseTime(timeSpan*3600,startDay);
				String protocolItems = items[items.length-1];
				String[] eachProtocol = protocolItems.split(";");
				String ipPair = items[1]+"-"+items[2];
				if(isReadBetween){
					if(time.compareTo(date1) < 0 || time.compareTo(date2) > 0){
						continue;
					}
					//start=pHour.getHour();
				}
				
				while(SumDataItems.getLength() <= timeSpan-start)
				{
					int j = SumDataItems.getLength();  //j为新增数据点的下标
					SumDataItems.add1Data(parseTime((j+start)*3600, startDay), "0");
				}
				//对所有端口进行求和
				double currentTrffic = 0;
				for(String protocol:eachProtocol){
					String[] proAndTraffic=protocol.split(":");
					if(proAndTraffic.length < 1)
						continue;
					currentTrffic += Double.parseDouble(proAndTraffic[1]); //traffic
				}
				int index = SumDataItems.time.indexOf(time);
				double lastTraffic = Double.parseDouble(SumDataItems.data.get(index));
				
				SumDataItems.data.set(index,String.valueOf(currentTrffic+lastTraffic));
			}
			fileDay += 86400000; // 下一天的文件名时间戳
		}
		System.out.println("该IP下包含数据点的个数为："+SumDataItems.getLength());
		System.out.println(SumDataItems.getData().toString());
		return SumDataItems;
	
	}
	
}
