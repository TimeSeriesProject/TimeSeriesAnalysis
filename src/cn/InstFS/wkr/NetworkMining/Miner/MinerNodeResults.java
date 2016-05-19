package cn.InstFS.wkr.NetworkMining.Miner;

public class MinerNodeResults {
	private MinerResultsStatistics retStatistics;
	private MinerResultsPM retPM;
	private MinerResultsOM retOM;
	private MinerResultsSM retSM;
	
	public MinerNodeResults(){
		retStatistics=new MinerResultsStatistics();
		retPM=new MinerResultsPM();
		retOM=new MinerResultsOM();
		retSM=new MinerResultsSM();
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
	
}
