package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

public class ParamsPP extends IParamsNetworkMining implements Serializable{
    private static final long serialVersionUID = -6215239678007025066L;
    private Double periodThreshold;  //PP参数，确定周期的平均熵阈值
    public ParamsPP() {
        periodThreshold = 0.2;
    }

    public Double getPeriodThreshold(){
        return this.periodThreshold;
    }
    public void setPeriodThreshold(Double periodThreshold){
        this.periodThreshold=periodThreshold;
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


    public static ParamsPP newInstance(ParamsPP p){
        ParamsPP param = new ParamsPP();
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(ParamsPP.class);
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
}
