package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth_with_strings.AlgoFPGrowth_Strings;
import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.Sequences;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.PanelShowResultsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ITaskDisplayer;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerFP implements INetworkMiner {

	TaskElement task;
	MinerResults results;
	
	Timer timer;
	FPTimerTask timerTask;
	
	boolean isStarted;
	
	public NetworkMinerFP(TaskElement task) {
		this.task = task; 
	}
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsSM   timer开始");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		timer = new Timer();
		results = new MinerResults(this);
		timerTask = new FPTimerTask(task, results);
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

	FPTimerTask(TaskElement task, MinerResults results){
		this.task = task;
		this.results = results;
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
		DataInputUtils di = new DataInputUtils(task);
		DataItems data = di.readInput();
		
		
		synchronized (results) {
//			results.getRetSM().setStrForcasts(strForcasts);
//			results.getRetSM().setStrRules(strRules);
		}	
		lastTimeStoped = true;		
		if (UtilsUI.autoChangeResultsPanel ||
				MainFrame.topFrame.getSelectedTask() == task||
				MainFrame.topFrame.getSelectedTask() == null)
			TaskElement.display1Task(task, ITaskDisplayer.DISPLAY_RESULTS);
			
	}		
}