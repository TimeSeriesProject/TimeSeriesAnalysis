package RTreeUtil;

import java.util.ArrayList;
import java.util.List;

public class LeafNode {
	private static int N=50;  //空间维度
	private static int M=2;  //最大子节点数目
	
	public double[] hSpace;
	public double[] lSPace;
	public List<TimeSeries> timeSeries;
	public int timeSeiresNum;
	public NonLeafNode parent;
	
	public LeafNode(){
		hSpace=new double[N];
		lSPace=new double[N];
		timeSeries=new ArrayList<TimeSeries>();
		timeSeiresNum=0;
	}
	
	public boolean isFull(){
		if(timeSeiresNum==M)
			return true;
		else 
			return false;
	}
}
