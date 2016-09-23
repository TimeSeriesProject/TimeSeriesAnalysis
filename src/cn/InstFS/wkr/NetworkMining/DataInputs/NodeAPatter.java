package cn.InstFS.wkr.NetworkMining.DataInputs;
/**
 * @author LYH
 * @decription 节点的出现与消失的线段模式
 * */
public class NodeAPatter {
	private int length;
	private int start;
	private int end;
	private int label; //取0,1
	
	public NodeAPatter(){}
	public NodeAPatter(int length,int start,int end,int label){
		this.length = length;
		this.start = start;
		this.end = end;
		this.label = label;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
		
}
