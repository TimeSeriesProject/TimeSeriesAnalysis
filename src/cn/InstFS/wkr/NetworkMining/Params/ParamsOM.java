package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMFastFourierParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMGuassianParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMMultidimensionalParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMPiontPatternParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMSAXPartternParams;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMTEOParams;

public class ParamsOM extends IParamsNetworkMining implements Serializable {
     
    OMGuassianParams omGuassianParams = null;
    OMFastFourierParams omFastFourierParams = null;
    OMPiontPatternParams omPiontPatternParams = null;
    OMTEOParams omteoParams = null;
    OMSAXPartternParams omSaxPartternParams = null;
    OMMultidimensionalParams omMultidimensionalParams = null;
    public ParamsOM() {}
    public OMGuassianParams getOmGuassianParams() {
		return omGuassianParams;
	}

	public void setOmGuassianParams(OMGuassianParams omGuassianParams) {
		this.omGuassianParams = omGuassianParams;
	}	
	
    public OMFastFourierParams getOmFastFourierParams() {
		return omFastFourierParams;
	}
	public void setOmFastFourierParams(OMFastFourierParams omFastFourierParams) {
		this.omFastFourierParams = omFastFourierParams;
	}
	
	
	public OMPiontPatternParams getOmPiontPatternParams() {
		return omPiontPatternParams;
	}
	public void setOmPiontPatternParams(OMPiontPatternParams omPiontPatternParams) {
		this.omPiontPatternParams = omPiontPatternParams;
	}
	
	
	public OMTEOParams getOmteoParams() {
		return omteoParams;
	}
	public void setOmteoParams(OMTEOParams omteoParams) {
		this.omteoParams = omteoParams;
	}
	
	public OMSAXPartternParams getOmSaxPartternParams() {
		return omSaxPartternParams;
	}
	public void setOmSaxPartternParams(OMSAXPartternParams omSaxPartternParams) {
		this.omSaxPartternParams = omSaxPartternParams;
	}
	
	public OMMultidimensionalParams getOmMultidimensionalParams() {
		return omMultidimensionalParams;
	}
	public void setOmMultidimensionalParams(
			OMMultidimensionalParams omMultidimensionalParams) {
		this.omMultidimensionalParams = omMultidimensionalParams;
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
    public static ParamsOM newInstance(ParamsOM p){
        ParamsOM param = new ParamsOM();
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(ParamsOM.class);
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
