package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataPretreatment;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;

public class SeriesStatistics {
	private double mean;
	private double std;
	private double complex;
	private double sampleEntropy;
	private DataItems dataItems;
	private int timeSpan;
	private int splitM;
	private double ratio;
	public SeriesStatistics(DataItems dataItems,int splitM,double ratio){
		this.dataItems=dataItems;
		this.splitM=splitM;
		this.ratio=ratio;
	}
	public SeriesStatistics(DataItems dataItems){
		this.dataItems=dataItems;
		this.splitM=2;
		this.ratio=0.2;
	}
	
	
	public void statistics(){
		getMeanAndStd(dataItems);
		sampleEntropy(dataItems);
		LZComplex(dataItems);
	}
	
	private void getMeanAndStd(DataItems di){
		List<String>datas=di.getData();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String data:datas){
			statistics.addValue(Double.parseDouble(data));
		}
		double mean=statistics.getMean();
		double std=statistics.getStandardDeviation();
		this.mean=mean;
		this.std=std;
		timeSpan=di.getLength();
	}
	
	private void sampleEntropy(DataItems di){
		List<Double[]> splits=splitDataItems(splitM,di);
		double entropy1=averageEntropy(splits);
		splits.clear();
		splits=splitDataItems(splitM+1,di);
		double entropy2=averageEntropy(splits);
		double sampleEntropy=-(Math.log(entropy2/entropy1)/Math.log(Math.E));
		this.sampleEntropy=sampleEntropy;
	}
	private void LZComplex(DataItems di){
		DataItems disDI=DataPretreatment.toDiscreteNumbers(di, DiscreteMethod.各区间数值范围相同, 20, "");
		List<String> data=disDI.getData();
		int numN=data.size();
		int numC=0;
		List<String> listS=new ArrayList<String>();
		List<String> listQ=new ArrayList<String>();
		listS.add(data.get(0));
		numC++;
		for(int i=1;i<numN;i++){
			listQ.add(data.get(i));
			List<String> SQP=generateSQP(listS, listQ);
			if(!isSubList(SQP, listQ)){
				listS.addAll(listQ);
				listQ.clear();
				numC++;
			}
		}
		double bn=(numN*1.0)/Math.log(numN*1.0);
		double complex=(numC*1.0)/bn;
		this.complex=complex;
	}
	
	private double averageEntropy(List<Double[]> splits){
		int size=splits.size();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int i=0;i<size;i++){
			int num=0;
			for(int j=0;j<size;j++){
				if(i==j)
					continue;
				if(dist(splits.get(i), splits.get(j))<(ratio*std))
					num++;
			}
			statistics.addValue(num*1.0/(size-1));
		}
		return statistics.getMean();
	}
	
	private List<Double[]> splitDataItems(int splitM,DataItems di){
		List<String> datas=di.getData();
		int timeSpan=datas.size();
		List<Double[]> splits=new ArrayList<Double[]>();
		for(int i=0;i<timeSpan-splitM+1;i++){
			Double[] split=new Double[splitM];
			for(int j=0;j<splitM;j++)
				split[j]=Double.parseDouble(datas.get(i+j));
			splits.add(split);
		}
		return splits;
	}
	
	private double dist(Double[] split1,Double[] split2){
		int len=split1.length;
		double max=0;
		for(int i=0;i<len;i++){
			if(Math.abs(split1[i]-split2[i])>max)
				max=Math.abs(split1[i]-split2[i]);
		}
		return max;
	}
	
	private boolean isSubList(List<String> list1,List<String> list2){
		int size1=list1.size();
		int size2=list2.size();
		boolean isSublist=true;
		for(int i=0;i<size1-size2+1;i++){
			isSublist=true;
			for(int j=0;j<size2;j++){
				if(!list1.get(i+j).equals(list2.get(j))){
					isSublist=false;
					break;
				}
			}
			if(isSublist)
				break;
		}
		return isSublist;
	}
	
	private List<String> generateSQP(List<String>S,List<String>Q){
		List<String> SQP=new ArrayList<String>();
		SQP.addAll(S);
		SQP.addAll(Q);
		SQP.remove(SQP.size()-1);
		return SQP;
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
	public double getSampleEntropy() {
		return sampleEntropy;
	}
	public void setSampleEntropy(double sampleEntropy) {
		this.sampleEntropy = sampleEntropy;
	}
	public DataItems getDataItems() {
		return dataItems;
	}
	public void setDataItems(DataItems dataItems) {
		this.dataItems = dataItems;
	}
	public int getTimeSpan() {
		return timeSpan;
	}
	public void setTimeSpan(int timeSpan) {
		this.timeSpan = timeSpan;
	}
	public int getSplitM() {
		return splitM;
	}
	public void setSplitM(int splitM) {
		this.splitM = splitM;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
}
