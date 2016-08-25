package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.SeriesStatisticsAlogorithm.SeriesStatistics;
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
	private ScheduledExecutorService timer;
	private StatisticsTimerTask timerTask;
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
		isRunning = true;
		timer =Executors.newScheduledThreadPool(1);
		timerTask = new StatisticsTimerTask(task, results, displayer,reader,isOver);
		
		ScheduledFuture<?> future=timer.schedule(timerTask, 10, TimeUnit.MILLISECONDS);
		try{
			future.get();
		}catch(Exception e){
			isRunning=false;
			isOver.setIsover(false);
			timer.shutdownNow();
		}
		task.setRunning(isRunning);	
		return true;
	}

	@Override
	public boolean stop() {
		timer = null;
		if (timerTask != null && !timerTask.isRunning()){
			timerTask.cancel();
			timerTask = null;
		}
		
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
	
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	private IsOver isOver;
	IReader reader;
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,IsOver isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.isOver=isOver;
	}
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,Timer timer,IsOver isOver,int splitM,double ratio){
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.isOver=isOver;
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

		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		isRunning = true;
		
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
	}
	
	
}
