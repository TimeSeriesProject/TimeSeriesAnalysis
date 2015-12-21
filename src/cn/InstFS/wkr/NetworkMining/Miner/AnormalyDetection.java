package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

/**
 * Created by xzbang on 2015/3/24.
 */
public class AnormalyDetection implements IMinerTSA {
    private int initWindowSize = 30;
    private int maxWindowSize = 60;
    private int expWindowSize = 3;
    private double k=3.0;
    private DataItems di;
    private DataItems outlies;
    public AnormalyDetection(){}
    public AnormalyDetection(DataItems di){
    	this.di=di;
    }
    public AnormalyDetection(int initWindowSize,int maxWindowSize,int expWindowSize,double k,DataItems di){
    	this.initWindowSize = initWindowSize;
    	this.maxWindowSize=maxWindowSize;
    	this.expWindowSize = expWindowSize;
    	this.k = k;
    	this.di=di;
    }
    
    @Override
    public void TimeSeriesAnalysis() {
    	if(di==null){
    		return;
    	}
    	HashMap<Long,Long> slice = new HashMap<Long, Long>();
    	List<String> data = di.data;
    	List<Date> time = di.time;
    	outlies = new DataItems();
    	int size = time.size();
    	for(int i = 0;i < size;i++){
    		slice.put((long)i, Math.round(Double.parseDouble(data.get(i))));
    	}
    	HashMap<Long,Long> result = detect(slice);

    	Iterator<Entry<Long, Long>> iterator=result.entrySet().iterator();
    	while (iterator.hasNext()) {
			long index=iterator.next().getKey();
			outlies.add1Data(time.get((int)index), data.get((int)index));
		}
     	for(int i=0;i < size;i++){
    		if(result.containsKey((long)i)){
    			System.out.println(i+1);
    			outlies.add1Data(time.get(i), data.get(i));
    		}
    	}
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
                            slice.put((long)index, (long)normalDistributionTest.getMean());
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

    @Override
    public DataItems getOutlies() {
    	return outlies;
    }
    
    @Override
    public DataItems getPredictItems() {
    	// TODO Auto-generated method stub
    	return null;
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
	public double getK() {
		return k;
	}
	public void setK(double k) {
		this.k = k;
	}
	public DataItems getDi() {
		return di;
	}
	public void setDi(DataItems di) {
		this.di = di;
	}
	
}
