package RTreeUtil;

import java.util.ArrayList;
import java.util.List;

public class NonLeafNode {
	private static int N=50;  //空间维度
	private static int M=2;  //最大子节点数目
	
	public double[] hSpace;
	public double[] lSPace;
	public boolean nextTOLeaf=false; 
	public List<NonLeafNode> nonLeafChilds;
	public List<LeafNode> leafChilds;
	public int existChildsNum;
	public NonLeafNode parent;
	
	public NonLeafNode(){
		hSpace=new double[N];
		lSPace=new double[N];
		nonLeafChilds=new ArrayList<NonLeafNode>();
		leafChilds=new ArrayList<LeafNode>();
	}
	
	public boolean isFull(){
		if(existChildsNum==M){
			return true;
		}else{
			return false;
		}
	}

}
