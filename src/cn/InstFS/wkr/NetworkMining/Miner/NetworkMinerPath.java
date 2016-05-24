package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import WaveletUtil.PointPatternDetection;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.IReader;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.AggregateMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerPath implements INetworkMiner{
	private Timer timer;
	private PathTimerTask timerTask;
	private MinerResults results;
	private IResultsDisplayer displayer;
	boolean isRunning;
	IsOver Over;
	
	private TaskCombination taskCombination;
	
	public NetworkMinerPath(TaskCombination taskCombination) {
		this.taskCombination=taskCombination;
		results = new MinerResults(this);
		Over=new IsOver();
	}
	
	@Override
	public boolean start() {
		if (timer != null){
			UtilsUI.appendOutput(taskCombination.getName()+" -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(taskCombination.getName()+" -- Still running");
			return false;
		}
		timer = new Timer();
		
		timerTask = new PathTimerTask(taskCombination,results, displayer,timer,Over);
		timer.scheduleAtFixedRate(timerTask, new Date(), UtilsSimulation.instance.getForcastWindowSizeInSeconds() * 1000);
		isRunning = true;
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
		return Over.isIsover();
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

class PathTimerTask extends TimerTask{
	MinerResults results;
	Timer timer;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	private IsOver isOver;
	private TaskCombination taskCombination;
	public PathTimerTask(TaskCombination taskCombination, MinerResults results, IResultsDisplayer displayer,
			Timer timer,IsOver isOver) {
		this.taskCombination = taskCombination;
		this.results = results;
		this.displayer = displayer;
		this.timer=timer;
		this.isOver=isOver;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	@Override
	
	public void run() {
		if (isRunning){
			System.out.println(taskCombination.getName()+ " --> Still Running");
			return;
		}
		results.setDateProcess(UtilsSimulation.instance.getCurTime());
		
		isRunning = true;
		// 读取数据
		PMDetect(taskCombination.getDataItems(),taskCombination.getTasks());
	}
	
	private void PMDetect(DataItems dataItems,List<TaskElement>tasks){
		DataItems oriDataItems=dataItems;
		results.setInputData(oriDataItems);
		HashMap<String, MinerResultsPM> retPathPM=new HashMap<String, MinerResultsPM>();
		HashMap<String, MinerResultsOM> retPathOM=new HashMap<String, MinerResultsOM>();
		for(TaskElement task:tasks){
			dataItems=oriDataItems;
			
			List datas = new ArrayList();
			if (task.getMiningObject().equals("流量")){
				datas = dataItems.getNonNumData();
			} else if (task.getMiningObject().equals("通信次数")) {
				DataPretreatment.translateProbilityOfData(dataItems);//将跳转概率保存到文件中
				dataItems = DataPretreatment.changeDataToProb(dataItems); //计算每条路径的概率
				datas = dataItems.getProbMap();
			}
			
			Set<String>varset=dataItems.getVarSet();
			List<List<String>> seqs=new ArrayList<List<String>>();
			for(String item:varset){
				int row=0;
				List<String>seq=new ArrayList<String>();
				seq.add(item);
				
				Iterator iter = datas.iterator();
				while(iter.hasNext()){
					Map map = (Map) iter.next();
					if(map.containsKey(item)) {
						if(map.get(item) instanceof Double){	//用于区别Double路径概率与Integer流量
							int value=(int)((double)map.get(item)*1000);
							seq.add(value+"");
							row++;
						}else if(map.get(item) instanceof Integer){
							int value = (int) map.get(item);
							seq.add(value+"");
							row++;
						}
					}else{
						seq.add("0");
					}
				}
				
				if(row<dataItems.getLength()*0.05)
					continue;
				seqs.add(seq);
			}

			for (List<String> seq: seqs){
				DataItems newItem=new DataItems();
				String name=seq.get(0);
				seq.remove(0);
				newItem.setData(seq);
				newItem.setTime(dataItems.getTime());

				switch (task.getMiningMethod()){
					case MiningMethods_PeriodicityMining:
						IMinerPM pmMethod = null;
						if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ERPDistencePM)){
							pmMethod=new ERPDistencePM();
						}else{
							throw new RuntimeException("方法不存在！");
						}
						pmMethod.setDataItems(newItem);
						pmMethod.setOriginDataItems(newItem);
						pmMethod.predictPeriod();
						MinerResultsPM retPM = new MinerResultsPM();
						if(pmMethod.hasPeriod()){
							System.out.println("period:"+name+":"+pmMethod.getPredictPeriod()+":"+pmMethod.getFirstPossiblePeriod());
						}
						setPMResults(retPM, pmMethod);
						retPathPM.put(name, retPM);

						break;
					case MiningMethods_OutliesMining:
						IMinerOM omMethod = null;
						MinerResultsOM retOM = new MinerResultsOM();
						if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_TEOTSA)){
							omMethod = new PointPatternDetection(newItem,2,10);
							retOM.setIslinkDegree(true);
						}
						omMethod.TimeSeriesAnalysis();
						setOMResults(retOM, omMethod);
						retPathOM.put(name, retOM);
						break;
					default:
						break;
				}
			}
			results.getRetPath().setRetPM(retPathPM);
			results.getRetPath().setRetOM(retPathOM);
			/*switch (task.getMiningMethod()) {
			case MiningMethods_PeriodicityMining:
				IMinerPM pmMethod = null;
				
				//针对每一个seq做一次周期检测
				HashMap<String, MinerResultsPM> retPathPM=new HashMap<String, MinerResultsPM>();
				for(List<String>seq:seqs){
					DataItems newItem=new DataItems();
					String name=seq.get(0);
					seq.remove(0);
					newItem.setData(seq);
					newItem.setTime(dataItems.getTime());
					
					if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ERPDistencePM)){
						pmMethod=new ERPDistencePM();
					}else{
						throw new RuntimeException("方法不存在！");
					}
					pmMethod.setDataItems(newItem);
					pmMethod.setOriginDataItems(newItem);
					pmMethod.predictPeriod();
					MinerResultsPM retPM = new MinerResultsPM();
					if(pmMethod.hasPeriod()){
						System.out.println("period:"+name+":"+pmMethod.getPredictPeriod()+":"+pmMethod.getFirstPossiblePeriod());
					}
					setPMResults(retPM, pmMethod);
					retPathPM.put(name, retPM);
				}
				
				results.getRetPath().setRetPM(retPathPM);
				break;
			default:
				break;
			}*/

		}
		isRunning = false;
		isOver.setIsover(true);;
		System.out.println(taskCombination.getName()+" over");
		if (displayer != null)
			displayer.displayMinerResults(results);
		timer.cancel();
	}
	
	private void setPMResults(MinerResults results,IMinerPM pmMethod, String path){
		results.getRetPath().getRetPM().get(path).setHasPeriod(pmMethod.hasPeriod());
		results.getRetPath().getRetPM().get(path).setPeriod(pmMethod.getPredictPeriod());
		results.getRetPath().getRetPM().get(path).setDistributePeriod(pmMethod.getItemsInPeriod());
		results.getRetPath().getRetPM().get(path).setMinDistributePeriod(pmMethod.getMinItemsInPeriod());
		results.getRetPath().getRetPM().get(path).setMaxDistributePeriod(pmMethod.getMaxItemsInPeriod());
		results.getRetPath().getRetPM().get(path).setFeatureValue(pmMethod.getMinEntropy());
		results.getRetPath().getRetPM().get(path).setFeatureValues(pmMethod.getEntropies());
		results.getRetPath().getRetPM().get(path).setFirstPossiblePeriod(pmMethod.getFirstPossiblePeriod());//找出第一个呈现周期性的周期
		results.getRetPath().getRetPM().get(path).setConfidence(pmMethod.getConfidence());
	}

	private void setPMResults(MinerResultsPM retPM, IMinerPM pmMethod){
		retPM.setHasPeriod(pmMethod.hasPeriod());
		retPM.setPeriod(pmMethod.getPredictPeriod());
		retPM.setDistributePeriod(pmMethod.getItemsInPeriod());
		retPM.setMinDistributePeriod(pmMethod.getMinItemsInPeriod());
		retPM.setMaxDistributePeriod(pmMethod.getMaxItemsInPeriod());
		retPM.setFeatureValue(pmMethod.getMinEntropy());
		retPM.setFeatureValues(pmMethod.getEntropies());
		retPM.setFirstPossiblePeriod(pmMethod.getFirstPossiblePeriod());//找出第一个呈现周期性的周期
		retPM.setConfidence(pmMethod.getConfidence());
	}

	private void setOMResults(MinerResultsOM retOM, IMinerOM omMethod){
		retOM.setOutlies(omMethod.getOutlies());    //查找异常
		if(omMethod.getOutlies()!=null){
			DataItems outlies=omMethod.getOutlies();
			int outliesLen=outlies.getLength();
			int itemLen=taskCombination.getDataItems().getLength();
			if(Math.abs(outliesLen-itemLen)<=1){
				int confidence=0;
				for(String item:outlies.getData()){
					if(Double.parseDouble(item)>=8000){
						retOM.setHasOutlies(true);
						confidence++;
					}
				}
				if(confidence!=0)
					retOM.setConfidence(confidence);
			}else{
				if(outlies.getLength()>0){
					retOM.setHasOutlies(true);
					retOM.setConfidence(outlies.getLength());
				}
			}
		}
	}
}

