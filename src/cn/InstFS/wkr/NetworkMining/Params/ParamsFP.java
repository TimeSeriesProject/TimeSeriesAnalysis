package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Date;

public class ParamsFP extends IParamsNetworkMining {
    //高斯滑动窗口模型算法参数
    private double minSupport;	// 最小支持度
    private int sizeWindow;	// 时间窗长	（单位为秒）
    private int stepWindow;	// 步长
    private int minSeqLen;	// 最短序列模式长度

    public ParamsFP() {
        minSupport = 0.4;
        sizeWindow = 10;
        stepWindow = 10;
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

    public static ParamsFP newInstance(ParamsFP p){
        ParamsFP param = new ParamsFP();
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
