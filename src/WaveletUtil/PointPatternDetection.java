package WaveletUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;

public class PointPatternDetection implements IMinerOM{

	private DataItems dataItems;     //时间序列 
	private List<SegPattern> patterns;   //TEO 线段模式
	private int densityK;
	private int neighborK;
	private DataItems outlies;
	
	public PointPatternDetection(DataItems dataItems,int densityK,int neghborK){
		this.dataItems=dataItems;
		this.densityK=densityK;
		this.neighborK=neghborK;
		outlies=new DataItems();
	}
	
	public PointPatternDetection(){outlies=new DataItems();}
	
	/**
	 * 检测异常
	 * @return
	 */
	public void outliesDectation(){
		
		//模式奇异度map
		HashMap<SegPattern, Double> patternOutliesMap=new HashMap<SegPattern, Double>();
		
		//计算每个模式的K最近邻
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		double heightMax=0;double heightMin=Double.MAX_VALUE;
		double lengthMin=Double.MAX_VALUE;double lengthMax=0;
		double meanMin=Double.MAX_VALUE;double meanMax=0;
		double stdMin=Double.MAX_VALUE;double stdMax=0;
		
		//min-max归一化
		for(int i=0;i<patterns.size();i++){
			if(heightMax<patterns.get(i).getHeight()){
				heightMax=patterns.get(i).getHeight();
			}
			if(heightMin>patterns.get(i).getHeight()){
				heightMin=patterns.get(i).getHeight();
			}
			if(lengthMax<patterns.get(i).getLength()){
				lengthMax=patterns.get(i).getLength();
			}
			if(lengthMin>patterns.get(i).getLength()){
				lengthMin=patterns.get(i).getLength();
			}
			if(meanMax<patterns.get(i).getMean()){
				meanMax=patterns.get(i).getMean();
			}
			if(meanMin>patterns.get(i).getMean()){
				meanMin=patterns.get(i).getMean();
			}
			if(stdMax<patterns.get(i).getStd()){
				stdMax=patterns.get(i).getStd();
			}
			if(stdMin>patterns.get(i).getStd()){
				stdMin=patterns.get(i).getStd();
			}
		}
		for(int i=0;i<patterns.size();i++){
			patterns.get(i).setHeight((patterns.get(i).getHeight()-heightMin)/(heightMax-heightMin));
			patterns.get(i).setLength((patterns.get(i).getLength()-lengthMin)/(lengthMax-lengthMin));
			patterns.get(i).setMean((patterns.get(i).getMean()-meanMin)/(meanMax-meanMin));
			patterns.get(i).setStd((patterns.get(i).getStd()-stdMin)/(stdMax-stdMin));
		}
		
		
		//计算每一个pattern的k_dist距离
		for(int i=0;i<patterns.size();i++){
			List<Double> dist=new ArrayList<Double>();
			List<Double> distHList=new ArrayList<Double>();
			List<Double> distLList=new ArrayList<Double>();
			List<Double> distMList=new ArrayList<Double>();
			List<Double> distSList=new ArrayList<Double>();
			
			for(int j=0;j<patterns.size();j++){
				if(i==j){
					continue;
				}else{
					double distance=distanceOfPatterns(patterns.get(i),patterns.get(j));
					double disH=Math.abs(patterns.get(i).getHeight()-patterns.get(j).getHeight());
					double disL=Math.abs(patterns.get(i).getLength()-patterns.get(j).getLength());
					double disM=Math.abs(patterns.get(i).getMean()-patterns.get(j).getMean());
					double disS=Math.abs(patterns.get(i).getStd()-patterns.get(j).getStd());
					addNeighbor(dist, distance);
					addNeighbor(distHList, disH);
					addNeighbor(distLList, disL);
					addNeighbor(distMList, disM);
					addNeighbor(distSList, disS);
				}
			}
			statistics.clear();
			for(Double dis:dist){
				statistics.addValue(dis);
			}
			double k_mod=statistics.getMean();
			statistics.clear();
			for(Double dis:distHList){
				statistics.addValue(dis);
			}
			double k_modH=statistics.getMean();
			statistics.clear();
			for(Double dis:distLList){
				statistics.addValue(dis);
			}
			double k_modL=statistics.getMean();
			statistics.clear();
			for(Double dis:distMList){
				statistics.addValue(dis);
			}
			double k_modM=statistics.getMean();
			statistics.clear();
			for(Double dis:distSList){
				statistics.addValue(dis);
			}
			double k_modS=statistics.getMean();
			
			double pof=k_mod+Math.max(Math.max(k_modH, k_modL), Math.max(k_modM, k_modS));
			int start=patterns.get(i).getStart();
			int end=patterns.get(i).getEnd();
			for(int pos=start;pos<end;pos++){
				outlies.add1Data(dataItems.getTime().get(pos), ((int)(100*Math.max(pof, 0)))+"");
			}
			patternOutliesMap.put(patterns.get(i), pof);
		}
	}
	
	/**
	 * 更新模式的K最近邻，替换掉当前模式最远的近邻
	 * @param dist 模式和每个近邻的距离
	 * @param newDist 新的近邻距离
	 */
	private void addNeighbor(List<Double> dist,double newDist){
		if(dist.size()<neighborK){
			dist.add(newDist);
		}else{
			double max=0;
			int index=0;
			for(int i=0;i<neighborK;i++){
				if(dist.get(i)>max){
					max=dist.get(i);
					index=i;
				}
			}
			if(max>newDist){
				dist.remove(index);
				dist.add(newDist);
			}
		}
	}
	
	
	/**
	 * 两个模式之间的距离
	 * @param patternsA 
	 * @param patternsB 
	 * @return 模式间的距离
	 */
	private double distanceOfPatterns(SegPattern patternsA,SegPattern patternsB){
		
		double heightDist=Math.abs(patternsA.getHeight()-patternsB.getHeight());
		double lengthDist=Math.abs(patternsA.getLength()-patternsB.getLength());
		double meanDist=Math.abs(patternsA.getMean()-patternsB.getMean());
		double stdDist=Math.abs(patternsA.getStd()-patternsB.getStd());
		return Math.sqrt(heightDist*heightDist+lengthDist*lengthDist+meanDist*meanDist+stdDist*stdDist);
	}
	
	@Override
	public void TimeSeriesAnalysis() {
		PointSegment segment=new PointSegment(dataItems, densityK);
		patterns=segment.getPatterns();
		outliesDectation();
		System.out.println("");
	}
	
	@Override
	public DataItems getOutlies() {
		return outlies;
	}
}
