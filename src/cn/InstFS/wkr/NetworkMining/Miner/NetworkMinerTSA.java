/**
 * 时间序列分析Miner
 * 采用定时器实现定时运行，大部分可以重新写
 */


package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import WaveletUtil.TEOPartern;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerTSA implements INetworkMiner {
	Timer timer;
	TSATimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning;
	TaskElement task;
	IReader reader;
	
	
	public NetworkMinerTSA(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
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
		results = new MinerResults(this);
		timerTask = new TSATimerTask(task, results, displayer,reader);
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
		// TODO Auto-generated method stub
		return false;
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
class TSATimerTask extends TimerTask{
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	IReader reader;
	public TSATimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,IReader reader) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
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
		results.getRetTSA().setParamsTSA((ParamsTSA) task.getMiningParams());
		isRunning = true;
		ParamsTSA params = (ParamsTSA) task.getMiningParams();
		
		// 读取数据
		DataItems dataItems = null;
		dataItems=reader.readInputByText();
		if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
			DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
					dataItems.isAllDataIsDouble());
		}
		if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
			dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
					task.getDiscreteEndNodes());
		}

		results.setInputData(dataItems);
		IMinerTSA tsaMethod=null;
		
//		int dimension=Math.max(task.getDiscreteDimension(),dataItems.getDiscretizedDimension());
		if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ARTSA)){
			tsaMethod=new ARTSA(task, params.getPredictPeriod(),dataItems);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_FastFourier)){
			tsaMethod=new FastFourierOutliesDetection(dataItems);
			((FastFourierOutliesDetection)tsaMethod).setAmplitudeRatio(0.7);
			((FastFourierOutliesDetection)tsaMethod).setVarK(2.5);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_GaussDetection)){
			tsaMethod=new AnormalyDetection(dataItems);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ERPDistTSA)){
			tsaMethod=new ERPDistTSA(task,params.getPredictPeriod(),dataItems);
		}else if (task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_TEOTSA)) {
			tsaMethod=new TEOPartern(dataItems, 4, 4, 50);	
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_NeuralNetworkTSA)){
			TextUtils textUtils=new TextUtils();
			textUtils.setTextPath(task.getSourcePath()+task.getMiningObject());
			textUtils.writeOutput(dataItems);
			tsaMethod=new NeuralNetwork(task.getSourcePath()+task.getMiningObject(), dataItems.getLastTime(), task);
		}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ARIMATSA)){
			tsaMethod=new ARIMATSA(task, dataItems, 5);
		}else{
			throw new RuntimeException("方法不存在！");
		}
		tsaMethod.TimeSeriesAnalysis();
		results.getRetTSA().setOutlies(tsaMethod.getOutlies());            //查找异常
		results.getRetTSA().setPredictItems(tsaMethod.getPredictItems());  //预测
		
//		boolean isDiscrete = dataItems.isDiscrete();
//		if (!isDiscrete){	// 连续值
//			ContinuousPeriodDetection cpd = new ContinuousPeriodDetection();
//			cpd.Detection(results);
//		}else{
//			int dimension=Math.max(task.getDiscreteDimension(),dataItems.getDiscretizedDimension());
//			DiscreteTSA discreteTSA=new DiscreteTSA(dimension, task,task.getDiscreteEndNodes(),
//					params.getPeriodThreshold(),params.getOutlierThreshold()
//					,params.getPredictPeriod(),dataItems);
//			discreteTSA.predictByPeriod();
//			results.getRetTSA().setOutlies(discreteTSA.getOutlies());            //查找异常
//			results.getRetTSA().setPredictItems(discreteTSA.getPredictItems());  //预测
//		}
		isRunning = false;
		if (displayer != null)
			displayer.displayMinerResults(results);
	}
}
