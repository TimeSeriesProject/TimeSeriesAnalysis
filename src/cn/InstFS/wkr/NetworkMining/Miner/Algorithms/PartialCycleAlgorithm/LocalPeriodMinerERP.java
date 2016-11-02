package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm.ERPDistencePM;
import cn.InstFS.wkr.NetworkMining.Miner.Results.MinerResultsPartialCycle;

public class LocalPeriodMinerERP{
	DataItems dataItems = new DataItems();//原始数据
	private MinerResultsPartialCycle result = new MinerResultsPartialCycle();//挖掘结果
	HashMap<Integer,ArrayList<NodeSection>> partialCyclePos = new HashMap<Integer,ArrayList<NodeSection>>();//
	boolean hasPartialCycle = false;
	private double threshold;
	private int longestPeriod;
	private int minWindowSize = 30;
	public LocalPeriodMinerERP(){}
	public LocalPeriodMinerERP(DataItems di,double threshold,int longestPeriod){
		this();
		this.dataItems = di;
		this.threshold = threshold;
		this.longestPeriod = longestPeriod;
		run();
	}
	public void run(){
		//滑动窗口
		int len = dataItems.getLength();
		for(int i=len-1;i>=0;i--){//窗口从后向前滑动
			for(int j=0;j<i-minWindowSize;j++){//窗口从前向后滑动
				DataItems widowItems = new DataItems();//窗口内的数据
				for(int k=j;k<i;k++){
					widowItems.add1Data(dataItems.getElementAt(k));
				}
				ERPDistencePM erpDistencePM = new ERPDistencePM(threshold,longestPeriod);
				erpDistencePM.setOriginDataItems(widowItems);
				erpDistencePM.setDataItems(widowItems);
				erpDistencePM.predictPeriod();
				if(erpDistencePM.hasPeriod()){
					
					int period = erpDistencePM.getPredictPeriod();
//					int period = 65;					
					if(!partialCyclePos.containsKey(period)){
						ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
						NodeSection nodeSection = new NodeSection(j, i);
						nodeList.add(nodeSection);
						partialCyclePos.put(period, nodeList);
						hasPartialCycle = true;
					}else{
						ArrayList<NodeSection> nodeList = new ArrayList<NodeSection>();
						nodeList = partialCyclePos.get(period);
						NodeSection nodeSection = new NodeSection(j, i);
						nodeList.add(nodeSection);
						partialCyclePos.put(period, nodeList);
						hasPartialCycle = true;
					}
					
					i=j;
					break;					
				}else{
					continue;
				}
			}
		}
	}
	public MinerResultsPartialCycle getResult() {
		result.setPartialCyclePos(partialCyclePos);
		result.setHasPartialCycle(hasPartialCycle);
		return result;

	}
	public int getMinWindowSize() {
		return minWindowSize;
	}
	public void setMinWindowSize(int minWindowSize) {
		this.minWindowSize = minWindowSize;
	}
	
}
