package cn.InstFS.wkr.NetworkMining.DataInputs;
/**
 * 
 * @author chenwei
 *
 */
public class Segment {

	
	public double getCentery() {
		return centery;
	}
	public void setCentery(double centery) {
		this.centery = centery;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	double centery;
	double  slope;
	double length;
	Segment(double centery,double slope,double length)
	{
		this.centery = centery;
		this.slope   = slope;
		this.length  = length;
	}
	Segment()
	{}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
