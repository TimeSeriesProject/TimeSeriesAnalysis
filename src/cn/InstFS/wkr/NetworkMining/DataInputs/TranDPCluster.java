package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lineAssociation.ClusterWrapper;
import lineAssociation.DPCluster;
import lineAssociation.Linear;
import lineAssociation.SymbolNode;
import cn.InstFS.wkr.NetworkMining.Params.ParamsAPI;
import cn.InstFS.wkr.NetworkMining.Params.AssociationRuleParams.AssociationRuleLineParams;

public class TranDPCluster {
	DataItems dataItems = new DataItems();//原始数据
	DataItems clusterItems = new DataItems();//输出聚类结果   线段起始时间:类中心
	List<PatternMent> segPatterns = new ArrayList<PatternMent>();
	TreeMap<Integer, Linear> linears = new TreeMap<Integer, Linear>();
	public TreeMap<Integer,Double> GAMMA = new TreeMap<Integer, Double>();
	public TranDPCluster(DataItems dataItems,List<PatternMent> segPatterns,AssociationRuleLineParams arp){
		this.dataItems = dataItems;
		this.segPatterns = segPatterns;
		run();
	}
	public void run(){
		/*修改线段数据格式*/
		linears = TranLinears();
		
		/*开始DPC聚类*/
		System.out.println("开始运行DPCluster聚类算法！");
		ClusterWrapper clusterWrapper = new ClusterWrapper(linears,ParamsAPI.getInstance().getAssociationRuleParams().getAssociationRuleLineParams());
		Map<Integer,Integer> map = clusterWrapper.run();
		GAMMA = clusterWrapper.GAMMA;
		System.out.println("DPCluster聚类算法计算完毕！");
		/*聚类结果*/
		clusterItems = tranClusterMap(map); //线段起始时间:类中心
	}
	/**
	 * @title TranLinears
	 * @description 修改线段的数据格式
	 * @param 线段列表
	 * @return TreeMap<Integer, Linear> 线段起始位置:线段*/
	public TreeMap<Integer, Linear> TranLinears(){
		TreeMap<Integer, Linear> linears = new TreeMap<Integer, Linear>();
		for(int i=0;i<segPatterns.size();i++){
			double theta = segPatterns.get(i).getSlope();
			int startTime = segPatterns.get(i).getStart();
			int span = segPatterns.get(i).getLen();
			double startValue = segPatterns.get(i).getStartValue();
			double Hspan = segPatterns.get(i).getHspan();
			Linear linear = new Linear(theta,startTime,span,startValue);
			linear.hspan = Hspan;
			linears.put(startTime, linear);
		}
		return linears;
	}
	/**
	 * @title getSymbols
	 * @description 修改聚类结果的数据格式
	 * @param Map<Integer, Integer> 线段序列:是否为类中心(或类中心是哪条线段)
	 * @return TreeMap<Integer, Integer> 线段序列:类中心*/
	private TreeMap<Integer, Integer> getSymbols(Map<Integer, Integer> map) {

		TreeMap<Integer, Integer> symbolMap = new TreeMap<Integer, Integer>();
		for(int i:map.keySet())
		{
           int center = map.get(i);
           if(center==-2)     //异常点跳过
            	continue;
            else if(center==-1)
            	center = i;
//            SymbolNode symbolNode = new SymbolNode(center,series);
            symbolMap.put(i,center);  //哪个样本点归属那个中心，中心代表类别
            
		}
		return symbolMap;
	}
	/**
	 * @title tranClusterMap
	 * @description 修改聚类结果的数据格式
	 * @param Map<Integer, Integer> 线段序列:是否为类中心(或类中心是哪条线段)
	 * @return DataItems 线段序列起始时间:类中心*/
	private DataItems tranClusterMap(Map<Integer, Integer> map) {
		DataItems clusterItems = new DataItems();
		for(int i=0;i<dataItems.getLength();i++){
            Object center = map.get(i);
            if(center==null){
            	continue;
            }
            if(Integer.parseInt(String.valueOf(center))==-2){//异常点跳过
            	if(!map.containsValue(i)){
            		center = i;
            	}else{
            		center = i+1;
            	}
            }            	
            Date time = dataItems.getTime().get(i);
            clusterItems.add1Data(time, String.valueOf(center));            
		}
		return clusterItems;
	}
	
	
	public DataItems getClusterItems() {
		return clusterItems;
	}
	public void setClusterItems(DataItems clusterItems) {
		this.clusterItems = clusterItems;
	}

}
