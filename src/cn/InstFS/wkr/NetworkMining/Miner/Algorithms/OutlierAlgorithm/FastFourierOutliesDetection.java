package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.OutlierAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.InstFS.wkr.NetworkMining.Miner.Algorithms.common.NormalDistributionTest;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerOM;
import cn.InstFS.wkr.NetworkMining.Params.OMParams.OMFastFourierParams;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;

public class FastFourierOutliesDetection implements IMinerOM {

    private static FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    private static double[] original; //原始数据（非参数）
    private static double[] denoisedslicezz; //滤波后的数据（非参数）
    
    private static double varK = 3.0; //高斯距离阈值 (x-u)/sigma > varK 则x点是异常点（暂时没用到）
    private static double amplitudeRatio = 0.8; //滤波参数，傅里叶变换以后，保留amplitudeRatio的低频，过滤高频
    private static double sizeK = 8; //每段数据的长度len = 2^sizeK
    private static double diff = 0.2; //计算异常度阈值时的参数，判断前后2个差值是否满足(d1-d2)/d2 > diff，满足则d1是异常度阈值，否则不是
    
    private double threshold; //异常度阈值（非参数）
    private DataItems di = new DataItems();
    private DataItems outlies = new DataItems();//异常点
    private DataItems outDegree = new DataItems(); //异常度
    private Map<Date, Double> degreeMap = new HashMap<Date, Double>();
	private List<DataItems> outlinesSet = new ArrayList<DataItems>(); //异常线段
    
    public FastFourierOutliesDetection(DataItems di){
    	this.di=di;
    	outlies=new DataItems();
    }
    
    public FastFourierOutliesDetection(){
    	outlies=new DataItems();
    }
    public FastFourierOutliesDetection(OMFastFourierParams omFastFourierParams){
    	this.varK = omFastFourierParams.getVarK();
    	this.amplitudeRatio = omFastFourierParams.getAmplitudeRatio();
    	this.sizeK = omFastFourierParams.getSizeK();
    	this.diff = omFastFourierParams.getDiff();
    }
    public FastFourierOutliesDetection(OMFastFourierParams omFastFourierParams,DataItems di){
    	this.varK = omFastFourierParams.getVarK();
    	this.amplitudeRatio = omFastFourierParams.getAmplitudeRatio();
    	this.sizeK = omFastFourierParams.getSizeK();
    	this.diff = omFastFourierParams.getDiff();
    	this.di = di;
    }

    public Complex[] FFT(List<String> data) {
    	int size = data.size();
        size = (int)Math.pow(2,(int)(Math.log10(size)/Math.log10(2)));
        original = new double[size];
        for (int index = 0; index < size; index++)
        {
            original[index] = Double.parseDouble(data.get(index));
        }
        Complex[] result = fft.transform(original, TransformType.FORWARD);
        return result;
    }

    //反傅里叶变换
    public Complex[] IFFT(Complex[] importantCoefficiants) {
        Complex [] denoised = fft.transform(importantCoefficiants, TransformType.INVERSE);
        return denoised;
    }

    public void AnomalyDetection(DataItems dataItems) {
        double[] result = new double[dataItems.getLength()];
        

        for(int i=0;i < result.length;i++)
        {
            result[i] = denoisedslicezz[i]-original[i];                        
        }

        NormalDistributionTest nbt = new NormalDistributionTest(result,varK);
        for(int i=0;i < result.length;i++)
        {
            /*if(!nbt.isDawnToThisDistri(result[i]))
            {
            	outlies.add1Data(dataItems.getTime().get(i), original[i]+"");
            	//System.out.println(original[i]);
            }*/
            //计算异常度（高斯距离）
            double mean = nbt.getMean();
            double stv;
            if(nbt.getStdeviation()<=0){
            	stv = 1e-3;
            }else{
            	stv = nbt.getStdeviation();
            }
            double distance = Math.abs(result[i]-mean)/stv;            
            distance = distance>5 ? 1 : distance/5;
            degreeMap.put(dataItems.getTime().get(i),distance);            
            
        }
        
    }
    

    /**
     * 傅里叶变换
     * @param silce
     * @param k 取傅里叶变换之后的前K个分量
     * @return 傅里叶变换之后的序列
     */
    public List<String> FFTfilter(List<String> silce,int k) {
        Complex[] result = FFT(silce);

        ArrayList<Complex> denoisedcomplex = new ArrayList<Complex>();
        for (int index = 0; index < result.length; index++){
            if(index>k){
                denoisedcomplex.add(index,new Complex(0,0));
            }else{
                denoisedcomplex.add(index,result[index]);
            }
        }
        int denoisedcomplexSize = denoisedcomplex.size();
        result = new Complex[denoisedcomplexSize];
        Iterator<Complex> iterator = denoisedcomplex.iterator();
        int i = 0;
        while(iterator.hasNext()){
            result[i] = (Complex)iterator.next();
            i++;
        }
        Complex[] denoised = IFFT(result);//反傅里叶变换
        denoisedslicezz = new double[denoised.length];
        List<String> denoisedslice = new ArrayList<String>();
        for (int index = 0; index < denoised.length; index++){
            denoisedslicezz[index] = denoised[index].getReal();
            denoisedslice.add(String.valueOf(denoised[index].getReal()));
        }
        return denoisedslice;
    }
    
