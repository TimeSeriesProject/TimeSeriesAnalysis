package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.SeriesStatistics;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;


/**
 * 时间序列统计
 * @author wsc
 *
 */
public class NetworkMinerStatistics implements INetworkMiner {
	Timer timer;
	StatisticsTimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning=false;
	public IsOver isOver=new IsOver();
	TaskElement task;
	IReader reader;
	
	public NetworkMinerStatistics(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
		results = new MinerResults(this);
	}
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsTSA   timer开始");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(task.getTaskName() + " -- 上次挖掘尚未结束！");
			return false;
		}
		timer = new Timer();
		timerTask = new StatisticsTimerTask(task, results, displayer,reader,timer,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), 2000);
		isRunning = true;
		task.setRunning(isRunning);
//		TaskElement.modify1Task(task);		
		UtilsUI.appendOutput(task.getTaskName() + " -- 开始挖掘！");
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
//		SMTimerTask.setLastTimeStoped(true);
		
		isRunning = false;
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- 停止挖掘！");
		return true;
	}

	@Override
	public boolean isAlive() {
		return false;
	}
	
	@Override
	public boolean isOver() {
		return isOver.isIsover();
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
class StatisticsTimerTask extends TimerTask  {
	private int splitM;
	private double ratio;
	private double std;
	
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private Timer timer;
	private boolean isRunning = false;
	private IsOver isOver;
	IReader reader;
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,Timer timer,IsOver isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
		this.splitM=2;
		this.ratio=0.2;
	}
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,Timer timer,IsOver isOver,int splitM,double ratio){
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
		this.splitM=splitM;
		this.ratio=ratio;
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
		//results.getRetOM().setParamsTSA((ParamsTSA) task.getMiningParams());
		isRunning = true;
		//ParamsTSA params = (ParamsTSA) task.getMiningParams();
		
		// 读取数据 当Miner Reuslts中存在数据时，则不再读取
		DataItems dataItems = null;
		if(results.getInputData()==null||results.getInputData().getLength()==0){
			dataItems=reader.readInputByText();
			results.setInputData(dataItems);
		}else{
			dataItems=results.getInputData();
		}
		
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
					dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
					task.getDiscreteEndNodes());
		}
		
		SeriesStatistics statistics=new SeriesStatistics(dataItems);
		statistics.statistics();
		results.getRetStatistics().setComplex(statistics.getComplex());
		results.getRetStatistics().setMean(statistics.getMean());
		results.getRetStatistics().setSampleENtropy(statistics.getSampleEntropy());
		results.getRetStatistics().setSpan(statistics.getTimeSpan());
		results.getRetStatistics().setStd(statistics.getStd());
		isRunning = false;
		isOver.setIsover(true);
		
		if (displayer != null)
			displayer.displayMinerResults(results);
		timer.cancel();
	}
	
	
}
