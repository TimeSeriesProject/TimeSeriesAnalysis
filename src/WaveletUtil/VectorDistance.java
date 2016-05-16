package WaveletUtil;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.neighboursearch.PerformanceStats;

public class VectorDistance extends EuclideanDistance {
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
		double arg1Dist=0;
		double arg2Dist=0;
		double dist=0;
		int firstNumValues=arg0.numValues();
		int secondNumValues=arg1.numValues();
		for(int p1=0;p1<firstNumValues;p1++){
			arg1Dist+=(arg0.valueSparse(p1)*arg0.valueSparse(p1));
		}
		for(int p2=0;p2<secondNumValues;p2++){
			arg2Dist+=(arg1.valueSparse(p2)*arg1.valueSparse(p2));
		}
		for(int p2=0;p2<secondNumValues;p2++){
			dist+=((arg0.valueSparse(p2)-arg1.valueSparse(p2))*(arg0.valueSparse(p2)-arg1.valueSparse(p2)));
		}
		
		return (dist/(arg1Dist+arg2Dist));
	}
	
	@Override
	public double distance(Instance first, Instance second,
			PerformanceStats stats) {
		// TODO Auto-generated method stub
		return distance(first, second,Double.POSITIVE_INFINITY, stats);
	}
}
