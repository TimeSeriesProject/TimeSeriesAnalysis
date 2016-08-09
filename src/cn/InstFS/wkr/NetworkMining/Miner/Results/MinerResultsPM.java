package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;

public class MinerResultsPM implements Serializable{
	boolean hasPeriod;
	boolean hasPartialPeriod;
	ParamsPM params;	// 或者 java.util.Properties params;
	long period;
	private Double featureValue;	// 指标值
	private Double[] featureValues;	// 不同周期对应的指标值，如featureValues[2]代表周期为2时的指标值
	private DataItems distibutePeriod;
	private DataItems minDistributePeriod;
	private DataItems maxDistributePeriod;
	private int firstPossiblePeriod;
	private double confidence;
	private HashMap<Integer, List<List<Integer>>> partialPmMap;
	
	public MinerResultsPM(){}
	
	public MinerResultsPM(boolean hasPeriod,long period){
		this.hasPeriod=hasPeriod;
		this.period=period;
	}
	public ParamsPM getParamsPM(){
		return this.params;
	}
	public void setParamsPM(ParamsPM params){
		this.params=params;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	
	public void setDistributePeriod(DataItems item){
		this.distibutePeriod=item;
	}
	
	public void setHasPeriod(boolean hasPeriod) {
		this.hasPeriod = hasPeriod;
	}
	
	public long getPeriod() {
		return period;
	}
	
	public boolean getHasPeriod(){
		return this.hasPeriod;
	}
	public DataItems getDistributePeriod(){
		return this.distibutePeriod;
	}

	public double getFeatureValue() {
		return featureValue;
	}

	public void setFeatureValue(double featureValue) {
		this.featureValue = featureValue;
	}

	public Double[] getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(Double[] featureValues) {
		this.featureValues = featureValues;
	}

	public int getFirstPossiblePeriod() {
		return firstPossiblePeriod;
	}

	public void setFirstPossiblePeriod(int firstPossiblePeriod) {
		this.firstPossiblePeriod = firstPossiblePeriod;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public DataItems getMinDistributePeriod() {
		return minDistributePeriod;
	}

	public void setMinDistributePeriod(DataItems minDistributePeriod) {
		this.minDistributePeriod = minDistributePeriod;
	}

	public DataItems getMaxDistributePeriod() {
		return maxDistributePeriod;
	}

	public void setMaxDistributePeriod(DataItems maxDistributePeriod) {
		this.maxDistributePeriod = maxDistributePeriod;
	}

	public boolean isHasPartialPeriod() {
		return hasPartialPeriod;
	}

	public void setHasPartialPeriod(boolean hasPartialPeriod) {
		this.hasPartialPeriod = hasPartialPeriod;
	}

	public HashMap<Integer, List<List<Integer>>> getPartialPmMap() {
		return partialPmMap;
	}

	public void setPartialPmMap(HashMap<Integer, List<List<Integer>>> partialPmMap) {
		this.partialPmMap = partialPmMap;
	}
	
	
}