package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PartialCycleAlgorithm;

import java.io.Serializable;

public class NodeSection implements Serializable {
	public int begin;
	public int end;
	public NodeSection(int begin,int end){
		this.begin=begin;
		this.end=end;
	}
}
