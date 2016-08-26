package cn.InstFS.wkr.NetworkMining.DataInputs;

public class Pattern {
	private int start;
	private int end;
	private int len;
	private double span;
	private double hspan;
	private double slope; //倾斜角度
	private double angle; //与前一条线段的夹角
	private double average;
	private double startValue;
	
	public Pattern(){}
	public Pattern(int start,int end,int span,double slope,double startValue){
		this.start = start;
		this.end = end;
		this.len = (int)span;
		this.span = span;
		this.slope = slope;
		this.startValue = startValue;
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
	public void setLen(int len) {
		this.len = len;
	}
	
	
}
