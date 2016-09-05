package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.util.Date;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.INetworkMiner;


public class MinerResults {
	private INetworkMiner miner;
	
	private Date dateProcess;
	private boolean isAbnormal;	// 是否异常
	DataItems di;
	private MinerResultsPM retPM;
	private MinerResultsOM retOM;
	private MinerResultsSM retSM; 
	private MinerResultsFP retFP;
	private MinerResultsFM retFM;
	private MinerResultsFP_Line retFPLine;
	private MinerResultsFP_Whole retSim;
	private MinerNodeResults retNode;
	private MinerProtocolResults retProtocol;
	private MinerResultsStatistics retStatistics;
	private MinerResultsPath retPath;
	private MinerResultsPartialCycle retPartialCycle;
	
	public MinerResults(INetworkMiner miner) {
		setMiner(miner);
		//实例化，读取参数
		retPM = new MinerResultsPM();
		retOM = new MinerResultsOM();
		retSM = new MinerResultsSM();
		retFP = new MinerResultsFP();
		retFM = new MinerResultsFM();
		retFPLine=new MinerResultsFP_Line();
		retPath = new MinerResultsPath();
		retNode=new MinerNodeResults();
		retProtocol=new MinerProtocolResults();
		retStatistics=new MinerResultsStatistics();
		retPartialCycle = new MinerResultsPartialCycle();
	}
	
	public MinerResultsFP getRetFP() {
		return retFP;
	}

	public void setRetFP(MinerResultsFP retFP) {
		this.retFP = retFP;
	}

	public DataItems getInputData(){
		return di;
	}
	public void setInputData(DataItems di){
		this.di = di;
	}
	public Date getDateProcess() {
		return dateProcess;
	}
	public void setDateProcess(Date dateProcess) {
		this.dateProcess = dateProcess;
	}
	public MinerResultsPM getRetPM() {
		return retPM;
	}
	public void setRetPM(MinerResultsPM retPM) {
		this.retPM = retPM;
	}
	public boolean isAbnormal() {
		return isAbnormal;
	}
	public void setAbnormal(boolean isAbnormal) {
		this.isAbnormal = isAbnormal;
	}
	
	public MinerResultsSM getRetSM(String name) {
		return retSM;
	}
	
	public MinerResultsSM getRetSM(){
		return retSM;
	}
	
	public MinerResultsPath getRetPath() {
		return retPath;
	}

	public void setRetPath(MinerResultsPath retPath) {
		this.retPath = retPath;
	}

	public void setRetSM(MinerResultsSM retSM) {
		this.retSM = retSM;
	}
	public INetworkMiner getMiner() {
		return miner;
	}
	public void setMiner(INetworkMiner miner) {
		this.miner = miner;
	}

	public MinerResultsOM getRetOM() {
		return retOM;
	}

	public void setRetOM(MinerResultsOM retOM) {
		this.retOM = retOM;
	}

	public MinerResultsFM getRetFM() {
		return retFM;
	}

	public void setRetFM(MinerResultsFM retFM) {
		this.retFM = retFM;
	}

	public MinerResultsStatistics getRetStatistics() {
		return retStatistics;
	}

	public void setRetStatistics(MinerResultsStatistics retStatistics) {
		this.retStatistics = retStatistics;
	}

	public MinerNodeResults getRetNode() {
		return retNode;
	}

	public void setRetNode(MinerNodeResults retNode) {
		this.retNode = retNode;
	}

	public MinerProtocolResults getRetProtocol() {
		return retProtocol;
	}

	public void setRetProtocol(MinerProtocolResults retProtocol) {
		this.retProtocol = retProtocol;
	}

	public MinerResultsFP_Line getRetFPLine() {
		return retFPLine;
	}

	public void setRetFPLine(MinerResultsFP_Line retFPLine) {
		this.retFPLine = retFPLine;
	}

	public MinerResultsFP_Whole getRetSim() {
		return retSim;
	}

	public void setRetSim(MinerResultsFP_Whole retSim) {
		this.retSim = retSim;
	}

	public MinerResultsPartialCycle getRetPartialCycle() {
		return retPartialCycle;
	}

	public void setRetPartialCycle(MinerResultsPartialCycle retPartialCycle) {
		this.retPartialCycle = retPartialCycle;
	}
	
	
}

