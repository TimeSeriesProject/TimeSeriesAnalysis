package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm;

//import java.io.File;
//import java.io.FileWriter;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataInputUtils;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
import cn.InstFS.wkr.NetworkMining.Exception.NotFoundDicreseValueException;
import cn.InstFS.wkr.NetworkMining.Params.ParamsPM;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.DiscreteMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;
import cn.InstFS.wkr.NetworkMining.UIs.Utils.UtilsSimulation;

public class DiscretePM {
	
	private final int dimension;//离散的维度
	private TaskElement task;
	private Boolean hasPeriod; //是否有周期
	private int predictPeriod;   //周期长度
	private List<Integer> existPeriod;
	private int lastNumIndexInPeriod;//最后一个数在周期中的位置
	private DataItems di; //当前时间区间的数据集
	private DataItems distributePeriod;  //一个周期内的items，times的起始时间是item的第一个值的时间 values是一个周期内的值
	private Date startTime;    //序列中的起始时间
	private double threshold;  //是否具有周期的阈值
	
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //存储每个可能周期的平均熵或平均ERP距离
    private int[] predictValues;  //当存在周期时，一个周期中的值
    private HashMap<Integer, Integer[]> predictValuesMap;

	private int[][] distMatrix;
	
	
	/**
	 * 最小熵算法求得的周期
	 * @return 第一个符合要求的周期
	 */
	public int getFirstPossiblePeriod(){
		if (existPeriod == null || existPeriod.size() == 0){
			hasPeriod=false;
			return -1;
		}
		int len = existPeriod.size();
		for (int i=0;i<len;i++){
			hasPeriod=true;
			predictPeriod=existPeriod.get(i);
			Integer[] values=predictValuesMap.get(predictPeriod);
			predictValues=new int[values.length];
			for(int j=0;j<values.length;j++){
				predictValues[j]=values[j];
			}
			lastNumIndexInPeriod=(di.getLength()-1)%(predictPeriod);
			distributePeriod=new DataItems();
			for(int j=0;j<predictPeriod;j++){
				int index=predictValues[j];
				String value=index+"";
				distributePeriod.add1Data(di.getTime().get(j), value);
			}
			return predictPeriod;
		}
		hasPeriod=false;
		return -1;
	}
	
	public int getLastNumIndexInPeriod(){
		return lastNumIndexInPeriod;
	}
	
	/**
	 * @param task
	 * @param dimension 离散化的维数 即有多少个离散值
	 * @param values 离散值 如二维离散化{0,1} 
	 */
	
