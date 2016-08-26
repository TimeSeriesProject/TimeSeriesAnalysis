package cn.InstFS.wkr.NetworkMining.DataInputs;

public class Pattern {
	private int start;
	private int end;
	private double span;
	private double slope;
	private double hspan;
	private double average;
	private double startValue;
	public Pattern(){}
	public Pattern(int start,int end,int span,double slope,double startValue){
		this.start = start;
		this.end = end;
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
	
	
}
