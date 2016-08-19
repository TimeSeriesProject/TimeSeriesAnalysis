package cn.InstFS.wkr.NetworkMining.Params;

import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;

import java.lang.reflect.Field;

public class ParamsPM extends IParamsNetworkMining {
	private PMparam pmparam=null;
	public ParamsPM(){}
	public PMparam getPmparam() {
		return pmparam;
	}
	public void setPmparam(PMparam pmparam) {
		this.pmparam = pmparam;
	}

	@Override
	public boolean equals(IParamsNetworkMining params) {
		Field[] fields = this.getClass().getFields();
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
