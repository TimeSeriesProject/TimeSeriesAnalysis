package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.io.Serializable;

public class PatternMent implements Serializable{
	private int start;
	private int end;
	private int len; //end - start
	private double span;//时间跨度
	private double hspan;//纵轴跨度
	private double slope; //倾斜角度
	private double angle; //与前一条线段的夹角
	private double average;//平均值	
	private double std;//标准差
	private double startValue;//起始值
	public PatternMent(){}
	public PatternMent(int start,int span,double slope,double startValue){
		this.start = start;		
		this.len = (int)span;
		this.end = start+span;
		this.span = span;
		this.slope = slope;
		this.startValue = startValue;
		this.hspan = startValue+slope*span;
	}
	public PatternMent(int start,int len,double slope,double startValue,double average,double std){
		this.start = start;		
		this.len = len;
		this.span = (double)len;
		this.end = start+len;
		this.slope = slope;
		this.startValue = startValue;
		this.average = average;
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
	public double getSpan() {
		return span;
	}
	public void setSpan(double span) {
		this.span = span;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public double getHspan() {
		return hspan;
	}
	public void setHspan(double hspan) {
		this.hspan = hspan;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	public double getStartValue() {
		return startValue;
	}
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int d) {
		this.len = d;
	}
	public double getStd() {
		return std;
	}
	public void setStd(double std) {
		this.std = std;
	}
	
	
}
