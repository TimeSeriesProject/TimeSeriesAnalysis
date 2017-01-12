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
	public Map<String, ArrayList<Pair>> result;

	boolean hasPartialPeriod;

	public MinerResultsPartialPeriod(Map<String, ArrayList<Pair>> result) {
		super();
		this.result = result;
	}

	public MinerResultsPartialPeriod() {
		super();
	}

	public Map<String, ArrayList<Pair>> getResult() {
		return result;
	}

	public void setResult(Map<String, ArrayList<Pair>> result) {
		this.result = result;
	}

	public boolean isHasPartialPeriod() {
		return hasPartialPeriod;
	}

	public void setHasPartialPeriod(boolean hasPartialPeriod) {
		this.hasPartialPeriod = hasPartialPeriod;
	}

}
