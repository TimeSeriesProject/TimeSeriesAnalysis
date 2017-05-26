package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.poi.ss.formula.functions.Slope;
import org.omg.CORBA.INTERNAL;
/**极大极小值点线段化*/
public class PointSegment {

	private DataItems dataItems;
	private int ratio;
	private double patternThreshold;
	private ArrayList<Integer> pointsIndex;
	private int length;
	private List<PatternMent> patterns;
	private double std;
	
	public PointSegment(DataItems dataItems,int ratio){
		this.dataItems=dataItems;
		this.ratio=ratio;
		length=dataItems.getLength();
		pointsIndex=new ArrayList<Integer>();
		patterns=new ArrayList<PatternMent>();
//		setItemStd(dataItems);
	}
	public PointSegment(DataItems dataItems,int ratio,double patternThreshold){
		this.dataItems=dataItems;
		this.ratio=ratio;
		this.patternThreshold = patternThreshold;
		length=dataItems.getLength();
		pointsIndex=new ArrayList<Integer>();
		patterns=new ArrayList<PatternMent>();
//		setItemStd(dataItems);
	}
	
	public List<PatternMent> getPatterns(){
		splitByPointsInDistnce();
		addtionalPoint();
		/*List<Integer> copyPoints=new ArrayList<Integer>();
		for(int point:pointsIndex)
			copyPoints.add(point);
		for(int i=0;i<copyPoints.size()-1;i++){
			splitByPointsInArea(copyPoints.get(i), copyPoints.get(i+1));
		}*/
		genPatterns();
		return patterns;
	}
	
