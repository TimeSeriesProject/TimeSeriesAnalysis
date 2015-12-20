package cn.InstFS.wkr.NetworkMining.Miner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections.map.HashedMap;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import ec.tstoolkit.modelling.arima.CheckLast;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;


/** 
 * @author xzbang
 * 
 */
public class ArimaOutlierDetection implements IMinerTSA {
	/**
	 * 封装预测模型，获取score判断异常
	 * 
	 * 序列长度不少于40
	 * 
	 * 采用arima对输入数据dataItems进行预测
	 * 输入的dataItems需要经过预处理为均匀时间分布的时间序列
	 * 采用虚拟时间进行封装，以适应外部包
	 * 
	 * @param dataItems 训练数据
	 * @return DataItems 预测结果
	 */
	
	
	private DataItems di;
	private DataItems outlies;
	
	@Override
	public void TimeSeriesAnalysis(){
		if(di==null){
			return;
		}
		int size = di.getData().size();
		if(size<40){
			return;
		}
		
		//组合输出结果
        outlies = new DataItems();
        List<Date> fTime = new ArrayList<Date>();
        List<String> fData = new ArrayList<String>();
		
		for(int i = 40;i<size;i++){
			double[] values = new double[i];
			DataItems subDataItems = cutDataItems(di, size-i);
			for(int j=0;j<i;j++){
				values[j] = Double.parseDouble(subDataItems.getData().get(j));
			}
			
			//构造时间序列，生成虚拟时间标签
			TsPeriod start = new TsPeriod(TsFrequency.Monthly, 2012, 0);
	        TsData tsData = new TsData(start, values, true);
	        
	        //arima
	        CheckLast xTerror = new CheckLast(RegArimaSpecification.RG4.build());
	        xTerror.setBackCount(1);
	        xTerror.check(tsData);
	        double score = xTerror.getScore(0);
	        System.out.println(score);
	        if(Math.abs(score)>=4){
	        	fData.add(di.getData().get(i-1));
	        	fTime.add(di.getTime().get(i-1));
	        }
		}
        
		outlies.setData(fData);
		outlies.setTime(fTime);
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
	 * 切割时间序列，以便对序列中的每一个进行预测并计算score，判定异常
	 * 
	 * @param dataItems
	 * @param backCount
	 * @return
	 */
	private static DataItems cutDataItems(DataItems dataItems, int backCount) {
		if(dataItems==null){
			return null;
		}
		
		List<String> data = dataItems.getData();
		List<Date> time = dataItems.getTime();
		
		int size = data.size();
		if(size<=backCount){
			return null;
		}
		
		List<Date> tTime = new ArrayList<Date>();
        List<String> tData = new ArrayList<String>();
		
		for (int i=0;i<size-backCount;i++){
			tTime.add(time.get(i));
			tData.add(data.get(i));
		}
		
		DataItems tDataItems = new DataItems();
		tDataItems.setData(tData);
		tDataItems.setTime(tTime);
		return tDataItems;
	}
}
