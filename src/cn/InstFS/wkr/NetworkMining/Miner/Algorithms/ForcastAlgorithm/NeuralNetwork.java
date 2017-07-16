package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import WaveletUtil.generateFeatures;
import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.NeuralNetworkParams;
import org.rosuda.REngine.Rserve.RserveException;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerFM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

/**
 * 利用BP神经网络预测,利用自相关系数确定输入窗口大小k,利用前k个数据点预测未来数据
 *
 */
@SuppressWarnings("deprecation")
public class NeuralNetwork implements IMinerFM{
	/**
	 * 待预测序列
	 */
	private DataItems dataItems;
	/**
	 * 预测结果
	 */
	private DataItems predictItems;
	/**
	 *  原始数据结束时间
	 */
	private Date originDataEndTime;
	private TaskElement task;
	//参数列表
	/**
	 *  预测周期
	 */
	private int predictPeriod;	//
	/**
	 *  当更新weights时设置的动量 
	 */
	double momentum; //
	/**
	 *  学习速率
	 */
	double learnRate; //
	/**
	 *  Seed用于初始化随机数的生成。
	 */
	int seed;	//随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
	/**
	 *  训练的迭代次数
	 */
	int trianTime;	//
	
	public NeuralNetwork(DataItems dataItems,TaskElement task,NeuralNetworkParams p){
		this.dataItems=dataItems;
		this.task=task;
		this.originDataEndTime=dataItems.getLastTime();
		//初始化参数
		 this.predictPeriod=p.getPredictPeriod();	//预测周期
		 this.momentum=p.getMomentum(); //当更新weights时设置的动量 
		 this.learnRate=p.getLearnRate(); //学习速率
		 this.seed=p.getSeed();	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
		 this.trianTime=p.getTrianTime();	//训练的迭代次数。

	}
	
	public NeuralNetwork(){}
	
	/**
	 *  神经网络预测
	 *  1）	计算长度为N的时间序列S自相关系数，获取自相关数大于阈值（0.3:不可改动）的最大阶k
	 *  2）	初始化一个BP神经网络，输入层神经元个数设为k，初始化权值w和阈值θ为较小的随机值
	 *	3）	生成N-k个训练样本，每个样本预测值为S[i](i>k)，属性值为{S[i-1],S[i-2],…,S[i-k]}
	 *	4）	取样本对，分别计算隐含层和输出层各神经元的输出值
	 *	5）	计算输出层的输出值和样本预测值的误差，当误差大于阈值时，调准各层神经元的w和θ值
	 *	6）	返回步骤4重复计算，直至误差符合要求为止或训练次数超过设定值为止（误差：曼哈顿距离）
	 *	7）	将训练后的BP神经网络预测序列未来值
	 */
	public void TimeSeriesAnalysis(){
		try {
			generateFeatures features=new generateFeatures(dataItems,task.getName());
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
			double minDistance=Double.MAX_VALUE;
			double distance=0.0;
			for(double learnRateIndex=learnRate;learnRateIndex<=0.3;learnRateIndex+=0.1){
				for(double momentumIndex=momentum;momentumIndex<=0.2;momentumIndex+=0.1){
					for(int seedIndex=seed;seedIndex<=1;seedIndex++){
						classifier.setHiddenLayers("a");
						classifier.setLearningRate(learnRateIndex);
						classifier.setMomentum(momentumIndex);
						classifier.setSeed(seedIndex);
						classifier.setTrainingTime(800);
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
							trianTime=1000;
						}
						distance=0;
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
			for(int i=(trianInstancesNum+testInstanceNum+lastIndex);i<(trianInstancesNum+testInstanceNum+lastIndex+predictPeriod);i++){
				data.add(items[i]+"");
			}
			DataItems dataItems=new DataItems();
			dataItems.setData(data);
			dataItems.setTime(time);
			setPredictItems(dataItems);
		} catch (RserveException e) {
			throw new RuntimeException(e);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 属性的初始化，生成Instances对象用于存储数据
	 * @param attributes 数据存储属性，用于instances属性初始化
	 * @return instances 数据存储对象
	 */
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
	
	public DataItems getPredictItems() {
		return predictItems;
	}

	public void setPredictItems(DataItems predictItems) {
		this.predictItems = predictItems;
		System.out.println(predictItems.data);
	}

	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}
}
