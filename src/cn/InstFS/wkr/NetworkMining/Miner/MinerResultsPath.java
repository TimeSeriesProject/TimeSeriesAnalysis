package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class MinerResultsPath {
	private HashMap<String, MinerResultsPM> retPM;
	private int minPeriod = 0;
	private int maxPeriod = 0;
	private HashMap<String, MinerResultsOM> retOM;
	private int minOutliesConfidence = 0;
	private int maxOutliesConfidence = 0;
	private HashMap<String, DataItems> pathOriDataItems;

	public MinerResultsPath() {
		retPM = new HashMap<String, MinerResultsPM>();
		retOM = new HashMap<String, MinerResultsOM>();
		pathOriDataItems = new HashMap<>();
	}

	public HashMap<String, MinerResultsPM> getRetPM() {
		return retPM;
	}

	public void setRetPM(HashMap<String, MinerResultsPM> retPM) {
		this.retPM = retPM;
	}

	public HashMap<String, MinerResultsOM> getRetOM() {
		return retOM;
	}

	public void setRetOM(HashMap<String, MinerResultsOM> retOM) {
		this.retOM = retOM;
	}

	public HashMap<String, DataItems> getPathOriDataItems() {
		return pathOriDataItems;
	}

	public void setPathOriDataItems(HashMap<String, DataItems> pathOriDataItems) {
		this.pathOriDataItems = pathOriDataItems;
	}

	public void setMaxAndMinValue() {
		setMaxOrMinPeriod(1);
		setMaxOrMinPeriod(-1);

		setMaxOrMinOuliesConfidence(1);
		setMaxOrMinOuliesConfidence(-1);
	}
	/**
	 * 获取多路径中周期最值
	 * @param flag ‘1’--最大值 ‘-1’--最小值
	 * @return 周期最值
     */
	private void setMaxOrMinPeriod(int flag) {
		int period = 0;
		int i = 1;

		for (MinerResultsPM ret : retPM.values()) {
			int temp = (int)ret.getPeriod();

			if (flag * temp > flag * period || i == 1) {
				period = temp;
				i = 2;
			}
		}
		if (flag == 1)
			maxPeriod = period;
		else if (flag == -1)
			minPeriod = period;
	}

	/**
	 * 获取多路径中异常度最值
	 * @param flag ‘1’--最大值 ‘-1’--最小值
	 * @return 异常度最值
	 */
	private void setMaxOrMinOuliesConfidence(int flag) {
		int confidence = 0;
		int i = 1;

		for (MinerResultsOM ret : retOM.values()) {
			int temp = ret.getConfidence();

			if (flag * temp > flag * confidence || i == 1) {
				confidence = temp;
				i = 2;
			}
		}
		if (flag == 1) {
			maxOutliesConfidence = confidence;
		} else if (flag == -1)
			minOutliesConfidence = confidence;
	}

	public int getMinPeriod() {
		return minPeriod;
	}

	public int getMaxPeriod() {
		return maxPeriod;
	}

	public int getMinOutliesConfidence() {
		return minOutliesConfidence;
	}

	public int getMaxOutliesConfidence() {
		return maxOutliesConfidence;
	}

	public boolean getHasPeriod() {
		for (MinerResultsPM ret : retPM.values()) {
			if(ret.getHasPeriod())
				return true;
		}
		return false;
	}

	public boolean getHasOutlies() {
		for (MinerResultsOM ret : retOM.values()) {
			if (ret.isHasOutlies())
				return true;
		}
		return false;
	}

}
