package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;










import associationRules.AssociationRuleMain;
import associationRules.Rule;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ITaskDisplayer;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerFP implements INetworkMiner {

	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private IsOver isOver=new IsOver();
	Timer timer;
	FPTimerTask timerTask;
	
	boolean isStarted;
	private IReader reader;
	
	public NetworkMinerFP(TaskElement task,IReader reader) {
		this.task = task; 
		this.reader=reader;
		results = new MinerResults(this);
	}
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsSM   timer开始");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		timer = new Timer();
		timerTask = new FPTimerTask(task, results,displayer,reader,timer,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), 2000);
		isStarted = true;
		task.setRunning(true);
//		TaskElement.modify1Task(task);		
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
//		TaskElement.modify1Task(task);
		return true;
	}
	
	@Override
	public boolean isOver() {
		return isOver.isIsover();
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
		// TODO Auto-generated method stub
		
	}

}
class FPTimerTask extends TimerTask{
	private String strForcasts;
	private String strRules;
	private static boolean lastTimeStoped = true;
	private MinerResults results;
	private TaskElement task;
	private IResultsDisplayer displayer;
	private IReader reader;
	private boolean isRunning = false;
	private IsOver isOver;
	private Timer timer;

	FPTimerTask(TaskElement task, MinerResults results,IResultsDisplayer displayer,IReader reader,Timer timer,IsOver isOver){
		this.task = task;
		this.results = results;
		this.displayer=displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
	}
	public static void setLastTimeStoped(boolean lastTimeStoped) {
		FPTimerTask.lastTimeStoped = lastTimeStoped;
	}
	@Override
	public void run() {
		if (!lastTimeStoped){
			System.out.println(task.getTaskName() + " --> Still Running");
			return;
		}
		lastTimeStoped = false;
		isRunning=true;
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		String[] filePath=task.getSourcePath().split(",");
		((nodePairReader)reader).setFilePath(filePath[0]);
		DataItems dataItems1=reader.readInputByText();
		((nodePairReader)reader).setFilePath(filePath[1]);
		DataItems dataItems2=reader.readInputByText();
		results.getRetFP().getOriginItems().add(dataItems1);
		results.getRetFP().getOriginItems().add(dataItems2);
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			dataItems1=DataPretreatment.aggregateData(dataItems1, task.getGranularity(), 
					task.getAggregateMethod(), !dataItems1.isAllDataIsDouble());
			dataItems2=DataPretreatment.aggregateData(dataItems2, task.getGranularity(), 
					task.getAggregateMethod(), !dataItems2.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems1=DataPretreatment.toDiscreteNumbers(dataItems1, task.getDiscreteMethod(), task.getDiscreteDimension(), task.getDiscreteEndNodes());
			dataItems2=DataPretreatment.toDiscreteNumbers(dataItems2, task.getDiscreteMethod(), task.getDiscreteDimension(), task.getDiscreteEndNodes());
		}
		AssociationRuleMain associationRule=new AssociationRuleMain();
		associationRule.miningRules(dataItems1, dataItems2);
		System.out.println(associationRule.rules);
		StringBuilder sb=new StringBuilder();
		for(Rule rule:associationRule.rules){
			sb.append(rule.source);
			Set<Integer>set=rule.times;
			List<String> items=new ArrayList<String>();
			for(int index:set){
				sb.append(":").append(index).append(",").append(index+1);
				items.add(index+","+(index+1));
			}
			results.getRetFP().getAssociateRules().put(rule.source, items);
			System.out.println(sb.toString());
			sb.delete(0, sb.length());
		}
		for(Rule rule:associationRule.rules){
			sb.append(rule.target);
			Set<Integer>set=rule.times;
			for(int index:set){
				sb.append(":").append(index).append(",").append(index+1);
			}
			System.out.println(sb.toString());
			sb.delete(0, sb.length());
		}
		synchronized (results) {
			//TODO
		}	
		lastTimeStoped = true;		
		if (UtilsUI.autoChangeResultsPanel ||
				MainFrame.topFrame.getSelectedTask() == task||
				MainFrame.topFrame.getSelectedTask() == null)
			TaskElement.display1Task(task, ITaskDisplayer.DISPLAY_RESULTS);
		isOver.setIsover(true);
		timer.cancel();
	}		
}