	public List<PatternMent> getTEOPattern(){
		splitByPointsInDistnce();
		List<Integer> copyPoints=new ArrayList<Integer>();
		for(int point:pointsIndex)
			copyPoints.add(point);
		for(int i=0;i<copyPoints.size()-1;i++){
			splitByPointsInArea(copyPoints.get(i), copyPoints.get(i+1));
		}
		List<PatternMent> teoPatterns=new ArrayList<PatternMent>();
		Collections.sort(pointsIndex);
		int len=pointsIndex.size();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<len-1;i++){
			statistics.clear();
			int start=pointsIndex.get(i);
			int end=pointsIndex.get(i+1);
			PatternMent pattern=new PatternMent();
			pattern.setStart(start);
			pattern.setEnd(end);
			pattern.setSlope((getItem(end)-getItem(start))/(end-start+1));
			pattern.setSpan(end-start+1);
			for(int pos=start;pos<=end;pos++){
				statistics.addValue(getItem(pos));
			}
			pattern.setAverage(statistics.getMean());
			teoPatterns.add(pattern);
		}
		return teoPatterns;
	}
	
	private double getItem(int index){
		return Double.parseDouble(dataItems.getData().get(index));
	}
	
	private void genPatterns(){
		Collections.sort(pointsIndex);
		int len=pointsIndex.size();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<len-1;i++){
			statistics.clear();
			int start=pointsIndex.get(i);
			int end=pointsIndex.get(i+1);
			int span = end-start;
			double endData = Double.parseDouble(dataItems.getElementAt(end).getData());
			double startData = Double.parseDouble(dataItems.getElementAt(start).getData());
			double hspan = endData-startData;
			double angle = (endData-startData)/(end - start);
			//double height = getItem(end)-getItem(start);
									
			for(int pos=start;pos<=end;pos++){
				statistics.addValue(getItem(pos));
			}
			double mean = statistics.getMean();
			double std = statistics.getStandardDeviation();
			
			
			//pattern.setAngle(angle);
			PatternMent pattern=new PatternMent(start,span,angle,startData,mean,std);
			pattern.setHspan(hspan);
			patterns.add(pattern);
		}
	}
	
	private void splitByPointsInDistnce(){
		pointsIndex.add(0);
		int minIndex=1;
		int maxIndex=1;
		minIndex=findMaxinum(1, ratio);
		maxIndex=findMininum(1, ratio);
		if(minIndex<maxIndex){
			pointsIndex.add(minIndex);
			while(minIndex<length-1){
				maxIndex=findMininum(minIndex, ratio);
				minIndex=findMaxinum(maxIndex, ratio);
				if(minIndex==maxIndex)
					break;
				pointsIndex.add(maxIndex);
				pointsIndex.add(minIndex);
			}
		}else{
			pointsIndex.add(maxIndex);
			while(maxIndex<length-1){
				minIndex=findMaxinum(maxIndex, ratio);
				maxIndex=findMininum(minIndex, ratio);
				if(minIndex==maxIndex)
					break;
				pointsIndex.add(minIndex);
				pointsIndex.add(maxIndex);
			}
		}
		pointsIndex.add(length-1);
	}
	
	private void addtionalPoint(){
		List<Integer> points=new ArrayList<Integer>();
		points.addAll(pointsIndex);
		pointsIndex.clear();
		pointsIndex.add(0);
		int length=points.size();
		double sValue,dValue;
		double min,max;
		for(int i=0;i<length-1;i++){
			if(points.get(i+1)-points.get(i)<4){
				pointsIndex.add(points.get(i+1));
				continue;
			}
			sValue=getItem(points.get(i));
			dValue=getItem(points.get(i+1));
			min=sValue<dValue?sValue:dValue;
			max=sValue>dValue?sValue:dValue;
			int maxIndex=findmaxIndexBetween(points.get(i), points.get(i+1));
			int minIndex=findminIndexBetween(points.get(i), points.get(i+1));
			if(getItem(maxIndex)>max){
				if(max==sValue){
					pointsIndex.add(minIndex);
					pointsIndex.add(maxIndex);
					
				}else{
					pointsIndex.add(maxIndex);
					pointsIndex.add(minIndex);
				}
			}else if(getItem(minIndex)<min){
				if(min==sValue){
					pointsIndex.add(maxIndex);
					pointsIndex.add(minIndex);
					
				}else{
					pointsIndex.add(minIndex);
					pointsIndex.add(maxIndex);
				}
			}
			pointsIndex.add(points.get(i+1));
		}
	}
	
	private int findmaxIndexBetween(int start,int end){
		if(start>=end){
			throw new RuntimeException();
		}
		int maxIndex=0;
		double maxValue=Double.MIN_VALUE;
		for(int i=start+1;i<end;i++){
			if(getItem(i)>maxValue){
				maxValue=getItem(i);
				maxIndex=i;
			}
		}
		return maxIndex;
	}
	
	private int findminIndexBetween(int start,int end){
		if(start>=end){
			throw new RuntimeException();
		}
		int minIndex=0;
		double minValue=Double.MAX_VALUE;
		for(int i=start+1;i<end;i++){
			if(getItem(i)<minValue){
				minValue=getItem(i);
				minIndex=i;
			}
		}
		return minIndex;
	}
	
	private void splitByPointsInRatio(){
		int index=findFirstTwo();
		if(index<(length-1)&&(getItem(index)>getItem(0))){
			index=findMininum(index);
		}
		while(index<length-1){
			index=findMaxinum(index);
			index=findMininum(index);
		}
	}
	private void splitByPointsInArea(int start,int end){
		if(end-start<=5)
			return;
		double startY=getItem(start);
		double endY=getItem(end);
		double slope=(endY-startY)/(end-start);
		double maxSpan=1;
		int index=0;
		double span;
		for(int i=start+1;i<end;i++){
			double ax=start-i;
			double bx=end-i;
			double ay=getItem(start)-getItem(i);
			double by=getItem(end)-getItem(i);
			double aLen=Math.sqrt(ax*ax+ay*ay);
			double bLen=Math.sqrt(bx*bx+by*by);
			span=(ax*bx+ay*by)/(aLen*bLen);
			if(Math.cos(7*Math.PI/8)<=span&&span<=Math.cos(Math.PI/8)){
				if(Math.abs(span)<maxSpan){
					index=i;
					maxSpan=Math.abs(span);
				}
			}
		}
		if(Math.cos(7*Math.PI/8)<=maxSpan&&maxSpan<=Math.cos(Math.PI/8)){
			pointsIndex.add(index);
//			splitByPointsInArea(start, index);
//			splitByPointsInArea(index, end);
		}
	}
	
	private int findMininum(int index){
		int iMin=index;
		int i=index;
		while(i<(length-1)&&(getItem(i)/getItem(iMin))<ratio){
			if(getItem(i)<getItem(iMin)){
				iMin=i;
			}
			i+=1;
		}
		pointsIndex.add(iMin);
		return i;
	}
	private int findMininum(int index,int span){
		int iMin=index;
		int i=index;
		for(;i<(length-1);i++){
			if(getItem(i)<getItem(iMin))
				iMin=i;
			if(std>1){
				if((i-iMin)>=span&&getItem(i)>getItem(iMin)&&(getItem(i)-patternThreshold*getItem(i))>=getItem(iMin)){
					//pointsIndex.add(iMin);
					break;
				}
			}else{
				if((i-iMin)>=span&&getItem(i)>getItem(iMin)&&(getItem(i)-patternThreshold*std)>=getItem(iMin)){
					//pointsIndex.add(iMin);
					break;
				}
			}
		}
		return iMin;
	}
	
	private int findMaxinum(int index){
		int iMax=index;
		int i=index;
		while(i<(length-1)&&(getItem(iMax)/getItem(i))<ratio){
			if(getItem(iMax)<getItem(i)){
				iMax=i;
			}
			i+=1;
		}
		pointsIndex.add(iMax);
		return i;
	}
	
	private int findMaxinum(int index,int span){
		int iMax=index;
		int i=index;
		for(;i<(length-1);i++){
			if(getItem(i)>getItem(iMax))
				iMax=i;
			if(std>1){
				if((i-iMax)>=span&&getItem(i)<getItem(iMax)&&getItem(i)<=(getItem(iMax)-patternThreshold*getItem(iMax))){
					//pointsIndex.add(iMax);
					break;
				}
			}else{
				if((i-iMax)>=span&&getItem(i)<getItem(iMax)&&getItem(i)<=(getItem(iMax)-patternThreshold*std)){
					//pointsIndex.add(iMax);
					break;
				}
			}
			
		}
		return iMax;
	}
	
	private int findFirstTwo(){
		int iMin=0;
		int iMax=0;
		int i=0;
		while(i<(length-1)&&(getItem(iMax)/getItem(i))<ratio&&(getItem(i)/getItem(iMin))<ratio){
			if(getItem(iMax)<getItem(i)){
				iMax=i;
			}
			if(getItem(i)<getItem(iMin)){
				iMin=i;
			}
			i+=1;
		}
		if(iMin<iMax){
			pointsIndex.add(iMin);
			pointsIndex.add(iMax);
		}else if(iMin>iMax){
			pointsIndex.add(iMax);
			pointsIndex.add(iMin);
		}else{
			pointsIndex.add(iMin);
		}
		return i;
	}
	
	private int findFirstPoint(int span){
		int iMin=1;
		int iMax=1;
		int i=1;
		for(;i<(length-1);i++){
			if(getItem(i)>getItem(iMax))
				iMax=i;
			if(getItem(i)<getItem(iMin))
				iMin=i;
			if(getItem(i)<getItem(iMax)&&(i-iMax)>=span){
				pointsIndex.add(iMax);
				break;
			}
			if(getItem(i)>getItem(iMin)&&(i-iMin)>=span){
				pointsIndex.add(iMin);
				break;
			}
		}
		return i;
	}

	private void setItemStd(DataItems dataItems){
		List<String> datas=dataItems.getData();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String data:datas){
			statistics.addValue(Double.parseDouble(data));
		}
		std=statistics.getStandardDeviation();
	}
}
