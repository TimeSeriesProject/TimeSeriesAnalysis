package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class FrequentItemMinerFactory {
	private static FrequentItemMinerFactory freMinerFactoryInst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	private FrequentItemMinerFactory(){}
	public static FrequentItemMinerFactory getInstance(){
		if(freMinerFactoryInst==null){
			isMining=false;
			freMinerFactoryInst=new FrequentItemMinerFactory();
		}
		return freMinerFactoryInst;
	}
	
	public void detectFrequntItems(){
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
		HashMap<String, DataItems> rawDataItems=
				reader.readEachProtocolTrafficDataItems(dataFile.getAbsolutePath());
		int[] granularities={3600};
		for(int granularity:granularities){
			for(String protocol:rawDataItems.keySet()){
				DataItems dataItems=rawDataItems.get(protocol);
                if(!isDataItemSparse(dataItems)){
                	for(int num=2;num<=4;num++){
    					TaskElement task=new TaskElement();
    					String name=ip+"_"+protocol+"_auto_频繁模式挖掘"+"_"+num;
    					task.setTaskName(name);
    					task.setPatternNum(num);
    					task.setComments("挖掘  "+"ip 为"+ip+" 上，协议为"+protocol+"的频繁模式");
    					task.setDataSource("File");
    					task.setGranularity(granularity);
    					task.setAggregateMethod(AggregateMethod.Aggregate_MEAN);
    					task.setSourcePath(dataFile.getPath());
    					task.setTaskRange(TaskRange.SingleNodeRange);
    					task.setRange(ip);
    					task.setDiscreteMethod(DiscreteMethod.None);
    					task.setMiningMethod(MiningMethod.MiningMethods_SequenceMining);
    					task.setMiningObject(protocol);
    					TaskElement.add1Task(task, false);
    					NetworkMinerFactory minerFactory=NetworkMinerFactory.getInstance();
    					INetworkMiner miner=minerFactory.allMiners.get(task);
    					miner.getResults().setInputData(dataItems);
    				}
				}
			}
		}
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
