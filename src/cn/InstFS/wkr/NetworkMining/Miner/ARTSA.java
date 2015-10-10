package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class ARTSA implements IMinerTSA{
	private TaskElement task;
	private DataItems di;
	private DataItems outlies;           //异常点
	private int predictPeriod;           //预测的长度
	private DataItems predictItems;
	private Date endDate;                //序列中最后值的日期
	
	private double[] seq=null;           //存储平稳化后的序列
	private double seqMean;              //记录平稳前序列的平均值     
	private double[] offset;             //记录一天中每个时刻的偏差
	private double[] paramsφ=null;       //存储AR序列的参数
	private final int window=20;         //AR 的窗口长度 
	
	public ARTSA(TaskElement task,int predictPeriod,DataItems di){
		this.task=task;
		this.di=di;
		outlies=new DataItems();
		this.predictPeriod=predictPeriod;
		endDate=di.getLastTime();
	}
	
	@Override
	public void TimeSeriesAnalysis() {
		outlies=new DataItems();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		if(seq==null){
			transToStationarySeq();
		}
		int seqSize=di.getData().size();
		int window=20;//AR窗口=20  设AR阶为 2;即Xt=φ1*Xt-1+φ2*Xt-2+et  et为白噪声  求出φ1和φ2
		//逐个找到每一个异常值
		paramsφ=new double[2];
		for(int i=0;i<seqSize-window;i++){
			getParams(i,paramsφ);//获取paramsφ参数
			double e=0.0;//偏差  窗口中各个时刻AR估计值和实际值得偏差
			for(int j=i+2;j<i+window;j++){
				e=seq[j]-paramsφ[0]*seq[j-1]-paramsφ[1]*seq[j-2];
				statistics.addValue(e);
			}
			e=seq[i+window]-(paramsφ[0]*seq[i+window-1]-paramsφ[1]*seq[i+window-2]);
			double mean=statistics.getMean();
			double standardDeviation=statistics.getStandardDeviation();
			statistics.clear();
			if(e>(mean+3.0*standardDeviation)||mean<(mean-3.0*standardDeviation)){
				System.out.print(i+window+",");
				e=mean;
				//保存异常点
				outlies.add1Data(di.getTime().get(i+window),di.getData().get(i+window)); 
				//修复异常值使得程序可以预测下一个值是否异常
				seq[i+window]=paramsφ[0]*seq[i+window-1]+paramsφ[1]*seq[i+window-2]+e;  
			}
		}
		endDate=di.getLastTime();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(endDate);
		predictItems=new DataItems();
		for(int k=0;k<predictPeriod;k++){
			getParams(seqSize-window+k, paramsφ);
			double x1=seq[seqSize-1+k];
			double x2=seq[seqSize-2+k];
			seq[seqSize+k]=paramsφ[0]*x1+paramsφ[1]*x2;
			//计算预测值
			predictItems.getData().add((seq[seqSize+k]+seqMean+offset[(seqSize+k)%(offset.length)])+"");
			calendar.add(Calendar.SECOND, task.getGranularity());
			predictItems.getTime().add(calendar.getTime());
		}
		//将转换后的平稳序列还原至非平稳序列
		for(int k=0;k<seqSize+predictPeriod;k++){
			seq[k]=seq[k]+seqMean+offset[(k)%(offset.length)];
		}
	}
	
	/**
	 * 将数据dataItems转换成平稳序列，以满足AR平稳性要求
	 */
	private void transToStationarySeq(){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		List<String> items=di.getData();
		int seqSize=items.size();
		seq=new double[seqSize+predictPeriod];
		for(int i=0;i<seqSize;i++){
			seq[i]=Double.parseDouble(items.get(i));
			statistics.addValue(seq[i]);
		}
		
		seqMean=statistics.getMean();
		int recordsOfDay=(3600*24*1000)/(task.getGranularity()*1000);  //一天中的记录数

		int recordDays=seqSize/recordsOfDay;                //总天数
		offset=new double[recordsOfDay];           //记录一天中每个时刻的偏差
		for(int i=0;i<recordsOfDay;i++){
			double sum=0.0;
			for(int j=0;j<recordDays;j++){
				sum+=seq[i+j*recordsOfDay];
			}
			sum=(sum/recordDays)-seqMean;
			offset[i]=sum;  //表示每天的第i时刻和总的mean偏差
		}
		
		statistics.clear();
		//将序列平稳化   即去除平均值和周期性
		System.out.println("平稳序列");
		for(int i=0;i<seqSize;i++){
			seq[i]=(seq[i]-seqMean-offset[i%recordsOfDay]);
			int snumber=(int)seq[i]*100;
			double dnumber=snumber+0.0;
			System.out.print(dnumber/100.0+",");
			statistics.addValue(seq[i]);
		}
		System.out.println("");
		
		//0均值化，即均值为零
	 	double mean=statistics.getMean();
		for(int i=0;i<seqSize;i++){
			seq[i]-=mean;
		}
	}
	
	/**
	 * 求出每段AR的序列φ1和φ2参数
	 * @param index 在序列中的下标
	 * @param paramsφ  包含φ1和φ2参数并返回给调用函数
	 */
	private void getParams(int index,double[] paramsφ){
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		//找到 二维矩阵X*XT的逆矩阵
		double a11=0.0;
		double a12=0.0;
		double a21=0.0;
		double a22=0.0;
		for(int j=index+1;j<index+window-1;j++){
			a11+=(seq[j]*seq[j]);
			a12+=(seq[j]*seq[j-1]);
			a21+=(seq[j]*seq[j-1]);
		}
		for(int j=index;j<index+window-2;j++){
			a22+=(seq[j]*seq[j]);
		}
		
		double matrixNorm=a11*a22-a12*a21;//矩阵范数  当矩阵范数不为零时，矩阵存在逆矩阵
		if(matrixNorm!=0){
			double temp;
			temp=a11;
			a11=(a22/matrixNorm);
			a22=(temp/matrixNorm);
			a12=-(a12/matrixNorm);
			a21=-(a21/matrixNorm);
			
			//矩阵 XT*Y
			double b11=0.0;
			double b21=0.0;
			for(int j=index+2;j<index+window;j++){
				b11+=(seq[j]*seq[j-1]);
				b21+=(seq[j]*seq[j-2]);
			}
			double φ1=(a11*b11+a12*b21);
			double φ2=(a21*b11+a22*b21);
			paramsφ[0]=φ1;
			paramsφ[1]=φ2;
			statistics.clear();
		}else{
			return;
		}
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
	
	@Override
	public DataItems getPredictItems() {
		return predictItems;
	}
}
