package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.ForcastAlgorithm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.Params.PredictionAlgorithmParams.ARIMAParams;
import org.rosuda.REngine.Rserve.RConnection;

import RUtil.ARIMA;
import RUtil.R;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerFM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
/**
 *  ARIMA（p，d，q）算法预测：
 *  1、差分平稳化,通过adf单根检验确定差分阶数d 
 *  2、通过 自相关函数和偏自相关函数确定自回归项数p，滑动平均项数q 
 *  3、确定ARIMA参数（p，d，q），建立模型 
 *  4、序列预测 
 * @author  
 *
 */
public class ARIMATSA implements IMinerFM{
	
	private TaskElement task;
	/**
	 * 预测的序列
	 */
	private DataItems di;
	/**
	 * 预测的长度
	 */
	private int predictPeriod;    
	/**
	 * 预测的结果
	 */
	private DataItems predictItems;
	/**
	 * 序列中最后值的日期
	 */
	private Date endDate;                //
	
	/**
	 * 构造函数：不指定预测长度
	 */
	public ARIMATSA(TaskElement task,DataItems dataItems,ARIMAParams p){
		this.task=task;
		this.di=dataItems;
		this.predictPeriod=p.getPredictPeriod();
		predictItems=new DataItems();
		endDate=di.getLastTime();
		
	}
	/**
	 * 构造函数：指定预测长度
	 */
	public ARIMATSA(TaskElement task,DataItems dataItems,int predictPeriod){
		this.task=task;
		this.di=dataItems;
		this.predictPeriod=predictPeriod;
		predictItems=new DataItems();
		endDate=di.getLastTime();
		
	}
	
	/**
	 * ARIMA（p，d，q）算法预测：1、差分平稳化,通过adf单根检验确定差分阶数d 2、通过 自相关函数和偏自相关函数确定自回归项数p，滑动平均项数q 3、确定ARIMA参数（p，d，q），建立模型 4、模型预测
	 */
	@Override
	public void TimeSeriesAnalysis() {
		List<Double> seq=new ArrayList<Double>();
		for(int i=0;i<di.getLength();i++){
			try {
    			seq.add(Double.parseDouble(di.getData().get(i)));
     		} catch (Exception e) {
    			throw new RuntimeException("dataitems非数据型数据");
			}
		}
//		for(String item:di.getData()){
//			try {
//				seq.add(Double.parseDouble(item));
//			} catch (Exception e) {
//				throw new RuntimeException("dataitems非数据型数据");
//				
//			}
//		}
		R Renviroment=new R();
		Renviroment.connectRserve();
		int stationaryOrder=Renviroment.adfTest(seq);   //检测序列平稳阶数
		System.out.println("差分次数="+stationaryOrder);
		int[] ARMACoefficients=Renviroment.autoCoeAndPartAutoCoe(seq, stationaryOrder);
		System.out.println("p="+ARMACoefficients[0]+"、q="+ARMACoefficients[1]);
		ARIMA arimaModel=new ARIMA(ARMACoefficients[0], stationaryOrder, ARMACoefficients[1],predictPeriod, Renviroment.getInterpreteR());
		arimaModel.setSeq(seq);
		double[] Items=arimaModel.predictVector();
		for(double item:Items){
			System.out.print(item+",");
		}
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(endDate);
		for(double item:Items){
			calendar.add(Calendar.SECOND, task.getGranularity());
			predictItems.add1Data(calendar.getTime(), item+"");
		}
		Renviroment.closeRserve();
	}
	
	@Override
	public DataItems getPredictItems() {
		return predictItems;
	}
}
