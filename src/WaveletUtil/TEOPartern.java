package WaveletUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.Pattern;
import cn.InstFS.wkr.NetworkMining.DataInputs.PointSegment;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMTEOParams;

public class TEOPartern implements IMinerOM{
	private DataItems dataItems;     //时间序列 
	private int boundraryMinlen;                 //边缘点最短距离
	private int operatorLen;         //算子长度
	private List<Integer> boundaryPoints;  //边缘点
	private List<Pattern> patterns;       //TEO 线段模式
	private int densityK;
	private DataItems outlies;
	
	private double mergeThreshold;      //合并代价阈值
	public TEOPartern(DataItems dataItems,int len,int operatorLen,int densityK){
		this.dataItems=dataItems;
		this.boundraryMinlen=len;
		this.operatorLen=operatorLen;
		this.densityK=densityK;
		outlies=new DataItems();
	}
	
	public TEOPartern(DataItems dataItems,int densityK,double mergeThreshold){
		this.dataItems=dataItems;
		this.densityK=densityK;
		this.mergeThreshold=mergeThreshold;
		outlies=new DataItems();
	}
	
	public TEOPartern(){outlies=new DataItems();}
	
	public TEOPartern(OMTEOParams omteoParams,DataItems di){
		this.densityK = omteoParams.getNeighborK();
		this.mergeThreshold = omteoParams.getMergeThreshold();
		this.dataItems = di;
		outlies=new DataItems();
	}
	/**
	 * 通过从底至顶的方法生成线段模型
	 * @return 线段模型
	 */
	public List<Pattern> timeSeriesToPatternsByBottomToUp(){
		patterns=new ArrayList<Pattern>();
		int dataLen=dataItems.getData().size();
		double[] items=new double[dataLen];
		for(int i=0;i<dataLen;i++){
			try {
				items[i]=Double.parseDouble(dataItems.getData().get(i));
			} catch (Exception e) {
				throw new RuntimeException("非数字型时间序列");
			}
		}
		for(int i=0;i<dataLen-1;i++){
			Pattern pattern=new Pattern();
			pattern.setStart(i);
			pattern.setEnd(i+1);
			//pattern.setLength(Math.sqrt(1+(items[i+1]-items[i])*(items[i+1]-items[i])));
			pattern.setSlope(items[i+1]-items[i]);
			patterns.add(pattern);
		}
		List<Double> dist=patternDist(patterns,items);
		int minIndex=minDistBetweenItems(dist,mergeThreshold);
		
		while(minIndex!=-1){
			mergePattern(patterns,minIndex,items,dist);
			minIndex=minDistBetweenItems(dist, mergeThreshold);
		}
		DescriptiveStatistics lenStatistics=new DescriptiveStatistics();
		DescriptiveStatistics slopeStatistics=new DescriptiveStatistics();
		for(Pattern pattern:patterns){
			//lenStatistics.addValue(pattern.getLength());
			slopeStatistics.addValue(pattern.getSlope());
		}
		double lenMean=lenStatistics.getMean();
		double slopeMean=slopeStatistics.getMean();
		double lenStd=lenStatistics.getStandardDeviation();
		double slopeStd=slopeStatistics.getStandardDeviation();
		for(Pattern pattern:patterns){
			//pattern.setLength((pattern.getLength()-lenMean)/lenStd);
			pattern.setSlope((pattern.getSlope()-slopeMean)/slopeStd);
		}
		return patterns;
	}
	
	/**
	 * 计算给定线段模型中相邻线段之间的合并代价
	 * @param patterns 给定的线段模型
	 * @param items 
	 * @return 相邻线段的合并代价
	 */
	private List<Double> patternDist(List<Pattern> patterns,double[] items){
		int len=patterns.size();
		List<Double> distBetweenItem=new ArrayList<Double>();
		for(int i=0;i<len-1;i++){
			double dist=distanceBetweenPatterns(patterns.get(i), patterns.get(i+1), items);
			distBetweenItem.add(dist);
		}
		return distBetweenItem;
	}
	
	/**
	 * 计算两个线段模型的合并代价
	 * @param A 线段模型
	 * @param B 线段模型
	 * @param items
	 * @return
	 */
	private double distanceBetweenPatterns(Pattern A,Pattern B,double[] items){
		double ySpan=items[B.getEnd()]-items[A.getStart()];
		double xSpan=B.getEnd()-A.getStart();
		double slope=ySpan/xSpan;
		double yOrigin=items[A.getStart()];
		int xOrigin=A.getStart();
		double dist=0;
		for(int j=A.getStart();j<=B.getEnd();j++){
			dist+=Math.sqrt(((yOrigin+slope*(j-xOrigin))-items[j])*((yOrigin+slope*(j-xOrigin))-items[j]));
		}
		return dist;
	}
	
