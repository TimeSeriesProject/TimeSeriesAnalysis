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

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import cn.InstFS.wkr.NetworkMining.DataInputs.OracleUtils;
import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Miner.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsFP;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPP;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsClass;

public class TaskElement extends JDialog implements Serializable, Comparable<TaskElement>{
	public static String PATH_TO_SAVE_TASKS = "./tasks/";
	public static List<TaskElement>allTasks ;
	public static List<TaskCombination> allCombinationTasks;

	private String taskName;
	private Date dateStart;
	private Date dateEnd;
	private String comments;
	private String dataSource;//源文件类型  数据库 或 文本文件
	private String sourcePath; //当为文本文件时 文件路径
	private String pathSource;
	private String protocol;
	private int patternNum;   //检测序列频繁模式时，确定序列的频繁项
	private boolean isReadBetween = false; //判断是否读取区间文件

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

	public static Vector<ITaskElementEventListener> listeners = new Vector<ITaskElementEventListener>();


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
		TSAExample.setMiningMethod(MiningMethod.MiningMethods_OutliesMining);
		TSAExample.setMiningAlgo(MiningAlgo.MiningAlgo_ARTSA);
		TSAExample.setDataSource("File");
		TSAExample.setSourcePath("./configs/real-1-1.csv");
		TSAExample.setDiscreteMethod(DiscreteMethod.None);
		TSAExample.setDiscreteEndNodes("0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,"
				+ "2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0");
		TSAExample.setTaskRange(TaskRange.NodePairRange);
		TSAExample.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement FPExample=new TaskElement();
	static{
		FPExample.setTaskName("大流量TSA测试");
		FPExample.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		FPExample.setMiningObject("traffic");
		FPExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		FPExample.setFilterCondition("流量>100");
		FPExample.setGranularity(3600);
		FPExample.setMiningMethod(MiningMethod.MiningMethods_FrequenceItemMining);
		FPExample.setDataSource("File");
		FPExample.setSourcePath("./configs/real-1-71.csv,./configs/real-1-72.csv");
		FPExample.setDiscreteMethod(DiscreteMethod.None);
		FPExample.setDiscreteEndNodes("0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,"
				+ "2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0");
		FPExample.setTaskRange(TaskRange.NodePairRange);
		FPExample.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement TSAExample1=new TaskElement();
	static{
		TSAExample1.setTaskName("大流量TSA测试");
		TSAExample1.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExample1.setMiningObject("traffic");
		TSAExample1.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAExample1.setFilterCondition("流量>100");
		TSAExample1.setGranularity(3600);
		TSAExample1.setMiningMethod(MiningMethod.MiningMethods_OutliesMining);
		TSAExample1.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
		TSAExample1.setDataSource("File");
		TSAExample1.setSourcePath("./configs/real-1-37.csv");
		TSAExample1.setDiscreteMethod(DiscreteMethod.None);
		TSAExample1.setDiscreteEndNodes("0,40,80,120,160,200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,"
				+ "800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1640,1680,1720,1760,1800,1840,1880,1920,1960,2000");
		TSAExample1.setTaskRange(TaskRange.NodePairRange);
		TSAExample1.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement TSAExampleGauss=new TaskElement();
	static{
		TSAExampleGauss.setTaskName("大流量TSA测试");
		TSAExampleGauss.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExampleGauss.setMiningObject("traffic");
		TSAExampleGauss.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAExampleGauss.setFilterCondition("流量>100");
		TSAExampleGauss.setGranularity(3600);
		TSAExampleGauss.setMiningMethod(MiningMethod.MiningMethods_OutliesMining);
		TSAExampleGauss.setMiningAlgo(MiningAlgo.MiningAlgo_GaussDetection);
		TSAExampleGauss.setDataSource("File");
		TSAExampleGauss.setSourcePath("./configs/real-1-44.csv");
		TSAExampleGauss.setDiscreteMethod(DiscreteMethod.None);
		TSAExampleGauss.setDiscreteEndNodes("0,40,80,120,160,200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1640,1680,1720,1760,1800,1840,1880,1920,1960,2000");
		TSAExampleGauss.setTaskRange(TaskRange.NodePairRange);
		TSAExampleGauss.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement TSAExampleFourier=new TaskElement();
	static{
		TSAExampleFourier.setTaskName("大流量TSA测试");
		TSAExampleFourier.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExampleFourier.setMiningObject("traffic");
		TSAExampleFourier.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAExampleFourier.setFilterCondition("流量>100");
		TSAExampleFourier.setGranularity(3600);
		TSAExampleFourier.setMiningMethod(MiningMethod.MiningMethods_OutliesMining);
		TSAExampleFourier.setMiningAlgo(MiningAlgo.MiningAlgo_FastFourier);
		TSAExampleFourier.setDataSource("File");
		TSAExampleFourier.setSourcePath("./configs/real-1-70.csv");
		TSAExampleFourier.setDiscreteMethod(DiscreteMethod.None);
		TSAExampleFourier.setDiscreteEndNodes("0,40,80,120,160,200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,"
				+ "800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1640,1680,1720,1760,1800,1840,1880,1920,1960,2000");
		TSAExampleFourier.setTaskRange(TaskRange.NodePairRange);
		TSAExampleFourier.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement TSAExampleARIMA=new TaskElement();
	static{
		TSAExampleARIMA.setTaskName("大流量TSA测试");
		TSAExampleARIMA.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExampleARIMA.setMiningObject("traffic");
		TSAExampleARIMA.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAExampleARIMA.setFilterCondition("流量>100");
		TSAExampleARIMA.setGranularity(3600);
		TSAExampleARIMA.setMiningMethod(MiningMethod.MiningMethods_PredictionMining);
		TSAExampleARIMA.setMiningAlgo(MiningAlgo.MiningAlgo_ARIMATSA);
		TSAExampleARIMA.setDataSource("File");
		TSAExampleARIMA.setSourcePath("./configs/real-1-61.csv");
		TSAExampleARIMA.setDiscreteMethod(DiscreteMethod.None);
		TSAExampleARIMA.setDiscreteEndNodes("0,40,80,120,160,200,240,280,320,360,400,440,480,520,560,600,640,680,720,760,"
				+ "800,840,880,920,960,1000,1040,1080,1120,1160,1200,1240,1280,1320,1360,1400,1440,1480,1520,1560,1600,1640,1680,1720,1760,1800,1840,1880,1920,1960,2000");
		TSAExampleARIMA.setTaskRange(TaskRange.NodePairRange);
		TSAExampleARIMA.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement TSANeutralExample=new TaskElement();
	static{
		TSANeutralExample.setTaskName("大流量TSA测试");
		TSANeutralExample.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSANeutralExample.setMiningObject("traffic");
		TSANeutralExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSANeutralExample.setFilterCondition("流量>100");
		TSANeutralExample.setGranularity(3600);
		TSANeutralExample.setMiningMethod(MiningMethod.MiningMethods_PredictionMining);
		TSANeutralExample.setMiningAlgo(MiningAlgo.MiningAlgo_NeuralNetworkTSA);
		TSANeutralExample.setDataSource("File");
		TSANeutralExample.setSourcePath("./configs/real-1-68.csv");
		TSANeutralExample.setDiscreteMethod(DiscreteMethod.None);
		TSANeutralExample.setTaskRange(TaskRange.NodePairRange);
		TSANeutralExample.setRange("10.0.1.1,10.0.1.2");
	}
	public static TaskElement TSAAssociationExample=new TaskElement();
	static{
		TSAAssociationExample.setTaskName("多元时间序列关联规则测试");
		TSAAssociationExample.setComments("将流量较大的收发方作为挖掘对象, 挖掘关联规则模式");
		TSAAssociationExample.setMiningObject("traffic");
		TSAAssociationExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		TSAAssociationExample.setFilterCondition("流量>100");
		TSAAssociationExample.setGranularity(3600);
		TSAAssociationExample.setMiningMethod(MiningMethod.MiningMethods_FrequenceItemMining);
		//TSAAssociationExample.setMiningAlgo(MiningAlgo.MiningAlgo_NeuralNetworkTSA);
		TSAAssociationExample.setDataSource("File");
		TSAAssociationExample.setSourcePath("./configs/real-1-71.csv,./configs/real-1-72.csv");
		TSAAssociationExample.setDiscreteMethod(DiscreteMethod.None);
		TSAAssociationExample.setTaskRange(TaskRange.NodePairRange);
		TSAAssociationExample.setRange("10.0.1.1,10.0.1.2");
	}
	public static TaskElement example2 = new TaskElement();
	static {
		example2.setTaskName("大流量通信收发方");
		example2.setComments("将流量较大的收发方作为挖掘对象, 挖掘其序列模式");
		example2.setMiningObject("traffic");
		example2.setDataSource("Text");
		example2.setSourcePath("./configs/smtpPcap");
		example2.setTaskRange(TaskRange.NodePairRange);
		example2.setFilterCondition("protocol=402");
		example2.setGranularity(3600);
		example2.setRange("10.0.2.4,10.0.3.4");
		example2.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		example2.setMiningMethod(MiningMethod.MiningMethods_SequenceMining);		
	}
	
	public static TaskElement TSAExamplePM=new TaskElement();
	static{
		TSAExamplePM.setTaskName("大流量TSA测试");
		TSAExamplePM.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		TSAExamplePM.setMiningObject("traffic");
		TSAExamplePM.setAggregateMethod(AggregateMethod.Aggregate_MEAN);
		TSAExamplePM.setFilterCondition("流量>100");
		TSAExamplePM.setGranularity(3600);
		TSAExamplePM.setMiningMethod(MiningMethod.MiningMethods_PeriodicityMining);
		TSAExamplePM.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
		TSAExamplePM.setDiscreteMethod(DiscreteMethod.None);
		TSAExamplePM.setDiscreteEndNodes("0,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,2200,2300,2400,2500,2600");
		TSAExamplePM.setDataSource("File");
		TSAExamplePM.setSourcePath("./configs/real-1-55.csv");
		TSAExamplePM.setTaskRange(TaskRange.NodePairRange);
		TSAExamplePM.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement SMExample=new TaskElement();
	static{
		SMExample.setTaskName("大流量TSA测试");
		SMExample.setComments("将流量较大的收发方作为挖掘对象, 挖掘TSA模式");
		SMExample.setMiningObject("traffic");
		SMExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		SMExample.setFilterCondition("流量>100");
		SMExample.setGranularity(3600);
		SMExample.setMiningMethod(MiningMethod.MiningMethods_SequenceMining);
		SMExample.setDiscreteMethod(DiscreteMethod.None);
		SMExample.setDiscreteDimension(8);
		SMExample.setDiscreteEndNodes("0,5000,10000,15000,20000,25000,30000,35000,40000,45000,50000");
		SMExample.setDataSource("File");
		SMExample.setSourcePath("./configs/real-1-39.csv");
		SMExample.setTaskRange(TaskRange.NodePairRange);
		SMExample.setRange("10.0.1.1,10.0.1.2");
	}
	
	public static TaskElement PathPMExample=new TaskElement();
	static{
		PathPMExample.setTaskName("网络路径周期");
		PathPMExample.setComments("将网络路径作为挖掘对象, 挖掘PM模式");
		PathPMExample.setMiningObject("pathProb");
		PathPMExample.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		PathPMExample.setFilterCondition("流量>100");
		PathPMExample.setGranularity(3600);
		PathPMExample.setMiningMethod(MiningMethod.MiningMethods_PathProbilityMining);
		PathPMExample.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
		PathPMExample.setDataSource("File");
		PathPMExample.setSourcePath("./configs/pathData");
		PathPMExample.setDiscreteMethod(DiscreteMethod.None);
		PathPMExample.setPathSource("./configs/probPath.txt");
		//PathPMExample.setDiscreteEndNodes("0,2,4,6,8,10,12,14,16,18,20");
		PathPMExample.setTaskRange(TaskRange.NodePairRange);
		PathPMExample.setRange("10.0.1.0,10.0.2.0");
	}
	
	public static TaskElement PathProbTrans=new TaskElement();
	static{
		PathProbTrans.setTaskName("路由器跳转概率预测");
		PathProbTrans.setComments("将网络路径跳转概率作为挖掘对象");
		PathProbTrans.setMiningObject("path");
		PathProbTrans.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		PathProbTrans.setFilterCondition("流量>100");
		PathProbTrans.setGranularity(3600);
		PathProbTrans.setMiningMethod(MiningMethod.MiningMethods_PathProbilityMining);
		PathProbTrans.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
		PathProbTrans.setDataSource("File");
		PathProbTrans.setSourcePath("./configs/pathData");
		PathProbTrans.setDiscreteMethod(DiscreteMethod.None);
		PathProbTrans.setPathSource("./configs/probPath.txt");
		//PathPMExample.setDiscreteEndNodes("0,2,4,6,8,10,12,14,16,18,20");
		PathProbTrans.setTaskRange(TaskRange.NodePairRange);
		PathProbTrans.setRange("10.0.1.0,10.0.2.0");
	}
	
	
	public static TaskElement ClusterPMExample=new TaskElement();
	static{
		ClusterPMExample.setTaskName("网络簇系数测试");
		ClusterPMExample.setComments("将网络簇系数挖掘对象, 挖掘PM模式");
		ClusterPMExample.setMiningObject("节点变化");
		ClusterPMExample.setAggregateMethod(AggregateMethod.Aggregate_MAX);
		ClusterPMExample.setDiscreteMethod(DiscreteMethod.自定义端点);
		ClusterPMExample.setGranularity(3600);
		ClusterPMExample.setMiningMethod(MiningMethod.MiningMethods_PeriodicityMining);
		ClusterPMExample.setMiningAlgo(MiningAlgo.MiningAlgo_averageEntropyPM);
		ClusterPMExample.setDataSource("File");
		ClusterPMExample.setSourcePath("./configs/clusterPathData");
		ClusterPMExample.setDiscreteEndNodes("0,1");
		ClusterPMExample.setTaskRange(TaskRange.WholeNetworkRange);
	}
	
	public TaskElement() {
		setTaskName("qq");
		setFilterCondition("");
		setMiningObject("");
		setComments("");
		// 离散化
		setDiscreteMethod(DiscreteMethod.None);
		setDiscreteEndNodes("0,0,0,0");
		// 按时间聚合数据
		setGranularity(1);
		setAggregateMethod(AggregateMethod.Aggregate_NONE);
		setMiningAlgo(MiningAlgo.MiningAlgo_NULL);
		setMiningMethod(MiningMethod.MiningMethods_SequenceMining);
		setMiningParams(new ParamsSM());

		Calendar cal = Calendar.getInstance();
		cal.set(1, 0, 1, 0, 0, 0);
		setDateEnd(cal.getTime());
		cal.add(Calendar.MONTH, -1);
		setDateStart(cal.getTime());
		setDiscreteDimension(2);
		setDataSource("File");
		setTaskRange(TaskRange.NodePairRange);
		setRange(" ");
		setDataSource("");
		setSourcePath("");
		setSqlStr("");
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
		putProp(prop, "miningMethod", getMiningMethod());
		putProp(prop,"miningAlgo",getMiningAlgo());
		putProp(prop, "discreteMethod", getDiscreteMethod());
		putProp(prop, "aggregateMethod", getAggregateMethod());
		putProp(prop, "miningParams", getMiningParams());
		putProp(prop,"miningObject",getMiningObject());
		putProp(prop, "filterCondition", getFilterCondition());
		putProp(prop,"eventName", getTaskName());
		putProp(prop, "comments", getComments());
		putProp(prop, "dateStart", getDateStart());
		putProp(prop,"range",getRange());
		putProp(prop,"taskRange",getTaskRange());
		putProp(prop, "discreteDimsion", getDiscreteDimension());
		putProp(prop, "discreteEndNodes", getDiscreteEndNodes());
		putProp(prop, "granularity", getGranularity());
		putProp(prop, "dateEnd", getDateEnd());
		putProp(prop,"dataSource",getDataSource());
		putProp(prop,"sourcePath",getSourcePath());
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
		//add
		task.setMiningAlgo(this.getMiningAlgo());
		task.setRange(this.getRange());
		task.setTaskRange(this.getTaskRange());
		task.setDataSource(this.getDataSource());
		task.setSourcePath(this.getSourcePath());
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
	public static boolean add1Task(TaskCombination task, boolean saveToFile){
		if (allCombinationTasks == null)
			allCombinationTasks = new ArrayList<TaskCombination>();
		if (allCombinationTasks.contains(task)){
			return modify1Task(task,ITaskElementEventListener.TASK_MODIFY_ELSE);
		}else{
			allCombinationTasks.add(task);
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
	
	public static boolean modify1Task(TaskCombination task, int modify_type){
		if (!allCombinationTasks.contains(task))
			allCombinationTasks.add(task);
		notifyTaskListener(ITaskElementEventListener.TASK_MODIFY, task, modify_type);		
		return true;
	}	
	public static boolean display1Task(TaskCombination task, int displayType){
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
				else if (key.equalsIgnoreCase("dataSource"))
					task.setDataSource(p.getValue().toString());
				else if (key.equalsIgnoreCase("sourcePath"))
					task.setSourcePath(p.getValue().toString());
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
				else if(key.equalsIgnoreCase("range"))
					task.setRange(p.getValue().toString());
				else if(key.equalsIgnoreCase("protocol"))
					task.setProtocol(p.getValue().toString());
				else if(key.equalsIgnoreCase("taskRange")){
					if(p.getValue().toString().equalsIgnoreCase("NodePairRange"))
						task.setTaskRange(TaskRange.NodePairRange);
					else if(p.getValue().toString().equalsIgnoreCase("SingleNodeRange"))
						task.setTaskRange(TaskRange.SingleNodeRange);
					else if(p.getValue().toString().equalsIgnoreCase("WholeNetworkRange"))
						task.setTaskRange(TaskRange.WholeNetworkRange);
				}
				else if(key.equalsIgnoreCase("miningAlgo")){
					task.setMiningAlgo(MiningAlgo.fromString(p.getValue().toString()));
				}
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
	private static void notifyTaskListener(int taskEventType, TaskCombination task, int modify_type){
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
		if (miningMethod.equals(MiningMethod.MiningMethods_PredictionMining))
			this.setMiningParams(new ParamsTSA());
		else if(miningMethod.equals(MiningMethod.MiningMethods_OutliesMining))
			this.setMiningParams(new ParamsTSA());
		else if (miningMethod.equals(MiningMethod.MiningMethods_SequenceMining))
			this.setMiningParams(new ParamsSM());
		else if (miningMethod.equals(MiningMethod.MiningMethods_PeriodicityMining))
			this.setMiningParams(new ParamsPM());
		else if(miningMethod.equals(MiningMethod.MiningMethods_FrequenceItemMining))
			this.setMiningParams(new ParamsFP());
		else if(miningMethod.equals(MiningMethod.MiningMethods_PathProbilityMining))
			this.setMiningParams(new ParamsPP());
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
	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof TaskElement){
			return this.getTaskName().equals(((TaskElement)arg0).getTaskName());
		}else{
			return false;
		}	  
	}
	
	@Override
	public int hashCode() {
		return this.getTaskName().hashCode();
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

	public String getPathSource() {
		return pathSource;
	}

	public void setPathSource(String pathSource) {
		this.pathSource = pathSource;
	}

	public int getPatternNum() {
		return patternNum;
	}

	public void setPatternNum(int patternNum) {
		this.patternNum = patternNum;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public boolean getIsReadBetween(){
		return isReadBetween;
	}
	public void setIsReadBetween(boolean b){
		this.isReadBetween = b;
	}
}