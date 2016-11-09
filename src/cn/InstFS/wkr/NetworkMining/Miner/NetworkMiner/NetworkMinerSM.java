package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.PatternMent;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.FrequentAlgorithm.SequencePatternsDontSplit;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerSM implements INetworkMiner {

	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;

	Timer timer;
	SMTimerTask timerTask;

	boolean isStarted;
	public IsOver isOver=new IsOver();
	
	IReader reader;

	public NetworkMinerSM(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
		results = new MinerResults(this);
	}
	

	@Override
	public boolean start() {
//		System.out.println("PanelShowResultsSM   timer开始");
		if (timer != null) {
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		timer = new Timer();
		timerTask = new SMTimerTask(task, results,displayer,reader,timer,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), 2000);
		isStarted = true;
		task.setRunning(true);
		// TaskElement.modify1Task(task);
		UtilsUI.appendOutput(task.getTaskName() + " -- 启动成功！");
		return true;
	}

	@Override
	public boolean stop() {
		if (timer != null)
			timer.cancel();
		timer = null;
		if (timerTask != null)
			timerTask.cancel();
		timerTask = null;

		isStarted = false;
		task.setRunning(false);
		// TaskElement.modify1Task(task);
		return true;
	}

	@Override
	public boolean isAlive() {
		return isStarted;
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
		this.displayer=displayer;
	}

}

class SMTimerTask extends TimerTask {

	private boolean lastTimeStoped = true;
	private MinerResults results;
	private TaskElement task;
	private IResultsDisplayer displayer;
	private IReader reader;
	private boolean isRunning = false;
	private Timer timer;
	private IsOver isOver;
	
	/**
	 * 本次处理数据的时间
	 */
	private Date lastRunTime;	

	SMTimerTask(TaskElement task, MinerResults results,IResultsDisplayer displayer,IReader reader,Timer timer,IsOver isOver) {
		this.task = task;
		this.results = results;
		this.displayer=displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
	}

	public void setLastTimeStoped(boolean lastTimeStoped) {
		this.lastTimeStoped = lastTimeStoped;
	}

	@Override
	public void run() {
		if (!lastTimeStoped) {
			System.out.println(task.getTaskName() + " --> Still Running");
			return;
		}
//		if (UtilsSimulation.instance.isPaused())
//			return;
		lastTimeStoped = false;
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		
		ParamsSM paramsSM=null;  //获取参数
		isRunning=true;
		DataItems dataItems=null;
		
		//当Miner Reuslts中存在数据时，则不再读取
		if(results.getInputData()==null||results.getInputData().getLength()==0){
			dataItems=reader.readInputByText();
			results.setInputData(dataItems);
		}else{
			dataItems=results.getInputData();
		}
		
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(), 
					task.getAggregateMethod(), !dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(), task.getDiscreteEndNodes());
		}
		//符号化后的序列
		DataItems clusterItems=null;
		PointSegment segment=new PointSegment(dataItems, paramsSM.getSMparam().getSplitLeastLen());
		List<PatternMent> segPatterns=segment.getPatterns();
		
		if(task.getPatternNum()==0){
			clusterItems=WavCluster.SelfCluster(segPatterns,dataItems,paramsSM.getSMparam().getClusterNum(),
					task.getTaskName());
		}else{
			clusterItems=WavCluster.SelfCluster(segPatterns,dataItems, task.getPatternNum()
					,task.getTaskName());
		}
		
		SequencePatternsDontSplit sequencePattern=new SequencePatternsDontSplit(paramsSM);
		sequencePattern.setDataItems(clusterItems);
		sequencePattern.setTask(task);
		Map<Integer, List<String>>frequentItem=sequencePattern.printClusterLabelTOLines(clusterItems, dataItems);
		results.getRetSM().setFrequentItem(frequentItem);
		sequencePattern.patternMining();
		List<ArrayList<String>> patterns=sequencePattern.getPatterns();
		if(sequencePattern.isHasFreItems()){
			results.getRetSM().setPatters(patterns);
			results.getRetSM().setHasFreItems(true);
		}else{
			results.getRetSM().setHasFreItems(false);
		}
		
		lastTimeStoped = true;
		isRunning=false;
		if (MainFrame.topFrame == null || UtilsUI.autoChangeResultsPanel
				|| MainFrame.topFrame.getSelectedTask() == task
				|| MainFrame.topFrame.getSelectedTask() == null)
		isOver.setIsover(true);
		timer.cancel();
	}
}