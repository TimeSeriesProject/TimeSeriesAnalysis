package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class PeriodMinerFactory {
	private static PeriodMinerFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";
	private PeriodMinerFactory(){}
	public static PeriodMinerFactory getInstance(){
		if(inst==null){
			isMining=false;
			inst=new PeriodMinerFactory();
		}
		return inst;
	}
	
	public void minerAllPeriods(){
		
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
						reader.readEachProtocolDataItems(dataFile.getAbsolutePath());
		int[] granularities={3600};
		for(int granularity:granularities){
			for(String protocol:rawDataItems.keySet()){
				DataItems dataItems=rawDataItems.get(protocol);
				if(!isDataItemSparse(dataItems)){
					TaskElement task = new TaskElement();
					String name = ip+"_"+protocol+"_"+granularity+"_"+"周期挖掘_auto";
					task.setTaskName(name);
					task.setComments("挖掘  ip 为"+ip+" ，粒度为"+granularity+"s 的协议"+protocol+"的周期规律");
					task.setDataSource("File");
					task.setGranularity(granularity);
					task.setAggregateMethod(AggregateMethod.Aggregate_SUM);
					task.setSourcePath(dataFile.getPath());
					task.setTaskRange(TaskRange.SingleNodeRange);
					task.setRange(ip);
					task.setDiscreteMethod(DiscreteMethod.None);
					task.setDiscreteEndNodes("20000,20500,21000,21500,22000,22500,23000,23500,24000,24500,25000,25500,"
							+ "26000,26500,27000,27500,28000,28500,29000,29500,30000,30500,31000,31500,32000,32500,33000,335000,34000");
					task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
					task.setMiningMethod(MiningMethod.MiningMethods_PeriodicityMining);
					task.setMiningObject("traffic");
					task.setProtocol(protocol);
					TaskElement.add1Task(task, false);
					NetworkMinerFactory minerFactory=NetworkMinerFactory.getInstance();
					INetworkMiner miner=minerFactory.allMiners.get(task);
					miner.getResults().setInputData(dataItems);
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
