package RTreeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class KNNTimeSires {
	private static int K;
	private List<TimeSeries> results;
	private List<Double> distResults;
	private PriorityQueue<QueueNode> queue;
	private PriorityQueue<QueueNode> tempSeires;
	private NonLeafNode treeRoot;
	int num=0;
	
	public KNNTimeSires(int k,NonLeafNode root) {
		KNNTimeSires.K=k;
		queue=new PriorityQueue<QueueNode>();
		tempSeires=new PriorityQueue<QueueNode>();
		results=new ArrayList<TimeSeries>();
		distResults=new ArrayList<Double>();
		this.treeRoot=root;
	}
	
	public void K_search(TimeSeries query){
		
		queue.add(new QueueNode(0, treeRoot));
		while(!queue.isEmpty()){
			QueueNode top=queue.peek();
			for(int i=0;i<tempSeires.size();i++){
				if(tempSeires.peek().dist<=top.dist){
					results.add(tempSeires.peek().series);
					distResults.add(tempSeires.peek().dist);
					if(results.size()>=K)
						return;
					tempSeires.poll();
					i--;
				}
			}
			top=queue.poll();
			if(top.nodeType==1){
				List<String>list1=query.dataItems.getData();
				List<String>list2=top.series.dataItems.getData();
				int size1=list1.size();
				int size2=list2.size();
				double[][]matrix=new double[size1][size2];
				setMatrix(matrix,size1,size2);
				double dostance=DWT(list1, list2, size1-1, size2-1, matrix);
				num++;
				tempSeires.add(new QueueNode(dostance, top.series));
			}else if(top.nodeType==2){
				List<TimeSeries> seriesList=top.leafNode.timeSeries;
				for(TimeSeries series:seriesList){
					queue.add(new QueueNode(dist(series.PAA, query.paaHspace, query.paaLspace,query.dataItems.getLength()), series));
				}
			}else{
				if(top.nonLeafNode.nextTOLeaf){
					List<LeafNode> nodes=top.nonLeafNode.leafChilds;
					for(LeafNode node:nodes){
						double dist=dist(node.hSpace,node.lSPace, query.paaHspace, query.paaLspace,query.dataItems.getLength());
						queue.add(new QueueNode(dist, node));
					}
				}else{
					List<NonLeafNode> nodes=top.nonLeafNode.nonLeafChilds;
					for(NonLeafNode node:nodes){
						double dist=dist(node.hSpace,node.lSPace, query.paaHspace, query.paaLspace,query.dataItems.getLength());
						queue.add(new QueueNode(dist, node));
					}
				}
			}
		}
		while(results.size()<K) {
			if(tempSeires.size()>0){
				results.add(tempSeires.poll().series);
			}else {
				break;
			}
		}
	}
	
	private double dist(double[] PAA,double[] hSpace,double[] lSpace,int queryOriLength){
		int len=PAA.length;
		double sum=0.0;
		for(int i=0;i<len;i++){
			if(PAA[i]>hSpace[i])
				sum+=(PAA[i]-hSpace[i])*(PAA[i]-hSpace[i]);
			else if(PAA[i]<lSpace[i])
				sum+=(lSpace[i]-PAA[i])*(lSpace[i]-PAA[i]);
		}
		return Math.sqrt((queryOriLength*1.0/len)*sum);
	}
	
	private double dist(double[] hspace1,double[] lspace1,double[] hspace2,double[] lspace2,int queryOriLength){
		int len=hspace1.length;
		double sum=0.0;
		for(int i=0;i<len;i++){
			if(hspace1[i]<lspace2[i])
				sum+=(lspace2[i]-hspace1[i])*(lspace2[i]-hspace1[i]);
			else if(lspace1[i]>hspace2[i])
				sum+=(lspace1[i]-hspace2[i])*(lspace1[i]-hspace2[i]);
		}
		return Math.sqrt((queryOriLength*1.0/len)*sum);
	}
	
	private double DWT(List<String>list1,List<String>list2,int size1,int size2,double[][] matrix){
		if(size1<0&&size2<0){
			return 0;
		}else if(size1<0){
			int sum=0;
			for(int i=0;i<=size2;i++){
				sum+=Math.abs(Double.parseDouble(list2.get(i)));
			}
			return sum;
		}else if(size2<0){
			int sum=0;
			for(int i=0;i<=size1;i++){
				sum+=Math.abs(Double.parseDouble(list1.get(i)));
			}
			return sum;
		}
		
		if(matrix[size1][size2]>=0){
			return matrix[size1][size2];
		}else{
			double xItem=Double.parseDouble(list1.get(size1));
			double yItem=Double.parseDouble(list2.get(size2));
			double dis1=DWT(list1,list2,size1-1,size2-1,matrix)+Math.abs(xItem-yItem);
			double dis2=DWT(list1, list2,size1-1,size2,matrix)+Math.abs(xItem-yItem);
			double dis3=DWT(list1, list2,size1,size2-1,matrix)+Math.abs(xItem-yItem);
			double min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[size1][size2]=min;
			return min;
		}
	}
	private void setMatrix(double[][]matrix,int rows,int cols){
		for(int i=0;i<rows;i++)
			for(int j=0;j<cols;j++)
				matrix[i][j]=Double.MAX_VALUE;
		double ratio=(cols*1.0)/rows;
		int end=-1;
		for(int i=1;i<=rows;i++){
			int start=end+1;
			int span=(int)Math.ceil(i*ratio)-(int)Math.ceil((i-1)*ratio);
			end+=span;
			for(int j=start;j<=end&&j<cols;j++)
				matrix[i-1][j]=-1;
			for(int k=1;k<=50;k++){
				if((end+k)<cols)
					matrix[i-1][end+k]=-1;
				if((start-k)>=0)
					matrix[i-1][start-k]=-1;
			}
		}
	}

	public List<TimeSeries> getResults() {
		return results;
	}

	public void setResults(List<TimeSeries> results) {
		this.results = results;
	}

	public List<Double> getDistResults() {
		return distResults;
	}

	public void setDistResults(List<Double> distResults) {
		this.distResults = distResults;
	}
	
	public void clear(){
		results.clear();
		distResults.clear();
		queue.clear();
		tempSeires.clear();
	}
	
}
