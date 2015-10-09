package cn.InstFS.wkr.NetworkMining.TaskConfigure;

public enum DiscreteMethod {
	 None("无"),
	各区间数值范围相同("各区间数值范围相同"),
	各区间数据点数相同("各区间数据点数相同"),
	自定义端点("自定义端点");
	
	private String val;
	private DiscreteMethod(String str){
		this.val = str;
	}
	
	@Override
	public String toString() {
		return val;
	}
	public static DiscreteMethod fromString(String str){
		try{
			return valueOf(str);
		}catch(Exception e){
			return None;
		}
		
	}
}
