package cn.InstFS.wkr.NetworkMining.TaskConfigure;

import java.util.EventListener;

import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;

public interface ITaskElementEventListener extends EventListener {
	static int TASK_ADD = 1;
	static int TASK_DEL = 2;
	static int TASK_MODIFY = 3;
	static int TASK_DISPLAY = 4;
	
	static int TASK_START = 5;
	static int TASK_STOP = 6;
	
	
	static int TASK_MODIFY_RestartMining = 1;	// 修改了MiningMethod
	static int TASK_MODIFY_ELSE = 2;	
	

	
	public int eventType = 0;
	
	public void onTaskAdded(TaskElement task);
	public void onTaskDeleted(TaskElement task);
	public void onTaskModified(TaskElement task, int modify_type);
	public void onTaskToDisplay(TaskElement task);
	
	public void onTaskAdded(TaskCombination task);
	public void onTaskAddedDis(TaskCombination task);
	public void onTaskDeleted(TaskCombination task);
	public void onTaskModified(TaskCombination task, int modify_type);
	public void onTaskToDisplay(TaskCombination task);
	
	public void onTaskMiningToStart(TaskElement task);
	public void onTaskMiningToStop(TaskElement task);
	
	public void onCreate();
	public void onClosing();
}
