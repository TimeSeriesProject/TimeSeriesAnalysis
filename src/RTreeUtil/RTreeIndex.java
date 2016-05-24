package RTreeUtil;

import java.util.ArrayList;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class RTreeIndex {
	
	public NonLeafNode root;
	private List<Integer> path;    //记录访问的路径
	
	public RTreeIndex(){
		root=new NonLeafNode();
		root.parent=null;
	}
	
	public void insert(DataItems di,String name){
		TimeSeries timeSeries=new TimeSeries(di,name);
		path=new ArrayList<Integer>();
		insert(timeSeries,root,-1);
	}
	
	private void insert(TimeSeries timeSeries,NonLeafNode node,int pos){
		double[] PAA=timeSeries.PAA;
		if(node.existChildsNum==0){
			spaceDeepCopy(node.hSpace,PAA);
			spaceDeepCopy(node.lSPace,PAA);
			node.existChildsNum=1;
			LeafNode leafNode=new LeafNode();
			leafNode.timeSeiresNum=1;
			spaceDeepCopy(leafNode.hSpace,PAA);
			spaceDeepCopy(leafNode.lSPace,PAA);
			leafNode.timeSeries.add(timeSeries);
			leafNode.parent=node;
			node.leafChilds.add(leafNode);
			node.nextTOLeaf=true;
		}else{
			double addAreaMin=Double.MAX_VALUE;
			int index=0;
			if(node.nextTOLeaf){
				for(int i=0;i<node.existChildsNum;i++){
					double addArea=increseArea(PAA, node.leafChilds.get(i).hSpace, node.leafChilds.get(i).lSPace);
					if(addAreaMin>addArea){
						addAreaMin=addArea;
						index=i;
					}
				}
				changeSpace(PAA, node.hSpace, node.lSPace);
				path.add(index);
				insert(timeSeries, node.leafChilds.get(index),pos+1);
			}else{
				for(int i=0;i<node.existChildsNum;i++){
					double addArea=increseArea(PAA, node.nonLeafChilds.get(i).hSpace, node.nonLeafChilds.get(i).lSPace);
					if(addAreaMin>addArea){
						addAreaMin=addArea;
						index=i;
					}
				}
				changeSpace(PAA, node.hSpace, node.lSPace);
				path.add(index);
				insert(timeSeries, node.nonLeafChilds.get(index),pos+1);
			}
		}
	}
	
	
	
	private void insert(TimeSeries timeSeries,LeafNode leaf,int pos){
		if(!leaf.isFull()){
			leaf.timeSeries.add(timeSeries);
			leaf.timeSeiresNum++;
			double[] PAA=timeSeries.PAA;
			changeSpace(PAA, leaf.hSpace, leaf.lSPace);
			path.clear();
		}else{
			splitLeafNOde(leaf,timeSeries,pos);
		}
	}
	
	private void splitLeafNOde(LeafNode node,TimeSeries timeSeries,int pos){
		LeafNode leafNode1=new LeafNode();
		LeafNode leafNode2=new LeafNode();
		leafNode1.timeSeries.add(timeSeries);
		leafNode1.timeSeiresNum=1;
		spaceDeepCopy(leafNode1.hSpace,timeSeries.PAA);
		spaceDeepCopy(leafNode1.lSPace,timeSeries.PAA);
		int index=pickFirst(node, timeSeries);
		leafNode2.timeSeries.add(node.timeSeries.get(index));
		leafNode2.timeSeiresNum=1;
		spaceDeepCopy(leafNode2.hSpace,node.timeSeries.get(index).PAA);
		spaceDeepCopy(leafNode2.lSPace,node.timeSeries.get(index).PAA);
		node.timeSeries.remove(index);
		
		while(node.timeSeries.size()>0){
			pickNext(node, leafNode1, leafNode2);
		}
		leafNode1.parent=node.parent;
		node.parent.leafChilds.set(path.get(pos), leafNode1);   //pos存放的path的下标
		if(!node.parent.isFull()){
			leafNode2.parent=node.parent;
			node.parent.leafChilds.add(leafNode2);
			node.parent.existChildsNum+=1;
			path.clear();
		}else{
			splitNonLeafNOde(node.parent, leafNode2, pos-1);
		}
		node.parent=null;
	}
	
	
	private void splitNonLeafNOde(NonLeafNode node,LeafNode leafNode,int pos){
		NonLeafNode nonLeafNode1=new NonLeafNode();
		NonLeafNode nonLeafNode2=new NonLeafNode();
		nonLeafNode1.nextTOLeaf=true;
		nonLeafNode1.leafChilds.add(leafNode);
		leafNode.parent=nonLeafNode1;
		spaceDeepCopy(nonLeafNode1.hSpace,leafNode.hSpace);
		spaceDeepCopy(nonLeafNode1.lSPace,leafNode.lSPace);
		nonLeafNode1.existChildsNum=1;
		
		int index=pickFirst(node, leafNode);
		
		nonLeafNode2.nextTOLeaf=true;
		nonLeafNode2.leafChilds.add(node.leafChilds.get(index));
		node.leafChilds.get(index).parent=nonLeafNode2;
		spaceDeepCopy(nonLeafNode2.hSpace,node.leafChilds.get(index).hSpace);
		spaceDeepCopy(nonLeafNode2.lSPace,node.leafChilds.get(index).lSPace);
		nonLeafNode2.existChildsNum=1;
		node.leafChilds.remove(index);
		
		while(node.leafChilds.size()>0){
			pickNext(node, nonLeafNode1, nonLeafNode2);
		}
		if(node==root){
			generateRoot(nonLeafNode1,nonLeafNode2);
		}else{
			nonLeafNode1.parent=node.parent;
			node.parent.nonLeafChilds.set(path.get(pos), nonLeafNode1);
			if(!node.parent.isFull()){
				nonLeafNode2.parent=node.parent;
				node.parent.nonLeafChilds.add(nonLeafNode2);
				node.parent.existChildsNum+=1;
				path.clear();
			}else{
				splitNonLeafNOde(node.parent, nonLeafNode2, pos-1);
				node.parent=null;
			}
		}
	}
	
	private void splitNonLeafNOde(NonLeafNode node,NonLeafNode nonleafNode,int pos){
		NonLeafNode nonLeafNode1=new NonLeafNode();
		NonLeafNode nonLeafNode2=new NonLeafNode();
		nonLeafNode1.nextTOLeaf=false;
		nonLeafNode1.nonLeafChilds.add(nonleafNode);
		nonleafNode.parent=nonLeafNode1;
		nonLeafNode1.existChildsNum=1;
		spaceDeepCopy(nonLeafNode1.hSpace, nonleafNode.hSpace);
		spaceDeepCopy(nonLeafNode1.lSPace, nonleafNode.lSPace);
		
		int index=pickFirst(node, nonleafNode);
		nonLeafNode2.nextTOLeaf=false;
		nonLeafNode2.nonLeafChilds.add(node.nonLeafChilds.get(index));
		node.nonLeafChilds.get(index).parent=nonLeafNode2;
		nonLeafNode2.existChildsNum=1;
		spaceDeepCopy(nonLeafNode2.hSpace, node.nonLeafChilds.get(index).hSpace);
		spaceDeepCopy(nonLeafNode2.lSPace, node.nonLeafChilds.get(index).lSPace);
		
		node.nonLeafChilds.remove(index);
		
		while(node.nonLeafChilds.size()>0){
			pickNext(node, nonLeafNode1, nonLeafNode2);
		}
		if(node==root){
			generateRoot(nonLeafNode1,nonLeafNode2);
		}else{
			nonLeafNode1.parent=node.parent;
			node.parent.nonLeafChilds.set(path.get(pos), nonLeafNode1);
			if(!node.parent.isFull()){
				nonLeafNode2.parent=node.parent;
				node.parent.nonLeafChilds.add(nonLeafNode2);
				node.parent.existChildsNum+=1;
			}else{
				splitNonLeafNOde(node.parent,nonLeafNode2,pos-1);
				node.parent=null;
			}
		}
	}
	
	
	/**
	 * 分裂时选择第一个分裂出去的序列
	 * @param node 要分裂的节点
	 * @param timeSeries 要加入的序列
	 * @return 第一个分裂出来的序列
	 */
	private int pickFirst(LeafNode node,TimeSeries timeSeries){
		List<TimeSeries> series=node.timeSeries;
		int len=series.size();
		int index=0;
		double MaxArea=0;
		for(int i=0;i<len;i++){
			double area=areaDistance(series.get(i).PAA, timeSeries.PAA);
			if(MaxArea<area){
				MaxArea=area;
				index=i;
			}
		}
		return index;
	}
	
	private int pickFirst(NonLeafNode node,LeafNode leafNode){
		List<LeafNode> nodes=node.leafChilds;
		int len=nodes.size();
		int index=0;
		double MaxArea=0;
		for(int i=0;i<len;i++){
			double area=areaDistance(nodes.get(i).hSpace, nodes.get(i).lSPace, leafNode.hSpace, leafNode.lSPace);
			if(area>MaxArea){
				MaxArea=area;
				index=i;
			}
		}
		return index;
	}
	
	private int pickFirst(NonLeafNode node,NonLeafNode nonLeafNode){
		List<NonLeafNode> nodes=node.nonLeafChilds;
		int len=nodes.size();
		int index=0;
		double MaxArea=0;
		for(int i=0;i<len;i++){
			double area=areaDistance(nodes.get(i).hSpace, nodes.get(i).lSPace, nonLeafNode.hSpace, nonLeafNode.lSPace);
			if(area>MaxArea){
				MaxArea=area;
				index=i;
			}
		}
		return index;
	}
	
	private void pickNext(LeafNode node,LeafNode leafNode1,LeafNode leafNode2){
		List<TimeSeries> list=node.timeSeries;
		int index=0;
		double maxArea=0;
		for(int i=0;i<list.size();i++){
			double[] PAA=list.get(i).PAA;
			double increaseArea1=increseArea(PAA, leafNode1.hSpace, leafNode1.lSPace);
			double increaseArea2=increseArea(PAA, leafNode2.hSpace, leafNode2.lSPace);
			if(Math.abs(increaseArea1-increaseArea2)>maxArea){
				maxArea=Math.abs(increaseArea1-increaseArea2);
				index=i;
			}
		}
		TimeSeries selectedSeries=node.timeSeries.get(index);
		if(leafNode1.timeSeiresNum<=leafNode2.timeSeiresNum){
			leafNode1.timeSeries.add(selectedSeries);
			leafNode1.timeSeiresNum++;
			changeSpace(selectedSeries.PAA, leafNode1.hSpace, leafNode1.lSPace);
		}else{
			leafNode2.timeSeries.add(selectedSeries);
			leafNode2.timeSeiresNum++;
			changeSpace(selectedSeries.PAA, leafNode2.hSpace, leafNode2.lSPace);
		}
		node.timeSeries.remove(index);
	}
	
	
	private void pickNext(NonLeafNode node,NonLeafNode nonLeafNode1,NonLeafNode nonLeafNode2){
		if(node.nextTOLeaf){
			List<LeafNode> list=node.leafChilds;
			int index=0;
			double maxArea=0;
			for(int i=0;i<list.size();i++){
				double increaseArea1=increseArea(list.get(i).hSpace,list.get(i).lSPace,
						nonLeafNode1.hSpace, nonLeafNode1.lSPace);
				double increaseArea2=increseArea(list.get(i).hSpace,list.get(i).lSPace,
						nonLeafNode2.hSpace, nonLeafNode2.lSPace);
				if(Math.abs(increaseArea1-increaseArea2)>maxArea){
					maxArea=Math.abs(increaseArea1-increaseArea2);
					index=i;
				}
			}
			LeafNode selectedNode=node.leafChilds.get(index);
			if(nonLeafNode1.existChildsNum<=nonLeafNode2.existChildsNum){
				nonLeafNode1.leafChilds.add(selectedNode);
				nonLeafNode1.existChildsNum+=1;
				selectedNode.parent=nonLeafNode1;
				changeSpace(selectedNode.hSpace, selectedNode.lSPace, nonLeafNode1.hSpace, nonLeafNode1.lSPace);
			}else{
				nonLeafNode2.leafChilds.add(selectedNode);
				nonLeafNode2.existChildsNum+=1;
				selectedNode.parent=nonLeafNode2;
				changeSpace(selectedNode.hSpace, selectedNode.lSPace, nonLeafNode2.hSpace, nonLeafNode2.lSPace);
			}
			node.leafChilds.remove(index);
		}else{
			List<NonLeafNode> list=node.nonLeafChilds;
			int index=0;
			double maxArea=0;
			for(int i=0;i<list.size();i++){
				double increaseArea1=increseArea(list.get(i).hSpace,list.get(i).lSPace,
						nonLeafNode1.hSpace, nonLeafNode1.lSPace);
				double increaseArea2=increseArea(list.get(i).hSpace,list.get(i).lSPace,
						nonLeafNode2.hSpace, nonLeafNode2.lSPace);
				if(Math.abs(increaseArea1-increaseArea2)>maxArea){
					maxArea=Math.abs(increaseArea1-increaseArea2);
					index=i;
				}
			}
			NonLeafNode selectedNode=node.nonLeafChilds.get(index);
			if(nonLeafNode1.existChildsNum<=nonLeafNode2.existChildsNum){
				nonLeafNode1.nonLeafChilds.add(selectedNode);
				nonLeafNode1.existChildsNum+=1;
				selectedNode.parent=nonLeafNode1;
				changeSpace(selectedNode.hSpace, selectedNode.lSPace, nonLeafNode1.hSpace, nonLeafNode1.lSPace);
			}else{
				nonLeafNode2.nonLeafChilds.add(selectedNode);
				nonLeafNode2.existChildsNum+=1;
				selectedNode.parent=nonLeafNode2;
				changeSpace(selectedNode.hSpace, selectedNode.lSPace, nonLeafNode2.hSpace, nonLeafNode2.lSPace);
			}
			node.nonLeafChilds.remove(index);
		}
		
	}
	
	private double areaDistance(double[] series1,double[] series2){
		double area=1.0;
		int length=series1.length;
		for(int i=0;i<length;i++){
			area*=(Math.abs(series1[i]-series2[i]));
		}
		return area;
	}
	
	private double areaDistance(double[] hSpace1,double[] lSpace1,double[] hSpace2,double[] lSpace2){
		double area=1.0;
		int len=hSpace1.length;
		for(int i=0;i<len;i++){
			area*=(Math.max(hSpace1[i], hSpace2[i])-Math.min(lSpace1[i], lSpace2[i]));
		}
		return area;
	}
	
	private double increseArea(double[] PAA,double[] hSPace,double[] lSpace){
		double area=1.0;
		double addArea=1.0;
		int length=PAA.length;
		for(int i=0;i<length;i++){
			area*=(hSPace[i]-lSpace[i]);
		}
		for(int i=0;i<length;i++){
			if(hSPace[i]<PAA[i]){
				addArea*=(PAA[i]-lSpace[i]);
			}else if (lSpace[i]>PAA[i]) {
				addArea*=(hSPace[i]-PAA[i]);
			}else{
				addArea*=(hSPace[i]-lSpace[i]);
			}
		}
		return (addArea-area);
	}
	
	private double increseArea(double[] addHSpace,double[] addLSpace,double[] hSPace,double[] lSpace){
		double area=1.0;
		double addArea=1.0;
		int length=addHSpace.length;
		for(int i=0;i<length;i++){
			area*=(hSPace[i]-lSpace[i]);
		}
		for(int i=0;i<length;i++){
			addArea*=(Math.max(addHSpace[i], hSPace[i])-Math.min(addLSpace[i], lSpace[i]));
		}
		return (addArea-area);
	}
	
	private void changeSpace(double[] PAA,double[] hSpace,double[] lSpace){
		int length=PAA.length;
		for(int i=0;i<length;i++){
			if(hSpace[i]<PAA[i]){
				hSpace[i]=PAA[i];
			}else if (lSpace[i]>PAA[i]) {
				lSpace[i]=PAA[i];
			}
		}
	}
	
	private void changeSpace(double[] hSpace1,double[] lSpace1,double[] hSpace2,double[] lSpace2){
		int length=hSpace1.length;
		for(int i=0;i<length;i++){
			if(hSpace2[i]<hSpace1[i]){
				hSpace2[i]=hSpace1[i];
			}else if (lSpace2[i]>lSpace1[i]) {
				lSpace2[i]=lSpace1[i];
			}
		}
	}
	
	private void spaceDeepCopy(double[] PAA,double[] space){
		for(int i=0;i<space.length;i++)
			PAA[i]=space[i];
	}
	
	private void generateRoot(NonLeafNode nonLeafNode1,NonLeafNode nonLeafNode2){
		NonLeafNode newRootNode=new NonLeafNode();
		newRootNode.existChildsNum=2;
		newRootNode.nextTOLeaf=false;
		newRootNode.nonLeafChilds.add(nonLeafNode1);
		newRootNode.nonLeafChilds.add(nonLeafNode2);
		nonLeafNode1.parent=newRootNode;
		nonLeafNode2.parent=newRootNode;
		root=newRootNode;
		//更新 new root的空间
		spaceDeepCopy(root.hSpace, nonLeafNode1.hSpace);
		spaceDeepCopy(root.lSPace, nonLeafNode1.lSPace);
		changeSpace(nonLeafNode2.hSpace, nonLeafNode2.lSPace, root.hSpace, root.lSPace);
	}
}
