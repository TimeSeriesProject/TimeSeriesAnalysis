package WaveletUtil;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.neighboursearch.PerformanceStats;

public class CheByDistance extends EuclideanDistance {
	@Override
	public double distance(Instance first, Instance second) {
		// TODO Auto-generated method stub
		return distance(first, second,Double.POSITIVE_INFINITY,null);
	}
	
	@Override
	public double distance(Instance first, Instance second, double cutOffValue) {
		// TODO Auto-generated method stub
		return distance(first, second, cutOffValue,null);
	}
	
	@Override
	public double distance(Instance arg0, Instance arg1, double arg2,
			PerformanceStats arg3) {
		double diffFirstMax=Double.MIN_VALUE;
		double diffSecondMax=Double.MIN_VALUE;
		double diffFirstMin=Double.MAX_VALUE;
		double diffSecondMin=Double.MAX_VALUE;
		double dist=0;
		int firstNumValues=arg0.numValues();
		int secondNumValues=arg1.numValues();
		for(int p1=0;p1<firstNumValues;p1++){
			if(diffFirstMax<arg0.valueSparse(p1)){
				diffFirstMax=arg0.valueSparse(p1);
			}
			if(diffFirstMin>arg0.valueSparse(p1)){
				diffFirstMin=arg0.valueSparse(p1);
			}
		}
		for(int p2=0;p2<secondNumValues;p2++){
			if(diffSecondMax<arg1.valueSparse(p2)){
				diffSecondMax=arg1.valueSparse(p2);
			}
			if(diffSecondMin>arg0.valueSparse(p2)){
				diffSecondMin=arg0.valueSparse(p2);
			}
		}
		for(int p2=0;p2<secondNumValues;p2++){
			dist+=Math.abs(arg0.valueSparse(p2)-arg1.valueSparse(p2));
		}
		double span1=diffFirstMax/(diffFirstMin+0.01);
		double span2=diffSecondMax/(diffSecondMin+0.01);
//		if(Math.abs(diffFirstMax)>Math.abs(diffSecondMax)){
//			return Math.abs(diffFirstMax/diffSecondMax);
//		}else{
//			return Math.abs(diffSecondMax/diffFirstMax);
//		}
		return 0.5*dist/secondNumValues+0.5*(Math.abs(span1-span2));
	}
	
	@Override
	public double distance(Instance first, Instance second,
			PerformanceStats stats) {
		// TODO Auto-generated method stub
		return distance(first, second,Double.POSITIVE_INFINITY, stats);
	}
}