	/**
	 * 合并两个线段模型，并更新相应的合并代价数组
	 * @param patterns 给定的线段模型
	 * @param index 最小合并代价模型的下标
	 * @param items
	 * @param dists 合并代价数组
	 */
	private void mergePattern(List<Pattern> patterns,int index,double[] items,List<Double> dists){
		int rigthtPos=patterns.get(index+1).getEnd();
		int leftPos=patterns.get(index).getStart();
		double ySpan=items[rigthtPos]-items[leftPos];
		double xSpan=rigthtPos-leftPos;
		double slope=ySpan/xSpan;
		double length=Math.sqrt(Math.pow(xSpan, 2)+Math.pow(ySpan, 2));
		//更新合并后的线段模型
		patterns.get(index+1).setStart(leftPos);
		patterns.get(index+1).setSlope(slope);
		//patterns.get(index+1).setLength(length);
		//更新合并之后的合并代价数组
		if(index==0){
			double updateDist=distanceBetweenPatterns(patterns.get(index+1), 
					patterns.get(index+2), items);
			dists.set(index+1, updateDist);
		}else if(index==(patterns.size()-2)){
			double updateDist=distanceBetweenPatterns(patterns.get(index-1), 
					patterns.get(index+1), items);
			dists.set(index-1, updateDist);
		}else{
			double updateDist=distanceBetweenPatterns(patterns.get(index+1), 
					patterns.get(index+2), items);
			dists.set(index+1, updateDist);
			updateDist=distanceBetweenPatterns(patterns.get(index-1), 
					patterns.get(index+1), items);
			dists.set(index-1, updateDist);
		}
		//删除掉合并之后的线段模型
		patterns.remove(index);
		dists.remove(index);
	}
	
	private int minDistBetweenItems(List<Double> dist,double threshold){
		int size=dist.size();
		int index=-1;
		double min=Double.MAX_VALUE;
		for(int i=0;i<size;i++){
			if(min>dist.get(i)&&dist.get(i)<threshold){
				min=dist.get(i);
				index=i;
			}
		}
		return index;
	}
	
	
	
	/**
	 * 通过逐步的方式构造时间序列模式
	 */
	public void timeSeriesToPatternByStep(double threshold){
		patterns=new ArrayList<Pattern>();
		int dataitemLen=dataItems.getLength();
		double[] items=new double[dataitemLen];
		for(int i=0;i<dataitemLen;i++){
			try {
				items[i]=Double.parseDouble(dataItems.getData().get(i));
			} catch (Exception e) {
				throw new RuntimeException("非数字型时间序列");
			}
		}
		for(int i=0;i<dataitemLen;i++){
			Pattern pattern=new Pattern();
			pattern.setStart(i);
			if((i+1)<dataitemLen){
		    	pattern.setEnd(i+1);
			}else{
				break;
			}
			pattern.setAverage((items[i]+items[i+1])/2);
			pattern.setSlope((items[i+1]-items[i]));
			pattern.setSpan(2);
			double simility=0.0;
			while(simility<threshold){
				if(pattern.getEnd()<(dataitemLen-1)){
					simility=mergePointDistance(pattern, items);
					if(simility>=threshold){
						//设置下一个Pattern的起始位置，之所以减一是因为for循环一次 i要加1
						//i=pattern.getEnd()-1;
						i=pattern.getEnd()-1;
						break;
					}else{
						pattern.setSpan(pattern.getEnd()-pattern.getStart()+1);
						pattern.setEnd(pattern.getEnd()+1);
						pattern.setAverage((items[pattern.getStart()]+items[pattern.getEnd()])/2);
						pattern.setSlope((items[pattern.getEnd()]-items[pattern.getStart()])/(pattern.getEnd()-pattern.getStart()));
					}
				}else{
					//序列拟合完毕
					//i=pattern.getEnd()-1;
					i=pattern.getEnd()-1;
					break;
				}
			}
			patterns.add(pattern);
		}
		for(Pattern pattern:patterns){
			System.out.print(pattern.getStart()+",");
		}
		System.out.println(dataitemLen);
	}
	
