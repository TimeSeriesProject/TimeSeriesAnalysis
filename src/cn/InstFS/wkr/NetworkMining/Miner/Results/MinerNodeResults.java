package cn.InstFS.wkr.NetworkMining.Miner.Results;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

import java.io.Serializable;

public class MinerNodeResults implements Serializable{
	private static final long serialVersionUID = -5211605816802818178L;
	private DataItems di;
	private MinerResultsStatistics retStatistics;
	private MinerResultsPM retPM;
	private MinerResultsOM retOM;
	private MinerResultsSM retSM;
	private MinerResultsPartialCycle retPartialCycle; //局部周期
	private MinerResultsPartialPeriod retPartialPeriod; //部分周期
	public MinerNodeResults(){
		retStatistics=new MinerResultsStatistics();
		retPM=new MinerResultsPM();
		retOM=new MinerResultsOM();
		retSM=new MinerResultsSM();
		retPartialCycle = new MinerResultsPartialCycle();
		retFM=new MinerResultsFM();
		retPartialPeriod = new MinerResultsPartialPeriod();
	}

	public MinerResultsStatistics getRetStatistics() {
		return retStatistics;
	}

	public void setRetStatistics(MinerResultsStatistics retStatistics) {
		this.retStatistics = retStatistics;
	}

	public MinerResultsPM getRetPM() {
		return retPM;
	}

	public void setRetPM(MinerResultsPM retPM) {
		this.retPM = retPM;
	}

	public MinerResultsOM getRetOM() {
		return retOM;
	}

	public void setRetOM(MinerResultsOM retOM) {
		this.retOM = retOM;
	}

	public MinerResultsSM getRetSM() {
		return retSM;
	}

	public void setRetSM(MinerResultsSM retSM) {
		this.retSM = retSM;
	}

	public MinerResultsPartialCycle getRetPartialCycle() {
		return retPartialCycle;
	}

	public void setRetPartialCycle(MinerResultsPartialCycle retPartialCycle) {
		this.retPartialCycle = retPartialCycle;
	}
	private MinerResultsFM retFM;
	public MinerResultsFM getRetFM() {
		return retFM;
	}

	public void setRetFM(MinerResultsFM retFM) {
		this.retFM = retFM;
	}

	public MinerResultsPartialPeriod getRetPartialPeriod() {
		return retPartialPeriod;
	}

	public void setRetPartialPeriod(MinerResultsPartialPeriod retPartialPeriod) {
		this.retPartialPeriod = retPartialPeriod;
	}

	public DataItems getOriDataItems() {
		return di;
	}

	public void setOriDataItems(DataItems di) {
		this.di = di;
	}
}
