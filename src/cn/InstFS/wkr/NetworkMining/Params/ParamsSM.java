package cn.InstFS.wkr.NetworkMining.Params;

import java.io.Serializable;
import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.Params.SMParams.SMParam;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;

public class ParamsSM extends IParamsNetworkMining implements Serializable{
	
	private SMParam smparam;
	public ParamsSM(){}
	public SMParam getSMparam() {
		return smparam;
	}
	public void setSMparam(SMParam smparam) {
		this.smparam = smparam;
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
}
