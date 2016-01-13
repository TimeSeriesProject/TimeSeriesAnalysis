package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.StatUtils;
import org.netbeans.swing.plaf.util.UIUtils;

import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;
/**
 * 预处理类，包括：
 * 1、读取数据
 * 2、预处理（如用于序列模式挖掘的“离散化”处理）
 */
public class DataInputUtils {
	TaskElement task;
	OracleUtils conn;
	DataItems data;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public DataInputUtils(TaskElement task) {
		this.task = task;
		if(task.getDataSource().equals("DataBase")){
			conn = new OracleUtils();
			if (!conn.tryConnect())
				UtilsUI.showErrMsg("数据库无法连接！");
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
		
		if (task.getDateStart().equals(cal.getTime()))	// 如果没有设置在任务里设置时间，则采用软件总体的配置
			dStart = UtilsSimulation.instance.getStartTime();
		else	// 如果在任务里设置了起始时间，则按任务的来
			dStart = task.getDateStart();
		
		return readInputBetween(dStart, date);
	}
	public DataItems readInputBetween(Date date1, Date date2){
		String filter = "事件发生时间>'" + sdf.format(date1) + "' and " +
			"事件发生时间<='" + sdf.format(date2) + "'";
		return readInput(filter, true, true);
	}
	public DataItems readInput(){
		return readInput(true,true);
	}
	/**
	 * 按照任务配置读取数据，并按照任务配置里的时间粒度，对数据进行聚合
	 * @param doAggregate	是否聚合
	 * @param doDiscretize	是否离散化
	 * @return
	 */
	public DataItems readInput(boolean doAggregate, boolean doDiscretize){
		if (UtilsSimulation.instance.isUseSimulatedData())//使用 
		{
			TextUtils txt = new TextUtils();
			txt.setTextPath(task.getSourcePath());
			DataItems dataItems = txt.readInput();
			boolean isNonDouble = !dataItems.isAllDataIsDouble();//离散的或非double型的
			if (doAggregate)
			    dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(),
			    		task.getAggregateMethod(), isNonDouble);
			if (doDiscretize)
				dataItems=DataPretreatment.toDiscreteNumbers(dataItems,task.getDiscreteMethod(),task.getDiscreteDimension(),
						task.getDiscreteEndNodes());
			String endNodes=dataItems.discreteNodes();
			task.setDiscreteEndNodes(endNodes);
			return dataItems;
		}else{
			Calendar cal = Calendar.getInstance();
			cal.set(1, 0, 1, 0, 0, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date dStart;
			Date dEnd;
			if(task.getDateStart() == null || task.getDateStart().equals(cal.getTime()))
				dStart = UtilsSimulation.instance.getStartTime();
			else
				dStart = task.getDateStart();
			
			if (task.getDateEnd() == null || task.getDateEnd().equals(cal.getTime()))
				dEnd = UtilsSimulation.instance.getCurTime();
			else
				dEnd = task.getDateEnd();
			if (dEnd.after(UtilsSimulation.instance.getCurTime()))
				dEnd = UtilsSimulation.instance.getCurTime();
			return readInputBetween(dStart, dEnd);
		}
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
	 * 1.按照过滤条件读取数据<br>
	 * 2.按照任务配置里设定的离散化方法，进行离散化<br>
	 * 3.按照任务配置里的时间粒度，对数据进行聚合
	 * @param filterCondition	过滤条件
	 * @param doAggregate		是否按时间对数据进行聚合
	 * @param doDiscretize		是否考虑离散化
	 * @return
	 */
	private DataItems readInput(String filterCondition, boolean doAggregate, boolean doDiscretize){
		String sqlStr = "SELECT 事件发生时间," + task.getMiningObject() + " "
		+ "FROM " + conn.DB_TABLE + " WHERE ";
		if(task.getFilterCondition().length() > 0)
			sqlStr += task.getFilterCondition() + " AND ";
		if (filterCondition != null && filterCondition.length() > 0)
			sqlStr += filterCondition + " AND ";
		sqlStr += "1=1 ORDER BY 事件发生时间 asc";	// 按时间先后顺序读取数据
		conn.closeConn();
		ResultSet rs = conn.sqlQuery(sqlStr);
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
					else
						System.out.println("");
				}
					
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("共" + numRecords + "条记录！");
		System.out.println("读取完毕:" + data.getLength() + "条记录！");  
		
		boolean isNonDouble = !data.isAllDataIsDouble();
		// 先进行时间粒度上的聚合
		if (doAggregate)
			data =DataPretreatment.aggregateData(data, task.getGranularity(),
					task.getAggregateMethod(), isNonDouble);
		// 再进行离散化（只有数值型才能够离散化，否则应该会报错！）
		if (doDiscretize)
			data = DataPretreatment.toDiscreteNumbers(data, task.getDiscreteMethod(),
					task.getDiscreteDimension(), task.getDiscreteEndNodes());
		data.setGranularity(task.getGranularity());	// 设置数据的一些参数，如粒度
		String endNodes=data.discreteNodes();
		task.setDiscreteEndNodes(endNodes);
		return data;
	}
	
	public String printFormatData(DataItems data){
		String ret = "";
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		int numRows = data.getLength();
		try {
			for (int row = 0; row < numRows; row++) {
				DataItem s = data.getElementAt(row);
				bw.write(s.toString());
				bw.write("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			ret = sw.toString();
			if (bw != null)
				try{bw.close();}catch(Exception ee){}
				if (sw != null)
					try{sw.close();}catch(Exception ee){}
		}
		return ret;
	}


	public static DataItems sort(DataItems dataInput){
		DataItem datin[]=new DataItem[dataInput.getLength()];
		for( int i = 0; i < dataInput.getLength(); i ++){
			datin[i] = dataInput.getElementAt(i);
		}
		Arrays.sort(datin);
		DataItems di = new DataItems();
		di.setItems(datin);
		return di;
	}
	/**
	 * 对DataItems数据进行排序
	 * @param input 要排序的DataItems
	 * @return 排序后的DataItems
	 */
	
	//对频繁项集进行排序
	public static String sortFP(String FP, int DataSetsSize){
		String FPs[]=FP.split("\n");
		Item it[] = new Item[FPs.length];
		int i = 0;
		for(String FPs1:FPs){
			String FPs1s[]=FPs1.split(":");
			it[i] = new Item(FPs1s[1], FPs1s[0]);
			i++;
		}
		Arrays.sort(it, new MyCompratorFP());
		FP="";
		for(Item it1:it){
			FP+=it1.getData()+"\t\t"+it1.getTime()+"\t"+String.format("%.2f",Double.parseDouble(it1.getTime())/DataSetsSize)+"\n";
		}
		return FP;		
	}
	
	//获取给定秒数之后的时间
	public static Date getDateAfter(Date curTime, int miniSecondsAfter){
		Calendar cal = Calendar.getInstance();
		try{
		cal.setTime(curTime);
		}catch(Exception e){
			System.out.println("");
		}
		cal.add(Calendar.MILLISECOND, miniSecondsAfter);
		return cal.getTime();
	}
	
	//按照时间，sizeWindow和stepWindow将数据分成项集，再调用Itemset2File写入文件
	public static String[] movingdivide(DataItems datainput, TaskElement task , boolean FP ) throws IOException{
		int sizeWindow = (int)((ParamsSM)task.getMiningParams()).getSizeWindow() * 1000;//seconds
		int stepWindow = (int)((ParamsSM)task.getMiningParams()).getStepWindow() * 1000;//seconds

		int len = datainput.getLength();
		List<Date> time = datainput.getTime();
		List<String> data = datainput.getData();	
		
		List<String> DataSets = new ArrayList<String>();
		
		Date win_start_time = time.get(0);
		Date win_end_time = getDateAfter(win_start_time, sizeWindow);
		Date win_start_next = getDateAfter(win_start_time, stepWindow);
		int ind_next = -1;
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		do{
			DataItem item = datainput.getElementAt(i);
			i++;
			Date date = item.getTime();
			String val = item.getData();
			if (!date.before(win_start_time) && !date.after(win_end_time)){
				if (sb.length() != 0 )
					sb.append(" ");
				sb.append(val + " -1");
				if (!date.before(win_start_next) && ind_next == -1)
					ind_next = i - 1;
			}else{
				sb.append(" -2");
				DataSets.add(sb.toString());
				sb = new StringBuilder();
				
				if (ind_next == -1){
					if (!date.before(getDateAfter(win_end_time, stepWindow))){
						win_start_time = date;	
						if (sb.length() != 0 )
							sb.append(" ");
						sb.append(val + " -1");
					}else{
						win_start_time = win_start_next; // getDateAfter(win_start_time, stepWindow);
						if (sb.length() != 0 )
							sb.append(" ");
						sb.append(val + " -1");
					}					
				}else{
					i = ind_next;
					ind_next = -1;
					win_start_time = win_start_next;					
				}
				win_end_time = getDateAfter(win_start_time, sizeWindow);
				win_start_next = getDateAfter(win_start_time, stepWindow);
				
			}		
			
		}while (i < len);
		sb.append(" -2");
		DataSets.add(sb.toString());
		return DataSets.toArray(new String[0]);
	}

	public static DataItems string2DataItems(String str) {// 将输出结果的字符串转成DataItems的形式

		DataItems ans = new DataItems();
		if (str == null || str.length() == 0)
			return ans;

		String[] temp = str.split("\n");
		List<String> dataList = new ArrayList<String>();
		List<Double> probList = new ArrayList<Double>();
		List<Date> timeList = new ArrayList<Date>();
		for (String temp1 : temp) {
			String[] temp2 = temp1.split("\t\t");
			dataList.add(temp2[0]);
			Double prob = 0.0;
			try {
				prob = Double.parseDouble(temp2[1]);
			} catch (Exception ee) {
				System.out.println("");
			}
			probList.add(prob);
			timeList.add(null);
		}
		ans.setData(dataList);
		ans.setProb(probList);
		ans.setTime(timeList);
		return ans;
	}
}

class MyCompratorFP implements Comparator<Item> {

	@Override
	public int compare(Item t1, Item t2) {
		if(Integer.parseInt(t1.getTime())>Integer.parseInt(t2.getTime()))
			return -1;
		else if(Integer.parseInt(t1.getData())<Integer.parseInt(t2.getData()))
			return 1;
		else return 0;
	}
}
class Item{
	private String time;
	private String data;
	public Item(String time, String data){
		this.time = time;
		this.data = data;
	}
	public void setTime(String time){
		this.time = time;
	}
	public void setData(String data){
		this.data = data;
	}
	public String getTime(){
		return time;
	}
	public String getData(){
		return data;
	}
}

class ItemDouble implements Comparable<ItemDouble>{
	private Date time;
	private Double data;
	@Override
	public int compareTo(ItemDouble o) {
		return this.getData().compareTo(o.getData());
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Double getData() {
		return data;
	}
	public void setData(Double data) {
		this.data = data;
	}
	
}

class ItemTime implements Comparable<ItemTime>{
	private Date time;
	private String data;
	private Map<String, Integer> NonNumData;
	@Override
	public int compareTo(ItemTime o) {
		return this.getTimeInMill().compareTo(o.getTimeInMill());
	}
	
	public Date getTime(){
		return time;
	}
	
	public void setTime(Date time){
		this.time=time;
	}
	
	public String getData(){
		return data;
	}
	
	public Map<String, Integer> getNonNumData() {
		return NonNumData;
	}

	public void setNonNumData(Map<String, Integer> nonNumData) {
		NonNumData = nonNumData;
	}

	public void setData(String data){
		this.data=data;
	}
	
	public Long getTimeInMill(){
		return time.getTime();
	}
}