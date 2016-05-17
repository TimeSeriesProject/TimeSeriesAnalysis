package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;


/**
 * 时间序列统计
 * @author wsc
 *
 */
public class NetworkMinerStatistics implements INetworkMiner {
	Timer timer;
	StatisticsTimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	
	boolean isRunning=false;
	Boolean isOver=false;
	TaskElement task;
	IReader reader;
	
	public NetworkMinerStatistics(TaskElement task,IReader reader) {
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
		timerTask = new StatisticsTimerTask(task, results, displayer,reader,timer,isOver);
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
class StatisticsTimerTask extends TimerTask  {
	private int splitM;
	private double ratio;
	private double std;
	
	TaskElement task;
	MinerResults results;
	IResultsDisplayer displayer;
	private Timer timer;
	private boolean isRunning = false;
	private Boolean isOver;
	IReader reader;
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,Timer timer,Boolean isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
		this.splitM=2;
		this.ratio=0.2;
	}
	public StatisticsTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,
			IReader reader,Timer timer,Boolean isOver,int splitM,double ratio){
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.timer=timer;
		this.isOver=isOver;
		this.splitM=splitM;
		this.ratio=ratio;
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
		getMeanAndStd(dataItems);
		LZComplex(dataItems);
		sampleEntropy(dataItems);
	}
	
	private void getMeanAndStd(DataItems di){
		List<String>datas=di.getData();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String data:datas){
			statistics.addValue(Double.parseDouble(data));
		}
		double mean=statistics.getMean();
		double std=statistics.getStandardDeviation();
		results.getRetStatistics().setMean(mean);
		results.getRetStatistics().setStd(std);
		this.std=std;
		results.getRetStatistics().setSpan(di.getLength());
	}
	
	private void sampleEntropy(DataItems di){
		List<Double[]> splits=splitDataItems(splitM,di);
		double entropy1=averageEntropy(splits);
		splits.clear();
		splits=splitDataItems(splitM+1,di);
		double entropy2=averageEntropy(splits);
		double sampleEntropy=-(Math.log(entropy2/entropy1)/Math.log(Math.E));
		results.getRetStatistics().setSampleENtropy(sampleEntropy);
	}
	private void LZComplex(DataItems di){
		List<String> data=di.getData();
		int numN=data.size();
		int numC=0;
		List<String> listS=new ArrayList<String>();
		List<String> listQ=new ArrayList<String>();
		listS.add(data.get(0));
		numC++;
		for(int i=1;i<numN;i++){
			listQ.add(data.get(i));
			List<String> SQP=generateSQP(listS, listQ);
			if(!isSubList(SQP, listQ)){
				listS.addAll(listQ);
				numC++;
			}
		}
		double bn=(numN*1.0)/Math.log(numN*1.0);
		double complex=(numC*1.0)/bn;
		results.getRetStatistics().setComplex(complex);
	}
	
	private double averageEntropy(List<Double[]> splits){
		int size=splits.size();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<size;i++){
			int num=0;
			for(int j=0;j<size;j++){
				if(i==j)
					continue;
				if(dist(splits.get(i), splits.get(j))<(ratio*std))
					num++;
			}
			statistics.addValue(num*1.0/(size-1));
		}
		return statistics.getMean();
	}
	
	private List<Double[]> splitDataItems(int splitM,DataItems di){
		List<String> datas=di.getData();
		int timeSpan=datas.size();
		List<Double[]> splits=new ArrayList<Double[]>();
		for(int i=0;i<timeSpan-splitM+1;i++){
			Double[] split=new Double[splitM];
			for(int j=0;j<splitM;j++)
				split[j]=Double.parseDouble(datas.get(i+j));
			splits.add(split);
		}
		return splits;
	}
	
	private double dist(Double[] split1,Double[] split2){
		int len=split1.length;
		double max=0;
		for(int i=0;i<len;i++){
			if(Math.abs(split1[i]-split2[i])>max)
				max=Math.abs(split1[i]-split2[i]);
		}
		return max;
	}
	
	private boolean isSubList(List<String> list1,List<String> list2){
		int size1=list1.size();
		int size2=list2.size();
		boolean isSublist=true;
		for(int i=0;i<size1-size2+1;i++){
			isSublist=true;
			for(int j=0;j<size2;j++){
				if(!list1.get(i+j).equals(list2.get(j))){
					isSublist=false;
					break;
				}
			}
			if(isSublist)
				break;
		}
		return isSublist;
	}
	
	private List<String> generateSQP(List<String>S,List<String>Q){
		List<String> SQP=new ArrayList<String>();
		SQP.addAll(S);
		SQP.addAll(Q);
		SQP.remove(SQP.size()-1);
		return SQP;
	}
}
