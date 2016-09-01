/**
 * 时间序列分析Miner
 * 采用定时器实现定时运行，大部分可以重新写
 */


package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import WaveletUtil.PointPatternDetection;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.AnormalyDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.FastFourierOutliesDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.MultidimensionalOutlineDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerOM implements INetworkMiner {
	Timer timer;
	OMTimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning=false;
	public IsOver isOver=new IsOver();
	TaskElement task;
	IReader reader;
	
	
	public NetworkMinerOM(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
		results = new MinerResults(this);
	}
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsTSA   timer开始");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- 早已启动！");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(task.getTaskName() + " -- 上次挖掘尚未结束！");
			return false;
		}
		timer = new Timer();
		timerTask = new OMTimerTask(task, results, displayer,reader,timer,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), 2000);
		isRunning = true;
		task.setRunning(isRunning);
//		TaskElement.modify1Task(task);		
		UtilsUI.appendOutput(task.getTaskName() + " -- 开始挖掘！");
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
//		SMTimerTask.setLastTimeStoped(true);
		
		isRunning = false;
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- 停止挖掘！");
		return true;
	}

	@Override
	public boolean isAlive() {
		return false;
	}
	
	@Override
	public boolean isOver() {
		return isOver.isIsover();
	}

	public IsOver getIsOver() {
		return isOver;
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
		this.displayer = displayer;		
	}

}
class OMTimerTask extends TimerTask{
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private Timer timer;
	private boolean isRunning = false;
	private IsOver isOver;
	IReader reader;
	public OMTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,IReader reader,Timer timer,IsOver isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	@Override
	public void run() {
		if (isRunning){
			System.out.println(task.getTaskName() + " --> Still Running");
			return;
		}
//		if (UtilsSimulation.instance.isPaused())
//			return;
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		results.getRetOM().setParamsTSA((ParamsTSA) task.getMiningParams());
		isRunning = true;
		ParamsTSA params = (ParamsTSA) task.getMiningParams();
		
		// 读取数据 当Miner Reuslts中存在数据时，则不再读取
		DataItems dataItems = null;
		if(results.getInputData()==null||results.getInputData().getLength()==0){
			dataItems=reader.readInputByText();
			results.setInputData(dataItems);
		}else{
			dataItems=results.getInputData();
		}
		
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
					dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
					task.getDiscreteEndNodes());
		}
		IMinerOM tsaMethod=null;
		
		if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_FastFourier)){
			tsaMethod=new FastFourierOutliesDetection(dataItems);
			((FastFourierOutliesDetection)tsaMethod).setAmplitudeRatio(0.7);
			((FastFourierOutliesDetection)tsaMethod).setVarK(2.5);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_GaussDetection)){
			tsaMethod=new AnormalyDetection(dataItems);
		}else if (task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_TEOTSA)) {
			//tsaMethod=new TEOPartern(dataItems, 4, 4, 7);
			//tsaMethod=new MultidimensionalOutlineDetection(dataItems);
			tsaMethod=new PointPatternDetection(dataItems,2,10);
			
			results.getRetOM().setIslinkDegree(true);
		}else{
			throw new RuntimeException("方法不存在！");
		}
		
		//tsaMethod = new MultidimensionalOutlineDetection(dataItems);
		
		tsaMethod.TimeSeriesAnalysis();
		results.getRetOM().setOutlies(tsaMethod.getOutlies());    //查找异常
		results.getRetOM().setHasOutlies(false);
		if(tsaMethod.getOutlies()!=null){
			DataItems outlies=tsaMethod.getOutlies();
			if(outlies.getLength()==dataItems.getLength()){
				int confidence=0;
				for(String item:outlies.getData()){
					if(Double.parseDouble(item)>=8){
						results.getRetOM().setHasOutlies(true);
						confidence++;
					}
				}
				if(confidence!=0)
					results.getRetOM().setConfidence(confidence);
			}else{
				if(outlies.getLength()>0){
					results.getRetOM().setHasOutlies(true);
					results.getRetOM().setConfidence(outlies.getLength());
				}
			}
		}
		
		isRunning = false;
		isOver.setIsover(true);
		if (displayer != null)
			displayer.displayMinerResults(results);
		timer.cancel();
	}
}
