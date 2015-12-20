package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;

public class FastFourierOutliesDetection implements IMinerTSA {

    private static FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    private static double[] original; // 未变换之前的数据
    private static double[] denoisedslicezz;//变换之后的数据
    private static double varK = 1.5;
    private static double amplitudeRatio = 0.9;
    private DataItems di;
    private DataItems outlies;
    
    public FastFourierOutliesDetection(DataItems di){
    	this.di=di;
    	outlies=new DataItems();
    }
    
    public FastFourierOutliesDetection(){
    	outlies=new DataItems();
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

    //逆变换
    public Complex[] IFFT(Complex[] importantCoefficiants) {
        Complex [] denoised = fft.transform(importantCoefficiants, TransformType.INVERSE);
        return denoised;
    }

    public void AnomalyDetection(DataItems dataItems) {
        double[] result = new double[original.length];

        for(int i=0;i < result.length;i++)
        {
            result[i] = denoisedslicezz[i]-original[i];
        }

        NormalDistributionTest nbt = new NormalDistributionTest(result,varK);
        for(int i=0;i < result.length;i++)
        {
            if(!nbt.isDawnToThisDistri(result[i]))
            {
            	outlies.add1Data(di.getTime().get(i), di.getData().get(i));
            }
        }
    }
    

    /**
     * 傅里叶滤波，过滤掉高频的波 只取前K个波
     * @param silce
     * @param k 支取前K个波
     * @return 过滤后的波
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
        Complex[] denoised = IFFT(result);//反变换
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
    @Override
    public DataItems getPredictItems() {
    	// TODO Auto-generated method stub
    	return null;
    }

    /**
     * 滤波
     * @param silce
     * @param threshold
     * @return
     */
    public List<String> FFTfilter(List<String> silce,double threshold) {
        Complex[] result = FFT(silce);
        double all = 0,sum=0,p=0;
        
        // 注释
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
//        TreeMap<Long, Long> denoisedsilce = new TreeMap<Long, Long>();
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
		List<String>  curData = null;
		List<Date> time = di.getTime();
		int size = data.size();
		DataItems prediction_curTime = null;
		curData= FFTfilter(data,amplitudeRatio);
		prediction_curTime = new DataItems();
		prediction_curTime.setTime(time);
		prediction_curTime.setData(curData);
		AnomalyDetection(prediction_curTime);
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
	
	
}
