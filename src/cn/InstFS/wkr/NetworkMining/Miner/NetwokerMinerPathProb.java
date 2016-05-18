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
import java.util.Map.Entry;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

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
		this.results=new MinerResults(this);
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
		timerTask = new PathProbTimerTask(task, results, displayer,reader,timer,isOver);
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
	private Timer timer;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	
	IReader reader;
	public PathProbTimerTask(TaskElement task, MinerResults results, IResultsDisplayer displayer,IReader reader,Timer timer,Boolean isOver) {
		this.task = task;
		this.results = results;
		this.displayer = displayer;
		this.reader=reader;
		this.isOver=isOver;
		this.timer=timer;
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
		if(task.getMiningObject().toLowerCase().equals("pathprob")){
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
		}else if(task.getMiningObject().toLowerCase().equals("path")){
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
			pathPeriodDetect(dataItems);
		}else if(task.getMiningObject().toLowerCase().equals("allpath")){
			task.setMiningObject("path");
			Map<String, DataItems> dataMap=reader.readAllRoute();
			Iterator<Entry<String, DataItems>> iterator=dataMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, DataItems> entry=iterator.next();
				pathPeriodDetect(entry.getValue());
				getPrimaryPath(entry.getValue());
			}
		}else if(task.getMiningObject().toLowerCase().equals("allpathtraffic")){
			task.setMiningObject("path:traffic");
			Map<String, DataItems> dataMap=reader.readAllRoute();
			Iterator<Entry<String, DataItems>> iterator=dataMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, DataItems> entry=iterator.next();
				pathPeriodDetect(entry.getValue());
			}
		}
		isRunning = false;
		isOver=true;
		if (displayer != null)
			displayer.displayMinerResults(results);
		timer.cancel();
	}
	
	private void pathPeriodDetect(DataItems dataItems){
		List datas;
		if (task.getMiningObject().equals("path:traffic")){
			datas = dataItems.getNonNumData();
		} else {
			DataPretreatment.translateProbilityOfData(dataItems);//将跳转概率保存到文件中
			dataItems = DataPretreatment.changeDataToProb(dataItems); //计算每条路径的概率
			datas = dataItems.getProbMap();
		}
		results.setInputData(dataItems);
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
					if(new Double(map.get(item).toString()) < 1){
						int value=(int)((double)map.get(item)*1000);
						seq.add(value+"");
						row++;
					}else{
						int value = (int)((double)map.get(item));
						seq.add(value+"");
						row++;
					}
				}else{
					seq.add("0");
				}
			}
			
			/*for(Map<String, Double>map:datas){
				if(map.containsKey(item)){
					int value=(int)(map.get(item)*1000);
					seq.add(value+"");
					row++;
				}else{
					seq.add("0");
				}
			}*/
			if(row<dataItems.getLength()*0.05)
				continue;
			seqs.add(seq);
		}
		//针对每一个seq做一次周期检测
		MinerResultsPath retPath=new MinerResultsPath();
		for(List<String>seq:seqs){
			DataItems newItem=new DataItems();
			String name=seq.get(0);
			seq.remove(0);
			newItem.setData(seq);
			newItem.setTime(dataItems.getTime());
			IMinerPM pmMethod=new ERPDistencePM();
			pmMethod.setDataItems(newItem);
			pmMethod.setOriginDataItems(newItem);
			pmMethod.predictPeriod();
			if(pmMethod.hasPeriod()){
				System.out.println("period:"+name+":"+pmMethod.getPredictPeriod()+":"+pmMethod.getFirstPossiblePeriod());
				retPath.getPeriodPath().put(name, pmMethod.getPredictPeriod());
				retPath.getFirstPeriodOfPath().put(name, pmMethod.getFirstPossiblePeriod());
				retPath.getItemsInPeriod().put(name, pmMethod.getItemsInPeriod());
			}
		}
		results.setRetPath(retPath);
	}
	
	/**
	 * 获取通信的主要路径
	 * @param dataItems 含有NonNumData的dataItems，NonNumData记录各小时各路径上的通信次数/流量和
	 * @return primaryPath
     */
	private String getPrimaryPath(DataItems dataItems){
		String primaryPath = new String ();
		List<Map<String, Integer>> NonNumData = dataItems.getNonNumData();
		HashMap<String, Integer> total = new HashMap<>();

		Iterator it = NonNumData.iterator();

		//统计每条路径通信次数和
		while (it.hasNext()){
			HashMap<String, Integer> map = (HashMap<String,Integer>)it.next();
			Iterator keys = map.keySet().iterator();

			while (keys.hasNext()){
				String key = (String) keys.next();
				int value = map.get(key);
				if (total.containsKey(key)) {
					int oriValue = total.get(key);
					total.put(key, value + oriValue);
				} else
					total.put(key, 0);
			}
		}

		Iterator totalKeys = total.keySet().iterator();
		int maxV = -1;
		while (totalKeys.hasNext()){
			String key = (String)totalKeys.next();
			int value = total.get(key);

			if (value > maxV) {
				maxV = value;
				primaryPath = key;
			}
		}
		System.out.println("主要路径："+ primaryPath.toString() + "次数"+ total.get(primaryPath));
		return primaryPath;
	}
}
