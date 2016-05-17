package cn.InstFS.wkr.NetworkMining.Miner;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.WavCluster;


public class PartialPM{
	
	private int clusterNum;
	private int threshold;
	private HashMap<Integer, List<List<Integer>>> partialPmMap;
	private DataItems dataItems;
	private MinerResults results;
	private String arffFileName;
	
	public PartialPM(MinerResults results,DataItems dataItems,String arffFileName) {
		this.results = results;
		clusterNum=8;
		threshold=5;
		this.dataItems=dataItems;
		this.arffFileName=arffFileName;
		partialPmMap=new HashMap<Integer, List<List<Integer>>>();
	}
	
	public PartialPM(MinerResults results,int clusterNum,int threshold,DataItems dataItems,String arffFileName) {
		this.results = results;
		this.clusterNum=clusterNum;
		this.threshold=threshold;
		this.dataItems=dataItems;
		this.arffFileName=arffFileName;
		partialPmMap=new HashMap<Integer, List<List<Integer>>>();
	}
	
	
	public void miningPartialPM() {
		PointSegment segment=new PointSegment(dataItems, 5);
		List<SegPattern> segPatterns=segment.getPatterns();
		results.setInputData(dataItems);
		DataItems clusterItems=WavCluster.SelfCluster(segPatterns,dataItems, clusterNum,arffFileName);
		SequencePatternsDontSplit sequencePattern=new SequencePatternsDontSplit();
		sequencePattern.setDataItems(clusterItems);
		sequencePattern.setStepSize(1);
		sequencePattern.setThreshold(threshold);
		sequencePattern.patternMining();
		List<ArrayList<String>> patterns=sequencePattern.getPatterns();
		for(ArrayList<String>pattern:patterns){
			isPartialPeriod(pattern, clusterItems);
		}
	}
	
	private void isPartialPeriod(List<String> pattern,DataItems dataItems){
		List<String>data=dataItems.getData();
		int patternSize=pattern.size();
		int length=dataItems.getLength();
		boolean isEqual=true;
		int repeatNum=0;
		int partialPmStart=0;
		int partialPmEnd=patternSize-1;
		for(int i=0;i<(length-patternSize+1);i++){
			if(!isEqual)
				partialPmStart=i;
			isEqual=true;
			for(int j=0;j<patternSize;j++){
				if(!pattern.get(j).equals(data.get(i+j))){
					isEqual=false;
					break;
				}
			}
			if(isEqual){
				repeatNum++;
				i+=(patternSize-1);   //调到下一个频繁项中
				partialPmEnd=i;
			}else{
				if(repeatNum>=threshold){  //连续出现的次数大于阈值，认定局部周期存在
					List<Integer>partialPm=new ArrayList<Integer>();
					Date firstTime=dataItems.getTime().get(0);
					Date startTime=dataItems.getTime().get(partialPmStart);
					Date endTime=startTime;
					if(partialPmEnd==dataItems.getLength()-1){
						endTime=this.dataItems.getLastTime();
					}else{
						endTime=dataItems.getTime().get(partialPmEnd+1);
					}
					int startSpan=(int)((startTime.getTime()-firstTime.getTime())/1000)/3600;
					int endSpan=(int)((endTime.getTime()-firstTime.getTime())/1000)/3600;
					partialPm.add(startSpan);
					partialPm.add(endSpan);
					int period=(endSpan-startSpan)/repeatNum;
					if(!isContain(startSpan, endSpan)){
						if(partialPmMap.containsKey(period)){
							List<List<Integer>> pms=partialPmMap.get(period);
							pms.add(partialPm);
						}else{
							List<List<Integer>> pms=new ArrayList<List<Integer>>();
							pms.add(partialPm);
							partialPmMap.put(period, pms);
						}
					}
					repeatNum=0;
				}else{
					repeatNum=0;
				}
			}
		}
	}
	
	private boolean isContain(int startSpan,int endSpan){
		boolean isContain=false;
		Iterator<Entry<Integer, List<List<Integer>>>>iterator=partialPmMap.entrySet().iterator();
		while (iterator.hasNext()) {
			List<List<Integer>> pms=iterator.next().getValue();
			for(List<Integer>pm:pms){
				if(pm.get(0)<=startSpan&&pm.get(1)>=endSpan){
					isContain=true;
					return isContain;
				}
			}
		}
		return isContain;
	}
}
