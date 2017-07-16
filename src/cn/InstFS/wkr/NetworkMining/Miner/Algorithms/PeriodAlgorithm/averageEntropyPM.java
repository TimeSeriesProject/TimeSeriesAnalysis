package cn.InstFS.wkr.NetworkMining.Miner.Algorithms.PeriodAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.net.aso.k;
import cn.InstFS.wkr.NetworkMining.Miner.NetworkMiner.IMinerPM;
import cn.InstFS.wkr.NetworkMining.Params.PMParams.PMparam;
import cn.InstFS.wkr.NetworkMining.DataInputs.DataItems;
import cn.InstFS.wkr.NetworkMining.Exception.NotFoundDicreseValueException;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskElement;

/**
 *
 * 用平均熵算法进行离散时间序列周期检测
 */
public class averageEntropyPM implements IMinerPM {
	/**
	 * 离散的维度
	 */
	private final int dimension;//离散的维度
	private TaskElement task;
	/**
	 * 是否有周期
	 */
	private Boolean hasPeriod; //是否有周期
	/**
	 * 周期长度
	 */
	private int predictPeriod;   //周期长度
	/**
	 * 存放存在的周期
	 */
	private List<Integer> existPeriod;
	/**
	 * 当前时间区间的数据集
	 */
	private DataItems di; //当前时间区间的数据集
	private DataItems oriDi;
	/**
	 *序列中的起始时间
	 */
	private Date startTime;    //序列中的起始时间
	/**
	 * 一个周期内的iterm
	 */
	private DataItems itemsInPeriod;  //一个周期内的items
	private DataItems minItemsInPeriod;
	private DataItems maxItemsInPeriod;
	private Double minEntropy = Double.MAX_VALUE;
	/**
	 * 存储每个可能周期的平均熵
	 */
    private Double []entropies;   //存储每个可能周期的平均熵或平均ERP距离
    private HashMap<Integer, Double[]> predictValuesMap;
    private HashMap<Integer, Double[]> minPredictValuesMap;
    private HashMap<Integer, Double[]> maxPredictValuesMap;
	/**
	 * 是否具有周期的阈值
	 */
	private double threshold;  //是否具有周期的阈值
	private int longestPeriod;
	/**
	 * 最后一个数在周期中的位置
	 */
	private int lastNumIndexInPeriod;//最后一个数在周期中的位置
	private double confidence;
	
	private Map<String, List<Integer>> existPeriodOfNonNumDataItems;
	private Map<String, Boolean> hasPeriodOfNonNumDataItms;
	private Map<String, Integer> predictPeriodOfNonNumDataItems;
	private Map<String, Map<Integer, Double[]>> predictValuesMapOfNonNumDataItems;
	private Map<String, DataItems> itemsInperiodMapOfNonNumDataitems;


	/**
	 *
	 * @param taskElement 任务
	 * @param dimension 离散维度
     */
	public averageEntropyPM(TaskElement taskElement,int dimension){
		this.dimension=dimension;
		this.task=taskElement;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
	}

	/**
	 *
	 * @param task 任务
	 * @param dimension  离散维度
	 * @param threshold  周期阈值
     */
	public averageEntropyPM(TaskElement task,int dimension,Double threshold){
		this.dimension=dimension;
		this.task=task;
		hasPeriod = false;	
		predictPeriod=1;		
		this.threshold=threshold;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
	}
	
	public averageEntropyPM(TaskElement taskElement,int dimension,PMparam pmParam){
		this.dimension=dimension;
		this.task=taskElement;
		hasPeriod = false;	
		predictPeriod=1;
		minEntropy = Double.MAX_VALUE;
		predictValuesMap= new HashMap<>();
		minPredictValuesMap= new HashMap<>();
		maxPredictValuesMap= new HashMap<>();
		existPeriod=new ArrayList<Integer>();
		this.threshold=pmParam.getThreshold();
		this.longestPeriod=pmParam.getLongestPeriod();
	}
	
	/**
	 * 获取记录在dimension中的维度序号
	 * @param value 记录
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
		int maxPeriod = (numItems/2>longestPeriod)?longestPeriod:numItems/2;
		if (numItems == 0)
			return;
		if(di.isAllDataIsDouble()){
			generateEntroy(di.getTime(),di.getData(),numItems);
			isPeriodExist(maxPeriod,null,oriDi.getData());
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
		
	}

	/**
	 * 计算时间序列的平均信息熵
	 * @param times  时间序列
	 * @param values 时间序列对应的值序列
	 * @param numItems 当前时间序列内的iterm个数
     */
	
