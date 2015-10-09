package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsTSA implements IObjectDescriptor<ParamsTSA>{
	ParamsTSA core;

	String [] names = new String[]{"dimension", "dimensionValues", "expWindowSize", "fftVarK",
			"initWindowSize", "maxWindowSize", "outlierThreshold", "periodThreshold", "windowVarK"};
	String []CnNames = new String[]{"离散值个数", "离散化的区间(unused)", "expWindowSize", "fftVarK",
			"初始窗口大小", "最大窗口大小", "异常值门限(0~1???)", "周期门限(0~1)", "windowVarK"};
	HashMap<String, String> displayNames = new HashMap<String,String>();
	
	public PropertyUI_MiningParamsTSA(IParamsNetworkMining core) {
		this.core = (ParamsTSA) core;
		
		displayNames.clear();
		for(int i = 0; i < names.length; i ++)
			displayNames.put(names[i], CnNames[i]);		
	}
	@Override
	public String getDisplayName() {
		return "挖掘参数";
	}

	@Override
	public List<EnhancedPropertyDescriptor> getProperties() {
		List<EnhancedPropertyDescriptor> props = new ArrayList<EnhancedPropertyDescriptor>();
		props.add(getPropDesc("dimension", 0));
		props.add(getPropDesc("dimensionValues", 0));
		props.add(getPropDesc("expWindowSize", 0));
		props.add(getPropDesc("fftVarK", 0));
		props.add(getPropDesc("initWindowSize", 0));
		props.add(getPropDesc("maxWindowSize", 0));
		props.add(getPropDesc("outlierThreshold", 0));
		props.add(getPropDesc("periodThreshold", 0));
		props.add(getPropDesc("windowVarK", 0));

		return props;
	}

	@Override
	public ParamsTSA getCore() {
		return core;
	}
	
	private EnhancedPropertyDescriptor getPropDesc(String propStr, int id){
		return getPropDesc(propStr, id, getDisplayNameOfStr(propStr));
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
	
	private String getDisplayNameOfStr(String str){
		if(displayNames.containsKey(str))
			return displayNames.get(str);
		else
			return str;
	}
	
	public Double getPeriodThreshold(){
		return core.getPeriodThreshold();
	}
	public Double getOutlierThreshold() {
		return core.getOutlierThreshold();
	}
//	public int getDimension() {
//		return core.getDimension();
//	}
//	public String getDimensionValues() {
//		return core.getDimensionValues();
//	}

	public void setPeriodThreshold(Double periodThreshold){
		core.setPeriodThreshold(periodThreshold);
	}
	public void setOutlierThreshold(Double outlierThreshold) {
		core.setOutlierThreshold(outlierThreshold);
	}
//	public void setDimension(int dimension) {
//		core.setDimension(dimension);
//	}
//	public void setDimensionValues(String dimensionValues) {
//		if (dimensionValues.length() > 0)
//			core.setDimensionValues(dimensionValues);
//	}
	public int getInitWindowSize() {
		return core.getInitWindowSize();
	}
	public void setInitWindowSize(int initWindowSize) {
		core.setInitWindowSize(initWindowSize);
	}
	public int getMaxWindowSize() {
		return core.getMaxWindowSize();
	}
	public void setMaxWindowSize(int maxWindowSize) {
		core.setMaxWindowSize(maxWindowSize);
	}
	public int getExpWindowSize() {
		return core.getExpWindowSize();
	}
	public void setExpWindowSize(int expWindowSize) {
		core.setExpWindowSize(expWindowSize);
	}
	public double getWindowVarK() {
		return core.getWindowVarK();
	}
	public void setWindowVarK(double windowVarK) {
		core.setWindowVarK(windowVarK);
	}
	public double getFftVarK() {
		return core.getFftVarK();
	}
	public void setFftVarK(double fftVarK) {
		core.setFftVarK(fftVarK);
	}
	public double getAmplitudeRatio() {
		return core.getAmplitudeRatio();
	}
	public void setAmplitudeRatio(double amplitudeRatio) {
		core.setAmplitudeRatio(amplitudeRatio);
	}
	
}
