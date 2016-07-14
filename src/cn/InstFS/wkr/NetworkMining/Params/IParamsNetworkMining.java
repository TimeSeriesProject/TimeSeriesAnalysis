package cn.InstFS.wkr.NetworkMining.Params;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;


//import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;

public abstract class IParamsNetworkMining implements Serializable{
	public static String spliter = "__\t__";
	
	public abstract boolean equals(IParamsNetworkMining params);
	
	public static IParamsNetworkMining fromString(String str){
		
		IParamsNetworkMining params = new ParamsPM();//默认

		String []strs = str.split(spliter);
		for (int i = 0; i < strs.length; i++){
			String []strs1 = strs[i].split(":");	
			if(strs1.length > 1 && strs1[0].endsWith("class")){
				if (strs1[1].endsWith("ParamsPM")){
					params = new ParamsPM();
					break;
				}else if (strs1[1].endsWith("ParamsTSA")){
					params = new ParamsTSA();
					break;
				}else if (strs1[1].endsWith("ParamsSM")){
					params = new ParamsSM();
					break;
				}
			}			
		}
		
		PropertyDescriptor []descs = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(params.getClass());
			descs = beanInfo.getPropertyDescriptors();
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
			return params;
		}
		
		for (int i = 1; i < strs.length; i ++){
			String []strs1 = strs[i].split(":");			
			for (int j = 0; j < descs.length; j ++){
				if (descs[j].getName().equals(strs1[0]))
					try {
						Class cls = descs[j].getPropertyType();
						Object val = null;
						if (cls.equals(Integer.class) || cls.equals(int.class))
							val = Integer.parseInt(strs1[1].split("\\.")[0]);
						else if (cls.equals(Double.class) || cls.equals(double.class))
							val = Double.parseDouble(strs1[1]);
						else if(cls.equals(Date.class))
							val=new Date(Long.parseLong(strs1[1].substring(0,10))*1000);
						else
							val = strs1[1];
						if (descs[j].getWriteMethod() != null){
							if (strs1[0].equalsIgnoreCase("discreteMethod"))
								descs[j].getWriteMethod().invoke(params, new Object[]{DiscreteMethod.valueOf(val.toString())});
							else
								descs[j].getWriteMethod().invoke(params, new Object[]{val});	
						}
							
												
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
			}			
		}
		return params;
	}
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(spliter);
		try {
			BeanInfo info = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor []descs = info.getPropertyDescriptors();
			for(PropertyDescriptor desc : descs){
				Object val = desc.getReadMethod().invoke(this, new Object[0]);
				if (val != null) {
					sb.append(desc.getName());
					sb.append(":" + val);
					sb.append(spliter);
				}
			}
			
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
			return sb.toString();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static IParamsNetworkMining newInstance(IParamsNetworkMining oldParams){
		IParamsNetworkMining newP = null;
		if (oldParams instanceof ParamsPM)
			newP = ParamsPM.newInstance((ParamsPM)oldParams);
		else if (oldParams instanceof ParamsTSA)
			newP = ParamsTSA.newInstance((ParamsTSA)oldParams);
		else if (oldParams instanceof ParamsSM)
			newP = ParamsSM.newInstance((ParamsSM)oldParams);
		return newP;
	}
}
