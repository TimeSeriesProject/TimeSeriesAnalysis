package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import associationRules.ProtocolAssociationResult;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ProtocolAssRtree;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ProtocolAssociation;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ProtocolAssociationLine;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class ProtocolAssMiner implements INetworkMiner {
	Timer timer;
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
		if (timer != null){
			UtilsUI.appendOutput(taskCombination.getName() + " -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(taskCombination.getName() + " -- Still running");
			return false;
		}
		
		timer = new Timer();
		timerTask = new ProtocolMinerTask(taskCombination, results, displayer,timer,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), UtilsSimulation.instance.getForcastWindowSizeInSeconds() * 1000);
		isRunning = true;
		UtilsUI.appendOutput(taskCombination.getName() + " -- started");
		return true;
	}

	@Override
	public boolean stop() {
		if (timer != null)
			timer.cancel();
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
	Timer timer;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	private IsOver isOver;
	HashMap<String, HashMap<String, DataItems>>eachProtocolItems;
	public ProtocolMinerTask(TaskCombination taskCombination, MinerResults results, IResultsDisplayer displayer
			,Timer timer,IsOver isOver) {
		this.taskCombination = taskCombination;
		this.results = results;
		this.displayer = displayer;
		this.timer=timer;
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
				ProtocolAssociationLine pal = new ProtocolAssociationLine(eachProtocolItems,null);
				protocolAssResult.setRetFP(pal.miningAssociation());
				break;
			case MiningMethods_SimilarityMining:
				if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_SimilarityProtocolASS)){
					ProtocolAssociation pa=new ProtocolAssociation(eachProtocolItems, null);
					protocolAssResult.setRetSim(pa.miningAssociation());
				}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_RtreeProtocolASS)){
					ProtocolAssRtree rTreePa=new ProtocolAssRtree(eachProtocolItems); //没有参数
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
		timer.cancel();
	}
}
