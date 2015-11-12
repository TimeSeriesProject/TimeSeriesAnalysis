package cn.InstFS.wkr.NetworkMining.Params;

import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;

public class ParamsSM extends IParamsNetworkMining{
	
	private double minSupport;	// 最小支持度
	private int sizeWindow;	// 时间窗长	（单位为秒）
	private int stepWindow;	// 步长
	private int minSeqLen;	// 最短序列模式长度
	
	public ParamsSM() {
		minSupport = 0.3;
		sizeWindow = 80;
		stepWindow = 40;
		minSeqLen = 4;
	}
	
	@Override
	public boolean equals(IParamsNetworkMining params) {
		Field [] fields = this.getClass().getFields();
		boolean isSame = true;
		for (Field field : fields)
			try {
				if (!field.get(this).equals(field.get(params))){
					isSame = false;
					break;
				}
			} catch (IllegalArgumentException e) {
				isSame = false;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				isSame = false;
				e.printStackTrace();
			}
		return isSame;
	}	
	
	public static ParamsSM newInstance(ParamsSM p){
		ParamsSM param = new ParamsSM();
		param.setMinSeqLen(p.getMinSeqLen());
		param.setMinSupport(p.getMinSupport());
		param.setSizeWindow(p.getSizeWindow());
		param.setStepWindow(p.getStepWindow());
		return param;
	}
	
	public double getMinSupport() {
		return minSupport;
	}
	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}
	public int getSizeWindow() {
		return sizeWindow;
	}
	public void setSizeWindow(int sizeWindow) {
		this.sizeWindow = sizeWindow;
	}
	public int getStepWindow() {
		return stepWindow;
	}
	public void setStepWindow(int stepWindow) {
		this.stepWindow = stepWindow;
	}
	public int getMinSeqLen() {
		return minSeqLen;
	}
	public void setMinSeqLen(int minSeqLen) {
		this.minSeqLen = minSeqLen;
	}
}
