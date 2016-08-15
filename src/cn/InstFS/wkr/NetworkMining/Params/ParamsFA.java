package cn.InstFS.wkr.NetworkMining.Params;

import java.io.Serializable;
import java.lang.reflect.Field;

public class ParamsFA extends IParamsNetworkMining implements Serializable{
	//神经网络参数列表
	//参数列表
	private int predictPeriod;	//预测周期
	private double momentum; //当更新weights时设置的动量 
	private double learnRate; //学习速率
	private int seed;	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
	private int trianTime;	//训练的迭代次数。
	
	public ParamsFA(){
		//参数列表
		 predictPeriod=20;	//预测周期
		 momentum=0.1; //当更新weights时设置的动量 
		 learnRate=0.2; //学习速率
		 seed=0;	//Seed用于初始化随机数的生成。随机数被用于设定节点之间连接的初始weights，并且用于shuffling训练集 
		 trianTime=500;	//训练的迭代次数。
	}

	public static ParamsFA newInstance(ParamsFA p){
		ParamsFA param=new ParamsFA();
		param.setLearnRate(p.getLearnRate());
		param.setMomentum(p.getMomentum());
		param.setPredictPeriod(p.getPredictPeriod());
		param.setSeed(p.getSeed());
		param.setTrianTime(p.getTrianTime());
		return param;
	}
	
	
	@Override
	public boolean equals(IParamsNetworkMining params) {
		Field [] fields = this.getClass().getFields();
		boolean isSame = true;
		for (Field field : fields)
			try {
				if (!field.get(this).equals(field.get(params))){
					isSame = false;
					break;
				}
			} catch (IllegalArgumentException e) {
				isSame = false;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				isSame = false;
				e.printStackTrace();
			}
		return isSame;
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
