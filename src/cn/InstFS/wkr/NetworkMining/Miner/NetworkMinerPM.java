/**
 * 周期模式检测的Miner
 * 采用定时器实现定时运行，大部分可以重新写
 */
package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
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
		if (isRunning){
			System.out.println(task.getTaskName() + " --> Still Running");
			return;
		}
//		if (UtilsSimulation.instance.isPaused())
//			return;
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		results.getRetPM().setParamsPM((ParamsPM) task.getMiningParams());
		
		ParamsPM paramsPM= (ParamsPM) task.getMiningParams();	// 获取PARAMSPM内容
		isRunning = true;
		
		// 读取数据
		DataItems dataItems = null;
		dataItems=reader.readInputByText();
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
					dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
					task.getDiscreteEndNodes());
		}
		results.setInputData(dataItems);

		int dimension = task.getDiscreteDimension();
		dimension = Math.max(task.getDiscreteDimension(), dataItems.getDiscretizedDimension());
		
		DiscretePM discretePM=new DiscretePM(task,dimension, paramsPM.getPeriodThreshold());
		discretePM.setDataItems(dataItems);
		discretePM.predictBySeqSimility();//计算周期值
		results.setInputData(discretePM.getDi());
		MinerResultsPM retPM = results.getRetPM();
		retPM.setHasPeriod(discretePM.getHasPeriod());
		retPM.setPeriod(discretePM.getPredictPeriod());
		retPM.setDistributePeriod(discretePM.getDistributeItems());
		retPM.setFeatureValue(discretePM.getMinEntropy());
		retPM.setFeatureValues(discretePM.getEntropies());
		retPM.setFirstPossiblePeriod(discretePM.getFirstPossiblePeriod());//找出第一个呈现周期性的周期
		
		isRunning = false;
		if (displayer != null)
			displayer.displayMinerResults(results);
	}	
}
