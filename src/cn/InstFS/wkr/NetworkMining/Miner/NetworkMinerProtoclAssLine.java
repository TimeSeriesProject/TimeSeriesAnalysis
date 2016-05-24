package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Date;
import java.util.Timer;

import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerProtoclAssLine implements INetworkMiner{
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	IsOver isOver=new IsOver();
	Timer timer;
	FPTimerTask timerTask;
	
	boolean isStarted;
	private IReader reader;
	
	public NetworkMinerProtoclAssLine(TaskElement task,IReader reader) {
		this.task = task; 
		this.reader=reader;
		results = new MinerResults(this);
	}
	@Override
	public boolean start() {
		System.out.println("NetworkMinerProtoclAssLine   timer开始");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		timer = new Timer();
		isStarted = true;
		task.setRunning(true);
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
	}
}
