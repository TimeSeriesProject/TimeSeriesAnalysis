package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;

import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;

public class ParamsPM extends IParamsNetworkMining implements Serializable{
    private static final long serialVersionUID = 7245563716842366619L;
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
    public static ParamsPM newInstance(ParamsPM p){
    	ParamsPM param = new ParamsPM();
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(ParamsPM.class);
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
