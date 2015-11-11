package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.awt.List;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.InstFS.wkr.NetworkMining.PcapStatistics.IPStreamPool;
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
		String minierObject=task.getMiningObject();
		File sourceFile=new File(task.getSourcePath());
		if(sourceFile.isFile()){
			for(String ip:ipPair){
				if(isFileContainsIp(ip, sourceFile.getName())){
					readFile(sourceFile.getAbsolutePath(), minierObject, dataItems);
					break;
				}
			}
		}else{
			File[] files=sourceFile.listFiles();
			for(File file:files){
				for(String ip:ipPair){
					if(isFileContainsIp(ip, file.getName())){
						readFile(file.getAbsolutePath(), minierObject, dataItems);
						System.out.println("read file "+file.getName());
						break;
					}
				}
			}
		}
		return DataItems.sortByTimeValue(dataItems);
		
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
		File sourceFile=new File(task.getSourcePath());
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
	
	private Date parseTime(String timeStr){
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 9, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.SECOND,Integer.parseInt(timeStr));
		return cal.getTime();
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
	 * 读取文件
	 * @param filePath 文件路径
	 * @param minierObject 要读取的属性
	 * @param dataItems 读到的序列
	 */
	private void readFile(String filePath,String minierObject,DataItems dataItems){
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
		String line=null;
		boolean fixCondition=true;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			fixCondition=true;
			for(String ip:ipPair){
				if(!(columns[SIPColIndex].equals(ip)||columns[DIPColIndex].equals(ip))){
					fixCondition=false;
					break;
				}
			}
			if(fixCondition){
				dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
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
//			if(!fixCondition)
//			System.out.println("ggggg");
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
		int fileIndex=Integer.parseInt(fileName.split("-")[1]);
		int LANIndex=Integer.parseInt(ip.split("\\.")[2]);
		if((fileIndex/6+1)==LANIndex){
			return true;
		}else{
			return false;
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
}
