package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.NodeAMent;
import cn.InstFS.wkr.NetworkMining.DataInputs.NodeAPatter;
import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMGaussianNodeParams;
/**
 * @author LYH
 * @decription 应用于节点的出现与消失的高斯异常检测算法
 * @Alogo 统计0,1连续出现的长度序列，分别拟合高斯分布，认为概率小的线段为异常
 * */
public class GaussianOutlierDetection implements IMinerOM{
	private DataItems dataItems = new DataItems();
    private DataItems outlies = new DataItems();
    private DataItems outDegree = new DataItems(); //异常度
    private List<DataItems> outlinesSet = new ArrayList<DataItems>(); //异常线段
    private List<NodeAPatter> patters0 = new ArrayList<NodeAPatter>(); //存储序列为0的线段
    private List<NodeAPatter> patters1 = new ArrayList<NodeAPatter>(); //存储序列为1的线段
    private static double diff = 0.2; //计算异常度阈值时的参数，判断前后2个差值是否满足(d1-d2)/d2 > diff，满足则d1是异常度阈值，否则不是
    private double threshold; //异常度阈值（非参数）
    public GaussianOutlierDetection(DataItems di){
    	this.dataItems = di;
    }
    public GaussianOutlierDetection(OMGaussianNodeParams omGaussianNodeParams,DataItems di){
    	this.dataItems = di;
    	this.diff = omGaussianNodeParams.getDiff();
    }
    @Override
    public void TimeSeriesAnalysis(){
    	if(dataItems==null){
    		return;
    	}
    	//生成线段
    	NodeAMent nodeAMent0 = new NodeAMent(dataItems, 0);
    	NodeAMent nodeAMent1 = new NodeAMent(dataItems, 1);
    	patters0 = nodeAMent0.getPatters();
    	patters1 = nodeAMent1.getPatters();    	
    	//异常检测
    	OutlierDetection();
    }
    