	private double mergePointDistance(Pattern pattern,double[] items){
		int start=pattern.getStart();
		int end=pattern.getEnd()+1;
		double ySpan=items[end]-items[start];
		double xSpan=end-start;
		double slope=ySpan/xSpan;
		double yOrigin=items[start];
		int xOrigin=start;
		double dist=0;
		for(int j=start;j<=end;j++){
			dist+=Math.abs(((yOrigin+slope*(j-xOrigin))-items[j]));
		}
		return dist;
	}
	
	public List<Pattern> timeSeriesToPatternsByTEO(){
		patterns=new ArrayList<Pattern>();
		int dataItemsLen=dataItems.getData().size();
		double[] items=new double[dataItemsLen];
		for(int i=0;i<dataItemsLen;i++){
			try {
				items[i]=Double.parseDouble(dataItems.getData().get(i));
			} catch (Exception e) {
				throw new RuntimeException("非数字型时间序列");
			}
		}
		
		//初始化算子
		int[] operator=new int[operatorLen*2+1];
		for(int i=1;i<=operatorLen;i++){
			operator[i-1]=-i;
		}
		operator[operatorLen]=0;
		for(int i=1;i<=operatorLen;i++){
			operator[operatorLen+i]=operatorLen-i+1;
		}
		
		double[] boundaryValues=boundaryValueOfEachPoint(items,items.length,operator);
		boundaryPoints=getBoundaryPoint(boundaryValues);
		int boundaryPointLen=boundaryPoints.size();
		for(Integer point:boundaryPoints){
			System.out.print(point+",");
		}
		System.out.println();
		System.out.println("points len "+boundaryPointLen);
		if(boundaryPointLen>=2){
			for(int i=1;i<boundaryPointLen;i++){
				int leftPoint=boundaryPoints.get(i-1);
				int rightPoint=boundaryPoints.get(i);
				double XSpan=rightPoint-leftPoint;
				double YSpan=items[rightPoint]-items[leftPoint];
				Pattern pattern=new Pattern();
				pattern.setSlope(YSpan/XSpan);
				pattern.setStart(leftPoint);
				pattern.setEnd(rightPoint);
				pattern.setAverage((items[rightPoint]+items[leftPoint])/2.0);
				patterns.add(pattern);
			}
		}
		return patterns;
	}
	
