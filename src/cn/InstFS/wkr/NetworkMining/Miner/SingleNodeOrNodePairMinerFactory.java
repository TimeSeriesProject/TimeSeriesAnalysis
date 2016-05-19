package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class SingleNodeOrNodePairMinerFactory {
	private static SingleNodeOrNodePairMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	private MiningObject miningObject;
	private TaskRange taskRange;
	private MiningMethod method;
	
	private SingleNodeOrNodePairMinerFactory(){}
	
	public static SingleNodeOrNodePairMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new SingleNodeOrNodePairMinerFactory();
		}
		return inst;
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
	public void detect(){
		
		if(isMining)
			return;
		isMining=true;
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		if(dataDirectory.isFile()){
			parseFile(dataDirectory,reader);
		}else{
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<dataDirs.length;i++){
				parseFile(dataDirs[i],reader);
			}
		}
	}
	
	private void parseFile(File dataFile,nodePairReader reader){
		String ip=dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
		//事先读取每一个IP上，每一个协议的DataItems
		int granularity=3600;
		if(taskRange.toString().equals(TaskRange.SingleNodeRange.toString())){
			HashMap<String, DataItems> rawDataItems=null;
			switch (miningObject) {
			case MiningObject_Traffic:
				rawDataItems=reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
				break;
			case MiningObject_Times:
				rawDataItems=reader.readEachProtocolTimesDataItems(dataFile.getAbsolutePath());
				break;
			default:
				break;
			}
			for(String protocol:rawDataItems.keySet()){
				DataItems dataItems=rawDataItems.get(protocol);
				if(!isDataItemSparse(dataItems)){
					TaskCombination taskCombination=new TaskCombination();
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_PeriodicityMining));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_OutliesMining));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_Statistics));
					taskCombination.getTasks().add(generateTask(taskRange, granularity,
							dataFile, protocol, ip, MiningMethod.MiningMethods_SequenceMining));
					taskCombination.setMiningObject(miningObject.toString());
					taskCombination.setDataItems(dataItems);
					taskCombination.setProtocol(protocol);
					taskCombination.setRange(ip);
					taskCombination.setName();
					TaskElement.add1Task(taskCombination, false);
				}
			}
		}else if(taskRange.toString().equals(TaskRange.NodePairRange.toString())){
			HashMap<String, Map<String, DataItems>> ipPairRawDataItems=null;
			switch (miningObject) {
			case MiningObject_Traffic:
				ipPairRawDataItems=reader.readEachIpPairProtocolTrafficDataItems(dataFile.getAbsolutePath());
				break;
			case MiningObject_Times:
				ipPairRawDataItems=reader.readEachIpPairProtocolTimesDataItems(dataFile.getAbsolutePath());
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
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_PeriodicityMining));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_OutliesMining));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_Statistics));
						taskCombination.getTasks().add(generateTask(taskRange, granularity,
								dataFile, protocol, ipPair, MiningMethod.MiningMethods_SequenceMining));
						taskCombination.setMiningObject(miningObject.toString());
						taskCombination.setDataItems(dataItems);
						taskCombination.setProtocol(protocol);
						taskCombination.setRange(ipPair);
						taskCombination.setName();
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
			name=ipOrPair+"_"+protocol+"_异常检测_auto";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+" 上,协议"+protocol+"的异常");
			break;
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			name = ipOrPair+"_"+protocol+"_"+granularity+"_周期挖掘_auto";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+",粒度为"+granularity+"s 的协议"+protocol+"的周期规律");
			break;
		case MiningMethods_SequenceMining:
			name=ipOrPair+"_"+protocol+"_auto_频繁模式挖掘";
			task.setTaskName(name);
			task.setComments("挖掘  "+ipOrPair+" 上,协议为"+protocol+"的频繁模式");
			break;
		case MiningMethods_Statistics:
			name=ipOrPair+"_"+protocol+"_统计_auto";
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
