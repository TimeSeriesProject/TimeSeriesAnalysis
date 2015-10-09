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
 * @author xuzhaobang
 *
 */
public class ArimaOutlierDetection {
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
	public static DataItems outlierDetection(DataItems dataItems){
		if(dataItems==null){
			return null;
		}
		int size = dataItems.getData().size();
		if(size<40){
			return null;
		}
		
		//组合输出结果
        DataItems outlierDataItems = new DataItems();
        List<Date> fTime = new ArrayList<Date>();
        List<String> fData = new ArrayList<String>();
		
		for(int i = 40;i<size;i++){
			double[] values = new double[i];
			DataItems subDataItems = cutDataItems(dataItems, size-i);
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
	        	fData.add(dataItems.getData().get(i-1));
	        	fTime.add(dataItems.getTime().get(i-1));
	        }
		}
        
		outlierDataItems.setData(fData);
		outlierDataItems.setTime(fTime);
        
		return outlierDataItems;
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
	
	
	
	
	
	
	public static void main(String[] args){
		
		//测试数据路径
		String textPath = "D:\\Java\\workspace\\TestPro\\conf\\test.csv";
		
		//初始化测试数据
		DataItems dataItems = getDataItems(textPath);
		//获取异常结果
		DataItems fDataItems = outlierDetection(dataItems);
		//输出异常结果
		printResult(fDataItems);
	}

	
	
	
	
	


	/**
	 * 测试程序
	 * 输出并比较预测结果
	 * @param trainDataItems
	 * @param fDataItems
	 */
	private static void printResult(DataItems fDataItems) {
		
		List<String> fData = fDataItems.getData();
		List<Date> fTime = fDataItems.getTime();
		int fSize = fData.size();
		
		for(int i=0;i<fSize;i++){
			System.out.println(fTime.get(i)+"\t"+fData.get(i));
		}
		
	}

	/**
	 * 
	 * 测试程序
	 * 用于生成测试程序需要的dataItems
	 * @param textPath 测试数据的路径
	 * @return
	 */
	private static DataItems getDataItems(String textPath) {
		DataItems dataItems = new DataItems();
		
		//初始化测试数据
		TreeMap<String,Double> treeMap = new TreeMap<String,Double>();
		String line=null;
		try {
			FileInputStream is=new FileInputStream(new File(textPath));
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			reader.readLine();//跳过标题
			while((line=reader.readLine())!=null){
				String[] values=line.split(",");
				String key = values[0].substring(0, values[0].lastIndexOf("/"));
				double value = Double.parseDouble(values[1]);
				if(treeMap.containsKey(key)){
					treeMap.put(key, treeMap.get(key)+value);
				}else{
					treeMap.put(key, value);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Date> time = new ArrayList<Date>();
        List<String> data = new ArrayList<String>();
		
		long stime = 694195200000L;
		int size = treeMap.size();
		for(String key : treeMap.keySet()){
			Date date = new Date(stime);
			time.add(date);
			data.add(String.valueOf(treeMap.get(key)));
			stime+=86400000L;
		}
		dataItems.setData(data);
		dataItems.setTime(time);
		
		return dataItems;
	}
}
