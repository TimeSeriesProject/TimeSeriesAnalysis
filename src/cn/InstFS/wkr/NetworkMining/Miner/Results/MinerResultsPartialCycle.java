package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.util.Pair;


public class MinerResultsPartialCycle implements Serializable{
	
	boolean hasPartialCycle;
	/*
	 * 存储部分周期的起始点与循环节的长度
	 */
	HashMap<Integer,ArrayList<Pair<Integer,Integer>> >partialCyclePos= new HashMap<Integer,ArrayList<Pair<Integer,Integer>>>();
	//ArrayList<Integer> cycleLengthList= new ArrayList<Integer>();
	
	
	public MinerResultsPartialCycle(){
		
	}


	public boolean isHasPartialCycle() {
		return hasPartialCycle;
	}


	public void setHasPartialCycle(boolean hasPartialCycle) {
		this.hasPartialCycle = hasPartialCycle;
	}


	public HashMap<Integer, ArrayList<Pair<Integer, Integer>>> getPartialCyclePos() {
		return partialCyclePos;
	}


	public void setPartialCyclePos(
			HashMap<Integer, ArrayList<Pair<Integer, Integer>>> partialCyclePos) {
		this.partialCyclePos = partialCyclePos;
	}


	
	
}