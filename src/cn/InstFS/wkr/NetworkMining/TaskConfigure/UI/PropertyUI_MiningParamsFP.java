package cn.InstFS.wkr.NetworkMining.TaskConfigure.UI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Miner.INetworkMiner;
import cn.InstFS.wkr.NetworkMining.Params.IParamsNetworkMining;
import cn.InstFS.wkr.NetworkMining.Params.ParamsFP;
import cn.InstFS.wkr.NetworkMining.Params.ParamsSM;
import cn.InstFS.wkr.NetworkMining.Params.ParamsTSA;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;

public class PropertyUI_MiningParamsFP implements IObjectDescriptor<ParamsFP>{
    ParamsFP core;



    String [] names = new String[]{"periodThreshold", "endNodes", "discreteMethod"};
    String []CnNames = new String[]{ "检测门限","区间端点（逗号隔开）", "离散化方法"};
    HashMap<String, String> displayNames = new HashMap<String,String>();

    public PropertyUI_MiningParamsFP(IParamsNetworkMining core) {
        displayNames.clear();
        this.core = (ParamsFP) core;

        for(int i = 0; i < names.length; i ++)
            displayNames.put(names[i], CnNames[i]);

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
//        props.add(getPropDesc("sizeWindow", 0, "窗口大小"));
//        props.add(getPropDesc("stepWindow", 0, "窗口步长，即：<br>与上一窗口相比，当前窗口在时间上移动了多少"));
//        props.add(getPropDesc("minSeqLen", 0, "挖掘出来的最短序列长度"));
//        props.add(getPropDesc("minSupport", 0, "最小支持度门限<br>值越小，则挖掘速度越慢，挖掘出结果越丰富<br>值越大，挖掘速度越快，但结果相对较少"));

        return props;
    }

    @Override
    public ParamsFP getCore() {
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
    public int getSizeWindow() {
        return core.getSizeWindow();
    }
    public void setSizeWindow(int sizeWindow) {
        core.setSizeWindow(sizeWindow);
    }
    public int getStepWindow() {
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
