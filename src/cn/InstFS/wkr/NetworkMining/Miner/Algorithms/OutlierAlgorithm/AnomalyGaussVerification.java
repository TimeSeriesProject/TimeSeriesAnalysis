package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;

public class AnomalyGaussVerification implements IMinerOM{
	private DataItems di = new DataItems();
    private DataItems outlies = new DataItems();
    private DataItems outDegree = new DataItems(); //异常度
    private Map<Integer, Double> degreeMap = new HashMap<Integer, Double>();
	private List<DataItems> outlinesSet = new ArrayList<DataItems>(); //异常线段
	
	int gaosik = 3; //初始异常点的高斯值
	public AnomalyGaussVerification(DataItems di){
		this.di = di;
	}
		
	@Override
	public void TimeSeriesAnalysis() {
		if(di==null){
    		return;
    	}
		//数据预处理
    	HashMap<Long,Double> slice = new HashMap<Long, Double>(); //原始数据，map<i,data>
    	List<String> data = di.data;
    	List<Date> time = di.time;
    	outlies = new DataItems();
    	int size = time.size();
    	for(int i = 0;i < size;i++){
    		slice.put((long)i, Double.parseDouble(data.get(i)));
    	}
    	//进行异常检测
    	outliersDetection(slice);
	}
	
	public void outliersDetection(HashMap<Long,Double> slice){
		//找出初始全局异常点
		HashMap<Long, Double> iniOutliers = new HashMap<Long, Double>();
		double[] data = new double[slice.size()];
        for (int i=0;i<slice.size();i++) {
        	data[i] = slice.get((long)i);
        }        
        NormalDistributionTest normalDistributionTest = new NormalDistributionTest(data,gaosik);
        for(long i=0;i<slice.size();i++){
        	if(!normalDistributionTest.isDawnToThisDistri(slice.get(i))){
        		iniOutliers.put(i, slice.get(i));
        	}
        }
		//对异常点进行检测和判断
        for(long i : iniOutliers.keySet()){
        	if(isOutlier(slice, i)){
        		DataItem dataItem = di.getElementAt((int)i);        		
        		outlies.add1Data(dataItem);
        	}
        }
	}
	
	//对其中一个初始异常点扩展局部数据框,并验证其是否为异常点
	public boolean isOutlier(HashMap<Long,Double> slice,long index){
		boolean isOutlier = false;
		List<Double> list = new ArrayList<Double>();
		list.add(slice.get(index));
		long before = index-1;
		long after = index+1;
		while(before>=0 && after<slice.size()){
			double beforeData = slice.get(before);
			double afterData = slice.get(after);
			double data = slice.get(index);
			if(Math.abs(data-afterData)<Math.abs(data-beforeData)){
				list.add(afterData);
				after++;
			}else{
				list.add(beforeData);
				before--;
			}
			if(list.size()>60){
				NormalDistributionTest norDistributionTest = new NormalDistributionTest(list, gaosik);
				if(norDistributionTest.isNormalDistri()){
					if(norDistributionTest.isDawnToThisDistri(data)){
						isOutlier = true;
					}
					break;
				}	
			}
					
		}
		return isOutlier;
	}
	
	
	@Override
	public DataItems getOutlies() {
		// TODO Auto-generated method stub
		return outlies;
	}

	@Override
	public List<DataItems> getOutlinesSet() {
		// TODO Auto-generated method stub
		return outlinesSet;
	}

	@Override
	public DataItems getOutDegree() {
		// TODO Auto-generated method stub
		return outDegree;
	}
	
}
