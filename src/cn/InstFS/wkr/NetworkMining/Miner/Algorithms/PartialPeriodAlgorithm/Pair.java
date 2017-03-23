package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialPeriodAlgorithm;

import java.io.Serializable;

public class Pair implements Serializable{
	private static final long serialVersionUID = 151721725783244514L;
	private int begin;
	private int end;

	public Pair(int begin, int end) {
		this.begin = begin;
		this.end = end;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
