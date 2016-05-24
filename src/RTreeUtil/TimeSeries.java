package RTreeUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class TimeSeries {
	private static int N=50;
	public String name;
	public DataItems dataItems;
	public double[] PAA;
	public double[] paaHspace;
	public double[] paaLspace;
	
	public TimeSeries(DataItems dataItems,String name){
		this.dataItems=dataItems;
		PAA=new double[N];
		paaHspace=new double[N];
		paaLspace=new double[N];
		covertSeriesToSpace(this.dataItems, N);
		this.name=name;
	}
	
	private void covertSeriesToSpace(DataItems dataItems,int spaceDemen){
		List<String> datas=dataItems.getData();
		List<Double> standDatas=new ArrayList<Double>();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(String data:datas){
			double doubleData=Double.parseDouble(data);
			statistics.addValue(doubleData);
			standDatas.add(doubleData);
		}
		
//		double std=statistics.getStandardDeviation();
//		double mean=statistics.getMean();
		int length=standDatas.size();
//		for(int i=0;i<length;i++){
//			standDatas.set(i, (standDatas.get(i)-mean)/std);
//		}
		double span=(length*1.0/spaceDemen);
		for(int i=1;i<=spaceDemen;i++){
			statistics.clear();
			int start=(int)(span*(i-1));
			int end=(int)(span*i);
			for(int j=start;j<end;j++){
				statistics.addValue(standDatas.get(j));
			}
			PAA[i-1]=statistics.getMean();
			paaHspace[i-1]=statistics.getMax();
			paaLspace[i-1]=statistics.getMin();
		}
	}
}
