package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm;

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
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

public class ArimaForecast {
	
	/**
	 * 采用arima对输入数据dataItems进行预测
	 * 输入的dataItems需要经过预处理为均匀时间分布的时间序列
	 * 采用虚拟时间进行封装，以适应外部包
	 * 
	 * @param dataItems 训练数据
	 * @param backCount 需要预测的未来结果数
	 * @return DataItems 预测结果
	 */
	public static DataItems forecast(DataItems dataItems,int backCount){
		if(dataItems==null||backCount==0){
			return null;
		}
		int size = dataItems.getData().size();
		double[] values = new double[size+backCount];
		for(int i=0;i<size;i++){
			values[i] = Double.parseDouble(dataItems.getData().get(i));
		}
		//待预测数据部分设为0
		for(int i=0;i<backCount;i++){
			values[size+i]=0;
		}
		//构造时间序列，生成虚拟时间标签
//		Day day = Day.toDay();
//		TsPeriod start = new TsPeriod(TsFrequency.Undefined,day);
//		TsPeriod start = TsPeriod.year(2014);
		TsPeriod start = new TsPeriod(TsFrequency.Monthly,2012,0);
        TsData tsData = new TsData(start, values, true);
        
        //arima
        CheckLast xTerror = new CheckLast(RegArimaSpecification.RG4.build());
        xTerror.setBackCount(backCount);
        xTerror.check(tsData);
        double[] forecastData = xTerror.getForecastsValues();
        
        //计算时间粒度
        Date date1 = dataItems.getTime().get(size-1);
		Calendar cur1 = Calendar.getInstance();
		cur1.setTime(date1);
		Long curLong1 = cur1.getTimeInMillis();
		
		Date date2 = dataItems.getTime().get(size-2);
		Calendar cur2 = Calendar.getInstance();
		cur2.setTime(date2);
		Long curLong2 = cur2.getTimeInMillis();
        long particle = curLong1-curLong2;
        
        //组合输出结果
        DataItems forecastDataItems = new DataItems();
        List<Date> fTime = new ArrayList<Date>();
        List<String> fData = new ArrayList<String>();
        
        long nowLong = curLong1;
        for(int i=0;i<backCount;i++){
        	nowLong += particle;
        	Calendar newCur = Calendar.getInstance();
    		newCur.setTimeInMillis(nowLong);
    		Date newDate = newCur.getTime();
    		fTime.add(newDate);
    		fData.add(String.valueOf(forecastData[i]));
        }
        forecastDataItems.setData(fData);
        forecastDataItems.setTime(fTime);
        
		return forecastDataItems;
	}
	
	
	
	
	
	
	public static void main(String[] args){
		
		//测试数据路径
		String textPath = "D:\\Java\\workspace\\TestPro\\conf\\test.csv";
		
		//初始化测试数据
		DataItems dataItems = getDataItems(textPath);
		//预测结果个数
		int backCount = 5;
		//分割得到训练数据，最后backCount个数据用于检验预测结果
		DataItems trainDataItems = cutDataItems(dataItems,backCount);
		//获取预测结果
		DataItems fDataItems = forecast(trainDataItems, backCount);
		//输出并比较预测结果
		printResult(trainDataItems,fDataItems);
	}

	
	
	
	
	
	
	/**
	 * 测试程序
	 * 用于留下最后几位用于与测试结果比较
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

	/**
	 * 测试程序
	 * 输出并比较预测结果
	 * @param trainDataItems
	 * @param fDataItems
	 */
	private static void printResult(DataItems trainDataItems,DataItems fDataItems) {
		
		List<String> tData = trainDataItems.getData();
		List<Date> tTime = trainDataItems.getTime();
		
		List<String> fData = fDataItems.getData();
		List<Date> fTime = fDataItems.getTime();
		
		int tSize = tData.size();
		int fSize = fData.size();
		
		System.out.println("原始值	预测值");
		System.out.println("--------------------------");
		for(int i=0;i<fSize;i++){
			System.out.println(tData.get(tSize-fSize+i)+"\t"+fData.get(i));
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
//				System.out.println("line: "+line);
//				System.out.println("values[0]: "+values[0]);
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
