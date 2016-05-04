package cn.InstFS.wkr.NetworkMining.DataInputs;

public class SegPattern {
	private double height;
	private double length;
	private double mean;
	private double std;
	private int start;
	private int end;
	
	public SegPattern(){}
	public SegPattern(double height,double length,double mean,double std,int start,int end){
		this.height=height;
		this.length=length;
		this.mean=mean;
		this.std=std;
		this.start=start;
		this.end=end;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getStd() {
		return std;
	}
	public void setStd(double std) {
		this.std = std;
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
	
	
}
