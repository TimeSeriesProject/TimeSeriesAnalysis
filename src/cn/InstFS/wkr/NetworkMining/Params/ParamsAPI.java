package cn.InstFS.wkr.NetworkMining.Params;


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

	
	
}
