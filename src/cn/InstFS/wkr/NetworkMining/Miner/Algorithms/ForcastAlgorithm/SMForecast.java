package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.SegPattern;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerFM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class SMForecast implements IMinerFM{
	private DataItems dataItems;                                     //符号化之后的序列模式
	private List<ArrayList<String>> patterns;                        //频繁模式
	private HashMap<ArrayList<String>, Integer> patternsSupDegree;   //每个频繁模式的支持度
	private HashMap<String, SegPattern> patternCentriods;            //每个模式的聚类中心
	private TaskElement task;                                          
	private DataItems oriDataItems;                                  //原始的序列，即为符号化之前的时间序列
	private DataItems forecastItems;                                 //经过频繁模式预测出的序列
	public SMForecast(DataItems dataItems,List<ArrayList<String>> patterns,
			HashMap<ArrayList<String>, Integer> patternsSupDegree,HashMap<String, SegPattern> patternCentriods,
			TaskElement task,DataItems oriDataItems){
		this.dataItems=dataItems;
		this.patterns=patterns;
		this.patternsSupDegree=patternsSupDegree;
		this.patternCentriods=patternCentriods;
		this.task=task;
		this.oriDataItems=oriDataItems;
		forecastItems=new DataItems();
	}
	/**
	 * 根据频繁序列模式，对符号化的时间序列做出预测
	 * @return forecast Dataitems
	 */
	@Override
	public void TimeSeriesAnalysis() {
		List<String> datas=dataItems.data;
		String lastPattern=datas.get(datas.size()-1);             //符号化之后的时间序列最后一个模式
		List<String> forecastPattern=new ArrayList<String>();
		Date lastDate=oriDataItems.getLastTime();                 //时间序列最后的时间点
		//时间序列最后的值
		Double lastValue=Double.parseDouble(oriDataItems.getData().get(oriDataItems.getLength()-1));
		int index=-1;   //找出包含有指定模式的支持度最大的频繁模式
		int maxSupDegree=0;
		//支持度最大的频繁模式，且该模式必须包含时间序列最后一个频繁模式，
		for(int i=0;i<patterns.size();i++){
			ArrayList<String> pattern=patterns.get(i);
			boolean isContain=pattern.contains(lastPattern);
			if(isContain){
				if (patternsSupDegree.get(pattern)>maxSupDegree){
					index=i;
					maxSupDegree=patternsSupDegree.get(pattern);
				}
			}
		}
		int lastPatternIndex=-1;  //记录时间最后的模式在找出的频繁模式中位置
		if(index!=-1){
			List<String> pattern=patterns.get(index);
			for(int i=0;i<pattern.size();i++){
				if(pattern.get(i).equals(lastPattern)){
					lastPatternIndex=i;
					break;
				}
				continue;
			}
			if(lastPatternIndex==(pattern.size()-1))
				return;
			else{
				for(int i=lastPatternIndex+1;i<pattern.size();i++)
					forecastPattern.add(pattern.get(i));   //预测到的频繁模式
				//将预测到的频繁模式转换成普通的时间序列
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(lastDate);
				for(String item:forecastPattern){
					SegPattern segPattern=patternCentriods.get(item);
					if(segPattern == null)
						continue;
					int span=(int)(segPattern.getLength()*Math.cos(segPattern.getAngle())); 
					double angle=segPattern.getAngle();
					for(int i=1;i<=span;i++){
						calendar.add(Calendar.SECOND, task.getGranularity());
						lastValue=(lastValue+Math.tan(angle));
						forecastItems.time.add(calendar.getTime());
						forecastItems.data.add(String.valueOf(lastValue));
					}
				}
			}
		}else{
			return;
		}
	}
	
	@Override
	public DataItems getPredictItems(){
		return forecastItems;
	}
}
