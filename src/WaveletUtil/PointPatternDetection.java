package WaveletUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openide.nodes.Children.Map;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.PatternMent;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMPiontPatternParams;

public class PointPatternDetection implements IMinerOM{

	private DataItems dataItems;     //时间序列 
	private List<PatternMent> patterns;   //TEO 线段模式
	private int densityK = 2;//序列线段化时，找极值点的参数
	private double patternThreshold = 0.1;
	private int neighborK = 10;//计算模式P的K-邻域中的k
	private double threshold;
	private double diff = 0.1;
	private DataItems outlies = new DataItems();
	private DataItems outDegree = new DataItems(); //异常度
	private List<DataItems> outlinesSet = new ArrayList<DataItems>(); //异常线段
	
	
	public PointPatternDetection(DataItems dataItems,int densityK,int neghborK){
		this.dataItems=dataItems;
		this.densityK=densityK;
		this.neighborK=neghborK;		
	}
	
	public PointPatternDetection(DataItems dataItems){
		this.dataItems = dataItems;
	}
	
	public  PointPatternDetection(OMPiontPatternParams omPiontPatternParams,DataItems di) {
		this.dataItems=di;
		this.densityK=omPiontPatternParams.getDensityK();
		this.neighborK=omPiontPatternParams.getDensityK();
		this.patternThreshold = omPiontPatternParams.getPatternThreshold();	
		this.diff = omPiontPatternParams.getDiff();
	}
	/**
	 * 检测异常
	 * @return
	 */
	public void outliesDectation(){
		
		//模式奇异度map
		HashMap<Integer, Double> patternOutliesMap=new HashMap<Integer, Double>();
		
		//计算每个模式的K最近邻
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		double heightMax=0;double heightMin=Double.MAX_VALUE;
		double lengthMin=Double.MAX_VALUE;double lengthMax=0;
		double meanMin=Double.MAX_VALUE;double meanMax=0;
		double stdMin=Double.MAX_VALUE;double stdMax=0;
		
		//min-max归一化
		for(int i=0;i<patterns.size();i++){
			if(heightMax<patterns.get(i).getHspan()){
				heightMax=patterns.get(i).getHspan();
			}
			if(heightMin>patterns.get(i).getHspan()){
				heightMin=patterns.get(i).getHspan();
			}
			if(lengthMax<patterns.get(i).getLen()){
				lengthMax=patterns.get(i).getLen();
			}
			if(lengthMin>patterns.get(i).getLen()){
				lengthMin=patterns.get(i).getLen();
			}
			if(meanMax<patterns.get(i).getAverage()){
				meanMax=patterns.get(i).getAverage();
			}
			if(meanMin>patterns.get(i).getAverage()){
				meanMin=patterns.get(i).getAverage();
			}
			if(stdMax<patterns.get(i).getStd()){
				stdMax=patterns.get(i).getStd();
			}
			if(stdMin>patterns.get(i).getStd()){
				stdMin=patterns.get(i).getStd();
			}
		}
		for(int i=0;i<patterns.size();i++){
			patterns.get(i).setHspan((patterns.get(i).getHspan()-heightMin)/(heightMax-heightMin));
			patterns.get(i).setSpan((patterns.get(i).getLen()-lengthMin)/(lengthMax-lengthMin));
			patterns.get(i).setAverage((patterns.get(i).getAverage()-meanMin)/(meanMax-meanMin));
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
					double distance=distanceOfPatterns(patterns.get(i),patterns.get(j));//线段i，j的距离
					double disH=Math.abs(patterns.get(i).getHspan()-patterns.get(j).getHspan());
					double disL=Math.abs(patterns.get(i).getLen()-patterns.get(j).getLen());
					double disM=Math.abs(patterns.get(i).getAverage()-patterns.get(j).getAverage());
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
				outDegree.add1Data(dataItems.getTime().get(pos), String.valueOf(Math.max(pof, 0)));
			}
			patternOutliesMap.put(i, pof);
		}
		genOutPionts(patternOutliesMap);
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
			//找到dist里面的最大值
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
	private double distanceOfPatterns(PatternMent patternsA,PatternMent patternsB){
		
		double heightDist=Math.abs(patternsA.getHspan()-patternsB.getHspan());
		double lengthDist=Math.abs(patternsA.getLen()-patternsB.getLen());
		double meanDist=Math.abs(patternsA.getAverage()-patternsB.getAverage());
		double stdDist=Math.abs(patternsA.getStd()-patternsB.getStd());
		return Math.sqrt(heightDist*heightDist+lengthDist*lengthDist+meanDist*meanDist+stdDist*stdDist);
	}
	/**
	 * 有异常度获取异常点
	 * 
	 * @return 模式间的距离
	 */
	public void genOutPionts(HashMap<Integer, Double> map){
		ArrayList<Double> list = new ArrayList<Double>();
		int len = map.size();
		for(int i=0;i<map.size();i++){
			list.add(map.get(i));
		}
		Collections.sort(list);
		Collections.reverse(list);
		double threshold = list.get((int)(len*0.02));		
		for(int i=(int)(len*0.02);i>0;i--){
			if((list.get(i)-threshold)<diff*threshold){
				threshold = list.get(i);
			}else{
				threshold = list.get(i);
				break;
			}
		}
		threshold = threshold>0.4 ? threshold : 0.4;
		threshold = threshold>0.6 ?  0.6 :threshold;
		System.out.println("异常度阈值是："+threshold);
		
		for(int i=0;i<len;i++){
			if(map.get(i)>=threshold){
				int start = patterns.get(i).getStart();
				int end = patterns.get(i).getEnd();
				DataItems items = new DataItems();
				for(int j=start;j<end;j++){
					outlies.add1Data(dataItems.getElementAt(j));
					items.add1Data(dataItems.getElementAt(j));
				}
				outlinesSet.add(items);
			}
		}
	}
	@Override
	public void TimeSeriesAnalysis() {
		//PointSegment segment=new PointSegment(dataItems, densityK);
		PointSegment segment=new PointSegment(dataItems, densityK,patternThreshold);
		patterns=segment.getPatterns();
		outliesDectation();
		System.out.println("");
	}
	
	@Override
	public DataItems getOutlies() {
		return outlies;
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
