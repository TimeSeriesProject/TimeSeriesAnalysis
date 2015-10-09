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

public class ContinuousPeriodDetection {

    private static FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    private static double[] original; // 未变换之前的数据
    private static double[] denoisedslicezz;//变换之后的数据
    private static double varK = 1.5;
    private static double amplitudeRatio = 0.9;

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

    public DataItems AnomalyDetection(MinerResults results) {
    	DataItems dataItem = new DataItems();
    	dataItem.setTime(results.getInputData().time);
    	List<String> data = new ArrayList<String>();
    	boolean status = false;
        double[] result = new double[original.length];

        for(int i=0;i < result.length;i++)
        {
            result[i] = denoisedslicezz[i]-original[i];
        }

        NormalDistributionTest nbt = new NormalDistributionTest(result,varK);
        List<Double> probs = new ArrayList<Double>();
        for(int i=0;i < result.length;i++)
        {
            if(!nbt.isDawnToThisDistri(result[i]))
            {
            	data.add(String.valueOf(result[i]));
            	status=true;
            }else{
            	data.add("0");
            }
            probs.add(0.0);
        }
        dataItem.setData(data);
        dataItem.setProb(probs);
        if(status==true){
        	return dataItem;
        }else{
        	return null;
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
        double all = 0,sum=0,p=0;

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
   
    
    public void Detection(MinerResults results) {

    	DataItems dataItems = results.getInputData();
		if(results==null||results.getInputData()==null){
			return;
		}

		List<String> data = results.getInputData().getData();
		List<String>  curData = null;
		List<Date> time = results.getInputData().getTime();
		int size = data.size();
		DataItems prediction_curTime = null;
		curData= FFTfilter(data,amplitudeRatio);
		prediction_curTime = new DataItems();
		prediction_curTime.setTime(results.getInputData().getTime());
		prediction_curTime.setData(curData);
		//results.getRetTSA().setOutlies(AnomalyDetection(results));//TODO
		Calendar curstart =Calendar.getInstance();
		curstart.setTime(UtilsSimulation.instance.getCurrentStart());
		Calendar curend =Calendar.getInstance();
		curend.setTime(UtilsSimulation.instance.getCurrentEnd());
		List<Date> newTime = new ArrayList<Date>();
		List<Double>newProb = new ArrayList<Double>();
		long span = curend.getTimeInMillis()-curstart.getTimeInMillis();
		for(int i=0;i<size;i++){
			Date date = time.get(i);
			Calendar cur = Calendar.getInstance();
			cur.setTime(date);
			Long curLong = cur.getTimeInMillis()+span;
			Calendar newCur = Calendar.getInstance();
			newCur.setTimeInMillis(curLong);
			Date newDate = newCur.getTime();
			newTime.add(newDate);
			newProb.add(0.0);
		}
		DataItems newdataItem = new DataItems();
		newdataItem.setTime(newTime);
		newdataItem.setData(curData);	// TODO 这个明显有问题？？？
		newdataItem.setProb(newProb);
		//results.getRetTSA().forcasts_futureTime=newdataItem;
		results.setAbnormal((AnomalyDetection(results)!=null));

	}

	public static double getAmplitudeRatio() {
		return amplitudeRatio;
	}

	public static void setAmplitudeRatio(double amplitudeRatio) {
		ContinuousPeriodDetection.amplitudeRatio = amplitudeRatio;
	}

	public static double getVarK() {
		return varK;
	}

	public static void setVarK(double varK) {
		ContinuousPeriodDetection.varK = varK;
	}
}
