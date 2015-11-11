package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsClass;

public class TaskElement implements Serializable, Comparable<TaskElement>{
	public static String PATH_TO_SAVE_TASKS = "./tasks/";
	public static List<TaskElement>allTasks ;	

	private String taskName;
	private Date dateStart;
	private Date dateEnd;
	private String comments;
	private String dataSource;//源文件类型  数据库 或 文本文件
	private String sourcePath; //当为文本文件时 文件路径
	
	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	private String miningObject;
	
	//离散化方法
	private DiscreteMethod discreteMethod;
	//离散化维度
	private int discreteDimension;
	private String discreteEndNodes;
	
	//时间粒度聚合方法
	private AggregateMethod aggregateMethod;
	private String filterCondition;
	
	private MiningMethod miningMethod;
	private MiningAlgo miningAlgo;

	private int granularity;
	private IParamsNetworkMining miningParams;
	private String sqlStr;
	private TaskRange taskRange;
	private String range;
	
	public boolean isMining;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	static Vector<ITaskElementEventListener> listeners = new Vector<ITaskElementEventListener>();
	
	
	public static TaskElement example1 = new TaskElement();
	static {
		example1.setTaskName("大流量通信收发方");
		example1.setComments("将流量较大的收发方作为挖掘对象, 挖掘其序列模式");
		example1.setMiningObject("traffic");
		example1.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		example1.setFilterCondition("流量>100");
		example1.setGranularity(3600);
		example1.setMiningMethod(MiningMethod.MiningMethods_PeriodicityMining);
		example1.setDataSource("File");
		example1.setSourcePath("./configs/smtpPcap");
		example1.setDiscreteMethod(DiscreteMethod.自定义端点);
		example1.setDiscreteEndNodes("1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,"
				+ "22000,23000,24000,25000,26000,27000,28000,29000,30000,31000,32000,33000");
		example1.setTaskRange(TaskRange.NodePairRange);
		example1.setRange("10.0.2.4,10.0.3.4");
		example1.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
	}
	
	public TaskRange getTaskRange() {
		return taskRange;
	}

