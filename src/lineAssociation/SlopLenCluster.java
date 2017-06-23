package lineAssociation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SlopLenCluster {
	
	TreeMap<Integer, Linear> linears = null;
	public SlopLenCluster(TreeMap<Integer, Linear> linears) {
		 this.linears = linears;
	}
	public Map<Integer,Integer> run() {
		
		System.out.println("SlopLenCluster.run........");
		TreeMap<Integer, Integer> clusterCenter = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> slopeClusterCenter = caculateSlopeCenter();
		TreeMap<Integer, Integer> lenClusterCenter = caculateLenCenter();
		Iterator<Integer> iter = linears.keySet().iterator();
		while(iter.hasNext()) {
			
			int point = iter.next();
			int labelSlope = slopeClusterCenter.get(point);
			int labelLen = lenClusterCenter.get(point);
			int label = labelSlope*10+labelLen;
			clusterCenter.put(point, label);
		}
		return clusterCenter;
	}
	private TreeMap<Integer, Integer> caculateSlopeCenter() {
		System.out.println("SlopLenCluster.caculateSlopeCenter........");
		TreeMap<Integer, Integer> slopeClusterCenter = new TreeMap<Integer, Integer>();
		double[] slopeLadder = {-30,-20,-10,0,10,20,30,91};
		double maxSlope = Double.MIN_VALUE;
		double minSlope = Double.MAX_VALUE;
		HashMap<Integer, Integer> clusterNum = new HashMap<Integer, Integer>();
		Iterator<Integer> iter = linears.keySet().iterator();
		while(iter.hasNext()) {
			
			int start = iter.next();
			Linear linear = linears.get(start);
			double degree = linear.theta*180/(Math.PI);
			
			if(maxSlope < degree){
				maxSlope = degree;
			}
			if(minSlope > degree) {
				minSlope = degree;
			}
			for(int i = 0;i < slopeLadder.length;i++) {
				
				if(degree < slopeLadder[i]) {
					
					if(clusterNum.containsKey(i))
						clusterNum.put(i, clusterNum.get(i)+1);
					else {
						clusterNum.put(i, 1);
					}
					slopeClusterCenter.put(start, i);
					break;
				}
			}
		}
		System.out.println("最大斜率："+maxSlope+", 最小斜率："+minSlope);
		System.out.println("斜率聚类数为："+clusterNum.size());
		return slopeClusterCenter;
	}
	private TreeMap<Integer, Integer> caculateLenCenter() {
		
		System.out.println("SlopLenCluster.caculateLenCenter........");
		TreeMap<Integer, Integer> lenClusterCenter = new TreeMap<Integer,Integer>();
		double maxLen = Double.MIN_VALUE;
		double minLen = Double.MAX_VALUE;
		HashMap<Integer, Integer> clusterNum = new HashMap<Integer, Integer>();
		Iterator<Integer> iter = linears.keySet().iterator();
		while(iter.hasNext()) {
			
			int start = iter.next();
			Linear linear = linears.get(start);
			double distance = Math.sqrt(Math.pow(linear.hspan, 2)+Math.pow(linear.span, 2));
			if(distance > maxLen) {
				maxLen = distance;
			}
			if(distance < minLen) {
				minLen = distance;
			}
		}
		
		double perPart = (maxLen - minLen)/5;
		double[] distanceLadder = {minLen+perPart,minLen+2*perPart,minLen+3*perPart,minLen+4*perPart,maxLen+0.1};
		iter = linears.keySet().iterator();
		while(iter.hasNext()) {
			
			int start = iter.next();
			Linear linear = linears.get(start);
			double distance = Math.sqrt(Math.pow(linear.hspan, 2)+Math.pow(linear.span, 2));
			for(int i = 0;i < distanceLadder.length;i++) {
				
				if(distance < distanceLadder[i]) {
					if(clusterNum.containsKey(i))
						clusterNum.put(i, clusterNum.get(i)+1);
					else {
						clusterNum.put(i, 1);
					}
					
					lenClusterCenter.put(start, i);
					break;
				}
				
			}
		}
		System.out.println("最大长度："+maxLen+", 最小长度："+minLen);
		System.out.println("线段长度聚类数为："+clusterNum.size());
		return lenClusterCenter;
	}
}
