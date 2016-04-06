package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Files;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

public class OutliesMinerFactory {
	private static OutliesMinerFactory outMinerFactoryInst;
	public static boolean isMining=false;
	public String filePath;
	public String dataPath;
	private OutliesMinerFactory(){}
	public static OutliesMinerFactory getInstance(){
		if(outMinerFactoryInst==null){
			isMining=false;
			outMinerFactoryInst=new OutliesMinerFactory();
		}
		return outMinerFactoryInst;
	}
	
	public void detectOutlies(){
		if(isMining)
			return;
		isMining=true;
		File rootDirectory=new File(filePath);
		File dataDirectory=new File(dataPath);
		nodePairReader reader=new nodePairReader();
		if(rootDirectory.isFile()){
			parseFile(rootDirectory,dataDirectory,reader);
		}else{
			File[] rootDirs=rootDirectory.listFiles();
			File[] dataDirs=dataDirectory.listFiles();
			for(int i=0;i<rootDirs.length;i++){
				parseFile(rootDirs[i],dataDirs[i],reader);
			}
		}
	}
	
	private void parseFile(File protoclFile,File dataFile,nodePairReader reader){
		String[] ipAndProtocols=null;
		try {
			ipAndProtocols=getTaskElements(protoclFile);
		} catch (Exception e) {
			System.out.println("配置文件 "+protoclFile.getName()+" 读取错误");
		}
		int[] granularities={3600,3600*24,3600*24*7};
		for(int granularity:granularities){
			
			String ip=ipAndProtocols[0];
			//事先读取每一个IP上，每一个协议的DataItems
			HashMap<String, DataItems> rawDataItems=
					reader.readEachProtocolDataItems(dataFile.getAbsolutePath());
			for(int i=1;i<ipAndProtocols.length;i++){
				TaskElement task=new TaskElement();
				String name=ip+"_"+ipAndProtocols[i]+"_"+"周期挖掘";
				task.setTaskName(name);
				task.setComments("挖掘  "+"ip 为"+ip+" 上，协议"+ipAndProtocols[i]+"的周期规律");
				task.setDataSource("File");
				task.setGranularity(granularity);
				task.setAggregateMethod(AggregateMethod.Aggregate_MEAN);
				task.setSourcePath(dataFile.getPath());
				task.setTaskRange(TaskRange.SingleNodeRange);
				task.setRange(ip);
				task.setDiscreteMethod(DiscreteMethod.None);
				task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
				task.setMiningMethod(MiningMethod.MiningMethods_TsAnalysis);
				task.setMiningObject(ipAndProtocols[i]);
				TaskElement.add1Task(task, false);
				NetworkMinerFactory minerFactory=NetworkMinerFactory.getInstance();
				INetworkMiner miner=minerFactory.allMiners.get(task);
				DataItems dataItems=rawDataItems.get(ipAndProtocols[i]);
				miner.getResults().setInputData(dataItems);
			}
		}
	}
	
	private String[] getTaskElements(File file)throws Exception{
		List<String> ipAndProtocols=new ArrayList<String>();
		FileReader fileReader=new FileReader(file);
		BufferedReader bufferedReader=new BufferedReader(fileReader);
		String line=null;
		while((line=bufferedReader.readLine())!=null){
			ipAndProtocols.add(line.trim());
		}
		return (String[]) ipAndProtocols.toArray();
	}
}
