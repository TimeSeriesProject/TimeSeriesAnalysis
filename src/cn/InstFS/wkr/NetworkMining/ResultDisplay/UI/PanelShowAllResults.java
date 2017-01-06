package cn.InstFS.wkr.NetworkMining.ResultDisplay.UI;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;



import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.UI.ITaskDisplayer;

/**
 * 先创建PanelShowResults
 * 然后调用TaskElement.loadAllTasks，来更新TaskElement.allTasks
 * 最后调用PanelShowresults.startAllTasks来启动挖掘任务
 * @author wangshen
 *
 */
public class PanelShowAllResults extends JPanel implements ITaskDisplayer, ITaskElementEventListener{

	Map<TaskElement,IPanelShowResults> allPanels;
	/**
	 * Create the panel.
	 */
	public PanelShowAllResults() {
		setLayout(new CardLayout());
		onCreate();
	}

	public void displayTask(TaskElement task) {
		if (task == null || allPanels.get(task) == null)
			return;		
		CardLayout card = (CardLayout) getLayout();
		card.show(this, task.getTaskName());
		updateUI();
		allPanels.get(task).displayMinerResults();		
	}


	@Override
	public void onTaskAdded(TaskElement task) {
		if (allPanels == null)
			allPanels = new HashMap<TaskElement, IPanelShowResults>();
		if (allPanels.containsKey(task))
			return;

		MiningMethod miningMethodName = task.getMiningMethod();
		String taskName = task.getTaskName();
		IPanelShowResults panel = null;
		if (taskName.contains("路径")){
			panel = new PanelShowResultsPP(task);
		}else if (miningMethodName.equals(MiningMethod.MiningMethods_OutliesMining)){
			panel = new PanelShowResultsOM(task);
		}else if (miningMethodName.equals(MiningMethod.MiningMethods_PredictionMining)) {
			panel=new PanelShowResultsFM(task);
		} else if (miningMethodName.equals(MiningMethod.MiningMethods_SequenceMining)) {
			panel = new PanelShowResultsSM(task);
		} else if (miningMethodName.equals(MiningMethod.MiningMethods_PeriodicityMining))
			panel = new PanelShowResultsPM(task);
		else if (miningMethodName.equals(MiningMethod.MiningMethods_FrequenceItemMining))
			panel = new PanelShowResultsFP(task);
		else if (miningMethodName.equals(MiningMethod.MiningMethods_PathProbilityMining))
			panel = new PanelShowResultsPP(task);
		else if (miningMethodName.equals(MiningMethod.MiningMethods_Statistics))
			panel = new PanelShowResultsStatistics(task);
		else if(miningMethodName.equals(MiningMethod.MiningMethods_PartialCycle))
			panel = new PanelShowResultsPartialCycle(task);
		else if (miningMethodName.equals(MiningMethod.MiningMethods_PartialPeriod))
			panel = new PanelShowResultsPartialPeriod(task);
		if (panel != null){
			add((JPanel)panel, task.getTaskName());
			allPanels.put(task, panel);
		}
		return ;
	}

	@Override
	public void onTaskDeleted(TaskElement task) {
		if (allPanels.containsKey(task)){
			IPanelShowResults p = allPanels.get(task);
			remove((JPanel)p);
			allPanels.remove(task);
		}
	}

	@Override
	public void onTaskModified(TaskElement task, int modify_type) {
		if (modify_type == ITaskElementEventListener.TASK_MODIFY_RestartMining){
			remove((JPanel)allPanels.get(task));
			allPanels.remove(task);
			onTaskAdded(task);
		}
	}

	@Override
	public void onTaskToDisplay(TaskElement task) {
		displayTask(task);
	}
	@Override
	public void onCreate() {
		TaskElement.addTaskListener(this);
	}

	@Override
	public void onClosing() {	
		TaskElement.removeTaskListener(this);
	}

	@Override
	public void onTaskMiningToStart(TaskElement task) {
	}

	@Override
	public void onTaskMiningToStop(TaskElement task) {
		
	}

	@Override
	public int getDisplayType() {
		return ITaskDisplayer.DISPLAY_RESULTS;
	}
	
	@Override
	public void onTaskAdded(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskDeleted(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskModified(TaskCombination task, int modify_type) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTaskToDisplay(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskAddedDis(TaskCombination task) {
		// TODO Auto-generated method stub
		
	}

}
