package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsSM implements IObjectDescriptor<ParamsSM>{
	ParamsSM core;
	
	String [] names = new String[]{"sizeWindow", "stepWindow", "minSeqLen", "minSupport"};
	String []CnNames = new String[]{"时间窗长(s)", "步长(s)", "最小序列长度", "最小支持度(0-1)",
			"过滤条件", "挖掘方法", "挖掘参数"};
	HashMap<String, String> displayNames = new HashMap<String,String>();
	
	
	public PropertyUI_MiningParamsSM(IParamsNetworkMining core) {
		this.core = (ParamsSM) core;
		
		displayNames.clear();
		for(int i = 0; i < names.length; i ++)
			displayNames.put(names[i], CnNames[i]);		
	}
	@Override
	public String getDisplayName() {
		return "挖掘参数";
	}
	
	private String getDisplayNameOfStr(String str){
		if(displayNames.containsKey(str))
			return displayNames.get(str);
		else
			return str;
	}

	@Override
	public List<EnhancedPropertyDescriptor> getProperties() {
		List<EnhancedPropertyDescriptor>props = new ArrayList<EnhancedPropertyDescriptor>();

		props.add(getPropDesc("sizeWindow", 0, "窗口大小"));
		props.add(getPropDesc("stepWindow", 0, "窗口步长，即：<br>与上一窗口相比，当前窗口在时间上移动了多少"));
		props.add(getPropDesc("minSeqLen", 0, "挖掘出来的最短序列长度"));
		props.add(getPropDesc("minSupport", 0, "最小支持度门限<br>值越小，则挖掘速度越慢，挖掘出结果越丰富<br>值越大，挖掘速度越快，但结果相对较少"));

		return props;
	}

	@Override
	public ParamsSM getCore() {
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
	
	
	public double getMinSupport() {
		return core.getMinSupport();
	}
	public void setMinSupport(double minSupport) {
		core.setMinSupport(minSupport);
	}
	public double getSizeWindow() {
		return core.getSizeWindow();
	}
	public void setSizeWindow(int sizeWindow) {
		core.setSizeWindow(sizeWindow);
	}
	public double getStepWindow() {
		return core.getStepWindow();
	}
	public void setStepWindow(int stepWindow) {
		core.setStepWindow(stepWindow);
	}
	public int getMinSeqLen() {
		return core.getMinSeqLen();
	}
	public void setMinSeqLen(int minSeqLen) {
		core.setMinSeqLen(minSeqLen);
	}
	
}
