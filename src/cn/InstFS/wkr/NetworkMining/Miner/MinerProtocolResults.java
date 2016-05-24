package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import associationRules.ProtocolAssociationResult;

public class MinerProtocolResults {
	private MinerResultsFP_Line retFP;
	private Map<String, List<ProtocolAssociationResult>> retSim;
	public MinerProtocolResults() {
		this.retFP = new MinerResultsFP_Line();
		this.retSim = new HashMap<String, List<ProtocolAssociationResult>>();
	}
	public MinerResultsFP_Line getRetFP() {
		return retFP;
	}
	public void setRetFP(MinerResultsFP_Line retFP) {
		this.retFP = retFP;
	}
	public Map<String, List<ProtocolAssociationResult>> getRetSim() {
		return retSim;
	}
	public void setRetSim(Map<String, List<ProtocolAssociationResult>> retSim) {
		this.retSim = retSim;
	}
	
}
