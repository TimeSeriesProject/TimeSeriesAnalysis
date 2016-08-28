package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;

public class SingleNodeOrNodePairMinerFactory extends MinerFactorySettings {
	private static SingleNodeOrNodePairMinerFactory inst;
	private static SingleNodeOrNodePairMinerFactory pairInst;
	public static boolean isMining=false;

	public String dataPath;
	public String rootPath;

	private MiningObject miningObject;
	private TaskRange taskRange = TaskRange.SingleNodeRange;
	private MiningMethod method;
	
	private SingleNodeOrNodePairMinerFactory(String minertype){
		super(minertype);
		dataPath = GlobalConfig.getInstance().getDataPath() + "\\traffic";
		rootPath = GlobalConfig.getInstance().getDataPath() + "\\node";
		List<MiningObject> miningObjectList = this.getMiningObjectList();
		miningObjectList.add(MiningObject.MiningObject_Times);
		miningObjectList.add(MiningObject.MiningObject_Traffic);
		if (minertype.equals("节点规律挖掘"))
			miningObjectList.add(MiningObject.MiningObject_NodeDisapearEmerge);

		List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
		miningObjectCheck.addAll(miningObjectList);
	}
	
	public static SingleNodeOrNodePairMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new SingleNodeOrNodePairMinerFactory("节点规律挖掘");
		}
		return inst;
	}

	public static SingleNodeOrNodePairMinerFactory getPairInstance() {
		if(pairInst==null){
			isMining=false;
			pairInst=new SingleNodeOrNodePairMinerFactory("链路规律挖掘");
		}
		return pairInst;
	}
	
	public MiningMethod getMethod() {
		return method;
	}
	public void setMethod(MiningMethod method) {
		this.method = method;
	}
	public TaskRange getTaskRange() {
		return taskRange;
	}
	public void setTaskRange(TaskRange taskRange) {
		this.taskRange = taskRange;
	}
	public MiningObject getMiningObject() {
		return miningObject;
	}
	public void setMiningObject(MiningObject miningObject) {
		this.miningObject = miningObject;
	}
	public String getDataPath() {
		return dataPath;
	}
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
	public void reset(){
		isMining=false;
	}
	public void detect(){
		
		if(isMining)
			return;
		
		isMining=true;
		File dataDirectory = null;
		if(MiningObject.MiningObject_NodeDisapearEmerge.toString().equals(miningObject.toString()))
		{
			dataDirectory = new File(rootPath);
		}
		else{
			dataDirectory = new File(dataPath);
		}
		
		nodePairReader reader=new nodePairReader();
//		if(dataDirectory.isFile()){
//			parseFile(dataDirectory,reader);
//		}else{
		
		File[] dataDirs=dataDirectory.listFiles();
		for(int i=0;i<dataDirs.length;i++){
			//按天必须是文件夹
			if(dataDirs[i].isDirectory())
				parseFile(dataDirs[i],reader);
		}

	}
	
	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName();//.substring(0, dataFile.getName().lastIndexOf("."));
		//事先读取每一个IP上，每一个协议的DataItems
		//if(ip.equals("10.0.13.2"))
			//System.out.println();
		int granularity= Integer.parseInt(this.getGranularity());
		if(taskRange.toString().equals(TaskRange.SingleNodeRange.toString())){
			HashMap<String, DataItems> rawDataItems=null;
			boolean isNodeDisapearEmerge = false;
			
			switch (miningObject) {
			case MiningObject_Traffic:
				//rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
				//rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
				/**2016/7/14
				 * @author LYH
				 * 用于测试读取时间区间数据，单节点挖掘
				 * **/
				/*Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.set(2014, 9, 1, 0, 0, 0);
				cal2.set(2014,11,20,0,0,0);*/
				Date date1 = getStartDate();
				Date date2 = getEndDate();
				rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath(),false,date1,date2,3600);
				
				break;
			case MiningObject_Times:
				//rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath());
				//rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),reader.getTask().getIsReadBetween(),reader.getTask().getDateStart(),reader.getTask().getDateEnd());
				/**2016/7/14
				 * @author LYH
				 * 用于测试读取时间区间数据，单节点挖掘
				 * **/
				/*Calendar cal3 = Calendar.getInstance();
				Calendar cal4 = Calendar.getInstance();
				cal3.set(2014, 9, 1, 2, 0, 0);
				cal4.set(2014,9,3,2,0,0);
				Date date3 = cal3.getTime();
				Date date4 = cal4.getTime();*/
				Date date3 = getStartDate();
				Date date4 = getEndDate();
				rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath(),false,date3,date4,3600);
				break;
			case MiningObject_NodeDisapearEmerge:
				Date date5 = getStartDate();
				Date date6 = getEndDate();
				rawDataItems = reader.readEachNodeDisapearEmergeDataItems(dataFile.getAbsolutePath(),false,date5,date6,3600);
				isNodeDisapearEmerge = true;
				break;
			default:
				break;
			}
			for(String protocol:rawDataItems.keySet()){
				DataItems dataItems=rawDataItems.get(protocol);
				if(!isDataItemSparse(dataItems) || isNodeDisapearEmerge){
					TaskCombination taskCombination=new TaskCombination();
					taskCombination.setTaskRange(taskRange);
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_PeriodicityMining));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_Statistics));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_OutliesMining));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_SequenceMining));
					taskCombination.setMiningObject(miningObject.toString());
					taskCombination.setDataItems(dataItems);
					taskCombination.setProtocol(protocol);
					taskCombination.setRange(ip);
					taskCombination.setName();
					taskCombination.setMinerType(MinerType.MiningType_SinglenodeOrNodePair);
					TaskElement.add1Task(taskCombination, false);
				}
			}
		}else if(taskRange.toString().equals(TaskRange.NodePairRange.toString())){
			HashMap<String, Map<String, DataItems>> ipPairRawDataItems=null;
			switch (miningObject) {
			case MiningObject_Traffic:
				//ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath());
				/**2016/7/14
				 * @author LYH
				 * 用于测试读取时间区间数据，单节点挖掘
				 * **/
				/*Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.set(2014, 9, 1, 0, 0, 0);
				cal2.set(2014,11,20,0,0,0);
				Date date1 = cal1.getTime();
				Date date2 = cal2.getTime();*/
				Date date1 = getStartDate();
				Date date2 = getEndDate();
				ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath(),false,date1,date2,3600);
				break;
			case MiningObject_Times:
				//ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath());
				/**2016/7/14
				 * @author LYH
				 * 用于测试读取时间区间数据，单节点挖掘
				 * **/
				Calendar cal3 = Calendar.getInstance();
				Calendar cal4 = Calendar.getInstance();
				cal3.set(2014, 9, 10, 0, 0, 0);
				cal4.set(2014,10,1,0,0,0);
				Date date3 = cal3.getTime();
				Date date4 = cal4.getTime();
				ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath(),false,date3,date4,3600);
				break;
			default:
				break;
			}
			
			for(String ipPair:ipPairRawDataItems.keySet()){
				Map<String, DataItems> itemsMap=ipPairRawDataItems.get(ipPair);
				for(String protocol:itemsMap.keySet()){
					DataItems dataItems=itemsMap.get(protocol);
					if(!isDataItemSparse(dataItems)){
						TaskCombination taskCombination=new TaskCombination();
						taskCombination.setTaskRange(taskRange);
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_PeriodicityMining));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_Statistics));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_OutliesMining));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_SequenceMining));
						taskCombination.setMiningObject(miningObject.toString());
						taskCombination.setDataItems(dataItems);
						taskCombination.setProtocol(protocol);
						taskCombination.setRange(ipPair);
						taskCombination.setName();
						taskCombination.setMinerType(MinerType.MiningType_SinglenodeOrNodePair);
						TaskElement.add1Task(taskCombination, false);
					}
				}
			}
		}
	}
	
	public TaskElement generateTask(TaskRange taskRange,int granularity,File dataFile,String protocol,
			String ipOrPair,MiningMethod method){
		TaskElement task = new TaskElement();
		task.setDataSource("File");
		task.setGranularity(granularity);
		task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
		task.setSourcePath(dataFile.getPath());
		task.setTaskRange(taskRange);
		task.setRange(ipOrPair.replace('-', ','));
		task.setDiscreteMethod(DiscreteMethod.None);
		task.setMiningMethod(method);
		String name=null;
		switch (method) {
		case MiningMethods_OutliesMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
			name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_异常检测_auto";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+" 上,协议"+protocol+"的异常");
			break;
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			name = ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_周期挖掘_auto";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+",粒度为"+granularity+"s 的协议"+protocol+"的周期规律");
			break;
		case MiningMethods_SequenceMining:
			name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_auto_频繁模式挖掘";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+" 上,协议为"+protocol+"的频繁模式");
			break;
		case MiningMethods_Statistics:
			name=ipOrPair+"_"+protocol+"_"+granularity+"_"+miningObject.toString()+"_统计_auto";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+" 上,协议"+protocol+"的统计");
			break;
		default:
			break;
		}
		task.setMiningObject(miningObject.toString());
		task.setProtocol(protocol);
		return task;
	}
	/**
	 * 判断给定的时间序列是否稀疏，（稀疏即意味着时间序列大于50%的值都是0） 如果稀疏返回True
	 * @param dataItems 时间序列
	 * @return true if 时间序列稀疏  否则返回 false
	 */
	private boolean isDataItemSparse(DataItems dataItems){
		int length=dataItems.getLength();
		int sparseNum=0;
		for(int i=0;i<length;i++){
			if(dataItems.getData().get(i).equals("0")){
				sparseNum+=1;
			}
		}
		
		if(sparseNum*1.0/length>=0.5)
			return true;
		else 
			return false;
	}
}