    @Override
    public DataItems getOutlies() {
    	return outlies;
    }

    /**
     * 傅里叶滤波
     * @param silce
     * @param threshold
     * @return
     */
    public List<String> FFTfilter(List<String> silce,double threshold) {
        Complex[] result = FFT(silce);
        double all = 0,sum=0,p=0;
        
        for (int index = 0; index < result.length; index++)
        {
            double rr = (result[index].getReal());
            double ri = (result[index].getImaginary());

            all += Math.sqrt((rr * rr) + (ri * ri));
        }

        ArrayList<Complex> denoisedcomplex = new ArrayList();
        int num = 0;
        for (int index = 0; index < result.length; index++)
        {

            double rr = (result[index].getReal());
            double ri = (result[index].getImaginary());

            sum += Math.sqrt((rr * rr) + (ri * ri));
            p = sum/all;

            if(p>threshold)
            {
                denoisedcomplex.add(index,new Complex(0,0));
                num++;
            }
            else{
                denoisedcomplex.add(index,result[index]);
            }
        }

        int denoisedcomplexSize = denoisedcomplex.size();
        result = new Complex[denoisedcomplexSize];
        Iterator iterator = denoisedcomplex.iterator();
        int i = 0;
        while(iterator.hasNext()){
            result[i] = (Complex)iterator.next();
            i++;
        }
        Complex[] denoised = IFFT(result);
        denoisedslicezz = new double[denoised.length];
        List<String> denoisedsilce = new ArrayList<String>();
        for (int index = 0; index < denoised.length; index++)
        {
            denoisedslicezz[index] = denoised[index].getReal();
            denoisedsilce.add(String.valueOf(denoised[index].getReal()));
        }
        return denoisedsilce;
    }
   
    @Override
    public void TimeSeriesAnalysis() {
		if(di==null){
			return;
		}

		List<String> data = di.getData();
		List<String> dataSlice=new ArrayList<String>();
		List<String>  curData = null;
		List<Date> time = di.getTime();
		List<Date> timeSlice = new ArrayList<Date>();
		int size = data.size();
		for(int i=0;i<((int)(size/Math.pow(2, sizeK)));i++){
			dataSlice.clear();
			timeSlice.clear();
			for(int j=0;j<Math.pow(2, sizeK);j++){
				dataSlice.add(data.get(i*(int)Math.pow(2, sizeK)+j));
				timeSlice.add(time.get(i*(int)Math.pow(2, sizeK)+j));
			}
			DataItems prediction_curTime = null;
			curData= FFTfilter(dataSlice,amplitudeRatio);
			prediction_curTime = new DataItems(); //当前时间段内的数据
			prediction_curTime.setTime(timeSlice);
			prediction_curTime.setData(curData);
			AnomalyDetection(prediction_curTime);
		}
		
		DataItems prediction_curTime = new DataItems();
		dataSlice.clear();
		timeSlice.clear();
		for(int i=(int)(size-Math.pow(2, sizeK));i<size;i++){
			dataSlice.add(data.get(i));
			timeSlice.add(time.get(i));
		}
		curData= FFTfilter(dataSlice,amplitudeRatio);
		prediction_curTime.setTime(timeSlice);
		prediction_curTime.setData(curData);
		AnomalyDetection(prediction_curTime);		
		
		//把degreeMap转换为outDegree
		List<Double> list = new ArrayList<Double>();
		for(int i=0;i<degreeMap.size();i++){
			Date date = di.getTime().get(i);
			double d = degreeMap.get(date);
			outDegree.add1Data(date, String.valueOf(d));
			list.add(degreeMap.get(date));
		}
		//取异常度前2%的点作为异常点
				
        Collections.sort(list);
        Collections.reverse(list);
        double d = list.get((int)(list.size()*0.02));
        //threshold = threshold>0.4 ? threshold : 0.4;
        for(int i=(int)(list.size()*0.02);i>0;i--){
			if(list.get(i)<0.5){				
				continue;
			}
			else if((list.get(i)-d)/d<diff){
				continue;
			}else{
				threshold = list.get(i);
				break;
			}
		}
        threshold = threshold<0.6 ? 0.6 : threshold;
		System.out.println("应用快速法里叶算法，异常度阈值是："+threshold);
		for(int i=0;i<list.size();i++){
			if(Double.parseDouble(outDegree.getData().get(i))>=threshold){
				outlies.add1Data(di.getElementAt(i));
			}
		}
		
	}

	public static double getAmplitudeRatio() {
		return amplitudeRatio;
	}

	public static void setAmplitudeRatio(double amplitudeRatio) {
		FastFourierOutliesDetection.amplitudeRatio = amplitudeRatio;
	}

	public static double getVarK() {
		return varK;
	}

	public static void setVarK(double varK) {
		FastFourierOutliesDetection.varK = varK;
	}

	public DataItems getDi() {
		return di;
	}

	public void setDi(DataItems di) {
		this.di = di;
	}
	
	@Override
	public DataItems getOutDegree() {
		return outDegree;
	}
	@Override
	public List<DataItems> getOutlinesSet() {
		return outlinesSet;
	}
	
}
