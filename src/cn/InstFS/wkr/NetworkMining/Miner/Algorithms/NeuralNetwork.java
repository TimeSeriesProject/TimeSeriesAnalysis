package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import WaveletUtil.generateFeatures;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsFA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

@SuppressWarnings("deprecation")
public class NeuralNetwork implements IMinerFM{
	private String inputFilePath;
	private DataItems predictItems;

	private Date originDataEndTime;
	private TaskElement task;
	//参数列表
	private int predictPeriod;	//预测周期
	double momentum; //当更新weights时设置的动量 
	double learnRate; //学习速率
	int seed;	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
	int trianTime;	//训练的迭代次数。

	
	
/*	public NeuralNetwork(String inputFilePath,int predictPeriod,Date originDataEndTime,TaskElement task){
		this.predictPeriod=predictPeriod;
		this.inputFilePath=inputFilePath;
		this.task=task;
		this.originDataEndTime=originDataEndTime;
	}*/
	
	public NeuralNetwork(String inputFilePath,Date originDataEndTime,TaskElement task,ParamsFA p){
		this.inputFilePath=inputFilePath;
		this.task=task;
		this.originDataEndTime=originDataEndTime;
		//初始化参数
		 this.predictPeriod=p.getPredictPeriod();	//预测周期
		 this.momentum=p.getMomentum(); //当更新weights时设置的动量 
		 this.learnRate=p.getLearnRate(); //学习速率
		 this.seed=p.getSeed();	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
		 this.trianTime=p.getTrianTime();	//训练的迭代次数。

	}
	
	public NeuralNetwork(){}
	
	public void TimeSeriesAnalysis(){
		try {
			File inputFile=new File(inputFilePath);
			generateFeatures features=new generateFeatures(inputFilePath,inputFile.getName().split("\\.")[0],task.getMiningObject());
			features.generateItems();
			MultilayerPerceptron classifier=new MultilayerPerceptron();
			File trainFile=new File(features.getTrainOutputFilePath());
			File testFile=new File(features.getTestOutputFilePath());
			classifier.setAutoBuild(true);
			classifier.setGUI(false);
			CSVLoader loader=new CSVLoader();
			loader.setFile(trainFile);
			Instances trainInstances=loader.getDataSet();
			trainInstances.setClassIndex(trainInstances.numAttributes()-1);
			int attrNum=trainInstances.numAttributes();
			int trianInstancesNum=trainInstances.numInstances();
			loader.setFile(testFile);
			Instances testInstances=loader.getDataSet();
			testInstances.setClassIndex(testInstances.numAttributes()-1);
			int testInstanceNum=testInstances.numInstances();
/*			double momentum=0.1;
			double learnRate=0.2;
			int seed=0;
			int trianTime=500;
			*/
			double minDistance=Double.MAX_VALUE;
			double distance=0.0;
			for(double learnRateIndex=learnRate;learnRateIndex<=0.4;learnRateIndex+=0.1){
				for(double momentumIndex=momentum;momentumIndex<=0.3;momentumIndex+=0.1){
					for(int seedIndex=seed;seedIndex<=1;seedIndex++){
						for(int timeIndex=trianTime;timeIndex<=1000;timeIndex+=500){
							classifier.setHiddenLayers("a");
							classifier.setLearningRate(learnRateIndex);
							classifier.setMomentum(momentumIndex);
							classifier.setSeed(seedIndex);
							classifier.setTrainingTime(timeIndex);
							classifier.buildClassifier(trainInstances);
							for(int i=0;i<testInstanceNum;i++){
								double forecastValue=classifier.classifyInstance(testInstances.get(i));
								double originValue=testInstances.instance(i).classValue();
								distance+=Math.abs(forecastValue-originValue);
							}
							System.out.println(" distance "+distance);
							if(distance<minDistance){
								minDistance=distance;
								seed=seedIndex;
								momentum=momentumIndex;
								learnRate=learnRateIndex;
								trianTime=timeIndex;
							}
							distance=0;
						}
					}
				}
			}
			System.out.println("min distance "+minDistance);
			classifier.setHiddenLayers("a");
			classifier.setLearningRate(learnRate);
			classifier.setMomentum(momentum);
			classifier.setSeed(seed);
			classifier.setTrainingTime(trianTime);
			classifier.buildClassifier(trainInstances);
			
			int[] autoCorrelationIndex=features.getAutoCorrelation();
			int lastIndex=autoCorrelationIndex[autoCorrelationIndex.length-1];
			double[] items = new double[trianInstancesNum+testInstanceNum+predictPeriod+lastIndex];
			List<Date> time=new ArrayList<Date>();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(originDataEndTime);
			for(int i=0;i<(trianInstancesNum+testInstanceNum+lastIndex);i++){
				items[i]=features.getItems()[i];
			}
			
			
			Attribute[] attributes=new Attribute[attrNum];
			Instances instances=initializeAttribute(attributes);
			
			for(int i=(trianInstancesNum+testInstanceNum+lastIndex);i<(trianInstancesNum+testInstanceNum+lastIndex+predictPeriod);i++){
				double[] values=new double[instances.numAttributes()];
				for(int j=0;j<attrNum-1;j++){
					double value=items[i-autoCorrelationIndex[j]];
					values[j]=value;
				}
				calendar.add(Calendar.MILLISECOND, task.getGranularity()*1000);
				time.add(calendar.getTime());
				values[instances.numAttributes()-1]=0;
				Instance inst=new DenseInstance(1.0, values);
				instances.add(inst);
				double forecastValue=classifier.classifyInstance(instances.get(0));
				System.out.print((int)(forecastValue)+",");
				items[i]=forecastValue;
				instances.delete(0);
			}
			System.out.println(trianInstancesNum+testInstanceNum+lastIndex);
			List<String> data=new ArrayList<String>();
			for(int i=(trianInstancesNum+testInstanceNum);i<(trianInstancesNum+testInstanceNum+predictPeriod);i++){
				data.add(items[i]+"");
			}
			DataItems dataItems=new DataItems();
			dataItems.setData(data);
			dataItems.setTime(time);
			setPredictItems(dataItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private Instances initializeAttribute(Attribute[] attributes){
		for(int i=0;i<attributes.length-1;i++){
			String name="attr"+i;
			attributes[i]=new Attribute(name);
		}
		attributes[attributes.length-1]=new Attribute("value");
		FastVector<Attribute> attributesVector=new FastVector<Attribute>();
		for(Attribute attribute:attributes){
			attributesVector.addElement(attribute);
		}
		Instances instances=new Instances("forecastDataset", attributesVector, 0);
		instances.setClassIndex(instances.numAttributes()-1);
		return instances;
	}
	
	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	
	public DataItems getPredictItems() {
		return predictItems;
	}

	public void setPredictItems(DataItems predictItems) {
		this.predictItems = predictItems;
	}

	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}
}
