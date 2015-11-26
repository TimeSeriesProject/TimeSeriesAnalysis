package WaveletUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.net.aso.i;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import weka.core.pmml.jaxbbindings.Time;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItem;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.IMinerTSA;

public class TEOPartern implements IMinerTSA{
	private DataItems dataItems;     //时间序列 
	private int boundraryMinlen;                 //边缘点最短距离
	private int operatorLen;         //算子长度
	private List<Integer> boundaryPoints;  //边缘点
	private List<Patterns> patterns;       //TEO 线段模式
	private int densityK;
	
	private double mergeThreshold;      //合并代价阈值
	public TEOPartern(DataItems dataItems,int len,int operatorLen,int densityK){
		this.dataItems=dataItems;
		this.boundraryMinlen=len;
		this.operatorLen=operatorLen;
		this.densityK=densityK;
	}
	
	public TEOPartern(DataItems dataItems,int densityK,double mergeThreshold){
		this.dataItems=dataItems;
		this.densityK=densityK;
		this.mergeThreshold=mergeThreshold;
	}
	
	public TEOPartern(){}
	/**
	 * 通过从底至顶的方法生成线段模型
	 * @return 线段模型
	 */
	public List<Patterns> timeSeriesToPatternsByBottomToUp(){
		patterns=new ArrayList<Patterns>();
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
			Patterns pattern=new Patterns();
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
//		for(Patterns pattern:patterns){
//			System.out.println(pattern.getStart()+" "+pattern.getEnd()+":"+pattern.getSlope()+" "+pattern.getLength());
//		}
		DescriptiveStatistics lenStatistics=new DescriptiveStatistics();
		DescriptiveStatistics slopeStatistics=new DescriptiveStatistics();
		for(Patterns pattern:patterns){
			//lenStatistics.addValue(pattern.getLength());
			slopeStatistics.addValue(pattern.getSlope());
		}
		double lenMean=lenStatistics.getMean();
		double slopeMean=slopeStatistics.getMean();
		double lenStd=lenStatistics.getStandardDeviation();
		double slopeStd=slopeStatistics.getStandardDeviation();
		for(Patterns pattern:patterns){
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
	private List<Double> patternDist(List<Patterns> patterns,double[] items){
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
	private double distanceBetweenPatterns(Patterns A,Patterns B,double[] items){
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
	private void mergePattern(List<Patterns> patterns,int index,double[] items,List<Double> dists){
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
		patterns=new ArrayList<Patterns>();
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
			Patterns pattern=new Patterns();
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
					i=pattern.getEnd()-1;
					break;
				}
			}
			patterns.add(pattern);
		}
		for(Patterns pattern:patterns){
			System.out.print(pattern.getStart()+",");
		}
		System.out.println(dataitemLen);
	}
	
	private double mergePointDistance(Patterns pattern,double[] items){
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
	
	/**
	 * 通过IEO的方式将时间序列表示呈现段模式
	 * 即通过找出出时间序列中的奇异点构造线段
	 * 奇异点的定义为一段时间中的极大值点或则极小值点
	 */
	public void timeSeriesToPatternsByIEO(){
		patterns=new ArrayList<Patterns>();
		int dataitemLen=dataItems.getLength();
		double[] items=new double[dataitemLen];
		
		double[] wDiff=new double[dataitemLen];//边缘强度
		double[] wError=new double[dataitemLen];//边缘误差
		for(int i=0;i<dataitemLen;i++){
			try {
				items[i]=Double.parseDouble(dataItems.getData().get(i));
			} catch (Exception e) {
				throw new RuntimeException("非数字型时间序列");
			}
		}
		
		//计算边缘强度及边缘误差
		for(int i=0;i<dataitemLen;i++){
			double w1=0.0;
			for(int j=(i-operatorLen);j<=(i+operatorLen);j++){
				if(j<0){
					continue;
				}else if(j>=dataitemLen){
					break;
				}
				if(items[i]-items[j]>0){
					w1+=1;
				}else if(items[i]-items[j]<0){
					w1-=1;
				}
				wDiff[i]=w1;
			}
			if(i==0||i==dataitemLen-1){
				wError[i]=0;
			}else{
				wError[i]=0.5*Math.abs(items[i]-items[i-1]);
				if((items[i]-items[i-1])*(items[i+1]-items[i])>=0){
					wError[i]-=Math.abs(items[i+1]-items[i]);
				}else if((items[i]-items[i-1])*(items[i+1]-items[i])<0){
					wError[i]+=Math.abs(items[i+1]-items[i]);
				}
			}
		}
		
		
		List<Integer> boundPoints=new ArrayList<Integer>();
		//addInflectPoint(items,boundPoints);
		int comLen=(int)(dataitemLen*1.0*0.2)-boundPoints.size(); //压缩比例 15%
		boundPoints.add(0);
		List<Point> tendToAddPointIndex=new ArrayList<Point>();
		for(int i=2*operatorLen;i>=0;i--){
			int ii=0;
			tendToAddPointIndex.clear();
			for(int j=0;j<dataitemLen;j++){
				if(Math.abs(wDiff[j])==i){
					ii++;
					Point point=new Point();
					point.setPointIndex(j);
					point.setPointValue(wError[j]);
					tendToAddPointIndex.add(point);
				}
			}
			if(ii==comLen){
				for(Point point:tendToAddPointIndex){
					boundPoints.add(point.getPointIndex());
				}
				break;
			}else if(ii<comLen){
				for(Point point:tendToAddPointIndex){
					boundPoints.add(point.getPointIndex());
				}
				comLen-=ii;
			}else{
				addPreBoundPoints(tendToAddPointIndex,boundPoints,comLen);
				break;
			}
		}
		boundPoints.add(dataitemLen-1);
		
		Collections.sort(boundPoints);
		int boundaryPointLen=boundPoints.size();
//		double slopeMin=Double.MAX_VALUE,slopeMax=Double.MIN_VALUE;
//		double averMin=Double.MAX_VALUE,averMax=Double.MIN_VALUE;
		double slopeMean,slopeStd;
		double averMean,averStd;
		DescriptiveStatistics slopeStatistics=new DescriptiveStatistics();
		DescriptiveStatistics averageStatistics=new DescriptiveStatistics();
		if(boundaryPointLen>=2){
			for(int i=1;i<boundaryPointLen;i++){
				int leftPoint=boundPoints.get(i-1);
				int rightPoint=boundPoints.get(i);
				if(rightPoint==leftPoint){
					continue;
				}
				double XSpan=rightPoint-leftPoint;
				double YSpan=items[rightPoint]-items[leftPoint];
				Patterns pattern=new Patterns();
				//pattern.setLength(Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2)));
//				if(lengthMin>Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2)))
//					lengthMin=Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2));
//				if(lengthMax<Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2))){
//					lengthMax=Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2));
//				}
				pattern.setSpan(pattern.getEnd()-pattern.getStart()+1);
				pattern.setSlope(YSpan/XSpan);
				slopeStatistics.addValue(YSpan/XSpan);
//				if(slopeMin>YSpan/XSpan)
//					slopeMin=(YSpan/XSpan);
//				if(slopeMax<YSpan/XSpan)
//					slopeMax=YSpan/XSpan;
				pattern.setStart(leftPoint);
				pattern.setEnd(rightPoint);
				pattern.setAverage((items[rightPoint]+items[leftPoint])/2.0);
				averageStatistics.addValue((items[rightPoint]+items[leftPoint])/2.0);
//				if(averMin>(items[rightPoint]+items[leftPoint])/2.0)
//					averMin=(items[rightPoint]+items[leftPoint])/2.0;
//				if(averMax<(items[rightPoint]+items[leftPoint])/2.0){
//					averMax=(items[rightPoint]+items[leftPoint])/2.0;
//				}
				patterns.add(pattern);
			}
		}
		slopeMean=slopeStatistics.getMean();
		slopeStd=slopeStatistics.getStandardDeviation();
		averMean=averageStatistics.getMean();
		averStd=averageStatistics.getStandardDeviation();
		for(Patterns pattern:patterns){
			pattern.setAverage(((pattern.getAverage()-averMean)/(averStd)));
			pattern.setSlope(((pattern.getSlope()-slopeMean)/(slopeStd)));
			//pattern.setAverage((pattern.getLength()-lengthMin)/(lengthMax-lengthMin));
		}
		for(Patterns pattern:patterns){
			System.out.print(pattern.getStart()+",");
		}
	}
	
	/**
	 * z找出时间序列中的拐点
	 * @param items 时间序列
	 * @param boundPoints 返回的拐点
	 */
	private void addInflectPoint(double[] items,List<Integer> boundPoints){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		int len=items.length;
		double[] inflect=new double[len];
		for(int i=operatorLen;i<len-operatorLen;i++){
			double leftSum=0;
			double rightSum=0;
			for(int j=1;j<=operatorLen;j++){
				leftSum+=items[i-j];
				rightSum+=items[i+j];
			}
			inflect[i]=(leftSum>rightSum)?(leftSum/rightSum):(rightSum/leftSum);
			statistics.addValue(inflect[i]);
		}
		double mean=statistics.getMean();
		int sum=0;
		for(int i=operatorLen;i<len-operatorLen;i++){
			sum=0;
			if(inflect[i]>mean){
				for(int j=-operatorLen;j<=operatorLen;j++){
					if(inflect[i]>inflect[i+j]){
						sum+=1;
					}else if(inflect[i]<inflect[i+j]){
						sum-=1;
					}
				}
				if(sum==2*operatorLen){
					boundPoints.add(i);
					if(items[i+1]/items[i-1]>mean){
						boundPoints.add(i-1);
					}else if(items[i-1]/items[i+1]>mean){
						boundPoints.add(i+1);
					}
				}
			}
		}
		
	}
	
	/**
	 * IEO 线段模式 中添加边缘偏差最小的前addNumber个边缘候选点到边缘点集合
	 * @param tendToAddPoints 边缘点候选集
	 * @param boundPoints 边缘点集合
	 * @param addNumber 要添的边缘点个数
	 */
	private void addPreBoundPoints(List<Point> tendToAddPoints,List<Integer> boundPoints,int addNumber){
		Collections.sort(tendToAddPoints);
		int len=tendToAddPoints.size();
		for(int i=0;i<addNumber;i++){
			boundPoints.add(tendToAddPoints.get(len-1-i).getPointIndex());
		}
	}
	
	public List<Patterns> timeSeriesToPatternsByTEO(){
		patterns=new ArrayList<Patterns>();
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
				Patterns pattern=new Patterns();
				//pattern.setLength(Math.sqrt(Math.pow(XSpan, 2)+Math.pow(YSpan, 2)));
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
		HashMap<Patterns, List<Patterns>>patternMap=new HashMap<Patterns, List<Patterns>>();
		//模式可达数量
		HashMap<Patterns, Integer> patternReachMap=new HashMap<Patterns, Integer>();
		//模式奇异度map
		HashMap<Patterns, Double> patternOutliesMap=new HashMap<Patterns, Double>();
		//计算每个模式的K最近邻
		
		DescriptiveStatistics statistics=new DescriptiveStatistics();
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
			System.out.print(patterns.get(i).getStart()+"-"+patterns.get(i).getEnd()+":");
			statistics.clear();
			for(Double dis:dist){
				statistics.addValue(dis);
			}
			
			System.out.println(statistics.getMean());
			List<Patterns> patternList=new ArrayList<Patterns>();
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
		
//		Iterator<Entry<Patterns,Integer>> iterator=patternReachMap.entrySet().iterator();
//		int index=1;
//		while(iterator.hasNext()){
//			Map.Entry<Patterns, Integer> entry=iterator.next();
//			if(entry.getKey().getStart()==638||entry.getKey().getStart()==11||entry.getKey().getStart()==486){
//				System.out.println(index+":"+entry.getKey().getStart()+"-"+entry.getKey().getEnd()+" "+entry.getValue());
//				List<Patterns> list=patternMap.get(entry.getKey());
//				for(Patterns pattern:list){
//					System.out.println("------------"+pattern.getStart()+"-"+pattern.getEnd()+" "+distanceOfPatterns(pattern,entry.getKey()));
//
//				}
//			}
//			index++;
//		}
		for(Patterns pattern:patterns){

			if(!patternReachMap.containsKey(pattern)){
				patternOutliesMap.put(pattern, 1.0);
				System.out.println(pattern.getStart()+","+pattern.getEnd()+",1.0");
			}else{
				double pof=0.0;
				List<Patterns> neighborPatterns=patternMap.get(pattern);
				double size=neighborPatterns.size();
				double reachSize=patternReachMap.get(pattern);
				for(Patterns neighborPattern:neighborPatterns){
					double insize=patternReachMap.get(neighborPattern);
					pof+=(reachSize/insize);
				}
				pof/=size;
				patternOutliesMap.put(pattern, Math.max(1-pof, 0));
//				if(Math.max(1-pof, 0)>0.6){
//					//System.out.println(pattern.getStart()+"-"+pattern.getEnd()+" "+Math.max(1-pof, 0));
//					for(int j=pattern.getStart();j<=pattern.getEnd();j++){
//						System.out.print(j+",");
//					}
//				}
			}
		}
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
	private double distanceOfPatterns(Patterns patternsA,Patterns patternsB){
		double spanDist=Math.abs(patternsA.getSpan()-patternsB.getSpan())/
				Math.min(Math.abs(patternsA.getSpan()), Math.abs(patternsB.getSpan()));
		if(Math.min(Math.abs(patternsA.getSpan()),Math.abs(patternsB.getSpan()))==0){
        	if(Math.abs(patternsA.getSpan()-patternsB.getSpan())==0){
        		spanDist=0;
        	}else{
        		spanDist=10000;
        	}
        	
		}
		double slopeDist=Math.abs(patternsA.getSlope()-patternsB.getSlope())/
				Math.min(Math.abs(patternsA.getSlope()),Math.abs(patternsB.getSlope()));
        if(Math.min(Math.abs(patternsA.getSlope()),Math.abs(patternsB.getSlope()))==0){
        	if(Math.abs(patternsA.getSlope()-patternsB.getSlope())==0){
        		slopeDist=0;
        	}else{
        		slopeDist=10000;
        	}
        	
		}
		double averageDist=Math.abs(patternsA.getAverage()-patternsB.getAverage())/
				Math.min(Math.abs(patternsA.getAverage()), Math.abs(patternsB.getAverage()));
		if(Math.min(Math.abs(patternsA.getAverage()), Math.abs(patternsB.getAverage()))==0){
			if(Math.abs(patternsA.getAverage()-patternsB.getAverage())==0){
				averageDist=0;
			}else{
		    	averageDist=100000;
			}
		}
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
//			for(int j=-operatorLen;j<=operatorLen;j++){
//				if((i+j)<0||(i+j)>=length){
//					continue;
//				}
//				boundaryValue+=points[i+j]*operatorAtIndex[j+operatorLen];
//			}
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
	
	private class Patterns{
		private int start;
		private int end;
		private int span;
		public int getSpan() {
			return span;
		}
		public void setSpan(int span) {
			this.span = span;
		}
		public int getStart() {
			return start;
		}
		public void setStart(int start) {
			this.start = start;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		private double slope;
		//private double length;
		private double average;
		public double getAverage() {
			return average;
		}
		public void setAverage(double average) {
			this.average = average;
		}
		public double getSlope() {
			return slope;
		}
		public void setSlope(double slope) {
			this.slope = slope;
		}
//		public double getLength() {
//			return length;
//		}
//		public void setLength(double length) {
//			this.length = length;
//		}
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
		//partern.timeSeriesToPatternsByIEO();
		partern.outliesDectation();
		
	}

	@Override
	public void TimeSeriesAnalysis() {
		//timeSeriesToPatternsByTEO();
		//timeSeriesToPatternsByIEO();
		//timeSeriesToPatterns();
		timeSeriesToPatternByStep(50);
		outliesDectation();
	}

	@Override
	public DataItems getOutlies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataItems getPredictItems() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class Point implements Comparable<Point>{
		private int pointIndex;
		private double pointValue;
		public int getPointIndex() {
			return pointIndex;
		}
		public void setPointIndex(int pointIndex) {
			this.pointIndex = pointIndex;
		}
		public Double getPointValue() {
			return pointValue;
		}
		public void setPointValue(double pointValue) {
			this.pointValue = pointValue;
		}
		
		@Override
		public int compareTo(Point o) {
			return this.getPointValue().compareTo(o.getPointValue());
		}
	}
}


