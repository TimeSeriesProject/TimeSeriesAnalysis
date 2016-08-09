package cn.InstFS.wkr.NetworkMining.Miner.Results;

import java.io.Serializable;

public class MinerResultsStatistics implements Serializable {
	private double mean;
	private double std;
	private double complex;
	private double sampleENtropy;
	private int span;

	
	public MinerResultsStatistics(){}
	
	public MinerResultsStatistics(double mean, double std, double complex,
			double sampleENtropy, int span) {
		this.mean = mean;
		this.std = std;
		this.complex = complex;
		this.sampleENtropy = sampleENtropy;
		this.span = span;
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
	public double getComplex() {
		return complex;
	}
	public void setComplex(double complex) {
		this.complex = complex;
	}
	public double getSampleENtropy() {
		return sampleENtropy;
	}
	public void setSampleENtropy(double sampleENtropy) {
		this.sampleENtropy = sampleENtropy;
	}
	public int getSpan() {
		return span;
	}
	public void setSpan(int span) {
		this.span = span;
	}
	
	
}
