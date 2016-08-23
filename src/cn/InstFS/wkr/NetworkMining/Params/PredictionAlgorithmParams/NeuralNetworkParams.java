package cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams;

import java.io.Serializable;
import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import org.jdom.Element;

public class NeuralNetworkParams {
	//神经网络参数列表
	//参数列表
	private int predictPeriod;	//预测周期
	private double momentum; //当更新weights时设置的动量 
	private double learnRate; //学习速率
	private int seed;	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
	private int trianTime;	//训练的迭代次数。
	
	public NeuralNetworkParams(){
		//参数列表
		 predictPeriod=20;	//预测周期
		 momentum=0.1; //当更新weights时设置的动量 
		 learnRate=0.2; //学习速率
		 seed=0;	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
		 trianTime=500;	//训练的迭代次数。
	}

	public NeuralNetworkParams(Element paramsConfig) {
		String param = "";
		param = paramsConfig.getChildText("predictPeriod");
		if(param != null){
			predictPeriod = Integer.parseInt(param);
		}
		param = paramsConfig.getChildText("momentum");
		if(param != null){
			momentum = Double.parseDouble(param);
		}
		param = paramsConfig.getChildText("learnRate");
		if(param != null){
			learnRate = Double.parseDouble(param);
		}
		param = paramsConfig.getChildText("seed");
		if(param != null){
			seed = Integer.parseInt(param);
		}
		param = paramsConfig.getChildText("trianTime");
		if(param != null){
			trianTime = Integer.parseInt(param);
		}
	}

	public int getPredictPeriod() {
		return predictPeriod;
	}
	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}
	public double getMomentum() {
		return momentum;
	}
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
	public double getLearnRate() {
		return learnRate;
	}
	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}
	public int getTrianTime() {
		return trianTime;
	}
	public void setTrianTime(int trianTime) {
		this.trianTime = trianTime;
	}


}