	private void generateEntroy(List<Date> times,List<String> values,int numItems){
		
        startTime = times.get(0);
		int period=1;
		int maxPeriod = Math.min(numItems/2, longestPeriod);
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
		predictValuesMap= new HashMap<>();
		Map<Integer, Double> ratio=new HashMap<Integer, Double>();
		hasPeriod=false;
		for(int i=1;i<maxPeriod;i++){
			if(isPeriod(entropies, i+1)){
				if(i==1){
					ratio.put(2, entropies[2]/entropies[1]);
				}else if(i==entropies.length-1){
					ratio.put(i+1, entropies[i-1]/entropies[i]);
				}else{
					ratio.put(i+1, Math.min(entropies[i-1]/entropies[i],entropies[i+1]/entropies[i]));
				}
				hasPeriod=true;
				existPeriod.add(i+1);
				Double[] predictValues=new Double[i+1];
				Double[] minPredictValues=new Double[i+1];
				Double[] maxPredictValues=new Double[i+1];
				for(int index=0;index<=i;index++){
					predictValues[index]=0.0;
					minPredictValues[index]=Double.MAX_VALUE;
					maxPredictValues[index]=Double.MIN_VALUE;
				}
				for(int j=0;j<di.getLength();j++){
					double value=Double.parseDouble(seq.get(j));
					predictValues[j%(i+1)]+=value;
					if(minPredictValues[j%(i+1)]>value){
						minPredictValues[j%(i+1)]=value;
					}
					if(maxPredictValues[j%(i+1)]<value){
						maxPredictValues[j%(i+1)]=value;
					}
				}
				for(int j=0;j<(i+1);j++){
					predictValues[j]/=(di.getLength()/(i+1));
				}
				predictValuesMap.put((i+1), predictValues);
				minPredictValuesMap.put((i+1),minPredictValues);
				maxPredictValuesMap.put((i+1),maxPredictValues);
			}
		}
		
//		double ratios=0;
		int possiPeriod=0;
//		for(Integer key:ratio.keySet()){
//			if(ratio.get(key)>ratios){
//				ratios=ratio.get(key);
//				possiPeriod=key;
//			}
//		}
//		predictPeriod=possiPeriod;
//		confidence=ratios;        //该检测周期的置信度
		double minEntropy=Double.MAX_VALUE;
		for(Integer key:ratio.keySet()){
			if(entropies[key-1]<minEntropy){
				minEntropy=entropies[key-1];
				predictPeriod=key;
				possiPeriod=key;
				confidence=ratio.get(key);
			}
		}
		
		for(int i=1;i<maxPeriod;i++){
			if(entropies[i]<minEntropy){
				minEntropy=entropies[i];
			}
		}
		
		if(hasPeriod){
			itemsInPeriod=new DataItems();
			minItemsInPeriod=new DataItems();
			maxItemsInPeriod=new DataItems();
			Double[] predictValues=predictValuesMap.get(predictPeriod);
			Double[] minPredictValues=minPredictValuesMap.get(predictPeriod);
			Double[] maxPredictValues=maxPredictValuesMap.get(predictPeriod);
			if(predictValues==null){
				predictValues=new Double[predictPeriod];
				minPredictValues=new Double[predictPeriod];
				maxPredictValues=new Double[predictPeriod];
				for(int index=0;index<predictPeriod;index++){
					predictValues[index]=0.0;
					minPredictValues[index]=Double.MAX_VALUE;
					maxPredictValues[index]=Double.MIN_VALUE;
				}
				for(int j=0;j<di.getLength();j++){
					double value=Double.parseDouble(seq.get(j));
					predictValues[j%(predictPeriod)]+=value;
					if(minPredictValues[j%(predictPeriod)]>value){
						minPredictValues[j%(predictPeriod)]=value;
					}
					if(maxPredictValues[j%(predictPeriod)]<value){
						maxPredictValues[j%(predictPeriod)]=value;
					}
				}
				for(int j=0;j<(predictPeriod);j++){
					predictValues[j]/=(di.getLength()/(predictPeriod));
				}
				predictValuesMap.put(predictPeriod, predictValues);
				minPredictValuesMap.put(predictPeriod, minPredictValues);
				maxPredictValuesMap.put(predictPeriod, maxPredictValues);
			}
			for(int i=0;i<possiPeriod;i++){
				itemsInPeriod.add1Data(di.getTime().get(i),predictValues[i]+"");
				minItemsInPeriod.add1Data(di.getTime().get(i),minPredictValues[i]+"");
				maxItemsInPeriod.add1Data(di.getTime().get(i),maxPredictValues[i]+"");
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

	/**
	 * 判断输入的索引号的信息熵是否比相邻索引号的信息熵大
	 * @param Entropies 信息熵列表
	 * @param index 信息熵列表的索引，即第几个信息熵
	 * @param isnext index的下一个是否存在
	 * @param origin 起始索引
     * @return 若输入的索引号的信息熵是否比相邻索引号信息熵大，返回ture，否则返回false
     */
	private boolean maxThanNeighbor(Double[] Entropies,int index,boolean isnext,int origin){
		boolean isMaxThanNeighbor=false;
		
		if(index==2){
			if(Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold){
				isMaxThanNeighbor=true;
			}
		}else if(index==Entropies.length){
			if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold){
				isMaxThanNeighbor=true;
			}
		}else{
			if(isnext){
				if(origin-index==1){
					if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold)
						isMaxThanNeighbor=true;
				}else if(origin-index==-1){
					if(Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold)
						isMaxThanNeighbor=true;
				}
			}else if(Entropies[index-1]-Entropies[index-2]<=-Entropies[index-1]*threshold&&
            		Entropies[index-1]-Entropies[index]<=-Entropies[index-1]*threshold){
            	isMaxThanNeighbor=true;
			}
		}
		return isMaxThanNeighbor;
	}

	/**
	 * 判断是否是周期
	 * @param Entropies 信息熵列表
	 * @param index  索引
     * @return 若有周期，返回true，否则返回false
     */
	private boolean isPeriod(Double[] Entropies,int index){
		if(maxThanNeighbor(Entropies, index,false,index)){
			if(nextPeriod(Entropies, index)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}


	/**
	 * 判断下一个周期是否存在
	 * @param Entropies 信息熵列表
	 * @param index 索引
	 * @return 若存在，返回true，否则返回false
     */
	private boolean nextPeriod(Double[] Entropies,int index){
		int i=index;
		boolean period=true;
		int num=0;
		while(i<=Entropies.length&&num<4){
			if(i==2){
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i+1,true,i))){
					period=false;
					break;
				}
			}else if(i==(Entropies.length)){
				
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i-1,true,i))){
					period=false;
					break;
				}
			}else{
				if(!(maxThanNeighbor(Entropies, i,false,i)||maxThanNeighbor(Entropies, i-1,true,i)||
						maxThanNeighbor(Entropies, i+1,true,i))){
					period=false;
					break;
				}
			}
			i+=index;
			num++;
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
		predictValuesMapOfNonNumDataItems= new HashMap<>();
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
	
	
	
	public DataItems getMinItemsInPeriod() {
		return minItemsInPeriod;
	}

	public void setMinItemsInPeriod(DataItems minItemsInPeriod) {
		this.minItemsInPeriod = minItemsInPeriod;
	}

	public DataItems getMaxItemsInPeriod() {
		return maxItemsInPeriod;
	}

	public void setMaxItemsInPeriod(DataItems maxItemsInPeriod) {
		this.maxItemsInPeriod = maxItemsInPeriod;
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
	
	@Override
	public void setOriginDataItems(DataItems dataItems) {
		this.oriDi=dataItems;
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
	public Map<String, Map<Integer, Double[]>> getPredictValuesMapOfNonNumDataItems() {
		return predictValuesMapOfNonNumDataItems;
	}
	public void setPredictValuesMapOfNonNumDataItems(
			Map<String, Map<Integer, Double[]>> predictValuesMapOfNonNumDataItems) {
		this.predictValuesMapOfNonNumDataItems = predictValuesMapOfNonNumDataItems;
	}

	public Map<String, DataItems> getItemsInperiodMapOfNonNumDataitems() {
		return itemsInperiodMapOfNonNumDataitems;
	}

	public void setItemsInperiodMapOfNonNumDataitems(
			Map<String, DataItems> itemsInperiodMapOfNonNumDataitems) {
		this.itemsInperiodMapOfNonNumDataitems = itemsInperiodMapOfNonNumDataitems;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	
	
}
