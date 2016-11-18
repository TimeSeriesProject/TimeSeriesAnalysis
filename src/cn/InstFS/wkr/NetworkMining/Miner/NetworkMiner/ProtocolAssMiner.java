package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AssociationAlgorithm.ProtocolAssRtree;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AssociationAlgorithm.ProtocolAssociation;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.AssociationAlgorithm.ProtocolAssociationLine;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.MinerFactorySettings;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.ProtocolAssMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Params.ParamsAPI;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgress;
import cn.InstFS.wkr.NetworkMining.Results.MiningResultsFile;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class ProtocolAssMiner implements INetworkMiner {
	private ScheduledExecutorService timer;
	ProtocolMinerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning;
	TaskCombination taskCombination;
	IsOver isOver;
	
	public ProtocolAssMiner(TaskCombination taskCombination) {
		this.taskCombination = taskCombination;
		results = new MinerResults(this);
		isOver=new IsOver();
	}
	
	@Override
	public boolean start() {
		MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
		MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(taskCombination.getMiningObject()));
		if(resultsFile.hasFile(settings, taskCombination)) { // 已有挖掘结果存储，则不重新启动miner
			isOver.setIsover(true);
			MinerProtocolResults resultNode = (MinerProtocolResults) resultsFile.file2Result();
			results.setRetProtocol(resultNode);

			TaskProgress taskProgress = TaskProgress.getInstance();
			taskProgress.increaseComplete();
			return false;
		}

		if (timer != null){
			UtilsUI.appendOutput(taskCombination.getName() + " -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(taskCombination.getName() + " -- Still running");
			return false;
		}
		isRunning = true;
		timer = Executors.newScheduledThreadPool(1);
		timerTask = new ProtocolMinerTask(taskCombination, results, displayer,isOver);
		ScheduledFuture<?> future=timer.schedule(timerTask, 10, TimeUnit.MILLISECONDS);
		try {
			future.get();
		} catch (Exception e) {
			isRunning = false;
			isOver.setIsover(false);

			/* 记录发生异常的taskCombination至任务进度 */
			TaskProgress taskProgress = TaskProgress.getInstance();
			taskProgress.increaseComplete();
			taskProgress.addErrTaskCombination(taskCombination);
			timer.shutdownNow();
		}
		UtilsUI.appendOutput(taskCombination.getName() + " -- started");
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
		for(TaskElement task:taskCombination.getTasks())
			task.setRunning(isRunning);
		UtilsUI.appendOutput(taskCombination.getName() + " -- stopped");
		return true;
	}

	@Override
	public boolean isAlive() {
		return isRunning;
	}
	@Override
	public boolean isOver() {
		return isOver.isIsover();
	}
	@Override
	public TaskElement getTask() {
		return taskCombination.getTasks().get(0);
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

class ProtocolMinerTask extends TimerTask{
	TaskCombination taskCombination;
	MinerResults results;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	private IsOver isOver;
	HashMap<String, HashMap<String, DataItems>>eachProtocolItems;
	public ProtocolMinerTask(TaskCombination taskCombination, MinerResults results, IResultsDisplayer displayer
			,IsOver isOver) {
		this.taskCombination = taskCombination;
		this.results = results;
		this.displayer = displayer;
		this.isOver=isOver;
		this.eachProtocolItems=taskCombination.getEachIpProtocolItems();
		
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	@Override
	
	public void run() {
		if (isRunning){
			System.out.println(taskCombination.getName() + " --> Still Running");
			return;
		}
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		isRunning = true;
		
		//当Miner Reuslts中存在数据时，则不再读取
		Map<String,List<ProtocolAssociationResult>> protocolResult = null;
		MinerProtocolResults protocolAssResult = new MinerProtocolResults();
		List<TaskElement> tasks=taskCombination.getTasks();
		for(TaskElement task:tasks){
			switch (task.getMiningMethod()) {
			case MiningMethods_FrequenceItemMining:
				
				ProtocolAssociationLine pal = new ProtocolAssociationLine(eachProtocolItems,
						ParamsAPI.getInstance().getAssociationRuleParams().getAssociationRuleLineParams());
				protocolAssResult.setRetFP(pal.miningAssociation());
				results.setRetProtocol(protocolAssResult);
				break;
			case MiningMethods_SimilarityMining:
				if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_SimilarityProtocolASS)){
					ProtocolAssociation pa=new ProtocolAssociation(eachProtocolItems, 
							ParamsAPI.getInstance().getAssociationRuleParams().getAssociationRuleSimilarityParams()
							);
					protocolAssResult.setRetSim(pa.miningAssociation());
				}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_RtreeProtocolASS)){
					ProtocolAssRtree rTreePa=new ProtocolAssRtree(eachProtocolItems); //没有参数,不需要调用带参的构造函数
					protocolAssResult.setRetSim(rTreePa.miningAssociation());
				}
				results.setRetProtocol(protocolAssResult);
				break;
			default:
				break;
			}
		}
		
		isRunning = false;
		isOver.setIsover(true);
		
		if (displayer != null)
			displayer.displayMinerResults(results);
		/* 挖掘完成，保存结果文件 */
		MinerFactorySettings settings = ProtocolAssMinerFactory.getInstance();
		MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(taskCombination.getMiningObject()));
		newResultsFile.result2File(settings, taskCombination, results.getRetProtocol());

		TaskProgress taskProgress = TaskProgress.getInstance();
		taskProgress.increaseComplete();
	}
}
