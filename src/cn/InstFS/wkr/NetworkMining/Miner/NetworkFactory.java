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
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
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
	private TaskRange taskRange= TaskRange.WholeNetworkRange;
	private String miningObject ;
	
	public String getMiningObject() {
		return miningObject;
	}
	public void setMiningObject(String miningObject) {
		this.miningObject = miningObject;
	}
	NetworkFactory(){
		
	}
	public static NetworkFactory getInstance(){
		if(inst == null){
			isMining = false;
			inst = new NetworkFactory();
		}
		return inst;
	}
	public void reset(){
		isMining=false;
	}
	public void detect()
	{
		if(isMining)
			return;
		isMining=true;
		TaskElement task = new TaskElement();
		task.setSourcePath(dataPath);
		task.setGranularity(3600);
		task.setMiningObject(miningObject);
		CWNetworkReader reader = new CWNetworkReader(task);
		DataItems dataItems = reader.readInputByText();
		
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.setTaskRange(TaskRange.WholeNetworkRange);
		taskCombination.setMiningObject(miningObject);
		taskCombination.setMinerType(MinerType.MiningTypes_WholeNetwork);
		taskCombination.setRange("");
		taskCombination.setName();
		taskCombination.setDataItems(dataItems);
		taskCombination.getTasks().add(generateTask(3600,
				miningObject,  MiningMethod.MiningMethods_PeriodicityMining));
		taskCombination.getTasks().add(generateTask(3600,
				miningObject,  MiningMethod.MiningMethods_OutliesMining));
		taskCombination.getTasks().add(generateTask(3600,
				miningObject,  MiningMethod.MiningMethods_Statistics));
		taskCombination.getTasks().add(generateTask(3600,
				miningObject, MiningMethod.MiningMethods_SequenceMining));
		taskCombination.setMiningObject(MiningObject.MiningObject_None.toString());
		
		TaskElement.add1Task(taskCombination, false);
	}
	
	public TaskElement generateTask(int granularity,
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
			name = mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的异常");
			break;
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			name = mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+ "的周期规律");
			break;
		case MiningMethods_SequenceMining:
			name = mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的频繁模式");
		case MiningMethods_Statistics:
			name = mingObj;
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的统计量");
		default:
			break;
		}
		task.setMiningObject(MiningObject.MiningObject_None.toString());
		task.setProtocol(mingObj);
		return task;
	}
}
