package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.KolmogorovSmirnovCheckout;

/**
 * Created by qt on 2015/1/8.
 * 检查一组数据是否是从正常分布
 */
public class NormalDistributionTest {
    private double[] arr;
    private List<Double> list = new ArrayList<Double>();
    private double mean;
    private double stdeviation;
    private final static double CONFIDENCE = 0.01;//显著性水平。取值为0.01, 0.02, 0.05, 0.1, 0.2
    private static double K = 3;
    private NormalDistribution distribution;
    public NormalDistributionTest(double[] arr,double K){
        this(arr);
        this.K = K;
    }
    public NormalDistributionTest(double[] arr){
        this.arr = arr;
        DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (double o : arr) {
            statistics.addValue(o);
        }
        mean = statistics.getMean();
        stdeviation = statistics.getStandardDeviation();
        if(stdeviation <= 0){
            stdeviation = 0.0001;
        }
        distribution = new NormalDistribution(mean, stdeviation);
    } 
    public NormalDistributionTest(List<Double> list,double K){
    	this.K = K;
    	arr = new double[list.size()];
    	for (int i=0;i<list.size();i++) {
            arr[i] = list.get(i);
        }
    	DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (int i=0;i<list.size();i++) {
            statistics.addValue(list.get(i));
        }
        mean = statistics.getMean();
        stdeviation = statistics.getStandardDeviation();
        if(stdeviation <= 0){
            stdeviation = 0.0001;
        }
        distribution = new NormalDistribution(mean, stdeviation);        
    }    
    public boolean isNormalDistri(){
    	KolmogorovSmirnovCheckout ks = new KolmogorovSmirnovCheckout();
        return ks.kolmogorovSmirnovTest(distribution, arr,CONFIDENCE);
    }
    
    //检查以数据是否服从该分布
    public boolean isDawnToThisDistri(double i){
        if (stdeviation == 0) return i == mean;
        double limit = (i - mean) / stdeviation;
        return Math.abs(limit) < K;
    }
    public double getMean() {
        return mean;
    }

    public double getStdeviation() {
        return stdeviation;
    }

    public NormalDistribution getDistribution() {
        return distribution;
    }

    public static void main(String[] args) {
        double[] arr = {1, 2, 0, 1};
        NormalDistributionTest test = new NormalDistributionTest(arr);
        System.out.println(test.getMean() + " " + test.getStdeviation() + " " + test.getDistribution().getNumericalVariance() +"   " +test.isNormalDistri());
    }
}
