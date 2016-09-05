package cn.InstFS.wkr.NetworkMining.Miner.Factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.CWNetworkReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.*;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerProtocolResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPath;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningObject;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;
//import com.sun.javafx.tk.Toolkit;

public class NetworkMinerFactory implements ITaskElementEventListener{
	private static NetworkMinerFactory inst;
	private NetworkMinerFactory() {
		onCreate();
	}
	public static NetworkMinerFactory getInstance(){
		if (inst != null)
			return inst;
		inst = new NetworkMinerFactory();
		return inst;
	}
	
	public Map<TaskElement, INetworkMiner>allMiners = new HashMap<TaskElement, INetworkMiner>();
	public Map<TaskCombination, INetworkMiner> allCombinationMiners=
			new HashMap<TaskCombination, INetworkMiner>();
	public INetworkMiner createMiner(TaskElement task){
		IReader reader=null;
		INetworkMiner miner = null;
		if (task == null)
			return null;
		System.out.println("allminers 1::::::" + allMiners.size());
		if (allMiners.containsKey(task))
			return allMiners.get(task);
		if(task.getTaskRange().equals(TaskRange.NodePairRange)){
			reader=new nodePairReader(task, task.getRange().split(","));
		}else if(task.getTaskRange().equals(TaskRange.SingleNodeRange)){
			reader=new nodePairReader(task, task.getRange().split(","));
		}else if(task.getTaskRange().equals(TaskRange.WholeNetworkRange)){
			reader=new CWNetworkReader(task);
		}
		if (task.getMiningMethod().equals(MiningMethod.MiningMethods_SequenceMining)){
			miner = new NetworkMinerSM(task,reader);
		}else if (task.getMiningMethod().equals(MiningMethod.MiningMethods_OutliesMining)){
			miner = new NetworkMinerOM(task,reader);
		}else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_PredictionMining)){
			miner = new NetworkMinerFM(task,reader);
		}else if (task.getMiningMethod().equals(MiningMethod.MiningMethods_PeriodicityMining)){
			miner = new NetworkMinerPM(task, reader);
		}else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_FrequenceItemMining)){
			miner=new NetworkMinerFP(task, reader);
		}else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_PathProbilityMining)){
			miner=new NetwokerMinerPathProb(task, reader);
		}else if(task.getMiningMethod().equals(MiningMethod.MiningMethods_Statistics)){
			miner=new NetworkMinerStatistics(task, reader);
		}else{
			
		}
		allMiners.put(task, miner);
		return miner;
	}
	
	public INetworkMiner createMiner(TaskCombination taskCombination){
		if (taskCombination == null)
			return null;
		if (allCombinationMiners.containsKey(taskCombination))
			return allCombinationMiners.get(allCombinationMiners);
		INetworkMiner miner=null;
		switch (taskCombination.getMinerType()) {
		case MiningType_SinglenodeOrNodePair:
			miner=new NetworkMinerNode(taskCombination);
			break;
		case MiningType_ProtocolAssociation:
			miner=new ProtocolAssMiner(taskCombination);
			break;
		case MiningType_Path:
			miner = new NetworkMinerPath(taskCombination);
			break;
		case MiningTypes_WholeNetwork:
			miner=new NetworkMinerNode(taskCombination);	
			break;
		default:
			break;
		}
		allCombinationMiners.put(taskCombination, miner);
		return miner;
	}

	public INetworkMiner createMinerDis(TaskCombination taskCombination){
		allCombinationMiners.clear();//客户端allCombinationMiners只有一个，需要先清空
		if (taskCombination == null)
			return null;
		if (allCombinationMiners.containsKey(taskCombination))
			return allCombinationMiners.get(allCombinationMiners);
		INetworkMiner miner=null;
		switch (taskCombination.getMinerType()) {
			case MiningType_SinglenodeOrNodePair:
				miner=new NetworkMinerNode(taskCombination);
				break;
			case MiningType_ProtocolAssociation:
				miner=new ProtocolAssMiner(taskCombination);
				break;
			case MiningType_Path:
				miner = new NetworkMinerPath(taskCombination);
				break;
			case MiningTypes_WholeNetwork:
				miner=new NetworkMinerNode(taskCombination);
				break;
			default:
				break;
		}
		allCombinationMiners.put(taskCombination, miner);
		return miner;
	}
	public void removeMiner(TaskElement task){
		if (allMiners.containsKey(task)){
			allMiners.get(task).stop();
			allMiners.remove(task);
		}
	}
	public void removeMiner(TaskCombination taskCombination){
		if (allCombinationMiners.containsKey(taskCombination)){
			allCombinationMiners.get(taskCombination).stop();
			allCombinationMiners.remove(taskCombination);
		}
	}
	public void removeMiner(INetworkMiner miner){
		if (allMiners.containsKey(miner.getTask())){
			allMiners.get(miner.getTask()).stop();
			allMiners.remove(miner.getTask());
		}
	}
	
	public void startAllMiners(MiningMethod method){
		Iterator<Entry<TaskElement,INetworkMiner>>iterator=allMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskElement, INetworkMiner> entry=iterator.next();
			if(!entry.getValue().isOver()){
				if(entry.getKey().getMiningMethod().name().equals(method.name())){
					entry.getValue().start();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		iterator=allMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskElement, INetworkMiner> entry=iterator.next();
			if(entry.getKey().getMiningMethod().name().equals(method.name())){
				if(!entry.getValue().isOver()){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		autoTaskFilter();
	}
	
	public void startAllMiners(){
		startAllTaskMiners();
		autoTaskFilter();
	}
	
	public void startAllTaskMiners(){
		for(INetworkMiner miner :allMiners.values()){
			if(miner.isOver())
				continue;    //已经挖掘完的任务不需再次挖掘
			miner.start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		boolean isAllOver=false;
		while (!isAllOver) {
			isAllOver=true;
			Iterator<Entry<TaskElement, INetworkMiner>>iterator=allMiners.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<TaskElement, INetworkMiner> entry=iterator.next();
				INetworkMiner miner=entry.getValue();
				if(!miner.isOver()){
					isAllOver=false;
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public HashMap<TaskCombination, MinerNodeResults> startAllNodeMiners(MiningObject miningObject){
		HashMap<TaskCombination, MinerNodeResults>resultsMap=
				new HashMap<TaskCombination,MinerNodeResults>();
		startMinerOneByOne(MinerType.MiningType_SinglenodeOrNodePair,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_SinglenodeOrNodePair, miningObject, resultsMap, null,null);
		
		Iterator<Entry<TaskCombination, INetworkMiner>>iterator=allCombinationMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, INetworkMiner> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_SinglenodeOrNodePair))
				continue;
			taskAdd2allMiner(taskCombination, resultsMap);
		}
		return resultsMap;
	}

	//分布式单节点
	public HashMap<TaskCombination, MinerNodeResults> startAllNodeMinersDis(MiningObject miningObject){
		HashMap<TaskCombination, MinerNodeResults>resultsMap=
				new HashMap<TaskCombination,MinerNodeResults>();
		startMinerOneByOne(MinerType.MiningType_SinglenodeOrNodePair,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_SinglenodeOrNodePair, miningObject, resultsMap, null,null);

		return resultsMap;
	}

	public void showNodeMinersDis(MiningObject miningObject, HashMap<TaskCombination, MinerNodeResults> resultsMap) {
//		allMiners.clear();
		Iterator<Entry<TaskCombination, MinerNodeResults>>iterator=resultsMap.entrySet().iterator();
//		System.out.println("allCombinationMiners ::::" + resultsMap.size());
		while(iterator.hasNext()){
			Entry<TaskCombination, MinerNodeResults> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_SinglenodeOrNodePair))
				continue;
			taskAdd2allMiner(taskCombination, resultsMap);
		}
		System.out.println("allminers 2::::::" + allMiners.size());
	}

	public HashMap<TaskCombination, MinerNodeResults> startAllNetworkStructrueMiners(MiningObject miningObject){
		HashMap<TaskCombination, MinerNodeResults>resultsMap=
				new HashMap<TaskCombination,MinerNodeResults>();
		startMinerOneByOne(MinerType.MiningTypes_WholeNetwork,miningObject);
		waitUtilAllMinerOver(MinerType.MiningTypes_WholeNetwork, miningObject, resultsMap, null,null);
		
		Iterator<Entry<TaskCombination, INetworkMiner>>iterator=allCombinationMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, INetworkMiner> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningTypes_WholeNetwork))
				continue;
			taskAdd2allMiner(taskCombination, resultsMap);
		}
		return resultsMap;
	}

	//分布式网络结构
	public HashMap<TaskCombination, MinerNodeResults> startAllNetworkStructrueMinersDis(MiningObject miningObject){
		HashMap<TaskCombination, MinerNodeResults>resultsMap=
				new HashMap<TaskCombination,MinerNodeResults>();
		startMinerOneByOne(MinerType.MiningTypes_WholeNetwork,miningObject);
		waitUtilAllMinerOver(MinerType.MiningTypes_WholeNetwork, miningObject, resultsMap, null,null);
		return resultsMap;
	}

	public void showNetWorkMinersDis(MiningObject miningObject, HashMap<TaskCombination, MinerNodeResults> resultsMap) {
//		allMiners.clear();
		Iterator<Entry<TaskCombination, MinerNodeResults>>iterator=resultsMap.entrySet().iterator();
//		System.out.println("allCombinationMiners ::::" + resultsMap.size());
		while(iterator.hasNext()){
			Entry<TaskCombination, MinerNodeResults> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningTypes_WholeNetwork))
				continue;
			taskAdd2allMiner(taskCombination, resultsMap);
		}
		System.out.println("allminers 2::::::" + allMiners.size());
	}



	public HashMap<TaskCombination, MinerProtocolResults> startAllProtocolMiners(MiningObject miningObject){
		HashMap<TaskCombination, MinerProtocolResults>resultsMap=
				new HashMap<TaskCombination,MinerProtocolResults>();
		startMinerOneByOne(MinerType.MiningType_ProtocolAssociation,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_ProtocolAssociation, miningObject,null,resultsMap,null);

		Iterator<Entry<TaskCombination, INetworkMiner>>iterator=allCombinationMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, INetworkMiner> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_ProtocolAssociation))
				continue;
			for(TaskElement task:taskCombination.getTasks()){
				switch (task.getMiningMethod()) {
				case MiningMethods_SimilarityMining:
					NetworkMinerProtocolAssSim minerSim =new NetworkMinerProtocolAssSim(task,null);
					minerSim.getResults().setRetSim(resultsMap.get(taskCombination).getRetSim());
					minerSim.getResults().setInputData(taskCombination.getDataItems());
					minerSim.isOver.setIsover(true);
					allMiners.put(task, minerSim);
					break;
				case MiningMethods_FrequenceItemMining:
					
					NetworkMinerProtoclAssLine minerFPLine=new NetworkMinerProtoclAssLine(task, null);
					minerFPLine.getResults().setRetFPLine(resultsMap.get(taskCombination).getRetFP());
					minerFPLine.isOver.setIsover(true);
					minerFPLine.getResults().setInputData(taskCombination.getDataItems());
					allMiners.put(task, minerFPLine);
					break;
				default:
					break;
				}
			}
		}
		return resultsMap;
	}

	public HashMap<TaskCombination, MinerProtocolResults> startAllProtocolMinersDis(MiningObject miningObject){
		HashMap<TaskCombination, MinerProtocolResults>resultsMap=
				new HashMap<TaskCombination,MinerProtocolResults>();
		startMinerOneByOne(MinerType.MiningType_ProtocolAssociation,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_ProtocolAssociation, miningObject,null,resultsMap,null);

		return resultsMap;
	}

	public void showProtocolMinersDis(MiningObject miningObject, HashMap<TaskCombination, MinerProtocolResults> resultsMap){
		Iterator<Entry<TaskCombination, MinerProtocolResults>>iterator=resultsMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, MinerProtocolResults> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_ProtocolAssociation))
				continue;
			for(TaskElement task:taskCombination.getTasks()){
				switch (task.getMiningMethod()) {
					case MiningMethods_SimilarityMining:
						NetworkMinerProtocolAssSim minerSim =new NetworkMinerProtocolAssSim(task,null);
						minerSim.getResults().setRetSim(resultsMap.get(taskCombination).getRetSim());
						minerSim.getResults().setInputData(taskCombination.getDataItems());
						minerSim.isOver.setIsover(true);
						allMiners.put(task, minerSim);
						break;
					case MiningMethods_FrequenceItemMining:

						NetworkMinerProtoclAssLine minerFPLine=new NetworkMinerProtoclAssLine(task, null);
						minerFPLine.getResults().setRetFPLine(resultsMap.get(taskCombination).getRetFP());
						minerFPLine.isOver.setIsover(true);
						minerFPLine.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, minerFPLine);
						break;
					default:
						break;
				}
			}
		}
	}

	public HashMap<TaskCombination, MinerResultsPath> startAllPathMiners(MiningObject miningObject){
		HashMap<TaskCombination, MinerResultsPath>resultsMap=
				new HashMap<TaskCombination,MinerResultsPath>();
		startMinerOneByOne(MinerType.MiningType_Path,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_Path, miningObject, null, null,resultsMap);

		Iterator<Entry<TaskCombination, INetworkMiner>>iterator=allCombinationMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, INetworkMiner> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_Path))
				continue;
			taskAdd2allMiner(taskCombination,resultsMap);
		}
		return resultsMap;
	}

	//分布式路径
	public HashMap<TaskCombination, MinerResultsPath> startAllPathMinersDis(MiningObject miningObject){
		HashMap<TaskCombination, MinerResultsPath>resultsMap=
				new HashMap<TaskCombination,MinerResultsPath>();
		startMinerOneByOne(MinerType.MiningType_Path,miningObject);
		waitUtilAllMinerOver(MinerType.MiningType_Path, miningObject, null, null,resultsMap);

		return resultsMap;
	}

	public void showPathMinersDis(MiningObject miningObject, HashMap<TaskCombination, MinerResultsPath> resultsMap){
		//获取全部的TaskCombination，resultsMap中有，客户端allCombinationMiners没有，TaskCombinationList中也有
		Iterator<Entry<TaskCombination, MinerResultsPath>>iterator=resultsMap.entrySet().iterator();
//		System.out.println("allCombinationMiners :::: " + resultsMap.size());
		while(iterator.hasNext()){
			Entry<TaskCombination, MinerResultsPath> entry=iterator.next();
			TaskCombination taskCombination=entry.getKey();
			if(!taskCombination.getMiningObject().equals(miningObject.toString())||
					!taskCombination.getMinerType().equals(MinerType.MiningType_Path))
				continue;
			taskAdd2allMiner(taskCombination, resultsMap);

			System.out.println("allMiners 2:::::: " + allMiners.size());
		}
	}

	private void startMinerOneByOne(MinerType minerType,MiningObject miningObject){
//		System.out.println("allCombinationMiners: " + allCombinationMiners.size());
		Iterator<Entry<TaskCombination, INetworkMiner>>iterator=allCombinationMiners.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<TaskCombination, INetworkMiner> entry=iterator.next();
//			System.out.println("1:" + entry.getKey().getMinerType() + minerType );
//			System.out.println("2:" + entry.getKey().getMiningObject() + miningObject.toString() );
//			System.out.println("3:" + entry.getValue().isOver() );
			if(!entry.getKey().getMinerType().equals(minerType)||!entry.getKey().getMiningObject()
					.equals(miningObject.toString())||entry.getValue().isOver())
				continue;
			entry.getValue().start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void waitUtilAllMinerOver(MinerType type,MiningObject miningObject,HashMap<TaskCombination,
			MinerNodeResults> retNode,HashMap<TaskCombination, MinerProtocolResults>retPro, HashMap<TaskCombination, MinerResultsPath> retPath){
		Iterator<Entry<TaskCombination, INetworkMiner>> iterator;
		boolean isAllOver=false;
		while (!isAllOver) {
			isAllOver=true;
			iterator=allCombinationMiners.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<TaskCombination, INetworkMiner> entry=iterator.next();
				TaskCombination task=entry.getKey();
				if(!task.getMinerType().equals(type)||!task.getMiningObject().equals(miningObject.toString()))
					continue;
				INetworkMiner miner=entry.getValue();

				//判断任务是否已完成
				if(!miner.isOver()){
					//判断任务是否发生异常
					if(miner.isAlive()){
						isAllOver=false;
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(task.getName()+" not over");
					}

				}else{
					//正常结束时，保存结果
					if(miner.isOver()){
						switch (type) {
						case MiningType_SinglenodeOrNodePair:
							retNode.put(task, miner.getResults().getRetNode());
							break;
						case MiningType_ProtocolAssociation:
							retPro.put(task, miner.getResults().getRetProtocol());
							//System.out.println(task.getName()+" has over");
							break;
						case MiningType_Path:
							retPath.put(task, miner.getResults().getRetPath());
							break;
						case MiningTypes_WholeNetwork:
							retNode.put(task, miner.getResults().getRetNode());
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}


	private void autoTaskFilter(){
		for(int t=0;t<TaskElement.allTasks.size();t++){
			TaskElement task=TaskElement.allTasks.get(t);
			if(task.getTaskName().contains("周期挖掘_auto")){
				if(allMiners.containsKey(task)){
					INetworkMiner miner=allMiners.get(task);
					if(!miner.getResults().getRetPM().getHasPeriod()){
						allMiners.get(task).stop();
						allMiners.remove(task);
						TaskElement.allTasks.remove(t);
						t--;
					}
				}
			}else if(task.getTaskName().contains("auto_频繁模式挖掘")){
				if(allMiners.containsKey(task)){
					INetworkMiner miner=allMiners.get(task);
					if(!miner.getResults().getRetSM().isHasFreItems()){
						allMiners.get(task).stop();
						allMiners.remove(task);
						TaskElement.allTasks.remove(t);
						t--;
					}
				}
			}
		}
		System.out.println(TaskElement.allTasks.size());
	}
	public void startMiner(TaskElement task){
		if(allMiners.containsKey(task)){
			allMiners.get(task).start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void stopAllMiners(){
		for(INetworkMiner miner :allMiners.values()){
			miner.stop();
		}
	}
	public void stopMiner(TaskElement task){
		if(allMiners.containsKey(task)){
			allMiners.get(task).stop();
		}
	}

	public void taskCombinationAdd2allMiner(HashMap<TaskCombination, ?> resultsMap) {
		for (TaskCombination taskCombination: resultsMap.keySet()) {
			taskAdd2allMiner(taskCombination, resultsMap);
		}
	}

	/*public void taskCombinationAdd2allMiner(HashMap<TaskCombination, MinerResultsPath> resultsMap) {
		for
	}*/

	private void taskAdd2allMiner(TaskCombination taskCombination, HashMap<TaskCombination, ?> resultsMap) {
		if (resultsMap.get(taskCombination) instanceof MinerNodeResults) {
			MinerNodeResults results = (MinerNodeResults) resultsMap.get(taskCombination);
			for (TaskElement task : taskCombination.getTasks()) {
				switch (task.getMiningMethod()) {
					case MiningMethods_OutliesMining:
						NetworkMinerOM minerOM = new NetworkMinerOM(task, null);
						if (minerOM.getResults().getRetOM().isIslinkDegree()) {
							System.out.println("true");
						}
						minerOM.getResults().setRetOM(results.getRetOM());
						minerOM.getResults().setInputData(taskCombination.getDataItems());
						minerOM.isOver.setIsover(true);
						allMiners.put(task, minerOM);
						break;
					case MiningMethods_PeriodicityMining:
						NetworkMinerPM minerPM = new NetworkMinerPM(task, null);
						minerPM.getResults().setRetPM(results.getRetPM());
						minerPM.isOver.setIsover(true);
						minerPM.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, minerPM);
						break;
					case MiningMethods_SequenceMining:
						NetworkMinerSM minerSM = new NetworkMinerSM(task, null);
						minerSM.getResults().setRetSM(results.getRetSM());
						minerSM.isOver.setIsover(true);
						if (minerSM.getResults().getRetSM().isHasFreItems()) {
							System.out.println(minerSM.getResults().getRetSM().getPatterns().getData().size());
						} else {
							System.out.println(taskCombination.getName() + " none freitems");
						}
						minerSM.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, minerSM);
						break;
					case MiningMethods_Statistics:
						NetworkMinerStatistics statistics = new NetworkMinerStatistics(task, null);
						statistics.getResults().setRetStatistics(results.getRetStatistics());
						statistics.isOver.setIsover(true);
						statistics.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, statistics);
						break;
					case MiningMethods_PartialCycle:
						NetworkMinerPartialCycle partialCycle = new NetworkMinerPartialCycle(task,null);
						partialCycle.getResults().setRetPartialCycle(results.getRetPartialCycle());
						partialCycle.isOver.setIsover(true);
						partialCycle.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, partialCycle);
					default:
						break;
				}
			}
		} else if (resultsMap.get(taskCombination) instanceof MinerResultsPath){
			MinerResultsPath results= (MinerResultsPath) resultsMap.get(taskCombination);
			for(TaskElement task:taskCombination.getTasks()){
				switch (task.getMiningMethod()) {
					case MiningMethods_OutliesMining:
						NetworkMinerOM minerOM =new NetworkMinerOM(task,null);
						minerOM.getResults().getRetPath().setRetOM(results.getRetOM());
						minerOM.getResults().getRetPath().setPathOriDataItems(results.getPathOriDataItems());
						minerOM.getResults().setInputData(taskCombination.getDataItems());
						minerOM.isOver.setIsover(true);
						allMiners.put(task, minerOM);
						break;
					case MiningMethods_PeriodicityMining:
						NetworkMinerPM minerPM=new NetworkMinerPM(task, null);
						minerPM.getResults().getRetPath().setRetPM(results.getRetPM());
						minerPM.isOver.setIsover(true);
						minerPM.getResults().getRetPath().setPathOriDataItems(results.getPathOriDataItems());
						minerPM.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, minerPM);
						break;
					case MiningMethods_Statistics:
						NetworkMinerStatistics statistics=new NetworkMinerStatistics(task, null);
						statistics.getResults().getRetPath().setRetStatistic(results.getRetStatistic());
						statistics.getResults().getRetPath().setPathProb(results.getPathProb());
						statistics.isOver.setIsover(true);
						statistics.getResults().setInputData(taskCombination.getDataItems());
						allMiners.put(task, statistics);
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public void onTaskAdded(TaskElement task) {
		createMiner(task);
	}
	
	@Override
	public void onTaskAdded(TaskCombination task) {
		createMiner(task);
	}

	@Override
	public void onTaskAddedDis(TaskCombination task) {
		createMinerDis(task);
	}
	
	@Override
	public void onTaskDeleted(TaskElement task) {
		removeMiner(task);
	}
	
	@Override
	public void onTaskDeleted(TaskCombination task) {
		removeMiner(task);
		
	}
	@Override
	public void onTaskModified(TaskElement task, int modify_type) {
		if (allMiners.containsKey(task)){
			INetworkMiner miner = allMiners.get(task);
			if (miner.isAlive())
			{
				miner.stop();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				miner.start();
			}
		}
	}
	
	@Override
	public void onTaskModified(TaskCombination task, int modify_type) {
		if (allCombinationMiners.containsKey(task)){
			INetworkMiner miner = allCombinationMiners.get(task);
			if (miner.isAlive())
			{
				miner.stop();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				miner.start();
			}
		}
	}
	@Override
	public void onTaskToDisplay(TaskElement task) {
		
	}
	@Override
	public void onTaskToDisplay(TaskCombination task) {
		
	}
	@Override
	public void onTaskMiningToStart(TaskElement task) {
		if (allMiners.containsKey(task))
			allMiners.get(task).start();		
	}
	@Override
	public void onTaskMiningToStop(TaskElement task) {
		if (allMiners.containsKey(task))
			allMiners.get(task).stop();		
	}
	@Override
	public void onCreate() {
		TaskElement.addTaskListener(this);		
	}
	@Override
	public void onClosing() {
	}
	
}