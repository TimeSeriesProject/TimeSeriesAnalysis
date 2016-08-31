package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math3.util.Pair;


public class MinerResultsPartialCycle implements Serializable{
	
	boolean hasPartialCycle;
	/*
	 * 存储部分周期的起始点与循环节的长度
	 */
	ArrayList<Pair<Integer,Integer>> partialCyclePos= new ArrayList<Pair<Integer,Integer>>();
	ArrayList<Integer> cycleLengthList= new ArrayList<Integer>();
	
	
	public MinerResultsPartialCycle(){
		
	}


	public boolean isHasPartialCycle() {
		return hasPartialCycle;
	}


	public void setHasPartialCycle(boolean hasPartialCycle) {
		this.hasPartialCycle = hasPartialCycle;
	}


	public ArrayList<Pair<Integer, Integer>> getPartialCyclePos() {
		return partialCyclePos;
	}


	public void setPartialCyclePos(ArrayList<Pair<Integer, Integer>> partialCyclePos) {
		this.partialCyclePos = partialCyclePos;
	}


	public ArrayList<Integer> getCycleLengthList() {
		return cycleLengthList;
	}


	public void setCycleLengthList(ArrayList<Integer> cycleLengthList) {
		this.cycleLengthList = cycleLengthList;
	}
	
}