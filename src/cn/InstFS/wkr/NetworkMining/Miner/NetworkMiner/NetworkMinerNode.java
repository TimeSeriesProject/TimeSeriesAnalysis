package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import common.ErrorLogger;
import common.ErrorTrace;
import common.Logger;
import lineAssociation.ClusterWrapper;
import lineAssociation.DPCluster;
import lineAssociation.Linear;
import lineAssociation.SymbolNode;
import PropertyTest.OutlinesTest.OutliersTest;
import PropertyTest.PredictTest.PredictTest;
import WaveletUtil.PointPatternDetection;
import WaveletUtil.SAXPartternDetection;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.DataInputs.LinePattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.PatternMent;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.TranDPCluster;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.*;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm.ARIMATSA;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm.NeuralNetwork;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm.SMForecast;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.FrequentAlgorithm.SequencePatternsDontSplit;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.AnomalyGaussVerification;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.AnormalyDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.FastFourierOutliesDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.GaussianOutlierDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.MultidimensionalOutlineDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm.PeriodBasedOutlierDetection;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriod;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodDetectionWitnDTW;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.LocalPeriodMinerERP;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.PartialCycle;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm.GetPositionAndMinerPaticalPeriod;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm.Pair;
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
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMFastFourierParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMGaussianNodeParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMGuassianParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMMultidimensionalParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMPiontPatternParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMperiodBasedParams;
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
		Logger.log("启动TaskCombination"+taskCombination.getName()+"挖掘");
		MinerFactorySettings settings = getMinerFactorySettings(taskCombination);
		MiningResultsFile resultsFile = new MiningResultsFile(MiningObject.fromString(taskCombination.getMiningObject()));
		if(resultsFile.hasFile(settings, taskCombination)) { // 已有挖掘结果存储，则不重新启动miner
			Logger.log("从原存储的挖掘结果中恢复");
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
			ErrorLogger.log("Error-----------------------------------------------------------");
			ErrorLogger.log("TaskCombination "+ taskCombination.getName() +"任务挖掘出错");
			ErrorLogger.log("异常信息", ErrorTrace.getTrace(e));
			ErrorLogger.log("----------------------------------------------------------------");
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
		/*ErrorLogger.log(taskCombination.getRange()+","+taskCombination.getMiningObject()+","+taskCombination.getMinerType(),"调用PMDetect");
		ErrorLogger.log("\t tasks",taskCombination.getTasks().toString());*/
		PMDetect(taskCombination.getDataItems(),taskCombination.getTasks());
	}
	
	private void PMDetect(DataItems dataItems,List<TaskElement>tasks){

		
		DataItems clusterItems=null;
		DataItems oriDataItems=dataItems;
		SequencePatternsDontSplit sequencePattern=null;
		//results.setInputData(oriDataItems);
		boolean minePartialCycle = true; // 判断是否挖掘部分周期
		boolean minePartialPeriod = true; // 判断是否挖掘部分周期
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
			IMinerFM forecastMethod = null;
			switch (task.getMiningMethod()) {
			case MiningMethods_PeriodicityMining:
				PMparam pMparam = ParamsAPI.getInstance().getParamsPeriodMiner().getPmparam();
				if (task.getMiningAlgo()!= null ) { // 不为空时，自定义算法，否则自动选择
					switch (task.getMiningAlgo()) {
						case MiningAlgo_averageEntropyPM:
							pmMethod = new averageEntropyPM(task, dimension,pMparam);//添加参数
							break;
						case MiningAlgo_ERPDistencePM:
							pmMethod=new ERPDistencePM(pMparam);
							break;
						default:
							throw new RuntimeException("方法不存在！");
					}
				} else { // 周期检测默认ERP
					pmMethod = new ERPDistencePM(pMparam);
				}

				pmMethod.setOriginDataItems(dataItems);
				if(task.getDiscreteMethod().equals(DiscreteMethod.None))
		    		dataItems=DataPretreatment.normalization(dataItems);
				pmMethod.setDataItems(dataItems);
				pmMethod.predictPeriod();
				setPMResults(results, pmMethod);
				break;
			case MiningMethods_OutliesMining:
				String retPath = new String();//存储测试结果的文件路径
				if (task.getMiningAlgo()!= null ) { // 不为空时，自定义算法，否则自动选择
					switch (task.getMiningAlgo()) {
						case MiningAlgo_GaussDetection:
							OMGuassianParams omGuassianParams = ParamsAPI.getInstance().getPom().getOmGuassianParams();
							tsaMethod = new AnormalyDetection(omGuassianParams,dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(false);
							break;
						case MiningAlgo_FastFourier:
							OMFastFourierParams omFourierParams = ParamsAPI.getInstance().getPom().getOmFastFourierParams();
							tsaMethod=new FastFourierOutliesDetection(omFourierParams,dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(false);
							break;
						case MiningAlgo_Muitidimensional:
							OMMultidimensionalParams omMultiParams = ParamsAPI.getInstance().getPom().getOmMultidimensionalParams();
							tsaMethod = new MultidimensionalOutlineDetection(omMultiParams,dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(true);
							break;
						case MiningAlgo_NodeOutlierDetection:
							OMGaussianNodeParams omGaussianNodeParams = ParamsAPI.getInstance().getPom().getOmGaussianNodeParams();
							tsaMethod = new GaussianOutlierDetection(omGaussianNodeParams,dataItems);
							results.getRetNode().getRetOM().setIslinkDegree(true);
							break;
						default:
							throw new RuntimeException("方法不存在！");
					}
					tsaMethod.TimeSeriesAnalysis();
					setOMResults(results, tsaMethod);
				} else { // 异常检测默认
					if(task.getMiningObject().equals(MiningObject.MiningObject_NodeDisapearEmerge.toString())){
						OMGaussianNodeParams omGaussianNodeParams = ParamsAPI.getInstance().getPom().getOmGaussianNodeParams();
						tsaMethod = new GaussianOutlierDetection(omGaussianNodeParams,dataItems);
						results.getRetNode().getRetOM().setIslinkDegree(true);
						/*tsaMethod.TimeSeriesAnalysis();
						setOMResults(results, tsaMethod);*/
					}else {
						if(results.getRetNode().getRetPM().getHasPeriod()){
							OMperiodBasedParams omPeriodParams = ParamsAPI.getInstance().getPom().getOMperiodBasedParams();
							tsaMethod = new PeriodBasedOutlierDetection(omPeriodParams,dataItems, results.getRetNode().getRetPM());
							results.getRetNode().getRetOM().setIslinkDegree(false);
							retPath = "testResult/outlierTest(基于周期的异常检测).txt";
						}
						else if(results.getRetNode().getRetPartialPeriod().isHasPartialPeriod() || results.getRetNode().getRetPartialCycle().isHasPartialCycle()){//部分周期


//							OMFastFourierParams omFourierParams = ParamsAPI.getInstance().getPom().getOmFastFourierParams();
//							tsaMethod=new FastFourierOutliesDetection(omFourierParams,dataItems);
							
							OMGuassianParams omgParams = ParamsAPI.getInstance().getPom().getOmGuassianParams();
							tsaMethod = new AnormalyDetection(omgParams,dataItems);							
							results.getRetNode().getRetOM().setIslinkDegree(false);
							retPath = "testResult/outlierTest(点异常).txt";
						}					
						else{
							OMPiontPatternParams omPointParams = ParamsAPI.getInstance().getPom().getOmPiontPatternParams();
							tsaMethod = new PointPatternDetection(omPointParams,dataItems);						
							results.getRetNode().getRetOM().setIslinkDegree(true);
							retPath = "testResult/outlierTest(线段异常).txt";
						}

					}
					
				}				
				tsaMethod.TimeSeriesAnalysis();
				setOMResults(results, tsaMethod);
				/*************************异常算法测试结果输出**********************/																		
				OutliersTest outliersTest = new OutliersTest(results.getRetNode().getRetOM(), task.getRange(),dataItems.getTime().get(0));
				if(results.getRetNode().getRetOM().isIslinkDegree()){
					outliersTest.evaluatIndicator2();
				}else{
					outliersTest.evaluatIndicator();
				}				
				outliersTest.appendWriteRet(retPath, task.getRange(), outliersTest.getPrecision(), outliersTest.getRecall());
				System.out.println("任务"+task.getRange()+"异常检测结果--准确率:"+outliersTest.getPrecision()+",召回率:"+outliersTest.getRecall());
				/*****************************测试结束************************/											
												
				break;
			case MiningMethods_Statistics:

				SeriesStatistics seriesStatistics=new SeriesStatistics(dataItems,
						ParamsAPI.getInstance().getParamsStatistic().getSsp());
				seriesStatistics.statistics();
				setStatisticResults(results,seriesStatistics);
				break;
			case MiningMethods_PredictionMining:
				int testsize = 10;//测试数据的长度
				MinerResultsPM resultsPM = results.getRetNode().getRetPM();
				PredictTest predicttest = new PredictTest(testsize);
				DataItems tDataItems = predicttest.getTestpredictData(dataItems);//获得除去后10个数据的实际原始数据段
				List<String> realtestData = predicttest.getTestRealData(dataItems);//获得原始数据的最后10个数据
				if (task.getMiningAlgo() != null) {
					switch (task.getMiningAlgo()) {
						case MiningAlgo_NeuralNetworkTSA:
							testsize = ParamsAPI.getInstance().getParamsPrediction().getNnp().getPredictPeriod();
							predicttest = new PredictTest(testsize);
							tDataItems = predicttest.getTestpredictData(dataItems);
							realtestData = predicttest.getTestRealData(dataItems);

							forecastMethod = new NeuralNetwork(tDataItems, task,
									ParamsAPI.getInstance().getParamsPrediction().getNnp());
							break;
						case MiningAlgo_ARIMATSA:
							testsize = ParamsAPI.getInstance().getParamsPrediction().getAp().getPredictPeriod();
							predicttest = new PredictTest(testsize);
							tDataItems = predicttest.getTestpredictData(dataItems);
							realtestData = predicttest.getTestRealData(dataItems);

							forecastMethod = new ARIMATSA(task, tDataItems,
									ParamsAPI.getInstance().getParamsPrediction().getAp());

								/*...................................测试开始.............................................*/

//							predicttest.resultWrite(forecastMethod.getPredictItems().getData(), realtestData, task.getRange(), task.getProtocol(), task.getMiningObject(), resultsPM.getHasPeriod());


								/*...................................测试结束.............................................*/

							break;
						default:
							throw new RuntimeException("方法不存在！");
					}
					System.out.println(task.getTaskName() + " forecast start");
					forecastMethod.TimeSeriesAnalysis();
					System.out.println(task.getTaskName() + " forecast over");

					 /*...................................测试开始.............................................*/

					predicttest.resultWrite(forecastMethod.getPredictItems().getData(), realtestData, task.getRange(), task.getProtocol(), task.getMiningObject(), resultsPM.getHasPeriod());

								/*...................................测试结束.............................................*/
					setForecastResult(results, forecastMethod);
				} else {
					//MinerResultsPM resultsPM = results.getRetNode().getRetPM();
					if (resultsPM.getHasPeriod()) { // 若有周期性
						DataItems predictItems = new DataItems();
						DataItems periodDi = resultsPM.getDistributePeriod();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(tDataItems.getLastTime());
						int len = tDataItems.getLength();
						//for(int i = 0; i< periodDi.getLength()/2; i++){//
						for (int i = 0; i < testsize; i++) {
							int index = (int) ((i + len) % resultsPM.getPeriod());
							calendar.add(Calendar.SECOND, task.getGranularity());
							predictItems.add1Data(calendar.getTime(), periodDi.getData().get(index));
						}

							/*...................................测试开始.............................................*/

						predicttest.resultWrite(predictItems.getData(), realtestData, task.getRange(), task.getProtocol(), task.getMiningObject(), resultsPM.getHasPeriod());


							/*...................................测试结束.............................................*/

						results.getRetNode().getRetFM().setPredictItems(predictItems);
					} else {
						SMForecast forecast = new SMForecast(clusterItems, sequencePattern.getPatterns(),
								sequencePattern.getPatternsSupDegree(), WavCluster.clusterCentroids,
								task, tDataItems);
						System.out.println(task.getTaskName() + " forecast start");
						forecast.TimeSeriesAnalysis();
						System.out.println(task.getTaskName() + " forecast over");
						if (forecast.getPredictItems() == null || forecast.getPredictItems().getLength() <= 0) {
							NeuralNetwork workforecast = new NeuralNetwork(tDataItems, task,
									ParamsAPI.getInstance().getParamsPrediction().getNnp());
							System.out.println(task.getTaskName() + " forecast start");
							workforecast.TimeSeriesAnalysis();
								/*....................................测试任务开始...............................................*/

							predicttest.resultWrite(workforecast.getPredictItems().getData(), realtestData, task.getRange(), task.getProtocol(), task.getMiningObject(), resultsPM.getHasPeriod());


								/*..............................测试结束....................................*/

							System.out.println(task.getTaskName());
							setForecastResult(results, workforecast);


						} else {
							setForecastResult(results, forecast);
						}
					}
				}

				break;
			case MiningMethods_SequenceMining:
				
				ParamsSM paramsSM = ParamsAPI.getInstance().getParamsSequencePattern();     //获取参数
				PointSegment segment=new PointSegment(dataItems, paramsSM.getSMparam().getSplitLeastLen());

//				PointSegment segment=new PointSegment(dataItems, 3);
//				LinePattern segment = new LinePattern(dataItems, 0.25);

//				PointSegment segment=new PointSegment(dataItems, 4);
//				LinePattern segment = new LinePattern(dataItems, 0.06);

				List<PatternMent> segPatterns=segment.getPatterns();
				
				//kmeans聚类
		        if(task.getPatternNum()==0){
					clusterItems=WavCluster.SelfCluster(segPatterns,dataItems,
							paramsSM.getSMparam().getClusterNum(),task.getTaskName());
				}else{
					clusterItems=WavCluster.SelfCluster(segPatterns,dataItems,
							task.getPatternNum(),task.getTaskName());
				}
		        
		        //DPC聚类
		       /*AssociationRuleLineParams arp = ParamsAPI.getInstance().getAssociationRuleParams().getAssociationRuleLineParams();
				TranDPCluster dpCluster = new TranDPCluster(dataItems, segPatterns, arp);
				clusterItems = dpCluster.getClusterItems();*/
				
				sequencePattern=new SequencePatternsDontSplit(paramsSM);
				sequencePattern.setDataItems(clusterItems);
				sequencePattern.setTask(task);

				Map<Integer, List<String>>frequentItem=sequencePattern.
						printClusterLabelTOLines(clusterItems, dataItems);
				List<LineElement> lineElements = sequencePattern.getLineElement(frequentItem);
				sequencePattern.patternMining();
				setFrequentResults(results, sequencePattern,frequentItem, lineElements,segPatterns);
//				setFrequentResults(results, sequencePattern,frequentItem, lineElements,segPatterns,dpCluster.GAMMA);
				break;
			case MiningMethods_PartialCycle:
				if (results.getRetNode().getRetPM().getHasPeriod()) { // 若有周期性,不挖掘局部周期
					minePartialPeriod = false;
				} else {
					/*LocalPeriodDetectionWitnDTW dtw=new LocalPeriodDetectionWitnDTW(dataItems,0.9,0.9,3);
					results.getRetNode().setRetPartialCycle(dtw.getResult());*/
//					LocalPeriodMinerERP localPeriodMinerERP = new LocalPeriodMinerERP(dataItems,0.15,300);
					LocalPeriod localPeriod = new LocalPeriod(dataItems,0.2,300);
//					results.getRetNode().setRetPartialCycle(localPeriodMinerERP.getResult());
					results.getRetNode().setRetPartialCycle(localPeriod.getResult());
				}				
				/*if(task.getRange().equals("10.0.7.2"))
				{
					PartialCycle partialCycle = new PartialCycle(results);
					partialCycle.setDataItems(dataItems);
					partialCycle.run();
				}*/
				break;
				
			case MiningMethods_PartialPeriod:
				System.out.println("开始进入部分周期挖掘");
				long start = System.currentTimeMillis();
				if (results.getRetNode().getRetPartialCycle().isHasPartialCycle()||results.getRetNode().getRetPM().getHasPeriod()) { // 若有周期性或局部周期,不挖掘部分周期
					minePartialCycle = false;
				} else {
					//没有周期和局部周期，才挖掘部分周期			
					ParamsSM paramsSM2 = ParamsAPI.getInstance().getParamsSequencePattern();     //获取参数
					PointSegment segment2=new PointSegment(dataItems, paramsSM2.getSMparam().getSplitLeastLen());
//					PointSegment segment2=new PointSegment(dataItems, 4);
//					LinePattern segment = new LinePattern(dataItems, 0.06);
					List<PatternMent> segPatterns2=segment2.getPatterns();

					//kmeans聚类
					if(task.getPatternNum()==0){
						clusterItems=WavCluster.SelfCluster(segPatterns2,dataItems,
								paramsSM2.getSMparam().getClusterNum(),task.getTaskName());
					}else{
						clusterItems=WavCluster.SelfCluster(segPatterns2,dataItems,
								task.getPatternNum(),task.getTaskName());
					}

					 //DPC聚类
			        /*AssociationRuleLineParams arp2 = ParamsAPI.getInstance().getAssociationRuleParams().getAssociationRuleLineParams();
					TranDPCluster dpCluster2 = new TranDPCluster(dataItems, segPatterns2, arp2,true);
					clusterItems = dpCluster2.getClusterItems();*/
					
					sequencePattern=new SequencePatternsDontSplit(paramsSM2);
					sequencePattern.setDataItems(clusterItems);
					sequencePattern.setTask(task);

					Map<Integer, List<String>>frequentItem2=sequencePattern.
							printClusterLabelTOLines(clusterItems, dataItems);
					List<LineElement> lineElements2 = sequencePattern.getLineElement(frequentItem2);
					sequencePattern.patternMining();

					//setFrequentResults(results, sequencePattern,frequentItem2, lineElements2,segPatterns2,dpCluster2.GAMMA);
					List<ArrayList<String>> patterns=sequencePattern.getPatterns();
					long mid = System.currentTimeMillis();
					GetPositionAndMinerPaticalPeriod gpampp=new GetPositionAndMinerPaticalPeriod(patterns,lineElements2,0.15);
					//写入结果
					//gpampp.setPaticalPeriodResult(results);
					if(gpampp.getResult()==null){
						System.out.println("部分周期挖掘结果为空");
					}
					results.getRetNode().setRetPartialPeriod(gpampp.getResult());
					
					
					// ************************部分周期测试************************"
					long end = System.currentTimeMillis();
					System.out
							.println("************************部分周期测试************************");
					File testFile = new File(
							"testResult/部分周期测试.txt");
					try {
						//FileOutputStream in = new FileOutputStream(testFile);
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(testFile, true)));
								
						try {
							out.write("************************部分周期测试************************\t\r");
							float pointcount=results.getInputData().getLength();
							out.write("消耗时间：" + (end - start)+" 平均消耗时间/每1000个点 ："+((end-start)/pointcount)*1000+"\t\r");
							Map<String, Double> testError = gpampp
									.getTestErrorResult();
							if (testError != null) {
								double modelErrorSum=0;//记录每个model的误差和
								for (Map.Entry<String, Double> entry : testError
										.entrySet()) {
									modelErrorSum=modelErrorSum+entry.getValue();
									out.write("模式：" + entry.getKey()+ "    " +"周期："+gpampp.getResult().periodResult.get(entry.getKey())
											+ "    " +"点数："+results.getInputData().getLength()+ "    " + "测试误差:"
											+ entry.getValue()+"\t\r");
									
									
									//out.write("起始位置：");
								/*	System.out.println("模式：" + entry.getKey()
											+ "    " + "测试误差:"
											+ entry.getValue());*/
								}
								out.write("总误差："+modelErrorSum/testError.size() + "\n");
							}
							out.close();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("消耗时间：" + (end - start));
						// System.out.println("消耗时间："+(end-start));

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
					
				}
				System.out.println("部分周期挖掘结束");
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
		Logger.log("TaskCombination"+ taskCombination.getName() + "挖掘完成");
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
		/*if(tsaMethod.getOutlies()!=null){
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
		}*/
		if(tsaMethod.getOutlies().getData().size()>0){
			results.getRetNode().getRetOM().setHasOutlies(true);
		}else{
			results.getRetNode().getRetOM().setHasOutlies(false);
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
	private void setFrequentResults(MinerResults results,SequencePatternsDontSplit sequencePattern,
			Map<Integer, List<String>>frequentItem, List<LineElement> lineElements,List<PatternMent> segPatterns,TreeMap<Integer,Double> GAMMA){		
		List<ArrayList<String>> patterns=sequencePattern.getPatterns();
		results.getRetNode().getRetSM().setSegPatterns(segPatterns);
		results.getRetNode().getRetSM().setGAMMA(GAMMA);
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

