package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.HashMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class MinerResultsPath {
	private HashMap<String, MinerResultsPM> retPM;
	private HashMap<String, MinerResultsOM> retOM;

	public MinerResultsPath() {
		retPM = new HashMap<String, MinerResultsPM>();
		retOM = new HashMap<String, MinerResultsOM>();
	}

	public HashMap<String, MinerResultsPM> getRetPM() {
		return retPM;
	}

	public void setRetPM(HashMap<String, MinerResultsPM> retPM) {
		this.retPM = retPM;
	}

	public HashMap<String, MinerResultsOM> getRetOM() {
		return retOM;
	}

	public void setRetOM(HashMap<String, MinerResultsOM> retOM) {
		this.retOM = retOM;
	}
}
