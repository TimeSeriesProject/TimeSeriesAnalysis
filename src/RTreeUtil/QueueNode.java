package RTreeUtil;

public class QueueNode implements Comparable<QueueNode>{
	public double dist;
	public int nodeType;
	public TimeSeries series;
	public LeafNode leafNode;
	public NonLeafNode nonLeafNode;
	
	@Override
	public int compareTo(QueueNode o) {
		if(this.dist<o.dist)
			return -1;
		else if(this.dist>o.dist){
			return 1;
		}
		return 0;
	}
	
	public QueueNode(double dist,TimeSeries series){
		this.nodeType=1;
		this.dist=dist;
		this.series=series;
	}
	
	public QueueNode(double dist,LeafNode leafNode) {
		this.nodeType=2;
		this.dist=dist;
		this.leafNode=leafNode;
	}
	
	public QueueNode(double dist,NonLeafNode nonLeafNode){
		this.nodeType=3;
		this.dist=dist;
		this.nonLeafNode=nonLeafNode;
	}

}
