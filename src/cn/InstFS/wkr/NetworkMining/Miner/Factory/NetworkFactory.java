package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import java.util.List;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;

/**
 * 2015/5/19
 * @author 艾长青
 *
 */
public class NetworkFactory extends MinerFactorySettings {
	private static NetworkFactory inst;
	public static boolean isMining=false;
	public String dataPath;

	private TaskRange taskRange= TaskRange.WholeNetworkRange;
	private MiningObject miningObject ;
	/*private Date startDate;
	private Date endDate;
	
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}*/
	public MiningObject getMiningObject() {
		return miningObject;
	}
	public void setMiningObject(MiningObject miningObject) {
		this.miningObject = miningObject;
	}
	NetworkFactory(){
		super(MinerType.MiningTypes_WholeNetwork.toString());
		dataPath = GlobalConfig.getInstance().getDataPath() + "\\route";
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
		DataItems dataItems = reader.readInputByText(false,getStartDate(),getEndDate());

		/*用于测试*/
		/*Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.set(2015, 10, 2, 0, 0, 0);
		cal2.set(2015,11,21,0,0,0);
		Date date1 = cal1.getTime();
		Date date2 = cal2.getTime();
		task.setIsReadBetween(true);
		task.setDateStart(date1);
		task.setDateEnd(date2);*/
//		DataItems dataItems = filtDataItems(alldataItems, task.getDateStart(), task.getDateEnd());
		
		TaskCombination taskCombination=new TaskCombination();
		taskCombination.setTaskRange(TaskRange.WholeNetworkRange);
		taskCombination.setMiningObject(miningObject.toString());
		taskCombination.setMinerType(MinerType.MiningTypes_WholeNetwork);
		taskCombination.setRange(miningObject.toString());
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

	/***
	 * @author LYH
	 * 功能：用于读取时间段内的DataItems*/
	public DataItems filtDataItems(DataItems allDataItems,Date date1,Date date2){
		DataItems dataItems = new DataItems();
		for(int i=0;i<allDataItems.getTime().size();i++){
			Date date = allDataItems.getElementAt(i).getTime();
			if(date.compareTo(date1)>0 && date.compareTo(date2)<0){
				DataItem dataItem = allDataItems.getElementAt(i);
				dataItems.add1Data(dataItem);
			}
		}
		return dataItems;
	}

}
