package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMGuassianParams;

/**
 * Created by xzbang on 2015/3/24.
 */
public class AnormalyDetection implements IMinerOM {
    private static int initWindowSize = 200;
    private static int maxWindowSize = 300;
    private static int expWindowSize = 3;
    private static double k=3.0; //高斯距离阈值 (x-u)/sigma > k 则x点是异常点（暂时没用到）
    private static double diff = 0.2; //计算异常度阈值时的参数，判断前后2个差值是否满足(d1-d2)/d2 > diff，满足则d1是异常度阈值，否则不是
    private double threshold; //异常度阈值（非参数）
    private double minthreshod = 0.7;

    private DataItems di;
    private DataItems outlies;
    private DataItems outDegree = new DataItems(); //异常度
    private Map<Integer, Double> degreeMap = new HashMap<Integer, Double>();
	private List<DataItems> outlinesSet = new ArrayList<DataItems>(); //异常线段
	private int outway = 1; //生成异常的方式，取值为1,2;对应的分别为genOutlier和genOutlier2
    public AnormalyDetection(){}
    public AnormalyDetection(DataItems di){
    	this.di=di;
    }
    public AnormalyDetection(DataItems di,double threshold){
    	this.di = di;
    	this.threshold = threshold;
    	this.outway = 2;
    }
    public AnormalyDetection(int initWindowSize,int maxWindowSize,int expWindowSize,int outway,double minthreshod ,DataItems di){
    	this.initWindowSize = initWindowSize;
    	this.maxWindowSize=maxWindowSize;
    	this.expWindowSize = expWindowSize;
    	this.outway = outway;
    	this.minthreshod = minthreshod;
    	this.di=di;
    }
    public AnormalyDetection(OMGuassianParams omGuassianParams,DataItems di){
    	this.initWindowSize = omGuassianParams.getInitWindowSize();
    	this.maxWindowSize=omGuassianParams.getMaxWindowSize();
    	this.expWindowSize = omGuassianParams.getExpWindowSize();
    	this.k = omGuassianParams.getWindowVarK();
    	this.diff = omGuassianParams.getDiff();
    	this.di=di;
    }
    @Override
    public void TimeSeriesAnalysis() {
    	if(di==null){
    		return;
    	}
    	HashMap<Long,Double> slice = new HashMap<Long, Double>(); //原始数据，map<i,data>
    	List<String> data = di.data;
    	List<Date> time = di.time;
    	outlies = new DataItems();
    	int size = time.size();
    	for(int i = 0;i < size;i++){
//    		slice.put((long)i, Math.round(Double.parseDouble(data.get(i))));
    		slice.put((long)i, Double.parseDouble(data.get(i)));
    	}
//    	detect(slice);
    	if(outway == 1){
    		detect1(slice);
    	}else{
    		detect(slice);
    	}
//    	detect1(slice);
     	outDegree = mapToDegree(degreeMap);     	
     	outlies = genOutlier(outDegree);
    }
    /**
     * 高斯滑动窗口
     * 窗口重叠*/
    public void detect(HashMap<Long,Double> slice){
        if(slice == null){
            return;
        }
        int size = slice.size();
        if(size < 1){
            return;
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
                	index += expWindowSize;
                	nowWindowSize+=expWindowSize;
                }else{
                    if(slice.get((long) index)!=null){
                        double target = (double)slice.get((long) index);
                        if(!normalDistributionTest.isDawnToThisDistri(target)){
                            //outlier.put((long) index,slice.get((long) index));
                            slice.put((long)index, normalDistributionTest.getMean());
                        }
                        double mean = normalDistributionTest.getMean();                        
                        double stv;
                    	if(normalDistributionTest.getStdeviation()<=0){
                        	stv = 1e-3;
                        }else {
        					stv = normalDistributionTest.getStdeviation();
        				}
                    	double distance = Math.abs(target - mean)/stv;
                    	distance = distance>5 ? 1 : distance/5;
                        degreeMap.put(index, distance);
                    }                    
                    break;
                }
            }
            index++;
            nowWindowSize=initWindowSize;
        }
       
    }
    /**@author LYH
     * 滑动高斯检测，滑动窗口不重叠
     * 
     * */
    public void  detect1(HashMap<Long,Double> slice){
        if(slice == null){
            return ;
        }
        int size = slice.size();
        if(size < 1){
            return ;
        }
//        HashMap<Long,Long> outlier = new HashMap<>();
        long max = getmaxKey(slice);
        double[] data;
        int index = initWindowSize;
        int nowWindowSize = initWindowSize;
        while(index<=max) {
            data = new double[nowWindowSize]; //窗口内数据
            for (int i = index-nowWindowSize; i < index; i++) {
                if(slice.get((long) i)==null){
                    data[i-index+nowWindowSize] = 0.0;
                }else{
                    data[i-index+nowWindowSize] = (double)slice.get((long)i);
                }
            }
            NormalDistributionTest normalDistributionTest = new NormalDistributionTest(data,k);
            if(!normalDistributionTest.isNormalDistri()&&nowWindowSize<maxWindowSize){            
            	index += expWindowSize;
                nowWindowSize+=expWindowSize;
            }else {               
            	double mean = normalDistributionTest.getMean();
                double stv = normalDistributionTest.getStdeviation();
            	
            	for(int i=index-nowWindowSize;i<index;i++){
                	if (slice.get((long) i)!=null) {						
						double target = (double)slice.get((long)i);
	                    /*if(!normalDistributionTest.isDawnToThisDistri(target)){
//	                        outlier.put((long)i,slice.get((long)i));
//	                        slice.put((long)i, normalDistributionTest.getMean());
	                    }*/
	                    double distance = Math.abs(data[i-index+nowWindowSize] - mean)/stv;
	                    distance = distance>5 ? 1 : distance/5;
	                    degreeMap.put(i, distance);
                    }  
                }       
            	nowWindowSize=initWindowSize;   
                index = index + nowWindowSize;                             
            }            
        }
        
        //从后面向前滑动一个窗口
        while(nowWindowSize<max){
        	int nowwindowSize = initWindowSize;
            data = new double[nowwindowSize];
            for(int i=(int)max-nowwindowSize;i<max;i++){        	
            	data[i+nowwindowSize-(int)max] = slice.get((long)i);
            }
            NormalDistributionTest normalDistributionTest = new NormalDistributionTest(data,k);
            if(!normalDistributionTest.isNormalDistri()&&nowWindowSize<maxWindowSize){                        	
                nowWindowSize+=expWindowSize;
            }else{
            	double mean = normalDistributionTest.getMean();
                double stv = normalDistributionTest.getStdeviation();
                
                for(int i=(int)max-nowWindowSize;i<max;i++){
                	double distance =Math.abs(slice.get((long)i) - mean)/stv;
                	distance = distance>5 ? 1 : distance/5;
                	degreeMap.put(i, distance);        	
                }
                outDegree = mapToDegree(degreeMap);
                break;
            }
            
        }              
    }
    private long getmaxKey(HashMap<Long, Double> slice) {
        Iterator<Long> iterator = slice.keySet().iterator();
        long max = 0;
        while(iterator.hasNext()){
            Long sliceid = (Long)iterator.next();
            if (sliceid > max){
                max = sliceid;
            }
        }
        return max;
    }
    //获取异常度
    private DataItems mapToDegree(Map<Integer, Double> map){
    	DataItems degree = new DataItems();
    	
    	for(int i=0;i<di.getLength();i++){
    		if(map.get(i) == null){
    			degree.add1Data(di.getTime().get(i), "0");	
    		}
    		else{
    			degree.add1Data(di.getTime().get(i),String.valueOf(map.get(i)));
    		}
    	}
    	
    	return degree;
    }
    //获取异常点
    public DataItems genOutlier(DataItems degreeItems){
    	List<Double> degree = new ArrayList<Double>();
    	for(int i=0;i<degreeItems.getLength();i++){
    		degree.add(Double.parseDouble(degreeItems.getData().get(i)));
    	}
    	DataItems outline = new DataItems();
		List<Double> list = new ArrayList<Double>();
		list.addAll(degree);		
		int len = degree.size();
		Collections.sort(list);
		Collections.reverse(list);
		double d = list.get((int)(len*0.01));
		//threshold = threshold>0.45 ? threshold : 0.45;
		for(int i=(int)(len*0.02);i>0;i--){
			if(list.get(i)<0.5){				
				continue;
			}
			else if((list.get(i)-d)/d<diff){
				continue;
			}else{
				threshold = list.get(i);
				break;
			}
		}
		threshold = threshold<minthreshod ? minthreshod : threshold;		
		System.out.println("异常度阈值是："+threshold);
		for(int i=0;i<len;i++){
			if(degree.get(i)>threshold){
				outline.add1Data(di.getElementAt(i));
			}
		}
		
		return outline;
	}
    public DataItems genOutlier2(DataItems degreeItems){
    	DataItems outline = new DataItems();
    	List<Double> degree = new ArrayList<Double>();
    	for(int i=0;i<degreeItems.getLength();i++){
    		degree.add(Double.parseDouble(degreeItems.getData().get(i)));
    	}
    	for(int i=0;i<degree.size();i++){
			if(degree.get(i)>=threshold){
				outline.add1Data(di.getElementAt(i));
			}
		}
		
		return outline;
    }
    @Override
    public DataItems getOutlies() {
    	return outlies;
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
	
	@Override
	public DataItems getOutDegree() {
		return outDegree;
	}
	@Override
	public List<DataItems> getOutlinesSet() {
		return outlinesSet;
	}
	
}
