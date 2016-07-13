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
public class NetworkFactory extends MinerFactorySettings {
	private static NetworkFactory inst;
	public static boolean isMining=false;
	public String dataPath="C:\\data\\out\\route";

	private TaskRange taskRange= TaskRange.WholeNetworkRange;
	private MiningObject miningObject ;
	
	public MiningObject getMiningObject() {
		return miningObject;
	}
	public void setMiningObject(MiningObject miningObject) {
		this.miningObject = miningObject;
	}
	NetworkFactory(){
		super();
		List<MiningObject> miningObjectList = this.getMiningObjectList();
		miningObjectList.add(MiningObject.MiningObject_Cluster);
		miningObjectList.add(MiningObject.MiningObject_Diameter);

		List<MiningObject> miningObjectCheck = this.getMiningObjectsChecked();
		miningObjectCheck.add(MiningObject.MiningObject_Cluster);
		miningObjectCheck.add(MiningObject.MiningObject_Diameter);
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
		int granularity = Integer.parseInt(this.getGranularity());
		task.setGranularity(granularity);
		task.setMiningObject(miningObject.toString());
		CWNetworkReader reader = new CWNetworkReader(task);
		DataItems dataItems = reader.readInputByText();
		
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.setTaskRange(TaskRange.WholeNetworkRange);
		taskCombination.setMiningObject(miningObject.toString());
		taskCombination.setMinerType(MinerType.MiningTypes_WholeNetwork);
		taskCombination.setRange(dataPath + miningObject.toString());
		taskCombination.setName();
		taskCombination.setDataItems(dataItems);
		taskCombination.getTasks().add(generateTask(granularity,
				miningObject.toString(),  MiningMethod.MiningMethods_PeriodicityMining));
		taskCombination.getTasks().add(generateTask(granularity,
				miningObject.toString(),  MiningMethod.MiningMethods_OutliesMining));
		taskCombination.getTasks().add(generateTask(granularity,
				miningObject.toString(),  MiningMethod.MiningMethods_Statistics));
		taskCombination.getTasks().add(generateTask(granularity,
				miningObject.toString(), MiningMethod.MiningMethods_SequenceMining));
		
		TaskElement.add1Task(taskCombination, false);
	}
	
	public TaskElement generateTask(int granularity,
			String mingObj,MiningMethod method){
		TaskElement task = new TaskElement();
		task.setDataSource("File");
		task.setGranularity(granularity);
		task.setDiscreteMethod(DiscreteMethod.None);
		task.setMiningMethod(method);
		String name=mingObj+method.toString();
		
		switch (method) {
		case MiningMethods_OutliesMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_TEOTSA);
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的异常");
			break;
		case MiningMethods_PeriodicityMining:
			task.setMiningAlgo(MiningAlgo.MiningAlgo_ERPDistencePM);
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+ "的周期规律");
			break;
		case MiningMethods_SequenceMining:
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的频繁模式");
		case MiningMethods_Statistics:
			task.setTaskName(name);
			task.setComments("挖掘  "+mingObj+" 的统计量");
		default:
			break;
		}
		task.setMiningObject(mingObj);
		task.setProtocol("");
		return task;
	}

	@Override
	public TaskRange getTaskRange() {
		return taskRange;
	}

	@Override
	public void setTaskRange(TaskRange taskRange) {
		this.taskRange = taskRange;
	}

	@Override
	public String getDataPath() {
		return dataPath;
	}

	@Override
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}


}
