package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ITaskDisplayer;
import cn.InstFS.wkr.NetworkMining.UIs.MainFrame;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerSM implements INetworkMiner {

	TaskElement task;
	MinerResults results;

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
		timerTask = new SMTimerTask(task, results);
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
		// TODO Auto-generated method stub

	}

}

class SMTimerTask extends TimerTask {

	private boolean lastTimeStoped = true;
	private MinerResults results;
	private TaskElement task;
	private static DataItems data = new DataItems();
	
	/**
	 * 本次处理数据的时间
	 */
	private Date lastRunTime;	

	SMTimerTask(TaskElement task, MinerResults results) {
		this.task = task;
		this.results = results;
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
		if (UtilsSimulation.instance.isPaused())
			return;
		lastTimeStoped = false;
		Date curTime1 = UtilsSimulation.instance.getCurTime();
		results.setDateProcess(curTime1);
		
		lastTimeStoped = true;
		if (MainFrame.topFrame == null || UtilsUI.autoChangeResultsPanel
				|| MainFrame.topFrame.getSelectedTask() == task
				|| MainFrame.topFrame.getSelectedTask() == null)
			TaskElement.display1Task(task, ITaskDisplayer.DISPLAY_RESULTS);
	}
}