	/**
	 * 检测异常
	 * @return
	 */
	public void outliesDectation(){
		//模式的K最近邻模式
		HashMap<Pattern, List<Pattern>>patternMap=new HashMap<Pattern, List<Pattern>>();
		//模式可达数量
		HashMap<Pattern, Integer> patternReachMap=new HashMap<Pattern, Integer>();
		//模式奇异度map
		HashMap<Pattern, Double> patternOutliesMap=new HashMap<Pattern, Double>();
		//计算每个模式的K最近邻
		
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		//DecimalFormat df=new DecimalFormat("0.00");
		double spanMax=0;double spanMin=Double.MAX_VALUE;
		double slopeMin=Double.MAX_VALUE;double slopeMax=0;
		double averageMin=Double.MAX_VALUE;double averageMax=0;
		for(int i=0;i<patterns.size();i++){
			if(spanMax<patterns.get(i).getSpan()){
				spanMax=patterns.get(i).getSpan();
			}
			if(spanMin>patterns.get(i).getSpan()){
				spanMin=patterns.get(i).getSpan();
			}
			if(slopeMax<patterns.get(i).getSlope()){
				slopeMax=patterns.get(i).getSlope();
			}
			if(slopeMin>patterns.get(i).getSlope()){
				slopeMin=patterns.get(i).getSlope();
			}
			if(averageMax<patterns.get(i).getAverage()){
				averageMax=patterns.get(i).getAverage();
			}
			if(averageMin>patterns.get(i).getAverage()){
				averageMin=patterns.get(i).getAverage();
			}
		}
		for(int i=0;i<patterns.size();i++){
			patterns.get(i).setSpan((patterns.get(i).getSpan()-spanMin)/(spanMax-spanMin));
			patterns.get(i).setSlope((patterns.get(i).getSlope()-slopeMin)/(slopeMax-slopeMin));
			patterns.get(i).setAverage((patterns.get(i).getAverage()-averageMin)/(averageMax-averageMin));
		}
		for(int i=0;i<patterns.size();i++){
			List<Integer> pos=new ArrayList<Integer>();
			List<Double> dist=new ArrayList<Double>();
			for(int j=0;j<patterns.size();j++){
				
				if(i==j){
					continue;
				}else{
					double distance=distanceOfPatterns(patterns.get(i),patterns.get(j));
					addNeighbor(pos, dist, distance, j);
				}
			}
			statistics.clear();
			for(Double dis:dist){
				statistics.addValue(dis);
			}
			
			List<Pattern> patternList=new ArrayList<Pattern>();
			for(Integer position:pos){
				patternList.add(patterns.get(position));
				if(!patternReachMap.containsKey(patterns.get(position))){
					patternReachMap.put(patterns.get(position), 1);
				}else{
					int size=patternReachMap.get(patterns.get(position));
					patternReachMap.remove(patterns.get(position));
					patternReachMap.put(patterns.get(position), size+1);
				}
			}
			patternMap.put(patterns.get(i), patternList);
		}
		
		int lastPof=0;
		for(Pattern pattern:patterns){

			if(!patternReachMap.containsKey(pattern)){
				patternOutliesMap.put(pattern, 1.0);
				for(int i=pattern.getStart();i<pattern.getEnd();i++){
					System.out.print(10+",");
					outlies.add1Data(dataItems.getTime().get(i), "10");
				}
				lastPof=10;
			}else{
				double pof=0.0;
				List<Pattern> neighborPatterns=patternMap.get(pattern);
				double size=neighborPatterns.size();
				double reachSize=patternReachMap.get(pattern);
				for(Pattern neighborPattern:neighborPatterns){
					double insize=patternReachMap.get(neighborPattern);
					pof+=(reachSize/insize);
				}
				pof/=size;
				patternOutliesMap.put(pattern, Math.max(1-pof, 0));
				for(int i=pattern.getStart();i<pattern.getEnd();i++){
					System.out.print((int)(10*Math.max(1-pof, 0))+",");
					outlies.add1Data(dataItems.getTime().get(i), ((int)(10*Math.max(1-pof, 0)))+"");
				}
				lastPof=(int)(10*Math.max(1-pof, 0));
				outlies.add1Data(dataItems.getLastTime(), (int)lastPof+"");
			}
		}
		System.out.print(lastPof);
	}
	
	/**
	 * 更新模式的K最近邻，替换掉当前模式最远的近邻
	 * @param pos 模式当前的近邻
	 * @param dist 模式和每个近邻的距离
	 * @param newDist 新的近邻距离
	 * @param newPos  新的近邻模式
	 */
	private void addNeighbor(List<Integer> pos,List<Double> dist,double newDist,int newPos){
		if(pos.size()<densityK){
			dist.add(newDist);
			pos.add(newPos);
		}else{
			double max=0;
			ArrayList<Integer> maxPos=new ArrayList<Integer>();
			for(int i=0;i<densityK;i++){
				if(dist.get(i)>max){
					max=dist.get(i);
					maxPos.clear();
					maxPos.add(i);
				}else if(dist.get(i)==max){
					maxPos.add(i);
				}
			}
			if(dist.get(maxPos.get(0))>newDist){
				if((dist.size()-maxPos.size()+1)<densityK){
					dist.add(newDist);
					pos.add(newPos);
				}else{
					for(int i=0;i<maxPos.size();i++){
						pos.remove(maxPos.get(i)-i);
						dist.remove(maxPos.get(i)-i);
					}
					dist.add(newDist);
					pos.add(newPos);
				}
			}else if(dist.get(maxPos.get(0))==newDist){
				dist.add(newDist);
				pos.add(newPos);
			}
		}
	}
	
	/**
	 * 两个模式之间的距离
	 * @param patternsA 
	 * @param patternsB 
	 * @return 模式间的距离
	 */
	private double distanceOfPatterns(Pattern patternsA,Pattern patternsB){
		
		double slopeDist=Math.abs(patternsA.getSlope()-patternsB.getSlope());
		double averageDist=Math.abs(patternsA.getAverage()-patternsB.getAverage());
		double spanDist=Math.abs(patternsA.getSpan()-patternsB.getSpan());
		return slopeDist+averageDist+spanDist;
	}
	
