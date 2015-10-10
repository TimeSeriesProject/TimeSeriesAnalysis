package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Exception.NotFoundDicreseValueException;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

public class averageEntropyPM implements IMinerPM{
	private final int dimension;//离散的维度
	private TaskElement task;
	private Boolean hasPeriod; //是否有周期
	private int predictPeriod;   //周期长度
	private List<Integer> existPeriod;
	private DataItems di; //当前时间区间的数据集
	private Date startTime;    //序列中的起始时间
	private DataItems itemsInPeriod;  //一个周期内的items
	private Double minEntropy = Double.MAX_VALUE;  
    private Double []entropies;   //存储每个可能周期的平均熵或平均ERP距离
    private HashMap<Integer, Integer[]> predictValuesMap;
	private double threshold;  //是否具有周期的阈值
	private int lastNumIndexInPeriod;//最后一个数在周期中的位置
	
	public averageEntropyPM(TaskElement taskElement,int dimension){
		this.dimension=dimension;
		this.task=taskElement;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public averageEntropyPM(TaskElement task,int dimension,Double threshold){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap=new HashMap<Integer, Integer[]>();
		existPeriod=new ArrayList<Integer>();
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
		}
		isPeriodExist(maxPeriod);
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
			itemsInPeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(minPeriod);
			for(int i=0;i<minPeriod;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}
	}
	
	@Override
	public boolean hasPeriod() {
		return hasPeriod;
	}
	
	@Override
	public void setDataItems(DataItems dataItems) {
		this.di=dataItems;
	}
	
	@Override
	public int getPredictPeriod() {
		return predictPeriod;
	}
	
	@Override
	public DataItems getItemsInPeriod() {
		return itemsInPeriod;
	}
	
	@Override
	public Double getMinEntropy() {
		return minEntropy;
	}
	
	@Override
	public Double[] getEntropies() {
		return entropies;
	}
	
	@Override
	public int getFirstPossiblePeriod() {
		if(hasPeriod()){
     		return existPeriod.get(0);
		}else{
			return -1;
		}
		
	}
	
	@Override
	public int getLastNumberIndexInperiod() {
		if(hasPeriod()){
	    	lastNumIndexInPeriod=(di.getLength()-1)%(predictPeriod);
	    	return lastNumIndexInPeriod;
		}else{
			return -1;
		}
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
	
	public TaskElement getTask(){
		return task;
	}
	
}
