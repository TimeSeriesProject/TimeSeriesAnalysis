package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsPM implements IObjectDescriptor<ParamsPM>{
	ParamsPM core;

	
	
	String [] names = new String[]{"dimension", "periodThreshold", "endNodes", "discreteMethod"};
	String []CnNames = new String[]{"离散值个数", "检测门限", "区间端点（逗号隔开）", "离散化方法"};
	HashMap<String, String> displayNames = new HashMap<String,String>();
	
	public PropertyUI_MiningParamsPM(IParamsNetworkMining core) {
		displayNames.clear();
		for(int i = 0; i < names.length; i ++)
			displayNames.put(names[i], CnNames[i]);	
		if (core.getClass().equals(ParamsPM.class))
			this.core = (ParamsPM) core;
		else
			this.core = new ParamsPM();
	}
	private String getDisplayNameOfStr(String str){
		if(displayNames.containsKey(str))
			return displayNames.get(str);
		else
			return str;
	}
	@Override
	public String getDisplayName() {
		return "挖掘参数";
	}

	@Override
	public List<EnhancedPropertyDescriptor> getProperties() {
		List<EnhancedPropertyDescriptor> props = new ArrayList<EnhancedPropertyDescriptor>();
		props.add(getPropDesc("periodThreshold", 0, "周期性检测门限"));
//		props.add(getPropDesc("dimension", 0, "若输入值为离散，则此值无效<br>否则，将输入值离散化为多个区间"));
//		props.add(getPropDesc("discreteMethod", 0, "离散化方法:<br>1.使得各区间数值范围相同<br>2.使得各区间数据点数相同<br>3.自定义端点"));
//		props.add(getPropDesc("endNodes", 0, "离散化时所使用的数值区间端点，逗号隔开<br>注意：仅在采用自定义离散化方法时有效！"));
		return props;
	}
	
	@Override
	public ParamsPM getCore() {
		return core;
	}
	
	private EnhancedPropertyDescriptor getPropDesc(String propStr, int id, String descStr) {
		if (propStr == null || propStr.length() == 0)
			return null;
        try {
        	String propStrUpper = propStr.substring(0,1).toUpperCase() + propStr.substring(1);
            PropertyDescriptor desc = new PropertyDescriptor(propStr, this.getClass(), 
            		"get" + propStrUpper, "set" + propStrUpper);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, id);
            desc.setDisplayName(getDisplayNameOfStr(propStr));
            desc.setShortDescription(descStr);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
	
	
	public Double getPeriodThreshold(){
		return core.getPeriodThreshold();
	}
//	public int getDimension() {
//		return core.getDimension();
//	}
	
	public void setPeriodThreshold(Double periodThreshold){
		core.setPeriodThreshold(periodThreshold);
	}
//	public void setDimension(int dimension) {
//		core.setDimension(dimension);
//	}
	
//	public DiscreteMethod getDiscreteMethod() {
//		return core.getDiscreteMethod();
//	}
//	public void setDiscreteMethod(DiscreteMethod discreteMethod) {
//		core.setDiscreteMethod(discreteMethod);
//	}
	
//	public String getEndNodes() {
//		return core.getEndNodes();
//	}
//	public void setEndNodes(String endNodes) {
//		core.setEndNodes(endNodes);
//	}
}
