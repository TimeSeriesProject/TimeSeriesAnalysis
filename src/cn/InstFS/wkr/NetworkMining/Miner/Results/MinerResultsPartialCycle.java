package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm.NodeSection;


public class MinerResultsPartialCycle implements Serializable{
	
	boolean hasPartialCycle;
	/*
	 * 存储部分周期的起始点与循环节的长度Map<Integer,ArrayList<NodeSection>> map
	 */
	HashMap<Integer,ArrayList<NodeSection> >partialCyclePos= new HashMap<Integer,ArrayList<NodeSection>>();
	//ArrayList<Integer> cycleLengthList= new ArrayList<Integer>();
	
	
	public MinerResultsPartialCycle(){
		
	}


	public boolean isHasPartialCycle() {
		return hasPartialCycle;
	}


	public void setHasPartialCycle(boolean hasPartialCycle) {
		this.hasPartialCycle = hasPartialCycle;
	}


	public HashMap<Integer, ArrayList<NodeSection>> getPartialCyclePos() {
		return partialCyclePos;
	}


	public void setPartialCyclePos(
			HashMap<Integer, ArrayList<NodeSection>> partialCyclePos) {
		this.partialCyclePos = partialCyclePos;
	}


	
	
}