	private List<Integer> getBoundaryPoint(double[] boundary){
		List<Integer> boundrayPoints=new ArrayList<Integer>();
		boundrayPoints.add(0);
		int iMax=0,iMIn=0,flag=0;
		for(int i=0;i<boundary.length;i++){
			if(boundary[i]<boundary[iMIn]){
				iMIn=i;
			}
			if(boundary[i]>boundary[iMax]){
				iMax=i;
			}
			
			if(boundary[i]>boundary[iMIn]&&(i-iMIn)>boundraryMinlen&&flag!=-1){
				boundrayPoints.add(i);
				iMIn=iMax=i;
				flag=-1;
			}
			if(boundary[i]<boundary[iMax]&&(i-iMax)>boundraryMinlen&&flag!=1){
				boundrayPoints.add(i);
				iMIn=iMax=i;
				flag=1;
			}
		}
		boundrayPoints.add(boundary.length-1);
		return boundrayPoints;
	}
	
	private double[] boundaryValueOfEachPoint(double[] points,int length,int[] operator){
		double[] boundaryValues=new double[length];
		for(int i=0;i<length;i++){
			int boundaryValue=0;
			for(int j=-operatorLen;j<=operatorLen;j++){
				if((i+j)<0){
					continue;
				}else if((i+j)>=length){
					break;
				}
				boundaryValue+=Math.abs(operator[j+operatorLen])*(points[i]-points[i+j]);
			}
			boundaryValues[i]=boundaryValue;
		}
		return boundaryValues;
	}
	
	
	public int getOperatorLen() {
		return operatorLen;
	}
	public void setOperatorLen(int operatorLen) {
		this.operatorLen = operatorLen;
	}
	public DataItems getDataItems() {
		return dataItems;
	}
	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}
	public int getLen() {
		return boundraryMinlen;
	}
	public void setLen(int len) {
		this.boundraryMinlen = len;
	}
	
	public static void main(String[] args){
		String traffics="11.0000000000000	17.0000000000000	17.0000000000000	13.0000000000000	14.0000000000000	36.5000000000000	245.000000000000	364.000000000000	165.000000000000	13.5000000000000	11.5000000000000	15.5000000000000	111.500000000000	368	232.500000000000	18.0000000000000	13.0000000000000	19.5000000000000	50.0000000000000	423	205.500000000000	14.5000000000000	13.5000000000000	17.5000000000000	265	309.500000000000	18.0000000000000	14.5000000000000	13.0000000000000	26.5000000000000	49.5000000000000	249.000000000000	295.500000000000	13.0000000000000	10.5000000000000	10	13.5000000000000	12.5000000000000	15.0000000000000	15.5000000000000	12	12.0000000000000	15.5000000000000	13.0000000000000	13.0000000000000	14	11.0000000000000	37.0000000000000	60.5000000000000	410.500000000000	263.500000000000	20.5000000000000	17	19.0000000000000	142.500000000000	599.500000000000	567.500000000000	18.5000000000000	14.0000000000000	13.0000000000000	22.0000000000000	590.500000000000	321.000000000000	12	11.0000000000000	17.0000000000000	38.5000000000000	614	448	15	13.0000000000000	33.5000000000000	44.5000000000000	555.500000000000	293.500000000000	11.0000000000000	11.0000000000000	15.0000000000000	12.0000000000000	15.5000000000000	19.5000000000000	12	15.0000000000000	16.5000000000000	20.0000000000000	12.5000000000000	20.5000000000000	18.0000000000000	15.0000000000000	10.5000000000000	19.0000000000000	666	456.000000000000	17.0000000000000	13.0000000000000	31.0000000000000	278.500000000000	730.500000000000	728	14.5000000000000	14.5000000000000	13.5000000000000	386.500000000000	749	324	15.5000000000000	14.0000000000000	40.5000000000000	417.500000000000	769.000000000000	354	13.0000000000000	9.00000000000000	12.5000000000000	198	140	66.5000000000000	14	12.0000000000000	17.0000000000000	13.0000000000000	14.0000000000000	15.0000000000000	10.0000000000000	6.50000000000000	4.50000000000000	9.50000000000000	18.0000000000000	23	15.0000000000000	14	16.0000000000000	19.5000000000000	182.500000000000	126.000000000000	14.0000000000000	12.5000000000000	13.5000000000000	135.000000000000	224	139.000000000000	15.5000000000000	12	20.0000000000000	91	270.500000000000	221.000000000000	11.5000000000000	11.0000000000000	13.5000000000000	205.500000000000	267.500000000000	95.5000000000000	13.0000000000000	25.5000000000000	14.0000000000000	24.5000000000000	241.500000000000	183.500000000000	17.5000000000000	17.0000000000000	12	13.0000000000000	16.5000000000000	10.5000000000000	16	14.5000000000000	14.5000000000000	8.50000000000000	12	17.0000000000000	15.0000000000000	9.50000000000000	14	103.000000000000	355.500000000000	263.000000000000	14.5000000000000	13.5000000000000	18.0000000000000	92.5000000000000	419.500000000000	254.000000000000	16.0000000000000	13.5000000000000	27.5000000000000	40.0000000000000	464.500000000000	245	13.0000000000000	16.5000000000000	12.5000000000000	18.0000000000000	442.000000000000	312.500000000000	15.0000000000000	12.0000000000000	13.5000000000000	326.500000000000	503.500000000000	277.500000000000	9.50000000000000	7.00000000000000	8.50000000000000	17.5000000000000	22.5000000000000	20.0000000000000	18.0000000000000	10.0000000000000	14.5000000000000	13.0000000000000	13.0000000000000	15.5000000000000	17.5000000000000	19	18.5000000000000	328.000000000000	556.000000000000	328.000000000000	11.5000000000000	7.50000000000000	6.00000000000000	39.0000000000000	462.500000000000	290.500000000000	10	6.50000000000000	5.00000000000000	59.5000000000000	565.500000000000	312.000000000000	14.5000000000000	11.0000000000000	6.50000000000000	228	495.000000000000	331.000000000000	15.5000000000000	8.00000000000000	5.50000000000000	330.000000000000	583.000000000000	166.500000000000	6.00000000000000	7.00000000000000	5.50000000000000	8.50000000000000	8.50000000000000	11.5000000000000	12.0000000000000	6.00000000000000	6.50000000000000	10.5000000000000	12.5000000000000	11.5000000000000	12.5000000000000	15.5000000000000	15.0000000000000	89.5000000000000	604.000000000000	326.500000000000	18.5000000000000	13.5000000000000	14.0000000000000	316	551.500000000000	158.000000000000	17.5000000000000	7.00000000000000	2.50000000000000	55.5000000000000	580	378.500000000000	17.5000000000000	10	8.50000000000000	66.5000000000000	616.500000000000	393.000000000000	15.5000000000000	9.50000000000000	8.50000000000000	427.500000000000	526.000000000000	124.500000000000	11.5000000000000	8.50000000000000	8.50000000000000	9.00000000000000	16.5000000000000	22.5000000000000	26.5000000000000	19.5000000000000	12.5000000000000	23.0000000000000	20	31	20.5000000000000	18	21.5000000000000	32.5000000000000	656	661	651.000000000000	647.500000000000	651.500000000000	656.000000000000	667.500000000000	392	26.0000000000000	17.5000000000000	22.0000000000000	35.5000000000000	619	485.500000000000	680.000000000000	80.5000000000000	22.5000000000000	86.0000000000000	714.000000000000	419.000000000000	27.5000000000000	23.5000000000000	18.0000000000000	416.000000000000	694.000000000000	415.500000000000	18.0000000000000	16.0000000000000	17.0000000000000	17.5000000000000	23	22.5000000000000	23	21.5000000000000	20.0000000000000	26.0000000000000	18.0000000000000	30.0000000000000	26.0000000000000	25.5000000000000	24.5000000000000	40.0000000000000	114	317.000000000000	26	21.0000000000000	21.0000000000000	38.0000000000000	658.500000000000	494.500000000000	21.0000000000000	24.5000000000000	25.0000000000000	71.5000000000000	944.000000000000	712.000000000000	260	22.0000000000000";
		String[] trafficArray=traffics.split("\\t+");
		DataItems dataItems=new DataItems();
		for(String traffic:trafficArray){
			DataItem dataItem=new DataItem();
			dataItem.setData(traffic.trim());
			dataItem.setTime(new Date());
			dataItems.add1Data(dataItem);
		}
		TEOPartern partern=new TEOPartern(dataItems, 4, 4, 20);
		partern.timeSeriesToPatternByStep(100);
		partern.outliesDectation();
		
	}

	@Override
	public void TimeSeriesAnalysis() {
		//MergeSegment mergeSeg=new MergeSegment(dataItems, 0.25);
		PointSegment segment=new PointSegment(dataItems, 20);
		patterns=segment.getTEOPattern();
		//timeSeriesToPatternByStep(5000);
		outliesDectation();
	}

	@Override
	public DataItems getOutlies() {
		return outlies;
	}
}