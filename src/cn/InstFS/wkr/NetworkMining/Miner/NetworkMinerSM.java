package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.ResultItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ITaskDisplayer;
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
	
	IReader reader;

	public NetworkMinerSM(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
	}
	

	@Override
	public boolean start() {
		System.out.println("PanelShowResultsSM   timer开始");
		if (timer != null) {
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		timer = new Timer();
		results = new MinerResults(this);
		timerTask = new SMTimerTask(task, results,displayer,reader);
		timer.scheduleAtFixedRate(timerTask, 0, (int)(((ParamsSM)task.getMiningParams()).getStepWindow()) * 1000);
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
	
	/**
	 * 本次处理数据的时间
	 */
	private Date lastRunTime;	

	SMTimerTask(TaskElement task, MinerResults results,IResultsDisplayer displayer,IReader reader) {
		this.task = task;
		this.results = results;
		this.displayer=displayer;
		this.reader=reader;
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
		ParamsSM paramsSM=(ParamsSM)task.getMiningParams();
		isRunning=true;
		DataItems dataItems=reader.readInputByText();
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(), 
					task.getAggregateMethod(), !dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(), task.getDiscreteEndNodes());
		}
		//符号化后的序列
		DataItems clusterItems=WavCluster.SelfCluster(dataItems, 24, 2);
		results.setInputData(clusterItems);
		
		SequencePatterns sequencePattern=new SequencePatterns();
		sequencePattern.setDataItems(clusterItems);
		sequencePattern.setTask(task);
		sequencePattern.setWinSize(paramsSM.getSizeWindow());
		sequencePattern.setThreshold(paramsSM.getMinSupport());
		sequencePattern.setStepSize(paramsSM.getStepWindow());
		sequencePattern.printClusterLabelTOLines(clusterItems, dataItems);
		sequencePattern.patternMining();
		sequencePattern.displayResult();
		List<ArrayList<String>> patterns=sequencePattern.getPatterns();
		results.getRetSM().setPatters(patterns);
		lastTimeStoped = true;
		isRunning=false;
		displayer.displayMinerResults(results);
		if (MainFrame.topFrame == null || UtilsUI.autoChangeResultsPanel
				|| MainFrame.topFrame.getSelectedTask() == task
				|| MainFrame.topFrame.getSelectedTask() == null)
			TaskElement.display1Task(task, ITaskDisplayer.DISPLAY_RESULTS);
	}
}