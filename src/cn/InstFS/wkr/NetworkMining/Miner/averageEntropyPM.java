package cn.InstFS.wkr.NetworkMining.Miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.net.aso.d;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.DataInputs.TextUtils;
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
	
	private Map<String, List<Integer>> existPeriodOfNonNumDataItems;
	private Map<String, Boolean> hasPeriodOfNonNumDataItms;
	private Map<String, Integer> predictPeriodOfNonNumDataItems;
	private Map<String, Map<Integer, Integer[]>> predictValuesMapOfNonNumDataItems;
	private Map<String, DataItems> itemsInperiodMapOfNonNumDataitems;
	
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
		value=value.split("\\.")[0];
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
		int maxPeriod = (numItems/2>100)?100:numItems/2;
		if (numItems == 0)
			return;
		if(di.isAllDataIsDouble()){
			generateEntroy(di.getTime(),di.getData(),numItems);
			isPeriodExist(maxPeriod,null,di.getData());
		}else{
			List<Map<String, Integer>> nonnumData=di.getNonNumData();
			Set<String> itemSet=di.getVarSet();
			for(String item:itemSet){
				List<String> seq=new ArrayList<String>();
				for(Map<String, Integer>map:nonnumData){
					if(map.containsKey(item)){
						int value=map.get(item);
						seq.add(value+"");
					}else{
						seq.add("0");
					}
				}
				System.out.println(item);
				generateEntroy(di.getTime(),seq, numItems);
				isPeriodExist(maxPeriod,item,seq);
			}
		}
		List<Date> times = di.getTime();
		List<String> values=di.getData();
		
		
	}
	
	private void generateEntroy(List<Date> times,List<String> values,int numItems){
		
        startTime = times.get(0);
		int period=1;
		int maxPeriod = Math.min(numItems/2, 100);
		entropies = new Double[maxPeriod];
		while((period+1)<= maxPeriod){
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
	}
	
	/**
	 * 确定周期是否存在，如果存在计算周期内的分布
	 * 确定最小熵
	 * @param maxPeriod 尝试的周期个数
	 */
	private void isPeriodExist(int maxPeriod,String item,List<String>seq){
		itemsInPeriod=new DataItems();
		existPeriod=new ArrayList<Integer>();
		predictValuesMap=new HashMap<Integer, Integer[]>();
		hasPeriod=false;
		for(int i=1;i<maxPeriod;i++){
			if(isPeriod(entropies, i+1)){
				hasPeriod=true;
				existPeriod.add(i+1);
				Integer[] predictValues=new Integer[i+1];
				for(int index=0;index<=i;index++){
					predictValues[index]=0;
				}
				for(int j=0;j<di.getLength();j++){
					predictValues[j%(i+1)]+=(int)Double.parseDouble(seq.get(j));
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
			}
		}
		int Period=maxPeriod;
		Set<Integer> keyset=predictValuesMap.keySet();
		for(Integer key:keyset){
			if(key<=Period){
				Period=key;
			}
		}
		predictPeriod=Period;
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			Integer[] predictValues=predictValuesMap.get(Period);
			for(int i=0;i<Period;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
			}
		}else{
			itemsInPeriod=null;
			predictPeriod=-1;
			existPeriod=null;
			predictValuesMap=null;
		}
		if(item!=null){
			hasPeriodOfNonNumDataItms.put(item, hasPeriod);
			itemsInperiodMapOfNonNumDataitems.put(item, itemsInPeriod);
			existPeriodOfNonNumDataItems.put(item, existPeriod);
			predictPeriodOfNonNumDataItems.put(item, predictPeriod);
			predictValuesMapOfNonNumDataItems.put(item, predictValuesMap);
		}
	}
	
	private boolean isPeriod(Double[] Entropies,int index){
		boolean period=true;
		int i=index;
		while(i<=Entropies.length){
			if(i==2){
				if(Entropies[i-1]-Entropies[i]>=-0.05){
					period=false;
					break;
				}
			}else if(i==(Entropies.length)){
				if(Entropies[i-1]-Entropies[i-2]>=-0.05){
					period=false;
					break;
				}
			}else{
				if(Entropies[i-1]-Entropies[i-2]>=-0.05||Entropies[i-1]-Entropies[i]>=-0.05){
					period=false;
					break;
				}
			}
			i+=index;
		}
		return period;
	}
	
	@Override
	public boolean hasPeriod() {
		return hasPeriod;
	}
	
	@Override
	public void setDataItems(DataItems dataItems) {
		this.di=dataItems;
		existPeriodOfNonNumDataItems=new HashMap<String, List<Integer>>();
		hasPeriodOfNonNumDataItms=new HashMap<String, Boolean>();
		predictPeriodOfNonNumDataItems=new HashMap<String, Integer>();
		predictValuesMapOfNonNumDataItems=new HashMap<String, Map<Integer,Integer[]>>();
		itemsInperiodMapOfNonNumDataitems=new HashMap<String, DataItems>();
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
	
	public Map<String, List<Integer>> getExistPeriodOfNonNumDataItems() {
		return existPeriodOfNonNumDataItems;
	}
	public void setExistPeriodOfNonNumDataItems(
			Map<String, List<Integer>> existPeriodOfNonNumDataItems) {
		this.existPeriodOfNonNumDataItems = existPeriodOfNonNumDataItems;
	}
	public Map<String, Boolean> getHasPeriodOfNonNumDataItms() {
		return hasPeriodOfNonNumDataItms;
	}
	public void setHasPeriodOfNonNumDataItms(
			Map<String, Boolean> hasPeriodOfNonNumDataItms) {
		this.hasPeriodOfNonNumDataItms = hasPeriodOfNonNumDataItms;
	}
	public Map<String, Integer> getPredictPeriodOfNonNumDataItems() {
		return predictPeriodOfNonNumDataItems;
	}
	public void setPredictPeriodOfNonNumDataItems(
			Map<String, Integer> predictPeriodOfNonNumDataItems) {
		this.predictPeriodOfNonNumDataItems = predictPeriodOfNonNumDataItems;
	}
	public Map<String, Map<Integer, Integer[]>> getPredictValuesMapOfNonNumDataItems() {
		return predictValuesMapOfNonNumDataItems;
	}
	public void setPredictValuesMapOfNonNumDataItems(
			Map<String, Map<Integer, Integer[]>> predictValuesMapOfNonNumDataItems) {
		this.predictValuesMapOfNonNumDataItems = predictValuesMapOfNonNumDataItems;
	}

	public Map<String, DataItems> getItemsInperiodMapOfNonNumDataitems() {
		return itemsInperiodMapOfNonNumDataitems;
	}

	public void setItemsInperiodMapOfNonNumDataitems(
			Map<String, DataItems> itemsInperiodMapOfNonNumDataitems) {
		this.itemsInperiodMapOfNonNumDataitems = itemsInperiodMapOfNonNumDataitems;
	}
	
	
	
}
