package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import WaveletUtil.PointPatternDetection;
import WaveletUtil.SAXPartternDetection;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.PatternMent;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.*;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm.ARIMATSA;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm.NeuralNetwork;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.FrequentAlgorithm.SequencePatternsDontSplit;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.AnormalyDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.FastFourierOutliesDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.GaussianOutlierDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.MultidimensionalOutlineDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodDetectionWitnDTW;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodMinerERP;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.PartialCycle;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm.ERPDistencePM;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm.averageEntropyPM;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.SeriesStatisticsAlogorithm.SeriesStatistics;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.MinerFactorySettings;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.NetworkFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.SingleNodeOrNodePairMinerFactory;
import cn.InstFS.wkr.NetworkMining.Miner.Factory.SingleNodeOrNodePairMinerFactoryDis;
import cn.InstFS.wkr.NetworkMining.Miner.Results.IResultsDisplayer;
import cn.InstFS.wkr.NetworkMining.Miner.Common.IsOver;
import cn.InstFS.wkr.NetworkMining.Miner.Common.LineElement;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerNodeResults;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResults;
import cn.InstFS.wkr.NetworkMining.Miner.Common.TaskCombination;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsAPI;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.ResultDisplay.UI.TaskProgress;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.*;
import cn.InstFS.wkr.NetworkMining.Results.MiningResultsFile;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsUI;

public class NetworkMinerNode implements INetworkMiner{
	private ScheduledExecutorService timer;
	private NodeTimerTask timerTask;
	private MinerResults results;
	private IResultsDisplayer displayer;
	boolean isRunning;
	IsOver Over;
	private TaskCombination taskCombination;
	
	public NetworkMinerNode(TaskCombination taskCombination) {
		this.taskCombination=taskCombination;
		results = new MinerResults(this);
		Over=new IsOver();
	}
	
	@Override
	public boolean start() {
		//System.out.println("PanelShowResultsPM   timer starting");
		MinerFactorySettings settings = getMinerFactorySettings(taskCombination);
		MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(taskCombination.getMiningObject()));
		if(resultsFile.hasFile(settings, taskCombination)) { // 已有挖掘结果存储，则不重新启动miner
			Over.setIsover(true);
			MinerNodeResults resultNode = (MinerNodeResults) resultsFile.file2Result();
			results.setRetNode(resultNode);

			TaskProgress taskProgress = TaskProgress.getInstance();
			taskProgress.increaseComplete();
			return false;
		}

		if (timer != null){
			UtilsUI.appendOutput(taskCombination.getName()+" -- already started");
			return false;
		}
		if (timerTask != null && timerTask.isRunning() == true){
			UtilsUI.appendOutput(taskCombination.getName()+" -- Still running");
			return false;
		}
		timer=Executors.newScheduledThreadPool(1);
		isRunning = true;
		timerTask = new NodeTimerTask(taskCombination,results, displayer,Over);
		ScheduledFuture<?> future=timer.schedule(timerTask, 10,TimeUnit.MILLISECONDS);
		try{
			future.get();
		}catch(Exception e){
			e.printStackTrace();
			isRunning = false;
			Over.setIsover(false);

			/* 记录发生异常的taskCombination至任务进度 */
			TaskProgress taskProgress = TaskProgress.getInstance();
			taskProgress.increaseComplete();
			taskProgress.addErrTaskCombination(taskCombination);
			timer.shutdownNow();
		}
		return isRunning;
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

	public static MinerFactorySettings getMinerFactorySettings(TaskCombination taskCombination) {
		MinerFactorySettings settings = null;
		switch (taskCombination.getMinerType()) {
			case MiningType_SinglenodeOrNodePair:
				if (taskCombination.getTaskRange().equals(TaskRange.SingleNodeRange)){
					settings = SingleNodeOrNodePairMinerFactory.getInstance();
				} else if (taskCombination.getTaskRange().equals(TaskRange.NodePairRange))
					settings = SingleNodeOrNodePairMinerFactory.getPairInstance();
				break;
			case MiningTypes_WholeNetwork:
				settings = NetworkFactory.getInstance();
				break;
			default:
				break;
		}
		return settings;
	}

	public static MinerFactorySettings getMinerFactorySettingsDis(TaskCombination taskCombination) {
		MinerFactorySettings settings = null;
		switch (taskCombination.getMinerType()) {
			case MiningType_SinglenodeOrNodePair:
				if (taskCombination.getTaskRange().equals(TaskRange.SingleNodeRange)){
					settings = SingleNodeOrNodePairMinerFactoryDis.getInstance();
				} else if (taskCombination.getTaskRange().equals(TaskRange.NodePairRange))
					settings = SingleNodeOrNodePairMinerFactoryDis.getPairInstance();
				break;
			case MiningTypes_WholeNetwork:
				settings = NetworkFactory.getInstance();
				break;
			default:
				break;
		}
		return settings;
	}
}

