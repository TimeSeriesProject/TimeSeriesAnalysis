package associationRules;

public class LinePos{
	
	double confidence = 0.0;
	public int A_start = 0;
	public int A_end = 0;
	public int B_start = 0;
	public int B_end = 0;
	public void setConfidence(double d){
		confidence = d;
	}
	public void setA_start(int a){
		A_start = a;
	}
	public void setA_end(int a){
		A_end = a;
	}
	public void setB_start(int a){
		B_start = a;
	}
	public void setB_end(int a){
		B_end = a;
	}
	public double getConfidence(){
		return confidence;
	}
	public int getA_start(){
		return A_start;
	}
	public int getA_end(){
		return A_end;
	}
	public int getB_start(){
		return B_start;
	}
	public int getB_end(){
		return B_end;
	}
}