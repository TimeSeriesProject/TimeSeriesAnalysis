package WaveletUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;

public class SAXPartternDetection implements IMinerOM{
	private DataItems dataItems;     //时间序列 
	private int patternLen;
	private int patternAlphaNum;
	private DataItems outlies;
	private List<Vector> vectorList;
	private List<Integer> numList;
	private double dist;
	private Node root;
	
	private DescriptiveStatistics statistics=new DescriptiveStatistics();
	
	public SAXPartternDetection(DataItems dataItems,int length){
		this.dataItems=dataItems;
		this.patternLen=length;
		patternAlphaNum=5;
		setpatternAlphaNum();
		outlies=new DataItems();
		vectorList=new ArrayList<Vector>();
		numList=new ArrayList<Integer>();
		root=new Node(patternAlphaNum);
	}
	
	private void setpatternAlphaNum(){
		patternAlphaNum=5;
		if(patternLen<=patternAlphaNum){
			patternAlphaNum=patternLen;
			return;
		}else if(patternLen<=2*patternAlphaNum){
			for(int i=2;i<patternAlphaNum;i++){
				if(patternLen%i==0){
					patternAlphaNum=i;
					return;
				}
			}
			patternAlphaNum=patternLen;
		}
	}
	
	private List<List<String>> split(List<String> list,int len){
		List<List<String>>splitLists=new ArrayList<List<String>>();
		int listLen=list.size();
		for(int i=0;i<=listLen-len;i++){
			List<String> splitSeg=new ArrayList<String>();
			for(int j=i;j<i+len;j++){
				splitSeg.add(list.get(j));
			}
			splitLists.add(splitSeg);
		}
		return splitLists;
	}
	
	private List<Double> transTODouble(List<String> list){
		List<Double> doubleList=new ArrayList<Double>();
		for(String num:list)
			doubleList.add(Double.parseDouble(num));
		return doubleList;
	}
	private List<String> transToString(List<Double> list){
		List<String> strList=new ArrayList<String>();
		for(double item:list)
			strList.add(item+"");
		return strList;
	}
	
	private List<Integer> transferToChar(List<Double> list){
		List<Integer> chars=new ArrayList<Integer>();
		List<Double> pattern=new ArrayList<Double>();
		for(int i=0;i<list.size()/patternAlphaNum;i++){
			pattern.clear();
			for(int j=i*patternAlphaNum;j<(i+1)*patternAlphaNum;j++)
				pattern.add(list.get(j));
			double mean=getMean(pattern);
			normalSplit(mean,chars);
		}
		
		if(list.size()%patternAlphaNum>0){
			statistics.clear();
			for(int i=(list.size()/patternAlphaNum)*patternAlphaNum;i<list.size();i++)
				statistics.addValue(list.get(i));
			double mean=statistics.getMean();
			normalSplit(mean, chars);
		}
		return chars;
	}
	
	private void normalSplit(double mean,List<Integer>chars){
		if(-0.25<=mean&&mean<=0.25)        chars.add(3);
		else if(0.25<mean&&mean<=0.84)     chars.add(4);
		else if(-0.84<=mean&&mean<-0.25)   chars.add(2);
		else if(mean>0.84)       chars.add(5);
		else     chars.add(1);
	}
	
	private void normalLize(List<Double> list){
		statistics.clear();
		for(double num:list)
			statistics.addValue(num);
		double mean=statistics.getMean();
		double std=statistics.getStandardDeviation();
		for(int i=0;i<list.size();i++)
			list.set(i, (list.get(i)-mean)/std);
	}
	
	private double getMean(List<Double> nums){
		statistics.clear();
		for(double num:nums)
			statistics.addValue(num);
		return statistics.getMean();
	}
	
	
	/**
	 * 检测异常
	 * @return
	 */
	public void outliesDectation(){
		List<String> data=dataItems.getData();
		List<Double> doubleData=transTODouble(data);
		normalLize(doubleData);
		List<String> normalData=transToString(doubleData);
		List<List<String>> lists=split(normalData, patternLen);
		List<List<Integer>> charlists=new ArrayList<List<Integer>>();
		for(List<String>list:lists){
			List<Double>doubleList=transTODouble(list);
			List<Integer> chars=transferToChar(doubleList);
			charlists.add(chars);
		}
		generateData(charlists);
		List<Double>nearstDists=new ArrayList<Double>();
		for(int i=0;i<vectorList.size();i++){
			double dist=innerSearch(i);
			if(dist>=6)
				System.out.println();
			double dist2=innerSearch(i);
			nearstDists.add(dist);
		}
		if(nearstDists.size() <= 0)
			return ;
		for(int i=0;i<patternLen-1;i++)
			nearstDists.add(nearstDists.get(nearstDists.size()-1));
		List<String> outliesData=transToString(nearstDists);
		for(int i=1;i<dataItems.getLength();i++){
			outlies.add1Data(dataItems.getTime().get(i), outliesData.get(i));
		}
	}
	
