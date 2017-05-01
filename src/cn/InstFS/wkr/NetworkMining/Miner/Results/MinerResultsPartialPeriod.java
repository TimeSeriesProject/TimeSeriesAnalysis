package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm.Pair;

/**
 * @// TODO: 17-1-3 chen 部分周期结果存储
 */
public class MinerResultsPartialPeriod implements Serializable {
	private static final long serialVersionUID = 151721725783244513L;
	public Map<String, ArrayList<Pair>> positionResult;
	public Map<String, Integer> periodResult;
	boolean hasPartialPeriod;
	
	public MinerResultsPartialPeriod(Map<String, ArrayList<Pair>> result,Map<String, Integer> periodResult) {
		super();
		this.positionResult = result;
		this.periodResult=periodResult;
	}

	public MinerResultsPartialPeriod() {
		super();
	}
	

	public Map<String, ArrayList<Pair>> getPositionResult() {
		return positionResult;
	}

	public void setPositionResult(Map<String, ArrayList<Pair>> positionResult) {
		this.positionResult = positionResult;
	}

	public Map<String, Integer> getPeriodResult() {
		return periodResult;
	}

	public void setPeriodResult(Map<String,Integer> periodResult) {
		this.periodResult = periodResult;
	}

	public void setResult(Map<String, ArrayList<Pair>> result,Map<String,Integer> periodResult) {
		this.positionResult = result;
		this.periodResult= periodResult;
	}

	public boolean isHasPartialPeriod() {
		return hasPartialPeriod;
	}

	public void setHasPartialPeriod(boolean hasPartialPeriod) {
		this.hasPartialPeriod = hasPartialPeriod;
	}

}
