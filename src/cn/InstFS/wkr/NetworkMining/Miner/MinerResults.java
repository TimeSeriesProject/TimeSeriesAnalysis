package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;


public class MinerResults {
	private INetworkMiner miner;
	
	private Date dateProcess;
	private boolean isAbnormal;	// 是否异常
	DataItems di;
	private MinerResultsPM retPM;
	private MinerResultsTSA retTSA;
	private MinerResultsSM retSM; 
	private MinerResultsFP retFP;
	
	
	public MinerResults(INetworkMiner miner) {
		setMiner(miner);
		//实例化，读取参数
		retPM = new MinerResultsPM();
		retTSA = new MinerResultsTSA();
		retSM = new MinerResultsSM();
		retFP=new MinerResultsFP();
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
	public MinerResultsTSA getRetTSA() {
		return retTSA;
	}
	public void setRetTSA(MinerResultsTSA retTSA) {
		this.retTSA = retTSA;
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
	
	public void setRetSM(MinerResultsSM retSM) {
		this.retSM = retSM;
	}
	public INetworkMiner getMiner() {
		return miner;
	}
	public void setMiner(INetworkMiner miner) {
		this.miner = miner;
	}
}

