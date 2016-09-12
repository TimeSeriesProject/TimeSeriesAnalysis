package cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner;

import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;


public interface IMinerPM {
	public void setDataItems(DataItems dataItems);
	public void predictPeriod();
	public boolean hasPeriod();
	public int getPredictPeriod();
	public int getFirstPossiblePeriod();
	public DataItems getItemsInPeriod();
	public Double getMinEntropy();
	public Double[] getEntropies();
	public int getLastNumberIndexInperiod();
	public double getConfidence();
	public DataItems getMinItemsInPeriod();
	public DataItems getMaxItemsInPeriod();
	public void setOriginDataItems(DataItems dataItems);

	public Map<String, List<Integer>> getExistPeriodOfNonNumDataItems();
	public Map<String, Boolean> getHasPeriodOfNonNumDataItms();
	public Map<String, Integer> getPredictPeriodOfNonNumDataItems() ;
	public Map<String, Map<Integer, Double[]>> getPredictValuesMapOfNonNumDataItems() ;
	public Map<String, DataItems> getItemsInperiodMapOfNonNumDataitems();

}