class NodeTimerTask extends TimerTask{
	MinerResults results;
	IResultsDisplayer displayer;
	private boolean isRunning = false;
	private IsOver isOver;
	private TaskCombination taskCombination;
	public NodeTimerTask(TaskCombination taskCombination, MinerResults results, IResultsDisplayer displayer
			,IsOver isOver) {
		this.taskCombination = taskCombination;
		this.results = results;
		this.displayer = displayer;
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
		//results.setInputData(oriDataItems);
		boolean minePartialCycle = true; // 判断是否挖掘部分周期
		for(TaskElement task:tasks){
			dataItems=oriDataItems;
			if(!task.getAggregateMethod().equals(AggregateMethod.Aggregate_NONE)){
				if(task.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())){
					DataItems items=DataPretreatment.aggregateData(oriDataItems, task.getGranularity(), task.getAggregateMethod(),
							!dataItems.isAllDataIsDouble());
					dataItems = NodeDisapearData(items);

				}else{
					dataItems=DataPretreatment.aggregateData(oriDataItems, task.getGranularity(), task.getAggregateMethod(),
							!dataItems.isAllDataIsDouble());
				}
			}
			
			if(!task.getDiscreteMethod().equals(DiscreteMethod.None)){
				dataItems=DataPretreatment.toDiscreteNumbers(dataItems, task.getDiscreteMethod(), task.getDiscreteDimension(),
						task.getDiscreteEndNodes());
			}
			results.setInputData(dataItems);
			taskCombination.setDataItems(dataItems);
			results.getRetNode().setOriDataItems(dataItems);

			int dimension = task.getDiscreteDimension();
			dimension = Math.max(task.getDiscreteDimension(), dataItems.getDiscretizedDimension());
			IMinerPM pmMethod=null;
			IMinerOM tsaMethod=null;
			switch (task.getMiningMethod()) {
			case MiningMethods_PeriodicityMining:
				PMparam pMparam = ParamsAPI.getInstance().getParamsPeriodMiner().getPmparam();
				if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_averageEntropyPM)){
					pmMethod=new averageEntropyPM(task, dimension,pMparam);//添加参数
				}else if(task.getMiningAlgo().equals(MiningAlgo.MiningAlgo_ERPDistencePM)){
					pmMethod=new ERPDistencePM(pMparam);
				}else{
					throw new RuntimeException("方法不存在！");
				}
				pmMethod.setOriginDataItems(dataItems);
				if(task.getDiscreteMethod().equals(DiscreteMethod.None))
		    		dataItems=DataPretreatment.normalization(dataItems);
				pmMethod.setDataItems(dataItems);
				pmMethod.predictPeriod();
				setPMResults(results, pmMethod);
				break;
			case MiningMethods_OutliesMining:
				if(task.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())){
					tsaMethod = new GaussianOutlierDetection(dataItems);
					results.getRetNode().getRetOM().setIslinkDegree(true);
				}else {
					/*if(task.getMiningObject().equals(MiningObject.MiningObject_Traffic.toString())){
						if(results.getRetNode().getRetStatistics().getMean() < 3500){
							tsaMethod = new MultidimensionalOutlineDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(true);
						}else{
							tsaMethod = new AnormalyDetection(dataItems);
							//tsaMethod = new FastFourierOutliesDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(false);
						}
					}else if(task.getMiningObject().equals(MiningObject.MiningObject_Times.toString())){
						if(results.getRetNode().getRetStatistics().getMean() < 100){
							tsaMethod = new MultidimensionalOutlineDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(true);
						}else{
							tsaMethod = new AnormalyDetection(dataItems);
							//tsaMethod = new FastFourierOutliesDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(false);
						}
					}*/ 
					
					/*else {
						if(results.getRetNode().getRetStatistics().getComplex() < 1.9){
							tsaMethod = new MultidimensionalOutlineDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(true);
						}else{
							tsaMethod = new AnormalyDetection(dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(false);
						}
					}*/
					if(results.getRetNode().getRetPM().getHasPeriod()){
						tsaMethod = new MultidimensionalOutlineDetection(dataItems);
						results.getRetNode().getRetOM().setIslinkDegree(true);
					}else{
						tsaMethod = new AnormalyDetection(dataItems);
						results.getRetNode().getRetOM().setIslinkDegree(false);
					}
				}
				tsaMethod.TimeSeriesAnalysis();
				setOMResults(results, tsaMethod);
				break;
			case MiningMethods_Statistics:

				SeriesStatistics seriesStatistics=new SeriesStatistics(dataItems,
						ParamsAPI.getInstance().getParamsStatistic().getSsp());
				seriesStatistics.statistics();
				setStatisticResults(results,seriesStatistics);
				break;
			case MiningMethods_PredictionMining:
				MinerResultsPM resultsPM = results.getRetNode().getRetPM();
				if (resultsPM.getHasPeriod()) { // 若有周期性
					DataItems predictItems = new DataItems();
					DataItems periodDi = resultsPM.getDistributePeriod();

					Calendar calendar=Calendar.getInstance();
					calendar.setTime(dataItems.getLastTime());
					int len = dataItems.getLength();
					for(int i = 0; i< periodDi.getLength()/2; i++){
						int index = (int) ((i+len) % resultsPM.getPeriod());
						calendar.add(Calendar.SECOND, task.getGranularity());
						predictItems.add1Data(calendar.getTime(), periodDi.getData().get(index)+"");
					}

					results.getRetNode().getRetFM().setPredictItems(predictItems);
				} /*else if (task.getMiningObject().equals("结点出现消失")){	// 无周期的结点出现消失规律应用神经网络挖掘
					NeuralNetwork forecast=new NeuralNetwork(dataItems, task,
							ParamsAPI.getInstance().getParamsPrediction().getNnp());
					System.out.println(task.getTaskName()+" forecast start");
					forecast.TimeSeriesAnalysis();
					System.out.println(task.getTaskName()+" forecast over");
					setForecastResult(results, forecast);
				} */else {
					NeuralNetwork forecast=new NeuralNetwork(dataItems, task,
							ParamsAPI.getInstance().getParamsPrediction().getNnp());
					System.out.println(task.getTaskName()+" forecast start");
					forecast.TimeSeriesAnalysis();
					System.out.println(task.getTaskName()+" forecast over");
					setForecastResult(results, forecast);
					/*ARIMATSA forecast=new ARIMATSA(task, dataItems,
							ParamsAPI.getInstance().getParamsPrediction().getAp());
					forecast.TimeSeriesAnalysis();
					setForecastResult(results, forecast);*/
				}

				break;
			case MiningMethods_SequenceMining:
				
				ParamsSM paramsSM = ParamsAPI.getInstance().getParamsSequencePattern();     //获取参数
				PointSegment segment=new PointSegment(dataItems, paramsSM.getSMparam().getSplitLeastLen());
				DataItems clusterItems=null;
				List<PatternMent> segPatterns=segment.getPatterns();
				
				if(task.getPatternNum()==0){
					clusterItems=WavCluster.SelfCluster(segPatterns,dataItems,
							paramsSM.getSMparam().getClusterNum(),task.getTaskName());
				}else{
					clusterItems=WavCluster.SelfCluster(segPatterns,dataItems,
							task.getPatternNum(),task.getTaskName());
				}
				SequencePatternsDontSplit sequencePattern=new SequencePatternsDontSplit(paramsSM);
				sequencePattern.setDataItems(clusterItems);
				sequencePattern.setTask(task);

				Map<Integer, List<String>>frequentItem=sequencePattern.
						printClusterLabelTOLines(clusterItems, dataItems);
				List<LineElement> lineElements = sequencePattern.getLineElement(frequentItem);
				sequencePattern.patternMining();
				setFrequentResults(results, sequencePattern,frequentItem, lineElements,segPatterns);
				break;
			case MiningMethods_PartialCycle:
				if (results.getRetNode().getRetPM().getHasPeriod()) { // 若有周期性,不挖掘部分周期
					minePartialCycle = false;
				} else {
					/*LocalPeriodDetectionWitnDTW dtw=new LocalPeriodDetectionWitnDTW(dataItems,0.9,0.9,3);
					results.getRetNode().setRetPartialCycle(dtw.getResult());*/
					LocalPeriodMinerERP localPeriodMinerERP = new LocalPeriodMinerERP(dataItems,0.15,300);
					results.getRetNode().setRetPartialCycle(localPeriodMinerERP.getResult());
				}				
				/*if(task.getRange().equals("10.0.7.2"))
				{
					PartialCycle partialCycle = new PartialCycle(results);
					partialCycle.setDataItems(dataItems);
					partialCycle.run();
				}*/
				break;
			default:
				break;
			}
		}
		isRunning = false;
		isOver.setIsover(true);
		System.out.println(taskCombination.getName()+" over");
		if (displayer != null)
			displayer.displayMinerResults(results);
		/* 挖掘完成，保存结果文件 */
		MinerFactorySettings settings = NetworkMinerNode.getMinerFactorySettings(taskCombination);
		MiningResultsFile newResultsFile = new MiningResultsFile(MiningObject.fromString(taskCombination.getMiningObject()));
		newResultsFile.result2File(settings, taskCombination, results.getRetNode());

		TaskProgress taskProgress = TaskProgress.getInstance();
		taskProgress.increaseComplete();
	}
	
	private void setPMResults(MinerResults results,IMinerPM pmMethod){
		results.getRetNode().getRetPM().setHasPeriod(pmMethod.hasPeriod());
		results.getRetNode().getRetPM().setPeriod(pmMethod.getPredictPeriod());
		results.getRetNode().getRetPM().setDistributePeriod(pmMethod.getItemsInPeriod());
		results.getRetNode().getRetPM().setMinDistributePeriod(pmMethod.getMinItemsInPeriod());
		results.getRetNode().getRetPM().setMaxDistributePeriod(pmMethod.getMaxItemsInPeriod());
		results.getRetNode().getRetPM().setFeatureValue(pmMethod.getMinEntropy());
		results.getRetNode().getRetPM().setFeatureValues(pmMethod.getEntropies());
		results.getRetNode().getRetPM().setFirstPossiblePeriod(pmMethod.getFirstPossiblePeriod());//找出第一个呈现周期性的周期
		results.getRetNode().getRetPM().setConfidence(pmMethod.getConfidence());
	}
	
	private void setOMResults(MinerResults results,IMinerOM tsaMethod){
		results.getRetNode().getRetOM().setOutlies(tsaMethod.getOutlies());    //查找异常
		results.getRetNode().getRetOM().setOutDegree(tsaMethod.getOutDegree());
		results.getRetNode().getRetOM().setOutlinesSet(tsaMethod.getOutlinesSet());
		if(tsaMethod.getOutlies()!=null){
			DataItems outlies=tsaMethod.getOutlies();
			int outliesLen=outlies.getLength();
			int itemLen=taskCombination.getDataItems().getLength();
			if(Math.abs(outliesLen-itemLen)<=1){
				int confidence=0;
				for(String item:outlies.getData()){
					if(Double.parseDouble(item)>=6){
						results.getRetNode().getRetOM().setHasOutlies(true);
						confidence++;
					}
				}
				if(confidence!=0)
					results.getRetNode().getRetOM().setConfidence(confidence);
			}else{
				if(outlies.getLength()>0){
					results.getRetNode().getRetOM().setHasOutlies(true);
					results.getRetNode().getRetOM().setConfidence(outlies.getLength());
				}
			}
		}
		System.out.println(results.getRetNode().getRetOM().isIslinkDegree());
	}
	
	private void setStatisticResults(MinerResults results,SeriesStatistics statistics){
		results.getRetNode().getRetStatistics().setMean(statistics.getMean());
		results.getRetNode().getRetStatistics().setStd(statistics.getStd());
		results.getRetNode().getRetStatistics().setComplex(statistics.getComplex());
		results.getRetNode().getRetStatistics().setSampleENtropy(statistics.getSampleEntropy());
	}
	
	private void setFrequentResults(MinerResults results,SequencePatternsDontSplit sequencePattern,
			Map<Integer, List<String>>frequentItem, List<LineElement> lineElements,List<PatternMent> segPatterns){		
		List<ArrayList<String>> patterns=sequencePattern.getPatterns();
		results.getRetNode().getRetSM().setSegPatterns(segPatterns);
		if(sequencePattern.isHasFreItems()){
			results.getRetNode().getRetSM().setPatters(patterns);
			results.getRetNode().getRetSM().setHasFreItems(true);
			results.getRetNode().getRetSM().setFrequentItem(frequentItem);
			results.getRetNode().getRetSM().setLineElements(lineElements);
			System.out.println(taskCombination.getName()+" has freitems");
		}else{
			results.getRetNode().getRetSM().setHasFreItems(false);
		}
	}
	private void setForecastResult(MinerResults result,IMinerFM fm){
		result.getRetNode().getRetFM().setPredictItems(
				fm.getPredictItems());
	}
	private DataItems NodeDisapearData(DataItems items){
		DataItems di = new DataItems();
		for(int i=0;i<items.getLength();i++){
			DataItem it = items.getElementAt(i);
			Date time = it.getTime();
			double data = Double.parseDouble(it.getData());
			data = data>0 ? 1 : 0;
			di.add1Data(time, String.valueOf(data));
		}
		return di;
	}
}