	public void setTaskRange(TaskRange taskRange) {
		this.taskRange = taskRange;
	}
	public static TaskElement TSAExample=new TaskElement();
	static{
		TSAExample.setTaskName("大流量TSA测试");
		TSAExample.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExample.setMiningObject("traffic");
		TSAExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAExample.setFilterCondition("流量>100");
		TSAExample.setGranularity(3600);
		TSAExample.setMiningMethod(MiningMethod.MiningMethods_TsAnalysis);
		TSAExample.setMiningAlgo(MiningAlgo.MiningAlgo_ARTSA);
		TSAExample.setDataSource("File");
		TSAExample.setSourcePath("./configs/sendTimesOfNet1.csv");
		TSAExample.setDiscreteMethod(DiscreteMethod.自定义端点);
		TSAExample.setDiscreteEndNodes("500,600,700,800,900,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,"
				+ "2200,2300,2400,2500,2600,2700,2800,2900,3000,3100,3200,3300");
		TSAExample.setTaskRange(TaskRange.NodePairRange);
		TSAExample.setRange("10.0.1.1,10.0.1.2");
	}
	public static TaskElement example2 = new TaskElement();
	static {
		example2.setTaskName("大流量通信收发方");
		example2.setComments("将流量较大的收发方作为挖掘对象, 挖掘其序列模式");
		example2.setMiningObject("流量");
		example2.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		example2.setFilterCondition("流量>100");		
		example2.setMiningMethod(MiningMethod.MiningMethods_TsAnalysis);		
	}
	public TaskElement() {
		setTaskName("");
		setFilterCondition("");
		setMiningObject("");
		setComments("");
		// 离散化
		setDiscreteMethod(DiscreteMethod.None);
		setDiscreteEndNodes("0,0,0,0");
		// 按时间聚合数据
		setGranularity(1);
		setAggregateMethod(AggregateMethod.Aggregate_SUM);
		
		setMiningMethod(MiningMethod.MiningMethods_SequenceMining);
		setMiningParams(new ParamsSM());
		
		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		setDateEnd(cal.getTime());
//		cal.add(Calendar.MONTH, -1);
		setDateStart(cal.getTime());
		setDiscreteDimension(2);
		setDataSource("DataBase");
	}
	
	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public TaskElement(TaskElement task){
		this();
		if (task != null)
			task.copyTo(this);
	}
	
	
	/**
	 * convert this object to String
	 */
	public String toStringDetailed() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTaskName() + "\r\n");
		sb.append("\teventName: " + getTaskName() + "\r\n");
		sb.append("\tcomments: " + getComments() + "\r\n");
		sb.append("\tminingObject: " + getMiningObject() + "\r\n");
		sb.append("\tminingMethod: " + getMiningMethod() + "\r\n");
		sb.append("\tfilterCondition: " + getFilterCondition() + "\r\n");
		return sb.toString();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTaskName());
		if (isMining)
			sb.append("-- runing");
		else
			sb.append("-- not running");
		return sb.toString();
	}
	
	public Properties toProperties(){
		Properties prop = new Properties();
		putProp(prop,"eventName", getTaskName());
		putProp(prop, "comments", getComments());
		putProp(prop, "miningObject", getMiningObject());
		putProp(prop, "discreteMethod", getDiscreteMethod());
		putProp(prop, "discreteDimsion", getDiscreteDimension());
		putProp(prop, "discreteEndNodes", getDiscreteEndNodes());
		putProp(prop, "granularity", getGranularity());
		putProp(prop, "aggregateMethod", getAggregateMethod());
		putProp(prop, "filterCondition", getFilterCondition());
		putProp(prop, "miningMethod", getMiningMethod());
		putProp(prop, "miningParams", getMiningParams());
		putProp(prop, "dateStart", getDateStart());
		putProp(prop, "dateEnd", getDateEnd());
		putProp(prop, "sqlStr", getSqlStr());
		
		return prop;
	}
	private void putProp(Properties prop, String key, Object obj){
		if (obj != null){
			if (obj instanceof Date){
				
				prop.put(key, sdf.format(((Date)obj)));
			}else	
				prop.put(key, obj.toString());
		}
			
	}
	public void copyTo(TaskElement task){
		if (task == null)
			return;
		task.setComments(this.getComments());
		task.setFilterCondition(this.getFilterCondition());
		task.isMining = this.isMining;
		task.setMiningMethod(this.getMiningMethod());
		task.setMiningObject(this.getMiningObject());
		task.setDiscreteMethod(this.getDiscreteMethod());
		task.setDiscreteEndNodes(this.getDiscreteEndNodes());
		task.setDiscreteDimension(this.getDiscreteDimension());
		task.setGranularity(this.getGranularity());
		task.setAggregateMethod(this.getAggregateMethod());
		task.setTaskName(this.getTaskName());
		task.setDateEnd(this.getDateEnd());
		task.setDateStart(this.getDateStart());
		task.setSqlStr(this.getSqlStr());

		task.setMiningParams(IParamsNetworkMining.newInstance(this.getMiningParams()));
	}
	
	//生成Task的查询语句，以从数据库中读取DataItems
	public String generateSqlStr(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT 事件发生时间,")
			.append(getMiningObject())
			.append(" FROM ")
			.append(OracleUtils.DB_TABLE)
			.append(" WHERE ");
		if (getFilterCondition().length() > 0)
			sb.append(getFilterCondition());
		String pattern = sdf.toPattern().replace("mm", "mi").replace("HH", "HH24");
		
		sb.append(" AND to_date(substr(事件发生时间,0,14),'").append(pattern)
		.append("') between ")
		.append("to_date('")
		.append(sdf.format(getDateStart()))
		.append("','").append(pattern).append("')")
		.append(" AND ")
		.append("to_date('")
		.append(sdf.format(getDateEnd()))
		.append("','").append(pattern).append("')");
		
		sb.append(" AND 1=1");

		return sb.toString();
	}
	public static void LoadAllTasks(){
		if (allTasks != null)
			return;
		String fpath = PATH_TO_SAVE_TASKS;
		removeAllTasks();
		
		File dirEvents = new File(fpath);
		if (!dirEvents.exists()){			
			return;
		}
		File [] files = dirEvents.listFiles();
		for(File file : files){
			if (file.isFile()){
				TaskElement task = LoadTask(file);
				if (task.getTaskName() != null)
					add1Task(task, false); 
			}
		}
	}
	public static void removeAllTasks(){
		if (allTasks == null){
			allTasks = new ArrayList<TaskElement>();
			return;
		}
		for (TaskElement task : allTasks){
			allTasks.remove(task);
			notifyTaskListener(ITaskElementEventListener.TASK_DEL, task, ITaskElementEventListener.TASK_MODIFY_ELSE);
		}
	}
	public static boolean add1Task(TaskElement task, boolean saveToFile){
		if (allTasks == null)
			allTasks = new ArrayList<TaskElement>();
		if (allTasks.contains(task)){
			return modify1Task(task,ITaskElementEventListener.TASK_MODIFY_ELSE);
		}else{
			if (saveToFile)
				if (!SaveTask(task))
					return false;
			
			allTasks.add(task);
			notifyTaskListener(ITaskElementEventListener.TASK_ADD, task, ITaskElementEventListener.TASK_MODIFY_ELSE);
			return true;
		}
		
	}
	public static void del1Task(TaskElement task){
		if (task == null)
			return;
		if (allTasks.contains(task)){
			allTasks.remove(task);
			
			String fname = PATH_TO_SAVE_TASKS + task.getTaskName() + ".xml";
			File f = new File(fname);
			if (f.exists())
				f.delete();
			
			notifyTaskListener(ITaskElementEventListener.TASK_DEL, task,  ITaskElementEventListener.TASK_MODIFY_ELSE);
		}
	}
	public static boolean modify1Task(TaskElement task, int modify_type){
		if (SaveTask(task)){
			if (!allTasks.contains(task))
				allTasks.add(task);
			notifyTaskListener(ITaskElementEventListener.TASK_MODIFY, task, modify_type);
		}		
		return true;
	}	
	public static boolean display1Task(TaskElement task, int displayType){
		notifyTaskListener(ITaskElementEventListener.TASK_DISPLAY, task,  ITaskElementEventListener.TASK_MODIFY_ELSE);
		return true;
	}	
	
	//从xml文件中加载出一个Task
	public static TaskElement LoadTask(File f){
		FileInputStream fis = null;
		InputStreamReader isr = null;
		
		TaskElement task = new TaskElement();
		Properties prop = new Properties();
		try{
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis, Charset.forName("utf8"));
			prop.loadFromXML(fis);
			for (Entry<Object, Object>p : prop.entrySet()){
				String key = p.getKey().toString();
				if(key.equalsIgnoreCase("eventName"))
					task.setTaskName(p.getValue().toString());
				else if (key.equalsIgnoreCase("comments"))
					task.setComments(p.getValue().toString());
				else if (key.equalsIgnoreCase("miningObject"))
					task.setMiningObject(p.getValue().toString());
				else if (key.equalsIgnoreCase("aggregateMethod"))
					task.setAggregateMethod(AggregateMethod.fromString(p.getValue().toString()));
				else if (key.equalsIgnoreCase("filterCondition"))
					task.setFilterCondition(p.getValue().toString());				
				else if (key.equalsIgnoreCase("miningMethod"))
					task.setMiningMethod(MiningMethod.fromString(p.getValue().toString()));
				else if (key.equalsIgnoreCase("miningParams"))
					task.setMiningParams(IParamsNetworkMining.fromString(p.getValue().toString()));
				else if (key.equalsIgnoreCase("dateStart"))
					task.setDateStart(sdf.parse(p.getValue().toString()));
				else if (key.equalsIgnoreCase("dateEnd"))
					task.setDateEnd(sdf.parse(p.getValue().toString()));
				else if (key.equalsIgnoreCase("sqlStr"))
					task.setSqlStr(p.getValue().toString());
				else if (key.equalsIgnoreCase("granularity"))
					task.setGranularity(Integer.parseInt(p.getValue().toString()));
				else if (key.equalsIgnoreCase("discreteMethod"))
					task.setDiscreteMethod(DiscreteMethod.fromString(p.getValue().toString()));
				else if (key.equalsIgnoreCase("discreteEndNodes"))
					task.setDiscreteEndNodes(p.getValue().toString());
				else if (key.equalsIgnoreCase("discreteDimsion"))
					task.setDiscreteDimension(Integer.parseInt(p.getValue().toString()));
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}finally{
			if (isr != null)
				try {
					isr.close();
				} catch (IOException e) {
				}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
		return task;
	}
	
	public static boolean SaveTask(TaskElement ee){
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		if (ee.getTaskName() == null || ee.getTaskName().length() == 0){
			JOptionPane.showMessageDialog(null, "没有事件名称！", "保存失败！", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		try{
			File f = new File(PATH_TO_SAVE_TASKS);
			if (!f.exists())
				f.mkdirs();
			fos = new FileOutputStream(PATH_TO_SAVE_TASKS + ee.getTaskName() + ".xml");
			osw = new OutputStreamWriter(fos, Charset.forName("utf8"));
			Properties prop = ee.toProperties();
			prop.storeToXML(fos, ee.getComments());
		}catch(Exception e1){
			return false;
		}finally{
			if (osw != null)
				try {
					osw.close();
				} catch (IOException e) {
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
		}
		return true;
	}

	public void setRunning(boolean running){
		this.isMining = running;
	}
	
	public static void addTaskListener(ITaskElementEventListener listener){
		listeners.add(listener);
	}
	public static void removeTaskListener(ITaskElementEventListener listener){
		listeners.remove(listener);
	}
	private static void notifyTaskListener(int taskEventType, TaskElement task, int modify_type){
		Iterator<ITaskElementEventListener> it = listeners.iterator();
		while(it.hasNext()){
			ITaskElementEventListener listener = it.next();
			if (taskEventType == ITaskElementEventListener.TASK_ADD)
				listener.onTaskAdded(task);
			else if (taskEventType == ITaskElementEventListener.TASK_DEL)
				listener.onTaskDeleted(task);
			else if (taskEventType == ITaskElementEventListener.TASK_DISPLAY)
				listener.onTaskToDisplay(task);
			else if (taskEventType == ITaskElementEventListener.TASK_MODIFY)
				listener.onTaskModified(task, modify_type);
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMiningObject() {
		return miningObject;
	}

	public void setMiningObject(String miningObject) {
		this.miningObject = miningObject;
	}



	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}


	public IParamsNetworkMining getMiningParams() {
		return miningParams;
	}

	public void setMiningParams(IParamsNetworkMining miningParams) {
		this.miningParams = miningParams;
	}
	


	public MiningMethod getMiningMethod() {
		return miningMethod;
	}

	public void setMiningMethod(MiningMethod miningMethod) {
		if (this.miningMethod != null && this.miningMethod.equals(miningMethod))
			return;
		this.miningMethod = miningMethod;
		if (miningMethod.equals(MiningMethod.MiningMethods_TsAnalysis))
			this.setMiningParams(new ParamsTSA());
		else if (miningMethod.equals(MiningMethod.MiningMethods_SequenceMining))
			this.setMiningParams(new ParamsSM());
		else if (miningMethod.equals(MiningMethod.MiningMethods_PeriodicityMining))
			this.setMiningParams(new ParamsPM());
	}
	public Date getDateStart() {
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public String getSqlStr() {
		return sqlStr;
	}
	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}
	public int getGranularity() {
		return granularity;
	}
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	
	@Override
	public int compareTo(TaskElement o) {
		return this.getTaskName().compareTo(o.getTaskName());
	}
	public AggregateMethod getAggregateMethod() {
		return aggregateMethod;
	}
	public void setAggregateMethod(AggregateMethod aggregateMethod) {
		this.aggregateMethod = aggregateMethod;
	}
	public DiscreteMethod getDiscreteMethod() {
		return discreteMethod;
	}
	public void setDiscreteMethod(DiscreteMethod discreteMethod) {
		this.discreteMethod = discreteMethod;
	}
	public String getDiscreteEndNodes() {
		return discreteEndNodes;
	}
	public void setDiscreteEndNodes(String discreteEndNodes) {
		this.discreteEndNodes = discreteEndNodes;
	}
	public int getDiscreteDimension() {
		return discreteDimension;
	}
	public void setDiscreteDimension(int discreteDimension) {
		this.discreteDimension = discreteDimension;
	}
	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
	
	public MiningAlgo getMiningAlgo() {
		return miningAlgo;
	}

	public void setMiningAlgo(MiningAlgo miningAlgo) {
		this.miningAlgo = miningAlgo;
	}

}