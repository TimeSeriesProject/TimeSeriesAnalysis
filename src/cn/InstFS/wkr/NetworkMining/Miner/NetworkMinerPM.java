/**
 * 周期模式检测的Miner
 * 采用定时器实现定时运行，大部分可以重新写
 */
package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.test1;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerPM implements INetworkMiner {
	Timer timer;
	PMTimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning;
	TaskElement task;
	
	IReader reader;
	
	public NetworkMinerPM(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
	}
	
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsPM   timer starting");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(task.getTaskName() + " -- Still running");
			return false;
		}
		timer = new Timer();
		results = new MinerResults(this);
		
		timerTask = new PMTimerTask(task, results, displayer,reader);
		timer.scheduleAtFixedRate(timerTask, new Date(), UtilsSimulation.instance.getForcastWindowSizeInSeconds() * 1000);
		isRunning = true;
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- started");
		return true;
	}

	@Override
	public boolean stop() {
		if (timer != null)
			timer.cancel();
		timer = null;
		if (timerTask != null && !timerTask.isRunning()){
			timerTask.cancel();
			timerTask = null;
		}
		
		isRunning = false;
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- stopped");
		return true;
	}

	@Override
	public boolean isAlive() {
		return isRunning;
	}


	@Override
	public TaskElement getTask() {
		return task;
	}
	@Override
	public MinerResults getResults() {
		return results;
	}
	@Override
	public void setResultsDisplayer(IResultsDisplayer displayer) {
		this.displayer = displayer;		
	}

}
class PMTimerTask extends TimerTask{
	TaskElement task;
	MinerResults results;
	
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	IReader reader;
	public PMTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,IReader reader) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	@Override
	
	public void run() {
//		if (isRunning){
//			System.out.println(task.getTaskName() + " --> Still Running");
//			return;
//		}
//		if (UtilsSimulation.instance.isPaused())
//			return;
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		results.getRetPM().setParamsPM((ParamsPM) task.getMiningParams());
		
		ParamsPM paramsPM= (ParamsPM) task.getMiningParams();	// 获取PARAMSPM内容
		isRunning = true;
		// 读取数据
		DataItems dataItems = null;
		dataItems=reader.readInputByText();
		results.setInputData(dataItems);
		Map<String, MinerResultsPM> retPmMap=new HashMap<String, MinerResultsPM>();
		if(dataItems.getData().size()==0&&dataItems.getNonNumData().size()!=0){
			for(String var:dataItems.varSet){
				DataItems varItems=new DataItems();
				varItems.setTime(dataItems.getTime());
				List<String> data=new ArrayList<String>();
				for(int i=0;i<dataItems.getLength();i++){
					if(dataItems.NonNumData.get(i).containsKey(var)){
						data.add("1");
					}else{
						data.add("0");
					}
				}
				varItems.setData(data);
				PMDetect(varItems,retPmMap,var);
			}
		}else{
			PMDetect(dataItems,retPmMap,task.getMiningObject());
		}
	}
	
	private void PMDetect(DataItems dataItems,Map<String, MinerResultsPM> retPmMap,String MiningItem){
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
					!dataItems.isAllDataIsDouble());
		}
		
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
					task.getDiscreteEndNodes());
		}
		for(int i=0;i<dataItems.getLength();i++){
			System.out.print(dataItems.getData().get(i).split("\\.")[0]+",");
		}
		int dimension = task.getDiscreteDimension();
		dimension = Math.max(task.getDiscreteDimension(), dataItems.getDiscretizedDimension());
		IMinerPM pmMethod=null;
		if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_averageEntropyPM)){
			pmMethod=new averageEntropyPM(task, dimension);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ERPDistencePM)){
			pmMethod=new ERPDistencePM();
		}else{
			throw new RuntimeException("方法不存在！");
		}
		
		pmMethod.setDataItems(dataItems);
		pmMethod.predictPeriod();
		
		if(dataItems.isAllDataIsDouble()){
			MinerResultsPM retPM = results.getRetPM();
			retPM.setHasPeriod(pmMethod.hasPeriod());
			retPM.setPeriod(pmMethod.getPredictPeriod());
			retPM.setDistributePeriod(pmMethod.getItemsInPeriod());
			retPM.setFeatureValue(pmMethod.getMinEntropy());
			retPM.setFeatureValues(pmMethod.getEntropies());
			retPM.setFirstPossiblePeriod(pmMethod.getFirstPossiblePeriod());//找出第一个呈现周期性的周期
			System.out.println("是否存在周期："+pmMethod.hasPeriod());
			if(pmMethod.hasPeriod()){
				System.out.println("周期值 "+pmMethod.getPredictPeriod());
			}
			retPmMap.put(MiningItem, retPM);
		}else{
			Set<String>itemSet=dataItems.getVarSet();
			for(String item:itemSet){
				if(pmMethod.getHasPeriodOfNonNumDataItms().get(item)==null){
					continue;
				}
				System.out.println(item);
				System.out.println("是否存在周期："+pmMethod.getHasPeriodOfNonNumDataItms().get(item));

				if(pmMethod.getHasPeriodOfNonNumDataItms().get(item)){
					System.out.println("周期值 "+pmMethod.getPredictPeriodOfNonNumDataItems().get(item));
				}
				MinerResultsPM retPM=new MinerResultsPM();
				retPM.setHasPeriod(pmMethod.getHasPeriodOfNonNumDataItms().get(item));
				retPM.setPeriod(pmMethod.getPredictPeriodOfNonNumDataItems().get(item));
				retPM.setDistributePeriod(pmMethod.getItemsInperiodMapOfNonNumDataitems().get(item));
				retPmMap.put(item, retPM);
			}
		}
		
		isRunning = false;
		if (displayer != null)
			displayer.displayMinerResults(results);
	}
}