	public DiscretePM(TaskElement task,int dimension){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public DiscretePM(TaskElement task,int dimension,Double threshold){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}

	public int[] getPreidctValues(){
		return predictValues;
	}
	
	public int getPredictPeriod(){
		if(hasPeriod){
			return predictPeriod;
		}else{
			return -1;
		}
		
	}
	
	public boolean hasPeriod(){
		return hasPeriod;
	}
	
	public int getDimension(){
		return dimension;
	}
	
	public TaskElement getTask(){
		return task;
	}
	
	public DataItems getDi(){
		return di;
	}
	
	public void setThreshold(double Threshold){
		this.threshold=Threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	
	/**
	 * 获取value值在dimension中的维度序号
	 * @param value
	 * @return 序号
	 * @throws NotFoundDicreseValueException
	 */
	private int getValueIndex(String value) throws NotFoundDicreseValueException {
		String endNodes=task.getDiscreteEndNodes();
		String[] nodes=endNodes.split(",");
		for(int i=0;i<nodes.length;i++){
			if(nodes[i].equals(value)){
				return i;
			}
		}
		throw new NotFoundDicreseValueException(value+" not exist");	
	}
	/**
	 * 根据第一个点的起始时间startTime，获取d所代表的时间在一个周期内的序号（从1开始）
	 * @param d			时间值
	 * @param period	周期值
	 * @return 周期内序号
	 */
	private int getTimeIndex(Date d, int period){
		double diffTime = (double)(d.getTime() - startTime.getTime()) / 1000.0;	// 距离起始点的秒数
		int granularity = task.getGranularity();
		return (int)(diffTime / granularity) % period + 1;
	}
	
	public void predictBySeqSimility(){
		int numItems=di.getLength();
		if(numItems==0){
			return;
		}
		
		List<String> seqX=new ArrayList<String>();
		List<String> seqY=new ArrayList<String>();
		int[][] ErpDistMatrix;

		for(int i=0;i<numItems;i++){
			seqX.add((int)Double.parseDouble(di.getData().get(i))+"");
	    	seqY.add((int)Double.parseDouble(di.getData().get(i))+"");
		}
		setDistMatrix(seqX);
		int maxPeriod = numItems/2;
		int period=1;
		seqX.remove(numItems-1);
		seqY.remove(0);
		entropies=new Double[maxPeriod];
		while((period+1)<= maxPeriod){
			period++;	//周期递加
			seqX.remove(seqX.size()-1);
			seqY.remove(0);
			ErpDistMatrix=new int[numItems-period][numItems-period];
			int seqDistance=ERPDistance(seqX, seqY, seqX.size()-1, seqY.size()-1, ErpDistMatrix, period);
			double diff=(seqDistance*1.0)/(numItems-period);
			entropies[period-1]=diff;
			System.out.println("period "+period+"'s diff is "+diff);
		}
		isPeriodExist(maxPeriod);
	}
	
	/**
	 * 设置序列中各元素间的ERP距离 对角线上的ERP距离设为无穷大
	 * @param items 序列
	 */
	private void setDistMatrix(List<String> items) {
		int length=items.size();
		distMatrix=new int[length][length];
		for(int i=0;i<length;i++){
			for(int j=0;j<length;j++){
				if(i==j)
					distMatrix[i][j]=7000*10;
				else
					distMatrix[i][j]=Math.abs((int)Double.parseDouble(items.get(i))-
							(int)Double.parseDouble(items.get(j)));
			}
		}
	}
	
	/**
	 * 返回两条序列的ERP距离
	 * @param seqX 第一条序列
	 * @param seqY 第二条序列
	 * @param offset 第二条序列是平移offset个单位得到的
	 * @return ERP距离
	 */
	private int ERPDistance(List<String> seqX,List<String>seqY,int xSize,int ySize,int [][]matrix,int offset){
		if(xSize<0&&ySize<0){
			return 0;
		}else if(xSize<0){
			int sum=0;
			for(int i=0;i<=ySize;i++){
				sum+=Integer.parseInt(seqY.get(i));
			}
			return sum;
		}else if(ySize<0){
			int sum=0;
			for(int i=0;i<=xSize;i++){
				sum+=Integer.parseInt(seqX.get(i));
			}
			return sum;
		}
		
		if(matrix[xSize][ySize]!=0){
			return matrix[xSize][ySize];
		}else{
			int xItem=Integer.parseInt(seqX.get(xSize));
			int yItem=Integer.parseInt(seqY.get(ySize));
			int dis1=ERPDistance(seqX,seqY,xSize-1,ySize-1,matrix,offset)+distMatrix[xSize][ySize+offset];
			int dis2=ERPDistance(seqX, seqY,xSize-1,ySize,matrix,offset)+Math.abs(xItem);
			int dis3=ERPDistance(seqX, seqY,xSize,ySize-1,matrix,offset)+Math.abs(yItem);
			int min=(dis1>dis2)?dis2:dis1;
			min= (min>dis3)?dis3:min;
			matrix[xSize][ySize]=min;
			return min;
		}
	}
	
	
	/**
	 * 预测周期和该周期内各粒度中离散值的概率分布
	 */
	public void predictPeriod(){
		if(!di.isDiscretized()){
			throw new RuntimeException("平均熵算法要求数据离散化");
		}
		int numItems=di.getLength();
		if (numItems == 0)
			return;
		List<Date> times = di.getTime();
		List<String> values=di.getData();
		startTime = times.get(0);
		
		int period=1;
		int maxPeriod = Math.min(numItems/2, 100);
		entropies = new Double[maxPeriod];
		while((period+1)< maxPeriod){
			period++;	//周期递加
			double entropy=0.0;
			double[][] data=new double[period+1][dimension];
			predictValues=new int[period+1];
			//初始化为零
			for(int i=1;i<=period;i++){
				for(int j=0;j<dimension;j++){
					data[i][j]=0.0;
				}
			}
			for (int i = 0; i < values.size(); i++) {
				try {
					int timeIndex = getTimeIndex(times.get(i), period);
					int valIndex = getValueIndex(values.get(i));
					data[timeIndex][valIndex]+=1;
				} catch (NotFoundDicreseValueException e) {
					e.printStackTrace();
				}
			}
			for(int i=1;i<=period;i++){
				double itemNum=0.0;
				for(int j=0;j<dimension;j++){
					itemNum+=data[i][j];
				}
				for(int j=0;j<dimension;j++){
					data[i][j]=(data[i][j])/(itemNum);
				}
				for(int j=0;j<dimension;j++){
					if(data[i][j]>0){
						entropy+=(-(data[i][j]*Math.log(data[i][j])));
					}
				}
			}
			System.out.println("周期:"+period+" 平均熵:"+(entropy/period)+" 熵："+entropy);
			entropies[period - 1] = (entropy/period);
			if((entropy/period)<threshold){//求平均熵最小的周期
				existPeriod.add(period);
				Integer[] predictValues=new Integer[period];
				double maxPoss=0;
				for(int i=1;i<=period;i++){
					maxPoss=0.0;
					for(int j=0;j<dimension;j++){
						if(data[i][j]>maxPoss){
							maxPoss=data[i][j];
							predictValues[i-1]=(int)Double.parseDouble(task.getDiscreteEndNodes().split(",")[j]);
						}
					}
				}
				predictValuesMap.put(period, predictValues);
				isPeriodExist(maxPeriod);
			}
		}		
	}
	
	/**
	 * 确定周期是否存在，如果存在计算周期内的分布
	 * 确定最小熵
	 * @param maxPeriod 尝试的周期个数
	 */
	private void isPeriodExist(int maxPeriod){
		int minPeriod=0;
		Double maxEntropy=Double.MAX_VALUE;
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<threshold){
				if(entropies[i]<maxEntropy){
					minPeriod=(i+1);
					maxEntropy=entropies[i];
				}
				hasPeriod=true;
				existPeriod.add(i+1);
				Integer[] predictValues=new Integer[i+1];
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(i+1)]+=(int)Double.parseDouble(di.getData().get(j));
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
			}
		}
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			distributePeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(minPeriod);
			for(int i=0;i<minPeriod;i++){
				distributePeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}
	}
	
	public DataItems getDistributeItems(){
		if(hasPeriod){
			return this.distributePeriod;
		}else{
			return null;
		}
		
	}
	
	public Double getMinEntropy() {
		return minEntropy;
	}
	
	public Double[] getEntropies(){
		return entropies;
	}

	public void setDataItems(DataItems dataItems) {
		this.di = dataItems;
	}
	
	
}
