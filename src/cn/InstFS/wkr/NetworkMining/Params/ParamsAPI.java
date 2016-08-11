package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.PcapParseParams.PcapParseParams;


public class ParamsAPI {

	/**
	 * 存放所有参数的总路径
	 */
	private String rootPath = "";
	/**
	 * 关联规则相关参数
	 * author:艾长青
	 */
	ParamsAR par = null; 
	/**
	 * pcap包解析参数
	 * 陈维
	 */
	private PcapParseParams pcapParseParams;
	/**
	 * 异常检测参数
	 * LYH
	 */
	ParamsOM pom = null;
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public ParamsAR getAssociationRuleParams() {
		return par;
	}

	public void setAssociationRuleParams(ParamsAR par) {
		this.par = par;
	}

	public PcapParseParams getPcapParseParams() {
		return pcapParseParams;
	}

	public void setPcapParseParams(PcapParseParams pcapParseParams) {
		this.pcapParseParams = pcapParseParams;
	}

	public ParamsOM getPom() {
		return pom;
	}
	public void setPom(ParamsOM pom) {
		this.pom = pom;
	}
	
	
	
}
