package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

public class ParamsFM extends IParamsNetworkMining implements Serializable {
    //高斯滑动窗口模型算法参数
    private int initWindowSize = 10;//初始窗口大小
    private int maxWindowSize = 30;//最大窗口大小
    private int expWindowSize = 3;//扩展窗口大小
    private double windowVarK=3.0;//异常标准差倍数阈值

    //傅里叶变换算法参数
    private double fftVarK = 1.5;//异常标准差倍数阈值
    private double amplitudeRatio = 0.9;//振幅接收比例


    private Double periodThreshold;
    private Double outlierThreshold;
    private int predictPeriod;

    public ParamsFM() {
        initWindowSize = 10;
        maxWindowSize = 30;
        expWindowSize = 3;
        windowVarK=3.0;
        fftVarK = 1.5;
        amplitudeRatio = 0.9;
        periodThreshold = 1.5;
        outlierThreshold = 0.95;
        predictPeriod=5;
    }

    public int getPredictPeriod() {
        return predictPeriod;
    }

    public void setPredictPeriod(int predictPeriod) {
        this.predictPeriod = predictPeriod;
    }

    public Double getPeriodThreshold(){
        return this.periodThreshold;
    }
    public Double getOutlierThreshold() {
        return outlierThreshold;
    }

    public void setPeriodThreshold(Double periodThreshold){
        this.periodThreshold=periodThreshold;
    }
    public void setOutlierThreshold(Double outlierThreshold) {
        this.outlierThreshold = outlierThreshold;
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
    public static ParamsFM newInstance(ParamsFM p){
        ParamsFM param = new ParamsFM();
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(ParamsFM.class);
            PropertyDescriptor []descs = info.getPropertyDescriptors();
            for (PropertyDescriptor desc : descs){
                Object val = desc.getReadMethod().invoke(p, new Object[0]);
                if (desc.getWriteMethod() != null)
                    desc.getWriteMethod().invoke(param, new Object[]{val});
            }
        } catch (Exception e) {
            return param;
        }
        return param;
    }
    public int getInitWindowSize() {
        return initWindowSize;
    }
    public void setInitWindowSize(int initWindowSize) {
        this.initWindowSize = initWindowSize;
    }
    public int getMaxWindowSize() {
        return maxWindowSize;
    }
    public void setMaxWindowSize(int maxWindowSize) {
        this.maxWindowSize = maxWindowSize;
    }
    public int getExpWindowSize() {
        return expWindowSize;
    }
    public void setExpWindowSize(int expWindowSize) {
        this.expWindowSize = expWindowSize;
    }
    public double getWindowVarK() {
        return windowVarK;
    }
    public void setWindowVarK(double windowVarK) {
        this.windowVarK = windowVarK;
    }
    public double getFftVarK() {
        return fftVarK;
    }
    public void setFftVarK(double fftVarK) {
        this.fftVarK = fftVarK;
    }
    public double getAmplitudeRatio() {
        return amplitudeRatio;
    }
    public void setAmplitudeRatio(double amplitudeRatio) {
        this.amplitudeRatio = amplitudeRatio;
    }
}
