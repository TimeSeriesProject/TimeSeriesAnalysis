package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public DataItems readInputBetween(Date date1, Date date2){
		if(textSource){
			String[] conditions=new String[2];
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
		sqlsb.append("SIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ").
		append("DIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ");
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
		return data;
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
		sqlsb.append("SIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ").
		append("DIP in('").append(ipPair[0]).append("','").append(ipPair[1]).append("') and ");
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
		return data;
	}
	
	@Override
	public DataItems readInputByText() {
		String minierObject=task.getMiningObject();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(task.getSourcePath());
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TimeColIndex=NameToIndex("Time", columns);
		int SIPColIndex=NameToIndex("SIP", columns);
		int DIPColIndex=NameToIndex("DIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP 属性在文件中未找到");
		}
		DataItems dataItems=new DataItems();
		String line=null;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			if((columns[SIPColIndex].equals(ipPair[0])||columns[SIPColIndex].equals(ipPair[1]))&&
				(columns[DIPColIndex].equals(ipPair[0])||columns[DIPColIndex].equals(ipPair[1]))){
				dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
			}
		}
		return dataItems;
	}
	
	@Override
	public DataItems readInputByText(String[] conditions) {
		String minierObject=task.getMiningObject();
		TextUtils textUtils=new TextUtils();
		textUtils.setTextPath(task.getDataSource());
		String header=textUtils.readByrow();
		String[] columns=header.split(",");
		int minerObjectIndex=NameToIndex(minierObject, columns);
		if(minerObjectIndex==-1){
			throw new RuntimeException("未找到挖掘对象");
		}
		int TimeColIndex=NameToIndex("Time", columns);
		int SIPColIndex=NameToIndex("SIP", columns);
		int DIPColIndex=NameToIndex("DIP", columns);
		if(TimeColIndex==-1||SIPColIndex==-1||DIPColIndex==-1){
			throw new RuntimeException("Time SIP SIP 属性在文件中未找到");
		}
		DataItems dataItems=new DataItems();
		//解析条件
		for(int i=0;i<conditions.length;i++){
			String condition=conditions[i];
			String compareOper="";
			Pattern pattern=Pattern.compile("[><=]+");
			Matcher matcher=pattern.matcher(condition);
			if(matcher.find()){
				compareOper=matcher.group(1);
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
			conditions[i]=condition;
		}
		String line=null;
		while((line=textUtils.readByrow())!=null){
			columns=line.split(",");
			if((columns[SIPColIndex]==ipPair[0]||columns[SIPColIndex]==ipPair[1])&&
				(columns[DIPColIndex]==ipPair[0]||columns[DIPColIndex]==ipPair[1])){
				//检查条件
				boolean fixCondition=true;
				for(int i=0;i<conditions.length;i++){
					String[] conditionColumn=conditions[i].split(",");
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
					dataItems.add1Data(parseTime(columns[TimeColIndex]), columns[minerObjectIndex]);
				}
			}
		}
		return dataItems;
	}
	
	private Date parseTime(String timeStr){
		int difLen = sdf.toPattern().length() - timeStr.length();
		StringBuilder sb = new StringBuilder();
		sb.append(timeStr);
		for (int i = 0; i < difLen; i ++)
			sb.append("0");
		try {
			return sdf.parse(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
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
	
	
}
