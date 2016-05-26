package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import associationRules.ProtocolAssociationResult;

public class MinerProtocolResults {
	private MinerResultsFP_Line retFP;
	private MinerResultsFP_Whole retSim;
	public MinerProtocolResults() {
		this.retFP = new MinerResultsFP_Line();
		this.retSim = new MinerResultsFP_Whole();
	}
	public MinerResultsFP_Line getRetFP() {
		return retFP;
	}
	public void setRetFP(MinerResultsFP_Line retFP) {
		this.retFP = retFP;
	}
	public MinerResultsFP_Whole getRetSim() {
		return retSim;
	}
	public void setRetSim(MinerResultsFP_Whole retSim) {
		this.retSim = retSim;
	}
	
}
