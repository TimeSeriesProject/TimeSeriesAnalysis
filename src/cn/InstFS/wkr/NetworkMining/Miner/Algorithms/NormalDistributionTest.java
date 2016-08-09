package cn.InstFS.wkr.NetworkMining.Miner.Algorithms;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;

/**
 * Created by qt on 2015/1/8.
 * 检查一组数据是否是从正常分布
 */
public class NormalDistributionTest {
    private double[] arr;
    private double mean;
    private double stdeviation;
    private final static double CONFIDENCE = 0.01;
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

    public boolean isNormalDistri(){
        return !TestUtils.kolmogorovSmirnovTest(distribution, arr,CONFIDENCE);
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
