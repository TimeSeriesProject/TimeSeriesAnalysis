package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Calendar;
import java.util.Date;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class ERPDistTSA implements IMinerOM {
	private DataItems outlies;          //异常点
	private TaskElement task;
	private int predictPeriod;          //预测的长度
	private DataItems predictItems;
	private DataItems di;
	
	private long cycleSpan;             //周期长度
	private boolean hasPeriod=false;    //是否存在周期
	private int[] periodValues;         //周期中各时间粒度中的值
	private int lastNumIndexInPeriod;   //最后的值在周期中的位置
	private Date endDate;               //序列中最后值的日期
	private double periodThreshold;     //存在的周期阈值
	
	public ERPDistTSA(TaskElement task,int predictPeriod,DataItems di){
		this.task=task;
		this.di=di;
		outlies=new DataItems();
		this.predictPeriod=predictPeriod;
		endDate=di.getLastTime();
	}
	
	public void TimeSeriesAnalysis(){
		ERPDistencePM pm=new ERPDistencePM();
		pm.setDataItems(di);
		pm.predictPeriod();
		pm.getFirstPossiblePeriod();
		hasPeriod=pm.hasPeriod();
		cycleSpan=pm.getPredictPeriod();
		periodValues=pm.getPreidctValues();
		lastNumIndexInPeriod=pm.getLastNumberIndexInperiod();
		endDate=di.getLastTime();
		if(!hasPeriod){
			return;
		}
		
		predictItems=new DataItems();
		Calendar start=Calendar.getInstance();
		start.setTime(endDate);//实际周期末尾
		for(int span=1;span<=predictPeriod;span++){
			String item=periodValues[(span+lastNumIndexInPeriod)%(int)cycleSpan]+"";
			predictItems.getData().add(item);
			start.add(Calendar.SECOND, task.getGranularity());
			predictItems.getTime().add(start.getTime());
		}
		
		//TODO find outlies
	}
	
	public DataItems getDi(){
		return di;
	}
	
	public void setDi(DataItems di) {
		this.di = di;
	}
	
	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}
	
	public TaskElement getTask(){
		return task;
	}
	
	public void setTask(TaskElement task) {
		this.task = task;
	}
	
	@Override
	public DataItems getOutlies() {
		return outlies;
	}
}
