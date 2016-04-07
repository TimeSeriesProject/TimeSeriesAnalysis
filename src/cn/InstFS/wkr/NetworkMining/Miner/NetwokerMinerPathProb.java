package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetwokerMinerPathProb implements INetworkMiner{
	Timer timer;
	PathProbTimerTask timerTask;
	MinerResults results;
	IResultsDisplayer displayer;
	private Boolean isOver=false;
	
	boolean isRunning;
	TaskElement task;
	
	IReader reader;
	
	public NetwokerMinerPathProb(TaskElement task,IReader reader) {
		this.task = task;
		this.reader=reader;
	}
	
	@Override
	public boolean start() {
		System.out.println("PanelShowResultsPathProb   timer starting");
		if (timer != null){
			UtilsUI.appendOutput(task.getTaskName() + " -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(task.getTaskName() + " -- Still running");
			return false;
		}
		timer = new Timer();
		results = new MinerResults(this);
		timerTask = new PathProbTimerTask(task, results, displayer,reader,isOver);
		timer.scheduleAtFixedRate(timerTask, new Date(), UtilsSimulation.instance.getForcastWindowSizeInSeconds() * 1000);
		isRunning = true;
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- started");
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
		task.setRunning(isRunning);
		UtilsUI.appendOutput(task.getTaskName() + " -- stopped");
		return true;
	}

	@Override
	public boolean isAlive() {
		return isRunning;
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

class PathProbTimerTask extends TimerTask{
	TaskElement task;
	MinerResults results;
	private Boolean isOver;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	
	IReader reader;
	public PathProbTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,IReader reader,Boolean isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
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
//		results.getRetPM().setParamsPM((ParamsPM) task.getMiningParams());
		isRunning = true;
		// 读取数据
		DataItems dataItems = null;
		if(task.getMiningObject().equals("pathProb")){
			TextUtils utils=new TextUtils();
			//给定路径，找到每条路径概率
			if(task.getPathSource()==null||task.getPathSource().equals("")){
				throw new RuntimeException("未给定给定路径的文件地址");
			}
			dataItems=utils.reaPathDataItemFromFile(task.getPathSource());
			List<String> paths=dataItems.getData();
			Map<String, Map<String, Double>> maps=new HashMap<String, Map<String,Double>>();
			for(String path:paths){
				String[] pathnodes=path.split(",");
				String fileName="translateProbOf"+pathnodes[0]+"-"+pathnodes[pathnodes.length-1]+".csv";
				Map<String, Double> map=null;
				if(maps.containsKey(fileName)){
					map=maps.get(fileName);
				}else{
					map=utils.readMapFromFile("./configs/"+fileName);
					maps.put(fileName, map);
				}
				double prob=DataPretreatment.getPathProb(map, path);
				dataItems.prob.add(prob);
			}
			for(int i=0;i<dataItems.getLength();i++){
				System.out.println(dataItems.data.get(i)+":"+dataItems.prob.get(i));
			}
		}else if(task.getMiningObject().equals("path")){
			//不给定路径，统计每条路径的概率
			dataItems=reader.readInputByText();
			if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
				dataItems=DataPretreatment.aggregateData(dataItems, task.getGranularity(), task.getAggregateMethod(),
						!dataItems.isAllDataIsDouble());
			}
			if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
				dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
						task.getDiscreteEndNodes());
			}
			DataPretreatment.translateProbilityOfData(dataItems);//将跳转概率保存到文件中
			dataItems=DataPretreatment.changeDataToProb(dataItems); //计算每条路径的概率
			results.setInputData(dataItems);
			List<Map<String, Double>>datas=dataItems.getProbMap();
			Set<String>varset=dataItems.getVarSet();
			List<List<String>> seqs=new ArrayList<List<String>>();
			for(String item:varset){
				int row=0;
				List<String>seq=new ArrayList<String>();
				seq.add(item);
				for(Map<String, Double>map:datas){
					if(map.containsKey(item)){
						int value=(int)(map.get(item)*1000);
						seq.add(value+"");
						row++;
					}else{
						seq.add("0");
					}
				}
				if(row<dataItems.getLength()*0.05)
					continue;
				seqs.add(seq);
			}
			String fileName;
			String[] pathExampleNodes=seqs.get(0).get(0).split(",");
			fileName="path_"+pathExampleNodes[0]+"-"+pathExampleNodes[pathExampleNodes.length-1]+".csv";
			TextUtils textUtils=new TextUtils();
			textUtils.writeLists(seqs, "./configs/"+fileName);
		}
		
		isRunning = false;
		isOver=true;
		if (displayer != null)
			displayer.displayMinerResults(results);
	}
}
