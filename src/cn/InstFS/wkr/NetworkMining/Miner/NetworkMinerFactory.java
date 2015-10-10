package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.nodePairReader;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.ITaskElementEventListener;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

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
	public INetworkMiner createMiner(TaskElement task){
		IReader reader=null;
		INetworkMiner miner = null;
		if (task == null)
			return null;
		if (allMiners.containsKey(task))
			return allMiners.get(task);
		if(task.getTaskRange().equals(TaskRange.NodePairRange)){
			reader=new nodePairReader(task, task.getRange().split(","));
		}else if(task.getTaskRange().equals(TaskRange.SingleNodeRange)){
			//TODO　生成单节点读取数据Reader
		}else if(task.getTaskRange().equals(TaskRange.WholeNetworkRange)){
			//TODO 生成整个网络读取数据Reader
		}
		if (task.getMiningMethod().equals(MiningMethod.MiningMethods_SequenceMining)){
			miner = new NetworkMinerSM(task,reader);
		}else if (task.getMiningMethod().equals(MiningMethod.MiningMethods_TsAnalysis)){
			miner = new NetworkMinerTSA(task,reader);
		}else if (task.getMiningMethod().equals(MiningMethod.MiningMethods_PeriodicityMining)){
			miner = new NetworkMinerPM(task,reader);
		}
		allMiners.put(task, miner);
		return miner;
	}
	public void removeMiner(TaskElement task){
		if (allMiners.containsKey(task)){
			allMiners.get(task).stop();
			allMiners.remove(task);
		}
	}
	public void removeMiner(INetworkMiner miner){
		if (allMiners.containsKey(miner.getTask())){
			allMiners.get(miner.getTask()).stop();
			allMiners.remove(miner.getTask());
		}			
	}
	
	public void startAllMiners(){
		for(INetworkMiner miner :allMiners.values()){
			miner.start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	@Override
	public void onTaskAdded(TaskElement task) {
		createMiner(task);
	}
	@Override
	public void onTaskDeleted(TaskElement task) {
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
	public void onTaskToDisplay(TaskElement task) {
		
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