    /**
     * 异常检测
     * */
    public void OutlierDetection(){
    	//生成数据集
    	List<Integer> dataSet0 = new ArrayList<Integer>();
    	List<Integer> dataSet1 = new ArrayList<Integer>();
    	for(int i=0;i<patters0.size();i++){
    		dataSet0.add(patters0.get(i).getLength());
    	}
    	for(int i=0;i<patters1.size();i++){
    		dataSet1.add(patters1.get(i).getLength());
    	}
    	
    	//高斯建模
    	NormalDistribution distribution0 = new NormalDistribution();
    	NormalDistribution distribution1 = new NormalDistribution();
    	if(dataSet0 != null){
    		distribution0 =  NormalDistributionTest(dataSet0);
    	}
    	if(dataSet1 != null){
    		distribution1 =  NormalDistributionTest(dataSet1);
    	}    	    	
    	//计算高斯距离,并生成异常度Map
    	Map<Integer, Double> degreeMap = new HashMap<Integer, Double>();    	
    	Map<Integer, Double> degreeMap0 = new HashMap<Integer, Double>();
    	Map<Integer, Double> degreeMap1 = new HashMap<Integer, Double>();
    	
    	
    	for(int i=0;i<dataSet0.size();i++){
    		double mean0 = distribution0.getMean();
    		double std0 = distribution0.getStandardDeviation();
    		double dis = Math.abs(dataSet0.get(i) - mean0)/std0;
    		int start = patters0.get(i).getStart();
    		int end = patters0.get(i).getEnd();
    		for(int j=start;j<=end;j++){
    			degreeMap.put(j, dis);
    		}
    		degreeMap0.put(i, dis);
    	}
    	for(int i=0;i<dataSet1.size();i++){
    		double mean1 = distribution1.getMean();    	
        	double std1 = distribution1.getStandardDeviation();
    		double dis = Math.abs(dataSet1.get(i) - mean1)/std1;
    		int start = patters1.get(i).getStart();
    		int end = patters1.get(i).getEnd();
    		for(int j=start;j<=end;j++){
    			degreeMap.put(j, dis);
    		} 
    		degreeMap1.put(i, dis);
    	}
    	
    	//生成异常度
    	for(int i=0;i<degreeMap.size();i++){
    		Date time = dataItems.getTime().get(i);
    		double data = degreeMap.get(i);
    		data = data>5 ? 1 : data/5;
    		outDegree.add1Data(time,String.valueOf(data));
    	}
    	//计算异常阈值
    	comThreshold(degreeMap);
    	//生成异常点
    	genOutlies(outDegree);
    	//生成异常线段
    	Map<Integer, DataItems> mapSet = genOutset(degreeMap0, degreeMap1);
    	for(int i=0;i<dataItems.getLength();i++){
    		if(mapSet.get(i)!=null){
    			outlinesSet.add(mapSet.get(i));
    		}
    	}
     }
    public NormalDistribution NormalDistributionTest(List<Integer> list){        
        DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (int o : list) {
            statistics.addValue(o);
        }
        double mean = statistics.getMean();
        double stdeviation = statistics.getStandardDeviation();
        if(stdeviation <= 0){
            stdeviation = 0.0001;
        }
        NormalDistribution distribution = new NormalDistribution();
        distribution = new NormalDistribution(mean, stdeviation);
        return distribution;
    }
    /**
     * @title genOutlies生成异常点
     * @param map 异常度Map
     * */
    public void comThreshold(Map<Integer, Double> map){
    	List<Double> list = new ArrayList<Double>();
    	for(int i=0;i<map.size();i++){
    		list.add(map.get(i));
    	}
    	Collections.sort(list);
    	Collections.reverse(list);
    	int len = list.size();
    	double d = list.get((int)(len*2/100));
    	
    	threshold = threshold>0.4 ? threshold : 0.4;
    	for(int i=(int)(len*0.02);i>0;i--){
    		if(list.get(i)<0.4){				
    			continue;
    		}else if((list.get(i)-d)/d<diff){
    			continue;
    		}else{
    			threshold = list.get(i);
    			break;
    		}    		    	
    	}
    	//threshold = threshold>0.6 ? 0.6 : threshold;
    	System.out.println("基于高斯异常检测的异常阈值："+threshold);
    }
    /**
     * @title genOutlies 生成异常点
     * @param map 异常度Map
     * */
    public void genOutlies(DataItems degree){
    	for(int i=0;i<degree.getLength();i++){
    		if(Double.parseDouble(degree.getData().get(i))>threshold){
    			outlies.add1Data(dataItems.getElementAt(i));
    		}
    	}
    }
    /**
     * @title genOutset 生成异常线段
     * @param map0,map1 线段0,1的异常度map
     * @return Map<Integer, DataItems>
     * */
    public Map<Integer, DataItems> genOutset(Map<Integer, Double> map0,Map<Integer, Double> map1){
    	Map<Integer, DataItems> map = new HashMap<Integer, DataItems>();
    	for(int i=0;i<map0.size();i++){
    		if(map0.get(i)>threshold*5){
    			DataItems items = new DataItems();
    			int start = patters0.get(i).getStart();
    			int end = patters0.get(i).getEnd();
    			for(int j=start;j<=end;j++){
    				items.add1Data(dataItems.getElementAt(j));
    			}
    			map.put(start, items);
    		}
    	}
    	for(int i=0;i<map1.size();i++){
    		if(map1.get(i)>threshold*5){
    			DataItems items = new DataItems();
    			int start = patters1.get(i).getStart();
    			int end = patters1.get(i).getEnd();
    			for(int j=start;j<=end;j++){
    				items.add1Data(dataItems.getElementAt(j));
    			}
    			map.put(start, items);
    		}
    	}
    	return map;
    }
    @Override
	public DataItems getOutlies(){
		return outlies;
	}
    @Override
	public List<DataItems> getOutlinesSet(){
		return outlinesSet;
	}
    @Override
	public DataItems getOutDegree(){
		return outDegree;
	}
}
