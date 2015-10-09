package cn.InstFS.wkr.NetworkMining.UIs.Utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class UtilsClass {

	
	public static void cloneExistingBean(Object src, Object dst){
		try {
			BeanInfo info = Introspector.getBeanInfo(dst.getClass());
			PropertyDescriptor []descs = info.getPropertyDescriptors();
			for (PropertyDescriptor desc : descs){
				Method read = desc.getReadMethod();
				Method write = desc.getWriteMethod();
				if(read != null && write != null){
					Object val = desc.getReadMethod().invoke(src, new Object[0]);
					if (val != null){
						desc.getWriteMethod().invoke(dst, new Object[]{val});
					}
				}				
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