	private double innerSearch(int pos){
		Vector vector=vectorList.get(pos);
		Node node=root;
		if(nearnestDist(vector,node,pos,0)){
			return 0;
		}else{
			dist=Double.MAX_VALUE;
			nearnestDist(vector,node,pos,0,0);
			return dist;
		}
	}
	
	private void generateData(List<List<Integer>> transferChars){
		HashMap<Vector, Integer> vectorMap=new HashMap<Vector, Integer>();
		for(List<Integer> chars:transferChars){
			Vector vector=new Vector(chars);
			if(vectorMap.containsKey(vector)){
				int num=vectorMap.get(vector);
				vectorMap.put(vector, num+1);
			}else{
				vectorMap.put(vector, 1);
			}
			vectorList.add(vector);
		}
		for(Vector vector:vectorList)
			numList.add(vectorMap.get(vector));
		buildTree(root,vectorList);
	}
	
	private void buildTree(Node node,List<Vector> vectorList){
		for(int i=0;i<vectorList.size();i++){
			node=root;
			Vector vector=vectorList.get(i);
			List<Integer> list=vector.chars;
			for(int item:list){
				if(node.childs.get(item-1)!=null){
					node=node.childs.get(item-1);
				}else{
					Node newNode=new Node(patternAlphaNum);
					node.childs.set(item-1, newNode);
					node=newNode;
				}
			}
			node.isLeaf=true;
			node.leafLink.add(i);
		}
	}
	
	
	private void nearnestDist(Vector vector,Node node,int pos,int level,double normalDist){
		if(node.isLeaf){
			for(int index:node.leafLink){
				if(Math.abs(pos-index)>=patternLen){
					double otherDist= distanceOfPatterns(vector.chars, vectorList.get(index).chars);
					if(otherDist<dist){
						dist=otherDist;
						break;
					}
				}
			}
			return;
		}
		for(int i=0;i<node.childs.size();i++){
			if(node.childs.get(i)==null)
				continue;
			double addDist=Math.abs(vector.chars.get(level)-i-1);
			if(normalDist+addDist>dist)
				continue;
			nearnestDist(vector,node.childs.get(i),pos,level+1,normalDist+addDist);
		}
	}
	
	private boolean nearnestDist(Vector vector,Node node,int pos,int level){
		if(node.isLeaf){
			for(int index:node.leafLink){
				if(Math.abs(pos-index)>=patternLen){
					return true;
				}
			}
			return false;
		}
		return nearnestDist(vector, node.childs.get(vector.chars.get(level)-1), pos, level+1);
	}
	
	
	/**
	 * 两个模式之间的距离
	 * @param patternsA 
	 * @param patternsB 
	 * @return 模式间的距离
	 */
	private double distanceOfPatterns(List<Integer> listA,List<Integer> listB){
		int size=listA.size();
		int dist=0;
		for(int i=0;i<size;i++){
			dist+=Math.abs(listA.get(i)-listB.get(i));
		}
		return dist;
	}
	
	@Override
	public void TimeSeriesAnalysis() {
		outliesDectation();
	}
	
	@Override
	public DataItems getOutlies() {
		return outlies;
	}
	
	private void sort(List<Integer> distArray,List<Integer> array){
		int size=distArray.size();
		int minval=Integer.MAX_VALUE;
		int index=0;
		int temp;
		for(int i=0;i<size;i++){
			minval=Integer.MAX_VALUE;
			index=i;
			for(int j=i;j<size;j++){
				if(distArray.get(j)<minval){
					minval=distArray.get(j);
					index=j;
				}
			}
			temp=distArray.get(i);
			distArray.set(i, distArray.get(index));
			distArray.set(index, temp);
			
			temp=array.get(i);
			array.set(i, array.get(index));
			array.set(index, temp);
		}
	}
	
	class Node{
		boolean isLeaf=false;
		List<Node> childs=new ArrayList<Node>();
		List<Integer> leafLink=new ArrayList<Integer>();
		public Node(int num){
			for(int i=0;i<num;i++)
				childs.add(null);
		}
	}
	class Vector{
		private List<Integer> chars;
		public Vector(List<Integer> chars){
			this.chars=chars;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Vector){
				Vector other=(Vector) obj;
				if(other.chars.size()==this.chars.size()){
					for(int i=0;i<this.chars.size();i++){
						if(!(other.chars.get(i)==this.chars.get(i))){
							return false;
						}
					}
					return true;
				}
				return false;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			int code=0;
			for(int num:this.chars){
				code*=10;
				code+=num;
			}
			return code;
		}
	}
}

