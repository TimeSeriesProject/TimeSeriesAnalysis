package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

public class ParamsPM {
	private PMparam pmparam=null;
	public ParamsPM(){}
	public PMparam getPmparam() {
		return pmparam;
	}
	public void setPmparam(PMparam pmparam) {
		this.pmparam = pmparam;
	}
	
}
