package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

/**
 * 2015/5/19
 * @author 艾长青
 *
 */
public class NetworkFactory {
	private static NetworkFactory inst;
	public static boolean isMining=false;
	public String dataPath="./tasks1/";

	
	NetworkFactory(){
		
	}
	public static NetworkFactory getInstance(){
		if(inst == null){
			isMining = false;
			inst = new NetworkFactory();
		}
		return inst;
	}
	public DataItems mineNetworkDiameterRule(){

		/**
		 * 网络直径挖掘
		 */
		TaskElement diameter_task = new TaskElement();
		diameter_task.setSourcePath(dataPath);
		diameter_task.setGranularity(3600);
		diameter_task.setMiningObject("网络直径");
		CWNetworkReader reader = new CWNetworkReader(diameter_task);
		System.out.println("网络直径");
		DataItems diameter_dataItems = reader.readInputByText();
		
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.getTasks().add(generateTask(3600,
				"网络直径", "周期预测", MiningMethod.MiningMethods_PeriodicityMining));
		taskCombination.getTasks().add(generateTask(3600,
				"网络直径", "异常监测", MiningMethod.MiningMethods_OutliesMining));
		taskCombination.getTasks().add(generateTask(3600,
				"网络直径", "统计", MiningMethod.MiningMethods_Statistics));
		taskCombination.getTasks().add(generateTask(3600,
				"网络直径", "频繁项挖掘", MiningMethod.MiningMethods_SequenceMining));
		taskCombination.setMiningObject(MiningObject.MiningObject_None.toString());
		taskCombination.setDataItems(diameter_dataItems);
		taskCombination.setProtocol("网络直径挖掘");
		taskCombination.setRange("");
		taskCombination.setName();
		TaskElement.add1Task(taskCombination, false);
		
		return diameter_dataItems;
		
	}
	public DataItems mineNetworkClusterRule(){

		/**
		 * 网络簇系数挖掘
		 */
		TaskElement cluster_task = new TaskElement();
		cluster_task.setSourcePath(dataPath);
		cluster_task.setGranularity(3600);
		cluster_task.setMiningObject("网络簇系数");
		CWNetworkReader reader = new CWNetworkReader(cluster_task);
		reader = new CWNetworkReader(cluster_task);
		System.out.println("网络簇系数");
		DataItems cluster_dataItems = reader.readInputByText();
		
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.getTasks().add(generateTask(3600,
				"网络簇系数", "周期预测", MiningMethod.MiningMethods_PeriodicityMining));
		taskCombination.getTasks().add(generateTask(3600,
				"网络簇系数", "异常监测", MiningMethod.MiningMethods_OutliesMining));
		taskCombination.getTasks().add(generateTask(3600,
				"网络簇系数", "统计", MiningMethod.MiningMethods_Statistics));
		taskCombination.getTasks().add(generateTask(3600,
				"网络簇系数", "频繁项挖掘", MiningMethod.MiningMethods_SequenceMining));
		taskCombination.setMiningObject(MiningObject.MiningObject_None.toString());
		taskCombination.setDataItems(cluster_dataItems);
		taskCombination.setProtocol("网络簇系数");
		taskCombination.setRange("");
		taskCombination.setName();
		TaskElement.add1Task(taskCombination, false);
		return cluster_dataItems;
	}
	
	public TaskElement generateTask(int granularity,String ip,
			String mingObj,MiningMethod method){
		TaskElement task = new TaskElement();
		task.setDataSource("File");
		task.setGranularity(granularity);
		task.setDiscreteMethod(DiscreteMethod.None);
		task.setMiningMethod(method);
		String name=null;
		
		switch (method) {
		case MiningMethods_OutliesMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
			name = ip+"_"+mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+ip+" 上的异常");
			break;
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			name = ip+"_"+mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+ip+ "的周期规律");
			break;
		case MiningMethods_SequenceMining:
			name = ip+"_"+mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+ip+" 上的频繁模式");
		case MiningMethods_Statistics:
			name = ip+"_"+mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+ip+" 上的统计");
		default:
			break;
		}
		task.setMiningObject(MiningObject.MiningObject_None.toString());
		task.setProtocol(mingObj);
		return task;
	}
}
