package cn.InstFS.wkr.NetworkMining.Miner.Algorithms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class DiscreteTSA{
	private final int dimension;//离散的维度
	private TaskElement task;
	private DataItems di;
	private final String[] discreteValues;
	private DataItems outlies;//异常点
	private int predictPeriod;            //预测的长度
	private DataItems predictItems;

	//基于周期算法
	private long cycleSpan;            //周期长度
	private boolean hasPeriod=false;    //是否存在周期
	private int[] periodValues;       //周期中各时间粒度中的值
	private int lastNumIndexInPeriod;  //最后的值在周期中的位置
	private Date endDate;              //序列中最后值的日期
	private double periodThreshold;    //存在的周期阈值
	
	//基于AR算法
	private double[] seq=null;            //存储平稳化后的序列
	private double seqMean;               //记录平稳前序列的平均值     
	private double[] offset;              //记录一天中每个时刻的偏差
	private double[] paramsφ=null;        //存储AR序列的参数
	private int window=20;                 //AR 的窗口长度 
	
	private double outlierThreshold=0.97;
	
	//正态分布表
	private double[][] normdist = {
         {0.5,0.504,0.508,0.512,0.516,0.5199,0.5239,0.5279,0.5319,0.5359},
         {0.5398,0.5438,0.5478,0.5517,0.5557,0.5596,0.5636,0.5675,0.5714,0.5753},
         {0.5793,0.5832,0.5871,0.591,0.5948,0.5987,0.6026,0.6064,0.6103,0.6141},
         {0.6179,0.6217,0.6255,0.6293,0.6331,0.6368,0.6406,0.6443,0.648,0.6517},
         {0.6554,0.6591,0.6628,0.6664,0.67,0.6736,0.6772,0.6808,0.6844,0.6879},
         {0.6915,0.695,0.6985,0.7019,0.7054,0.7088,0.7123,0.7157,0.719,0.7224},
         {0.7257,0.7291,0.7324,0.7357,0.7389,0.7422,0.7454,0.7486,0.7517,0.7549},
         {0.758,0.7611,0.7642,0.7673,0.7703,0.7734,0.7764,0.7794,0.7823,0.7852},
         {0.7881,0.791,0.7939,0.7967,0.7995,0.8023,0.8051,0.8078,0.8106,0.8133},
         {0.8159,0.8186,0.8212,0.8238,0.8264,0.8289,0.8315,0.834,0.8365,0.8389},
         {0.8413,0.8438,0.8461,0.8485,0.8508,0.8531,0.8554,0.8577,0.8599,0.8621},
         {0.8643,0.8665,0.8686,0.8708,0.8729,0.8749,0.877,0.879,0.881,0.883},
         {0.8849,0.8869,0.8888,0.8907,0.8925,0.8944,0.8962,0.898,0.8997,0.9015},
         {0.9032,0.9049,0.9066,0.9082,0.9099,0.9115,0.9131,0.9147,0.9162,0.9177},
         {0.9192,0.9207,0.9222,0.9236,0.9251,0.9265,0.9278,0.9292,0.9306,0.9319},
         {0.9332,0.9345,0.9357,0.937,0.9382,0.9394,0.9406,0.9418,0.943,0.9441},
         {0.9452,0.9463,0.9474,0.9484,0.9495,0.9505,0.9515,0.9525,0.9535,0.9545},
         {0.9554,0.9564,0.9573,0.9582,0.9591,0.9599,0.9608,0.9616,0.9625,0.9633},
         {0.9641,0.9648,0.9656,0.9664,0.9671,0.9678,0.9686,0.9693,0.97,0.9706},
         {0.9713,0.9719,0.9726,0.9732,0.9738,0.9744,0.975,0.9756,0.9762,0.9767},
         {0.9772,0.9778,0.9783,0.9788,0.9793,0.9798,0.9803,0.9808,0.9812,0.9817},
         {0.9821,0.9826,0.983,0.9834,0.9838,0.9842,0.9846,0.985,0.9854,0.9857},
         {0.9861,0.9864,0.9868,0.9871,0.9874,0.9878,0.9881,0.9884,0.9887,0.989},
         {0.9893,0.9896,0.9898,0.9901,0.9904,0.9906,0.9909,0.9911,0.9913,0.9916},
         {0.9918,0.992,0.9922,0.9925,0.9927,0.9929,0.9931,0.9932,0.9934,0.9936},
         {0.9938,0.994,0.9941,0.9943,0.9945,0.9946,0.9948,0.9949,0.9951,0.9952},
         {0.9953,0.9955,0.9956,0.9957,0.9959,0.996,0.9961,0.9962,0.9963,0.9964},
         {0.9965,0.9966,0.9967,0.9968,0.9969,0.997,0.9971,0.9972,0.9973,0.9974},
         {0.9974,0.9975,0.9976,0.9977,0.9977,0.9978,0.9979,0.9979,0.998,0.9981},
         {0.9981,0.9982,0.9982,0.9983,0.9984,0.9984,0.9985,0.9985,0.9986,0.9986},
         {0.9987,0.999,0.9993,0.9995,0.9997,0.9998,0.9998,0.9999,0.9999,1},
         {0.999032,0.999065,0.999096,0.999126,0.999155,0.999184,0.999211,0.999238,0.999264,0.999289},
         {0.999313,0.999336,0.999359,0.999381,0.999402,0.999423,0.999443,0.999462,0.999481,0.999499},
         {0.999517,0.999534,0.999550,0.999566,0.999581,0.999596,0.999610,0.999624,0.999638,0.999660},
         {0.999663,0.999675,0.999687,0.999698,0.999709,0.999720,0.999730,0.999740,0.999749,0.999760},
         {0.999767,0.999776,0.999784,0.999792,0.999800,0.999807,0.999815,0.999822,0.999828,0.999885},
         {0.999841,0.999847,0.999853,0.999858,0.999864,0.999869,0.999874,0.999879,0.999883,0.999880},
         {0.999892,0.999896,0.999900,0.999904,0.999908,0.999912,0.999915,0.999918,0.999922,0.999926},
         {0.999928,0.999931,0.999933,0.999936,0.999938,0.999941,0.999943,0.999946,0.999948,0.999950},
         {0.999952,0.999954,0.999956,0.999958,0.999959,0.999961,0.999963,0.999964,0.999966,0.999967},
         {0.999968,0.999970,0.999971,0.999972,0.999973,0.999974,0.999975,0.999976,0.999977,0.999978},
         {0.999979,0.999980,0.999981,0.999982,0.999983,0.999983,0.999984,0.999985,0.999985,0.999986},
         {0.999987,0.999987,0.999988,0.999988,0.999989,0.999989,0.999990,0.999990,0.999991,0.999991},
         {0.999991,0.999992,0.999992,0.999930,0.999993,0.999993,0.999993,0.999994,0.999994,0.999994},
         {0.999995,0.999995,0.999995,0.999995,0.999996,0.999996,0.999996,1.000000,0.999996,0.999996},
         {0.999997,0.999997,0.999997,0.999997,0.999997,0.999997,0.999997,0.999998,0.999998,0.999998},
         {0.999998,0.999998,0.999998,0.999998,0.999998,0.999998,0.999998,0.999998,0.999999,0.999999},
         {0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999},
         {0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999,0.999999},
         {1.000000,1.000000,1.000000,1.000000,1.000000,1.000000,1.000000,1.000000,1.000000,1.000000}
	 };

	static DecimalFormat format = new DecimalFormat("#.00");
	static{
	     format.setRoundingMode(RoundingMode.DOWN);
	 }
 
	
	public DiscreteTSA(int dimension,TaskElement task,String discreteValuesStr,Double periodThreshold,
			Double outlierThreshold,int predictPeriod,DataItems di){
		this.dimension=dimension;
		this.task=task;
		this.discreteValues=discreteValuesStr.split(",");
		this.outlierThreshold=outlierThreshold;
		this.di=di;
		outlies=new DataItems();
		this.predictPeriod=predictPeriod;
		this.periodThreshold=periodThreshold;
	}

    private double NORMSDIST(double x)
    {
        boolean flag = false;
        if(x<0)
        {
            x = -1*x;
            flag = true;
        }
        
        if(x>4.99)
        {
        	if(flag)
        		return 0;
        	else
                return 1;
        }
        x = Double.valueOf(format.format(x));

        int row = (int)(x*100)%10;
        int col = (int)(x*10);
        double rtn = normdist[col][row];
        if(flag)
            return 1-rtn;
        else
            return rtn;
    }
	
	
    
    /**
	 * 通过周期模型进行TSA分析  
	 * 主要功能：找出序列的异常值
	 *        预测给定区间的值
	 */
    public void predictByPeriod(){
    	DiscretePM discretePM=new DiscretePM(task,dimension,periodThreshold);
		discretePM.setDataItems(di);
		discretePM.predictPeriod();
		discretePM.getFirstPossiblePeriod();
		hasPeriod=discretePM.hasPeriod();
		cycleSpan=discretePM.getPredictPeriod();
		periodValues=discretePM.getPreidctValues();
		lastNumIndexInPeriod=discretePM.getLastNumIndexInPeriod();
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
		
		//异常检测部分
		List<Integer> erpDists=new ArrayList<Integer>();
		List<String> dataList=di.getData();
		List<Integer> seqX=new ArrayList<Integer>();
		List<Integer> seqY=new ArrayList<Integer>();
		int[][] matrix=null;
		for(int item:periodValues){
			seqX.add(item);
		}
		for(int i=0;i<dataList.size();i++){
			seqY.add((int)Double.parseDouble(dataList.get(i)));
			if((i+1)%cycleSpan==0){
				matrix=new int[seqX.size()][seqX.size()];
				erpDists.add(ERPDistance(seqX, seqY,seqX.size()-1,seqY.size()-1,matrix));
				seqY.clear();
			}
		}
		outlies=new DataItems();
		DescriptiveStatistics statistics=new DescriptiveStatistics();
		for(int dist:erpDists){
			statistics.addValue(dist);
		}
        double mean = statistics.getMean();
        double var = statistics.getStandardDeviation();
        double stddata;
        for(int i=0;i<erpDists.size();i++){
            stddata = (erpDists.get(i)-mean)/var;
            Double normData=NORMSDIST(stddata);
            if(normData>outlierThreshold||normData<(1-outlierThreshold)){
            	for(int j=(int) (cycleSpan*i);j<(int) (cycleSpan*i+cycleSpan);j++){
            		outlies.add1Data(di.getTime().get(j),di.getData().get(j));
            	}
            }
        }
    }
	
	/**
	 * 求两条序列的ARP距离，将此距离视为两条序列的相似度
	 * @param seqX  第一条序列
	 * @param seqY  第二条序列
	 * @param matrix 相似度矩阵
	 * @return  ERP距离
	 */
	private int ERPDistance(List<Integer> seqX,List<Integer>seqY,int xSize,int ySize,int[][] matrix){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=seqY.get(i);
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=seqX.get(i);
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]!=0){
			return matrix[xSize][ySize];
		}else{
			int xItem=seqX.get(xSize);
			int yItem=seqY.get(ySize);
			int dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix)+Math.abs(xItem-yItem);
			int dis2=ERPDistance(seqX,seqY,xSize-1,ySize,matrix)+Math.abs(xItem);
			int dis3=ERPDistance(seqX,seqY,xSize,ySize-1,matrix)+Math.abs(yItem);
			int min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
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
//		int recordsOfDay=(3600*24*1000)/(task.getGranularity()*1000);  //一天中的记录数
		int recordsOfDay=24;

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
		
		//强序列0均值话，即均值为零
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
	/**
	 * 通过AR模型进行TSA分析  
	 * 主要功能：找出序列的异常值
	 *        预测给定区间的值
	 */
	public void predictByAR(){
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
//				System.out.println(i+window+" is outlier ,e is "+e);
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
	
	public DataItems getDi(){
		return di;
	}
	
	public void setDi(DataItems di) {
		this.di = di;
	}
	
	public boolean getHasPeriod(){
		return hasPeriod;
	}
	
	public int getPredictPeriod() {
		return predictPeriod;
	}

	public void setPredictPeriod(int predictPeriod) {
		this.predictPeriod = predictPeriod;
	}

	public DataItems getOutlies() {
		return outlies;
	}

	public DataItems getPredictItems() {
		return predictItems;
	}
	
	public static void main(String[] args){
		DataItems items=new DataItems();
		Calendar calendar=Calendar.getInstance();
		calendar.set(2015, 9, 3, 4, 1,0);
		for(int i=0;i<200;i++){
			calendar.add(Calendar.SECOND, i);
			items.getTime().add(calendar.getTime());
			items.getData().add(i%24+"");
		}
		
		Random random=new Random();
		for(int i=0;i<20;i++){
			int ran=random.nextInt(200);
			System.out.print(ran+" ");
			int itemData=Integer.parseInt(items.getData().get(ran));
			itemData*=2;
			items.getData().set(ran, itemData+"");
		}
		System.out.println("");
		DiscreteTSA tsa=new DiscreteTSA(0, null, "", 0.0, 0.0, 6, items);
		tsa.predictByAR();
	}
}
