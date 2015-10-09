package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * Created by xzbang on 2015/3/24.
 */
public class AnormalyDetection {
    private static int initWindowSize = 10;
    private static int maxWindowSize = 30;
    private static int expWindowSize = 3;
    private static double k=3.0;
    public AnormalyDetection(){}
    public AnormalyDetection(int initWindowSize,int maxWindowSize,int expWindowSize,double k){
    	AnormalyDetection.initWindowSize = initWindowSize;
    	AnormalyDetection.maxWindowSize=maxWindowSize;
    	AnormalyDetection.expWindowSize = expWindowSize;
    	AnormalyDetection.k = k;
    }
    
    
    
    public DataItems detect(DataItems dataItem){
    	if(dataItem==null){
    		return null;
    	}
    	HashMap<Long,Long> slice = new HashMap<Long, Long>();
    	List<String> data = dataItem.data;
    	List<Date> time = dataItem.time;
    	int size = time.size();
    	for(int i = 0;i < size;i++){
    		//slice.put((long)i, Long.parseLong(data.get(i)));
    		slice.put((long)i, Math.round(Double.parseDouble(data.get(i))));
    	}
    	List<String> newData = new ArrayList<String>();
    	HashMap<Long,Long> result = detect(slice);
    	for(int i=0;i < size;i++){
    		if(result.containsKey((long)i)){
    			newData.add(String.valueOf(result.get((long)i)));
    		}else{
    			newData.add("0");
    		}
    	}
    	DataItems newDataItem = new DataItems();
    	newDataItem.setData(newData);
    	newDataItem.setTime(time);
    	return newDataItem;
    }
    public HashMap<Long,Long> detect(HashMap<Long,Long> slice){
        if(slice == null){
            return null;
        }
        int size = slice.size();
        if(size < 1){
            return null;
        }
        HashMap<Long,Long> outlier = new HashMap<>();
        long max = getmaxKey(slice);
        double[] data;
        int index = initWindowSize;
        int nowWindowSize = initWindowSize;
        while(index<=max) {
            while ((index-nowWindowSize) >= 0 && nowWindowSize <= maxWindowSize) {
                data = new double[nowWindowSize];
                for (int i = index-nowWindowSize; i < index; i++) {
                    if(slice.get((long) i)==null){
                        data[i-index+nowWindowSize] = 0.0;
                    }else{
                        data[i-index+nowWindowSize] = (double)slice.get((long)i);
                    }
                }
                NormalDistributionTest normalDistributionTest = new NormalDistributionTest(data,k);
                if(!normalDistributionTest.isNormalDistri()){
                    nowWindowSize+=expWindowSize;
                }else{
                    if(slice.get((long) index)!=null){
                        double target = (double)slice.get((long) index);
                        if(!normalDistributionTest.isDawnToThisDistri(target)){
                            outlier.put((long) index,slice.get((long) index));
                        }
                    }
                    break;
                }
            }
            index++;
            nowWindowSize=initWindowSize;
        }
        return outlier;
    }

    private long getmaxKey(HashMap<Long, Long> slice) {
        Iterator iterator = slice.keySet().iterator();
        long max = 0;
        while(iterator.hasNext()){
            Long sliceid = (Long)iterator.next();
            if (sliceid > max){
                max = sliceid;
            }
        }
        return max;
    }

    public static int getInitWindowSize() {
        return initWindowSize;
    }

    public static void setInitWindowSize(int initWindowSize) {
        AnormalyDetection.initWindowSize = initWindowSize;
    }

    public static int getMaxWindowSize() {
        return maxWindowSize;
    }

    public static void setMaxWindowSize(int maxWindowSize) {
        AnormalyDetection.maxWindowSize = maxWindowSize;
    }

    public static int getExpWindowSize() {
        return expWindowSize;
    }

    public static void setExpWindowSize(int expWindowSize) {
        AnormalyDetection.expWindowSize = expWindowSize;
    }
	public static double getK() {
		return k;
	}
	public static void setK(double k) {
		k = k;
	}

}
